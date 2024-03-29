package common;

import java.util.ArrayList;

public class PlayerHand {

    private boolean canCallUno;

    // List of Uno cards
    private ArrayList<UnoCard> cards;

    public PlayerHand() {
        this.cards = new ArrayList<>();
        this.canCallUno = false;
    }

    public void addCard(UnoCard card) {
        cards.add(card);
    }

    public ArrayList<UnoCard> getCards() {return this.cards;}

    public void clear() {
        this.cards.clear();
    }

    public void remove(UnoCard card) {
        // TODO: if we're removing a Wild - then consider uncolored version
        if(card instanceof WildCard) {
            card = new WildCard(card.getInfo());
        }

        // iterate through the list
        for (int i = 0; i < this.cards.size(); i++) {
            // find a match
            if (card.equals(this.cards.get(i))) {
                // remove and quit
                this.cards.remove(i);
                return;
            }
        }
    }

    @Override
    public String toString() {
        // loop through each card in the hand
        StringBuilder handDescription = new StringBuilder();
        for (UnoCard card : this.cards) {
            // get each of the text-form description
            // give strings together with commas in between
            handDescription.append(card.toString()).append(",");
        }

        return handDescription.substring(0, handDescription.length() - 1);
    }

    public boolean getCanCallUno() {
        return canCallUno;
    }

    public void setCanCallUno(boolean canCallUno) {
        this.canCallUno = canCallUno;
    }
}
