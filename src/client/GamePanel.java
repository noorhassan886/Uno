package client;

import javax.swing.*;

public class GamePanel extends JPanel {

    private JButton button1, button2;

    public GamePanel() {
        this.button1 = new JButton("text");
        this.button2 = new JButton("more text");

        add(this.button1);
        add(this.button2);
    }
}
