package client;

import common.PlayerHand;
import common.UnoCard;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GamePanel extends JPanel {

    private final JButton button1, button2;
    private PlayerHand hand;

    public GamePanel() {
        this.button1 = new JButton("text");
        this.button2 = new JButton("more text");
        this.hand = new PlayerHand();

        add(this.button1);
        add(this.button2);

    }

    public void initialize() {
        ConnectionThread connectionThread = ConnectionThread.getConnectionThread();

        // Event for when we receive new hand
        connectionThread.subscribe(new UnoEvent() {
            @Override
            public void run() {
                if (this.message.startsWith("NEW_HAND//")) {
                    this.message = this.message.substring("NEW_HAND//".length());
                    String[] cardDescriptions = message.split(",");
                    hand.clear();
                    for(String cardDescription: cardDescriptions) {
                        hand.addCard(UnoCard.fromString(cardDescription));
                    }
                    repaint();
                }
            }
        });
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        final int SIDE_BUFFER = 100;
        final int CARD_WIDTH = 100;

        int totalWidth = this.getWidth() - SIDE_BUFFER * 2;
        int gap = totalWidth / (this.hand.getCards().size() - 1);
        gap = Math.min(gap, 120);
        int neededWidth = gap * (this.hand.getCards().size() - 1);
        int startX = this.getWidth() / 2 - neededWidth / 2;

        for (int i = 0; i < hand.getCards().size(); i++) {
            UnoCard card = this.hand.getCards().get(i);
            g.drawImage(UnoCard.getImageForCard(card), startX + gap * i - CARD_WIDTH / 2, 500, CARD_WIDTH, 150, null);
        }
    }

}

