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

}
