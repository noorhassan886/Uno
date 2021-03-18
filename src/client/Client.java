package client;

import common.SocketWrapper;
import common.UnoCard;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        UnoCard.loadCardImage();
        SwingUtilities.invokeLater (() -> new GameWidow(new Dimension(1200, 720)));
    }
}
