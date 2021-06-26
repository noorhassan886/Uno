package Server;

import common.*;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UnoGameThread extends Thread {

    // Deck of cards to draw from
    private final Deck drawFrom;
    // Deck/pile of cards that are played
    private final Deck discardPile;
    // Representation of each player, including their hand
    private final PlayerHand[] hands;
    // Track the players
    private final SocketWrapper[] players;

    // Game state variables
    private int playerTurn = 0; // Index of the player whose turn it is
    private String direction = "cw"; // Direction determining who the next player will be
    private int plusTwoStacks = 0;  // Number of Draw 2's that are stacked
    private int plusFourStacks = 0; // Number of Draw 4's that are stacked

    public UnoGameThread(SocketWrapper... players) {
        this.drawFrom = new Deck(108);
        this.discardPile = new Deck(0);
        this.players = players;
        this.hands = new PlayerHand[this.players.length];
        for (int i = 0; i < this.hands.length; i++)
            hands[i] = new PlayerHand();
    }

    @Override
    public void run() {
        // Deal 7 cards per player
        for (PlayerHand hand : this.hands) {
            for (int i = 0; i < 7; i++) {
                hand.addCard(this.drawFrom.drawCard());
            }
        }

        // pick a card for the discard pile to start with
        while (this.discardPile.getNumCards() == 0 || this.discardPile.getCard() instanceof SpecialCard) {
            // taking a card from the draw pile and putting it on the discard pile
            this.discardPile.addCard(this.drawFrom.drawCard());
        }

        // Tell the players what their hand and top card is
        for (int i = 0; i < this.players.length; i++) {
            try {
                players[i].send("NEW_HAND//" + hands[i].toString());
                players[i].send("TOP_CARD//" + this.discardPile.getCard().toString());
                players[i].send("PLAYER_ID//" + i);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        broadcastHandSizes();

        // Begin listening to players
        final BlockingQueue<PlayerEvent> messageQueue = new LinkedBlockingQueue<>();
        final PlayerListeningThread[] listeningThreads = new PlayerListeningThread[this.players.length];
        for (int i = 0; i < listeningThreads.length; i++) {
            listeningThreads[i] = new PlayerListeningThread(messageQueue, this.players[i], i);
            listeningThreads[i].start();
        }

        while (!hasGameEnded()) {
            PlayerEvent event = null;
            try {
                event = messageQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }

            // If someone is trying to place a card
            if (event.getAction().equals("PLACE_CARD")) {
                // If it is the correct player's turn
                if (event.getId() == playerTurn) {
                    String card = event.getPayload();
                    // Convert "card" to an Uno Card
                    UnoCard convertedCard = UnoCard.fromString(card);

                    // Validate if card can be placed
                    boolean canBePlaced = canPlaceCard(convertedCard, hands[event.getId()]);

                    if (canBePlaced) {
                        // Put the card on the discard pile
                        discardPile.addCard(convertedCard);
                        // Remove the card from the hand
                        this.hands[playerTurn].remove(convertedCard);
                        // Send the updated hand to the player
                        try {
                            players[playerTurn].send("NEW_HAND//" + this.hands[playerTurn].toString());
                        } catch (IOException e) {
                            System.out.println("Error sending updated hand to player: " + e.getMessage());
                        }
                        broadcastHandSizes();

                        // Potentially make that person uno-able
                        if (this.hands[playerTurn].getCards().size() == 1)
                            this.hands[playerTurn].setCanCallUno(true);

                        // Tell all players about the new top card
                        for (SocketWrapper player : players) {
                            try {
                                player.send("TOP_CARD//" + this.discardPile.getCard().toString());
                            } catch (IOException e) {
                                System.out.println("Error sending new top card: " + e.getMessage());
                            }
                        }

                        // Handle effects of cards
                        if (convertedCard.getInfo().equals("Skip")) {
                            playerTurn += direction.equals("cw") ? 1 : -1;
                        }
                        // Reverse
                        else if (convertedCard.getInfo().equals("Reverse")) {
                            // Reverse direction in 4-player games
                            if (players.length == 4) {
                                direction = direction.equals("cw") ? "ccw" : "cw";
                            }
                            // Do nothing in 2-player games
                        }
                        // Draw 2
                        else if (convertedCard.getInfo().equals("+2")) {
                            plusTwoStacks += 1;
                        }
                        // Draw 4
                        else if (convertedCard.getInfo().equals("+4")) {
                            plusFourStacks += 1;
                        }

                        // Increment the current player's turn
                        if (direction.equals("cw")) {
                            playerTurn = (playerTurn + 1) % players.length;
                        } else {
                            playerTurn = (playerTurn - 1) % players.length;
                        }

                        handleCardStacks();
                    }
                }
            }

            // If someone is trying to draw a card
            else if (event.getAction().equals("DRAW_CARD")) {
                if (event.getId() == playerTurn) {
                    // Only do this if the player doesn't already have a card they can place
                    boolean needsToDraw = true;
                    for (UnoCard card : hands[playerTurn].getCards()) {
                        if (canPlaceCard(card, hands[playerTurn])) {
                            needsToDraw = false;
                            break;
                        }
                    }

                    if (needsToDraw)
                        addCardToPlayer(drawCardFromDeck(), playerTurn);
                }
            }

            // If someone calls Uno
            else if (event.getAction().equals("CALL_UNO")) {
                // See if they are calling Uno because of someone else
                boolean checkingSomeoneElse = false;
                for (int i = 0; i < hands.length; i++) {
                    if (i != event.getId()) {
                        if (hands[i].getCanCallUno()) {
                            checkingSomeoneElse = true;
                            hands[i].setCanCallUno(false);
                            for (int j = 0; j < 4; j++) {
                                addCardToPlayer(drawFrom.drawCard(), i);
                            }
                        }
                    }
                }

                // See if they are calling Uno because of themselves
                boolean savedSelf = hands[event.getId()].getCanCallUno();
                if (hands[event.getId()].getCanCallUno()) {
                    hands[event.getId()].setCanCallUno(false);
                }

                // Penalize them if neither of the above happened
                if (!checkingSomeoneElse && !savedSelf) {
                    for (int j = 0; j < 4; j++) {
                        addCardToPlayer(drawFrom.drawCard(), event.getId());
                    }
                }
            }
        }

        // TODO: Close things properly, kill threads properly
    }

    private boolean hasGameEnded() {
        for (PlayerHand hand : hands) {
            if (hand.getCards().size() == 0)
                return true;
        }
        return false;
    }

    private void handleCardStacks() {
        // Handle stacking card draws
        if (plusTwoStacks > 0 || plusFourStacks > 0) {
            if (plusTwoStacks > 0) {
                // Consume the stack only if they do not have a +2 to use
                if (!hands[playerTurn].toString().contains("+2")) {
                    // For each stack, give them 2 cards
                    for (int i = 0; i < plusTwoStacks; i++) {
                        addCardToPlayer(drawCardFromDeck(), playerTurn);
                        addCardToPlayer(drawCardFromDeck(), playerTurn);
                    }
                    // Reset number of cards that needs to be drawn
                    plusTwoStacks = 0;
                }
            } else {
                // Consume the stack only if they do not have a +4 to use
                if (!hands[playerTurn].toString().contains("Draw 4")) {
                    // For each stack, give them 2 cards
                    for (int i = 0; i < plusFourStacks; i++) {
                        addCardToPlayer(drawCardFromDeck(), playerTurn);
                        addCardToPlayer(drawCardFromDeck(), playerTurn);
                        addCardToPlayer(drawCardFromDeck(), playerTurn);
                        addCardToPlayer(drawCardFromDeck(), playerTurn);
                    }
                    // Reset number of cards that needs to be drawn
                    plusFourStacks = 0;
                }
            }
        }
    }

    private void addCardToPlayer(UnoCard card, int playerTurn) {
        // Add card to server copy of hand
        hands[playerTurn].addCard(card);
        // That player cannot call Uno anymore
        hands[playerTurn].setCanCallUno(false);
        // Tell that player what their new hand is
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
        // Draw a card and return it
        return drawFrom.drawCard();
    }

    private boolean canPlaceCard(UnoCard currentCard, PlayerHand hand) {
        boolean canBePlaced = discardPile.canPlaceCard(currentCard);
        if (!canBePlaced) return false;

        UnoCard match = null;

        for (UnoCard card : hand.getCards()) {
            // If the type matches (+2, +4, number, Skip, etc)
            if (card.getInfo().equals(discardPile.getCard().getInfo())) {
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
        payload.deleteCharAt(payload.length() - 1);

        try {
            for (SocketWrapper player : players) {
                player.send("DISPLAYING_PLAYER_HANDS//" + payload);
            }
        } catch (IOException e) {
            System.out.println("Error sending updated hand sizes to player: " + e.getMessage());
        }
    }

}
