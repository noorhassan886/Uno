package client;

import javax.swing.*;
import java.awt.*;

public class GameWidow extends JFrame {

    private  Dimension size;
    private MenuPanel menu;

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
        this.menu = new MenuPanel();
        getContentPane().add((this.menu));

    }

}
