package Server;

import common.*;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UnoGameThread extends Thread{

    // Deck of cards to draw from
    private Deck drawFrom;
    // Deck/pile of cards that are played
    private Deck discardPile;
    // Representation of each player, including hand
    private PlayerHand[] hands;
    // Tack players
    private SocketWrapper[] players;
    // Rules

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
        // Deal 7 cards per player
        for (PlayerHand hand : this.hands) {
            for (int i = 0; i < 7; i++) {
                hand.addCard(this.drawFrom.drawCard());
            }
        }

        // Pick a card for the discard pile to start with
        while (this.discardPile.getNumCards() == 0|| this.discardPile.getCard() instanceof SpecialCard) {
            // taking a card from the draw pile and putting it on the discard pile
            this.discardPile.addCard(this.drawFrom.drawCard());
        }

        // Tell the players what their hand and top card is
        for (int i = 0; i < this.players.length; i++) {
            try {
                players[i].send("NEW_HAND//" + hands[i].toString());
                players[i].send("TOP_CARD//" + this.discardPile.getCard().toString());
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

        // Game state variables
        int playerTurn = 0;
        String direction = "cw";


        while (true /* as long as the game is going */) {
            PlayerEvent event = null;
            try {
                event = messageQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }

            /*
            TODO:
                place a card
                draw a card
                call uno
             */

            // If someone is trying to place a card
            if(event.getAction().equals("PLACE_CARD")) {
                // If its the correct players turn
                if(event.getId() == playerTurn) {
                 /*
                TODO:
                    2. get their card of choice from the event playload
                    3. validation
                    4. place card on pile
                    5. Notify each player the card was placed
                    6. propagate effects onto next player
                 */

                    String card = event.getPayload();
                    // Convert "card" to an Uno Card
                    UnoCard convertedCard = UnoCard.fromString(card);

                    // Validate if card can be placed
                    boolean canBePlaced = discardPile.canPlaceCard(convertedCard);

                    if (canBePlaced) {
                        // Put the card on the discard pile
                        discardPile.addCard(convertedCard);
                        // tell all players abt new top card
                        for (SocketWrapper player : players) {
                            try {
                                player.send("TOP_CARD//" + this.discardPile.getCard().toString());
                            } catch (IOException e) {
                                System.out.println("Error sending new top card: " + e.getMessage());
                                ;
                            }

                        }
                        // Handle effects of cards
                        if(convertedCard.getInfo().equals("Skip"))
                            playerTurn += direction.equals("cw") ? 1: -1;

                    }
//                    reverse;
//                    +2;
//                    +4;
//                    wild color picking;

                    // Increment current players turn
                    if(direction.equals("cw")) {
                        playerTurn = (playerTurn + 1) % players.length;
                    } else {
                        playerTurn = (playerTurn - 1) % players.length;
                    }

                }

            }

        }

    }

}
