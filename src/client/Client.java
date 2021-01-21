package client;

import common.UnoCard;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Client {

    public static void main(String[] args) throws IOException {

        UnoCard.loadCardImage();
        SwingUtilities.invokeLater (() -> new GameWidow(new Dimension(1200, 720)));
    }



}
