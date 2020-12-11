package client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel {

    private JButton button1, button2;
    private BufferedImage card;

    private final int SPRITE_START_X = 30;
    private final int SPRITE_START_Y = 40;
    private final int SPRITE_WIDTH = 54;
    private final int SPRITE_HEIGHT = 81;
    private final int SPRITE_GAP = 8;



    public GamePanel() {
        this.button1 = new JButton("text");
        this.button2 = new JButton("more text");

        add(this.button1);
        add(this.button2);

        try {
            this.card = ImageIO.read(new File("img/uno_cards.png"));
            //this.card = this.card.getSubimage(19, 13,  140, 212);
        } catch (IOException ignored) {

        }

    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //g.drawImage(this.card.getSubimage(30, 40, 54, 81), 50, 50, 100, 150, null);
                for(int row = 0; row < 4; row++) {
                    for(int col = 0; col < 13; col++) {
                        BufferedImage tempCard = this.card.getSubimage(
                                SPRITE_START_X + SPRITE_WIDTH * col + SPRITE_GAP * col,
                                SPRITE_START_Y + SPRITE_HEIGHT * col + SPRITE_GAP * row,
                                SPRITE_WIDTH, SPRITE_HEIGHT
                        );
                        g.drawImage(tempCard, 25 + 125 * col, 25 + 175 * row, 100, 150, null);
                    }
//        g.drawImage(this.card.getSubimage(30+54+8, 40, 54, 81), 50+150, 50, 100, 150, null)
                };
//        g.drawImage(this.card.getSubimage(30, 40+81+8, 54, 81), 50, 50+200, 100, 150, null);
//        g.drawImage(this.card.getSubimage(30+54+8, 40+81+8, 54, 81), 50+150, 50+200, 100, 150, null);
    }
}

