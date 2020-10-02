package client;

import common.SocketWrapper;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GameWidow(new Dimension(1200, 720));
            }
        });

        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you want to play in a two-player or four-player lobby?");
        String mode = scanner.nextLine();

        Socket clientSocket = new Socket("localhost", 5000);
        SocketWrapper client = new SocketWrapper(clientSocket);
        client.send(mode);

    }



}
