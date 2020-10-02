package client;

import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {

    private JButton twoPlayerBtn, fourPlayerBtn, optionsBtn, quitBtn;

    MenuPanel() {
        setLayout(new GridBagLayout());

        this.twoPlayerBtn = new JButton("Join 2-Player lobby");
        this.twoPlayerBtn.setPreferredSize(new Dimension(100, 50));

        this.fourPlayerBtn = new JButton("Join 4-Player lobby");
        this.optionsBtn = new JButton("Options");
        this.quitBtn = new JButton("Quit");


        add(this.twoPlayerBtn);
        add(this.fourPlayerBtn);
        add(this.optionsBtn);
        add(this.quitBtn);
    }

}
