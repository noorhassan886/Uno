package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameWidow extends JFrame {

    private final Dimension size;
    private final MenuPanel menu;
    private final GamePanel game;

    public GameWidow(Dimension size) {
        //Default stuff
        this.size = size;
        setSize(this.size);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Uno!");
        setVisible(true);


        // Convenience stuff
        setResizable(false);
        setLocationRelativeTo(null);

        //Initialize show the opening menu
        this.menu = new MenuPanel(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuToGame();
            }
        });
        this.game = new GamePanel();

        getContentPane().add((this.menu));

    }
    public void menuToGame () {
        this.getContentPane().remove(this.menu);
        this.getContentPane().add(this.game);
        this.validate();
        this.repaint();
    }

}
