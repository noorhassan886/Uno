package Server;

import common.ColoredSpecialcard;
import common.RegularCard;
import common.UnoCard;
import common.WildCard;

import java.util.*;

public class Deck {

    private Stack<UnoCard> cards;

    public Deck(int numCards) {
        this.cards = new Stack<>();

        if (numCards != 0) {
            UnoCard[] startingCards = new UnoCard[108];
            String[] colors = {"Blue", "Red", "Yellow", "Green"};
            int position = 0;

            for (String color : colors) {
                for (int i = 0; i < 10; i++) {
                    startingCards[position] = new RegularCard(color, i);
                    position++;
                }
                for (int i = 1; i < 10; i++) {
                    startingCards[position] = new RegularCard(color, i);
                    position++;
                }
                // Doubling loop
                for (int i = 0; i < 2; i++) {
                    startingCards[position] = new ColoredSpecialcard(color, "Skip");
                    position++;
                    startingCards[position] = new ColoredSpecialcard(color, "Reverse");
                    position++;
                    startingCards[position] = new ColoredSpecialcard(color, "+2");
                    position++;
                }
            }

            for (int i = 0; i < 4; i++) {
                startingCards[position] = new WildCard("+0");
                position++;
                startingCards[position] = new WildCard("+4");
                position++;
            }

            List<UnoCard> list = Arrays.asList(startingCards);
            Collections.shuffle(list);
            this.cards.addAll(list);

        }

    }

    public UnoCard drawCard() {
        return this.cards.pop();
    }

    public int getNumCards() {
        return this.cards.size();
    }

    public void reset(Deck discardPile) {
        // Remove card from the discard pile
        Stack<UnoCard> discardStack = discardPile.cards;
        UnoCard[] discardCards = new UnoCard[discardStack.size()];
        for (int i = 0; i < discardCards.length; i++)
            discardCards[i] = discardStack.pop();

        // Shuffle cards
        List<UnoCard> list = Arrays.asList(discardCards);
        Collections.shuffle(list);

        // Remove cards already in the stack
        UnoCard[] leftoverCards = new UnoCard[this.cards.size()];
        for (int i = 0; i < leftoverCards.length; i++)
            leftoverCards[i] = this.cards.pop();

        // Put discard cards back in the stack
        this.cards.addAll(list);

        // Add the originals back in the order they were in originally
        this.cards.addAll(Arrays.asList(leftoverCards));
    }

    public UnoCard getCard() {
        return this.cards.peek();
    }

    public void addCard(UnoCard newCard) {
        this.cards.push(newCard);
    }

}
