package client;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Client {

    public static void main(String[] args) throws IOException {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GameWidow(new Dimension(1200, 720));
            }
        });
    }



}
