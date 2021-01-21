package client;

import common.UnoCard;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel {

    private JButton button1, button2;

    public GamePanel() {
        this.button1 = new JButton("text");
        this.button2 = new JButton("more text");

        add(this.button1);
        add(this.button2);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(UnoCard.getImageForCard("Red 7"), 5, 5, 100, 150, null);


    }

}

