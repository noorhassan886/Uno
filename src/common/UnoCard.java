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
        }
       map.put("Wild", ImageIO.read(new File("img/large/wild_+0_large.png")));
       map.put("Wild Draw 4", ImageIO.read(new File("img/large/wild_+4_large.png")));
       map.put("Card Back", ImageIO.read(new File("img/large/card_back_large.png")));

        System.out.println(map.size());


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
            if (cardDescription.equals("Wild Draw 4")) {
                return new WildCard("+4");
            }
            return new WildCard("+0");
        }
        String[] pieces = cardDescription.split(" ");
        if(pieces[1].equals("Reverse") || pieces[1].equals("Skip") || pieces[1].equals("+2")) {
            return new ColoredSpecialcard(pieces[0], pieces[1]);
        }

        return new RegularCard(pieces[0], Integer.parseInt(pieces[1]));

    }

    public abstract String getInfo();

}

