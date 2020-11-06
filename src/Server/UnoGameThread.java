package Server;

import common.PlayerHand;
import common.SocketWrapper;
import common.SpecialCard;
import common.WildCard;

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

        // pick a card for the discard pile to start with
        while (this.discardPile.getNumCards() == 0|| this.discardPile.getCard() instanceof SpecialCard) {
            // taking a card from the draw pile and putting it on the discard pile
            this.discardPile.addCard(this.drawFrom.drawCard());
        }

        // TODO: tell the players what their hand it
        for (int i = 0; i < this.players.length; i++) {
            players[i].send(hands[i].toString());
        }

        // TODO: player turn starts
    }

}
