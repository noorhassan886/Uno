package client;

import common.SocketWrapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

public class MenuPanel extends JPanel {

    private JButton twoPlayerBtn, fourPlayerBtn, optionsBtn, quitBtn;

    MenuPanel(ActionListener moveToGamePanel) {
        setLayout(new GridBagLayout());

        this.twoPlayerBtn = new JButton("Join 2-Player lobby");
        this.twoPlayerBtn.setPreferredSize(new Dimension(150, 50));
        this.twoPlayerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ConnectionThread.makeServerConnection().send("two");
                    moveToGamePanel.actionPerformed(e);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        // this.twoPlayerBtn.addActionListener(moveToGamePanel);

        this.fourPlayerBtn = new JButton("Join 4-Player lobby");
        this.fourPlayerBtn.setPreferredSize(new Dimension(150, 50));
        this.fourPlayerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ConnectionThread.makeServerConnection().send("four");
                    moveToGamePanel.actionPerformed(e);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        this.optionsBtn = new JButton("Options");
        this.optionsBtn.setPreferredSize(new Dimension(150, 50));


        this.quitBtn = new JButton("Quit");
        this.quitBtn.setPreferredSize(new Dimension(150, 50));
        this.quitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        add(this.twoPlayerBtn);
        add(this.fourPlayerBtn);
        add(this.optionsBtn);
        add(this.quitBtn);
    }

}
