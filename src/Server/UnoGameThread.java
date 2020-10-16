package Server;

import common.UnoCard;

public class UnoGameThread {

    // Deck of cards to draw from
    Deck drawFrom;
    // Deck/pile of cards that are played
    Deck playTo;
    // Representation of each player, including hand
    // Rules

    public UnoGameThread(){
        this.drawFrom = new Deck(108);
        // Debugging
        for (int i = 0; i < 10; i++) {
            UnoCard topCard = this.drawFrom.drawCard();
            System.out.println(topCard.toString());
        }

        this.playTo = new Deck(0);

    }

}
