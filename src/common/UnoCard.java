package common;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public abstract class UnoCard {

    protected String color;
    protected BufferedImage image;

    public static HashMap<String, BufferedImage> map;

    public static void loadCardImage() throws IOException

    {
        map = new HashMap<>();

        String[] colors = {"Blue", "Red", "Yellow", "Green"};
        for (String color : colors) {
            for (int i = 0; i < 10; i++) {
                String filename = color + "_" + i + "_large.png";
                BufferedImage image = ImageIO.read(new File("img/large/" + filename.toLowerCase()));
                map.put(color + " " + i, image);
            }
            // Reverse Card
            String filename = color + "_" + "reverse_large.png";
            BufferedImage image = ImageIO.read(new File("img/large/" + filename.toLowerCase()));
            map.put(color + " Reverse", image);
            // Skip Card
            filename = color + "_" + "skip_large.png";
            image = ImageIO.read(new File("img/large/" + filename.toLowerCase()));
            map.put(color + " Skip", image);
            // +2 Card
            filename = color + "_" + "+2_large.png";
            image = ImageIO.read(new File("img/large/" + filename.toLowerCase()));
            map.put(color + " +2", image);
            // Wild +0
            filename = "wild_+0_" + color + "_large.png";
            image = ImageIO.read(new File("img/large/" + filename.toLowerCase()));
            map.put("Wild " + color, image);
            // Wild +4
            filename = "wild_+4_" + color + "_large.png";
            image = ImageIO.read(new File("img/large/" + filename.toLowerCase()));
            map.put("Wild Draw 4 " + color, image);
        }

        // These null colors represent uncolored Wild cards
        map.put("Wild null", ImageIO.read(new File("img/large/wild_+0_large.png")));
        map.put("Wild Draw 4 null", ImageIO.read(new File("img/large/wild_+4_large.png")));
        map.put("Card Back", ImageIO.read(new File("img/large/card_back_large.png")));
    }

    public static BufferedImage getImageForCard(UnoCard card) {return map.get(card.toString());}

    public static BufferedImage getImageForCard(String card) {return map.get(card);}

    public UnoCard(String startingColor) {
        this.color = startingColor;
    }

    public String getColor() {
        return color;
    }

    public static UnoCard fromString(String cardDescription) {

        if(cardDescription.startsWith("Wild")) {
            String color = cardDescription.substring(cardDescription.lastIndexOf(" ")+1);
            WildCard card;
            if (cardDescription.startsWith("Wild Draw 4")) {
               card = new WildCard("+4");
               card.setColor(color.equals("null") ? null : color);
               return card;
            }
                card = new WildCard("+0");
                card.setColor(color.equals("null") ? null : color);
                return card;
        }
        String[] pieces = cardDescription.split(" ");
        if(pieces[1].equals("Reverse") || pieces[1].equals("Skip") || pieces[1].equals("+2")) {
            return new ColoredSpecialcard(pieces[0], pieces[1]);
        }

        return new RegularCard(pieces[0], Integer.parseInt(pieces[1]));
    }

    public abstract String getInfo();

    public boolean equals (Object other) {
        if (other == null) return false;
        if(!(other instanceof UnoCard)) return false;

        UnoCard card = (UnoCard) other;
        // not equal is type/info is different
        if(!this.getInfo().equals(card.getInfo())) return false;
        // if first is null the other is null
        if(this.getColor() == null) return card.getColor() == null;
        // colors need to match
        else return this.getColor().equals(card.getColor());
    }

}

