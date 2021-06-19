package Server;

import common.*;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UnoGameThread extends Thread {

    // Deck of cards to draw from
    private Deck drawFrom;
    // Deck/pile of cards that are played
    private Deck discardPile;
    // Representation of each player, including hand
    private PlayerHand[] hands;
    // Tack players
    private SocketWrapper[] players;
    // Rules

    // Game state variables
    private int playerTurn = 0; // index of player whose turn it is
    private String direction = "cw"; //determining who the next player will be
    private int plusTwoStacks = 0; // number of draw two's that are stacked
    private int plusFourStacks = 0; // number of draw four's that are stacked

    public UnoGameThread(SocketWrapper... player) {
        this.drawFrom = new Deck(108);
        // Debugging
//        for (int i = 0; i < 10; i++) {
//            UnoCard topCard = this.drawFrom.drawCard();
//            System.out.println(topCard.toString());
//        }

        this.discardPile = new Deck(0);
        this.players = player;
        this.hands = new PlayerHand[this.players.length];
        for (int i = 0; i < this.hands.length; i++) {
            hands[i] = new PlayerHand();
        }
    }

    @Override
    public void run() {
        System.out.println("Starting new two player game");
        // Deal 7 cards per player
        for (PlayerHand hand : this.hands) {
            for (int i = 0; i < 7; i++) {
                hand.addCard(this.drawFrom.drawCard());
            }
        }

        // Pick a card for the discard pile to start with
        while (this.discardPile.getNumCards() == 0 || this.discardPile.getCard() instanceof SpecialCard) {
            // taking a card from the draw pile and putting it on the discard pile
            this.discardPile.addCard(this.drawFrom.drawCard());
        }

        // Tell the players what their hand and top card is
        System.out.println("Sending players their opening hand");
        for (int i = 0; i < this.players.length; i++) {
            try {
                players[i].send("NEW_HAND//" + hands[i].toString());
                players[i].send("TOP_CARD//" + this.discardPile.getCard().toString());
                players[i].send("PLAYER_ID//" + i);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //  Begin listening to players
        final BlockingQueue<PlayerEvent> messageQueue = new LinkedBlockingQueue<>();
        final PlayerListeningThread[] listeningThreads = new PlayerListeningThread[this.players.length];
        for (int i = 0; i < listeningThreads.length; i++) {
            listeningThreads[i] = new PlayerListeningThread(messageQueue, this.players[i], i);
            listeningThreads[i].start();
        }

        while (true /* as long as the game is going */) {
            PlayerEvent event = null;
            try {
                event = messageQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }

            // If someone is trying to place a card
            if (event.getAction().equals("PLACE_CARD")) {
                // If its the correct players turn
                if (event.getId() == playerTurn) {
                    String card = event.getPayload();
                    // Convert "card" to an Uno Card
                    UnoCard convertedCard = UnoCard.fromString(card);

                    // Validate if card can be placed
                    // Make one card validation method for
                    // base Uno Compatibility and special card stacking rules

                    boolean canBePlaced = canPlaceCard(convertedCard, hands[event.getId()]);


                    if (canBePlaced) {
                        // Put the card on the discard pile
                        discardPile.addCard(convertedCard);

                        // remove from the card from the hand
                        this.hands[playerTurn].remove(convertedCard);

                        // send the updated hand to player
                        try {
                            players[playerTurn].send("NEW_HAND//" + this.hands[playerTurn].toString());
                        } catch (IOException e) {
                            System.out.println("Error sending updated hand to player " + e.getMessage());
                        }
                        broadcastHandSizes();

                        // potentially make that person uno-able
                        if(this.hands[playerTurn].getCards().size() == 1)
                            this.hands[playerTurn].setCanCallUno(true);

                        // tell all players abt new top card
                        for (SocketWrapper player : players) {
                            try {
                                player.send("TOP_CARD//" + this.discardPile.getCard().toString());
                            } catch (IOException e) {
                                System.out.println("Error sending new top card: " + e.getMessage());
                            }
                        }

                        // Handle effects of cards
                        if (convertedCard.getInfo().equals("Skip"))
                            playerTurn += direction.equals("cw") ? 1 : -1;

                    } else if (convertedCard.getInfo().equals("Reverse")) {
                        // Reverse direction for 4 player game
                        if (players.length == 4)
                            direction = direction.equals("cw") ? "ccw" : "cw";

                        // Do nothing for two player game
                    } else if (convertedCard.getInfo().equals("+2")) {
                        plusTwoStacks += 1;

                    } else if (convertedCard.getInfo().equals("+4")) {
                        plusFourStacks += 1;
                    }

                    // wild color picking;
                    // Increment current players turn
                    if (direction.equals("cw")) {
                        playerTurn = (playerTurn + 1) % players.length;
                    } else {
                        playerTurn = (playerTurn - 1) % players.length;
                    }

                    // Handing stacking cards
                    handleCardStacks();
                }

            }

            // If someone is trying to draw a card
            else if(event.getAction().equals("DRAW_CARD")) {
                if(event.getId() == playerTurn) {
                    // only do this if a player does not have a card to place
                    boolean needsToDraw = true;
                    for (UnoCard card : hands[playerTurn].getCards()) {
                        if(canPlaceCard(card, hands[playerTurn])) {
                            needsToDraw = false;
                            break;
                        }
                    }

                    if(needsToDraw)
                        addCardToPlayer(drawCardFromDeck(), playerTurn);
                }
            }

            // If someone calls Uno
            else if (event.getAction().equals("CALL_UNO")) {
                boolean checkingSomeoneElse = false;
                for (int i = 0; i < hands.length; i++) {
                    if (i != event.getId()) {
                        if (hands[i].getCanCallUno()) {
                            System.out.println("PLayer " + event.getId() + " calls Uno on player " + i);
                            checkingSomeoneElse = true;
                            hands[i].setCanCallUno(false);
                            for (int j = 0; j < 4; j++) {
                                addCardToPlayer(drawFrom.drawCard(), i);
                            }
                        }
                    }
                }
                boolean savedSelf = hands[event.getId()].getCanCallUno();
                if(hands[event.getId()].getCanCallUno()) {
                    hands[event.getId()].setCanCallUno(false);
                    System.out.println("Player " + event.getId() + " saved themselves");
                }

                if (checkingSomeoneElse && !savedSelf) {
                    for (int j = 0; j < 4; j++) {
                        addCardToPlayer(drawFrom.drawCard(), event.getId());
                    }
                }
            }
        }
    }

    private void handleCardStacks() {
        if (plusTwoStacks > 0 || plusFourStacks > 0) {
            if (plusTwoStacks > 0) {
                // consume the stack if they do not have a +2
                if (!hands[playerTurn].toString().contains("+2")) {
                    // for each stack give them two cards
                    for (int i = 0; i < plusTwoStacks; i++) {
                        addCardToPlayer(drawCardFromDeck(), playerTurn);
                        addCardToPlayer(drawCardFromDeck(), playerTurn);
                    }
                    // Reset num of cards that needs to be drawn
                    plusTwoStacks = 0;
                }
            }

        } else {
            // consume the stack if they do not have a +2
            if (!hands[playerTurn].toString().contains("Draw 4")) {
                // for each stack give them two cards
                for (int i = 0; i < plusFourStacks; i++) {
                    addCardToPlayer(drawCardFromDeck(), playerTurn);
                    addCardToPlayer(drawCardFromDeck(), playerTurn);
                    addCardToPlayer(drawCardFromDeck(), playerTurn);
                    addCardToPlayer(drawCardFromDeck(), playerTurn);
                }
                // Reset num of cards that needs to be drawn
                plusFourStacks = 0;
            }
        }
    }

    private void addCardToPlayer(UnoCard card, int playerTurn) {
        // Add card to server copy of hand
        hands[playerTurn].addCard(card);
        // That player cannot call Uno
        hands[playerTurn].setCanCallUno(false);
        // Tell player their new hand
        try {
            players[playerTurn].send("NEW_HAND//" + hands[playerTurn].toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        broadcastHandSizes();
    }

    private UnoCard drawCardFromDeck() {
        // Reset if necessary
        if (drawFrom.getNumCards() == 0)
            drawFrom.reset(discardPile);
        // Draw a card and return
        return drawFrom.drawCard();
    }

    private boolean canPlaceCard(UnoCard currentCard, PlayerHand hand) {
        boolean canBePlaced = discardPile.canPlaceCard(currentCard);
        if (!canBePlaced) return false;

        // TODO: validate that they can place +2 on +2; +4 on +4; skip/reverse
        UnoCard match = null;

        for (UnoCard card : hand.getCards()) {
            // If the type matches (+2, +4, number, Skip, etc.)
            if (card.getInfo().equals(discardPile.getCard().getClass())) {
                // Special case for duplicate regular cards where we need color to match as well
                if (card instanceof RegularCard && card.getColor().equals(discardPile.getCard().getColor())) {
                    match = card;
                } else if (!(card instanceof RegularCard)) {
                    match = card;
                }
            }
        }

        if (match == null) {
            return true;
        } else if (match instanceof SpecialCard) {
            return match.getInfo().equals(currentCard.getInfo());
        } else {
            return match.equals(currentCard);
        }
    }

    private void broadcastHandSizes() {
        StringBuilder payload = new StringBuilder();
        for (PlayerHand hand : this.hands) {
            payload.append(hand.getCards().size());
            payload.append(',');
        }
        payload.deleteCharAt(payload.length()-1);

        try {
            for (SocketWrapper player : players) {
                player.send("DISPLAYING_PLAYER_HANDS//" + payload);
            }
        } catch (IOException e) {
            System.out.println("Error sending updated hand to player " + e.getMessage());
        }
    }
}
