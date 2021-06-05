package client;

import common.PlayerHand;
import common.UnoCard;
import common.WildCard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

public class GamePanel extends JPanel {

    private PlayerHand hand;
    private ArrayList<JLabel> cardLabels;
    private final int SIDE_BUFFER = 100;
    private final int CARD_WIDTH = 100;
    private final int CARD_HEIGHT = 150;
    private final Container buttonsContainer;
    private UnoCard discardPileTopCard;
    private WildCard selectedWild = null;
//    private JLabel deckButton;

    private boolean choosingColor = false;
    private JButton[] colorButtons;

    public GamePanel() {
        this.setLayout(null);
        this.hand = new PlayerHand();
        this.cardLabels = new ArrayList<>();

        JButton[] colorButtons = new JButton[]{
                new JButton("Red"),  new JButton("Blue"),
                new JButton("Yellow"),  new JButton("Green"),
        };
        // Create a sub container for the Buttons
        buttonsContainer = new Container();
        add(buttonsContainer);
        // Add the buttons horizontally
       buttonsContainer.setLayout(new FlowLayout());
        for (JButton colorButton : colorButtons) {
            buttonsContainer.add(colorButton);
            colorButton.setVisible(true);
        }
        // Position right above hand of cards
        buttonsContainer.setBounds(400, 450, 500, 50);
        buttonsContainer.setVisible(false);

        // TODO: Add click on listener to each button to send a colored Wild Card
        colorButtons[0].addActionListener(e -> {
            // Color the currently selected wild
            selectedWild.setColor("Red");
            // Send the wild card
            try {
                ConnectionThread.getServerConnection().send("PLACE_CARD//" + selectedWild);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // hide all the buttons
            selectedWild = null;
            buttonsContainer.setVisible(false);
        });
        colorButtons[1].addActionListener(e -> {
            // Color the currently selected wild
            selectedWild.setColor("Blue");
            // Send the wild card
            try {
                ConnectionThread.getServerConnection().send("PLACE_CARD//" + selectedWild);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // hide all the buttons
            selectedWild = null;
            buttonsContainer.setVisible(false);
        });
        colorButtons[2].addActionListener(e -> {
            // Color the currently selected wild
            selectedWild.setColor("Yellow");
            // Send the wild card
            try {
                ConnectionThread.getServerConnection().send("PLACE_CARD//" + selectedWild);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // hide all the buttons
            selectedWild = null;
            buttonsContainer.setVisible(false);
        });
        colorButtons[3].addActionListener(e -> {
            // Color the currently selected wild
            selectedWild.setColor("Green");
            // Send the wild card
            try {
                ConnectionThread.getServerConnection().send("PLACE_CARD//" + selectedWild);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // hide all the buttons
            selectedWild = null;
            buttonsContainer.setVisible(false);
        });
    }

    public void initialize() {
        ConnectionThread connectionThread = ConnectionThread.getConnectionThread();
//
//        deckButton = new JLabel(new ImageIcon(UnoCard.getImageForCard("Card Back").getScaledInstance(CARD_WIDTH,CARD_HEIGHT,Image.SCALE_SMOOTH)));
//        deckButton.setBounds(this.getWidth() / 2 - CARD_WIDTH - 50,
//                this.getHeight() / 2 - CARD_HEIGHT / 2,
//                CARD_WIDTH, CARD_HEIGHT);
//        add(deckButton);
//        repaint();

         //Event for when we receive new hand
        connectionThread.subscribe(new UnoEvent() {
            @Override
            public void run() {
                if (this.message.startsWith("NEW_HAND//")) {
                    this.message = this.message.substring("NEW_HAND//".length());
                    String[] cardDescriptions = message.split(",");
                    hand.clear();
                    for (JLabel cardLabel : cardLabels) {
                        remove(cardLabel);
                    }
                    cardLabels.clear();
                    for(String cardDescription: cardDescriptions) {
                        hand.addCard(UnoCard.fromString(cardDescription));
                        cardLabels.add(new JLabel(new ImageIcon(UnoCard.getImageForCard(cardDescription)
                            .getScaledInstance(CARD_WIDTH,CARD_HEIGHT,Image.SCALE_SMOOTH))));
                    }
                    initCardLabels();
                    repaint();
                }
            }
        });

        connectionThread.subscribe(new UnoEvent() {
            @Override
            public void run() {
                if(this.message.startsWith("TOP_CARD//")) {
                    this.message = this.message.substring("TOP_CARD//".length());
                    discardPileTopCard = UnoCard.fromString(this.message);
                }
            }
        });
    }

    public void initCardLabels() {
        int totalWidth = this.getWidth() - SIDE_BUFFER * 2;
        int gap = totalWidth / (this.hand.getCards().size() - 1);
        gap = Math.min(gap, 120);
        int neededWidth = gap * (this.hand.getCards().size() - 1);
        int startX = this.getWidth() / 2 - neededWidth / 2;

        for (int i = 0; i < this.cardLabels.size(); i++) {
            this.cardLabels.get(i).setBounds(startX + gap * i - CARD_WIDTH / 2, 500, CARD_WIDTH, CARD_HEIGHT);
            this.add(cardLabels.get(i));
            int finalI = i;
            this.cardLabels.get(i).addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (hand.getCards().get(finalI) instanceof WildCard) {
                       buttonsContainer.setVisible(true);
                       selectedWild = (WildCard) hand.getCards().get(finalI);
                       repaint();
                    } else {
                        try {
                            ConnectionThread.getServerConnection().send("PLACE_CARD//" + hand.getCards().get(finalI));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.discardPileTopCard != null) {
            g.drawImage(UnoCard.getImageForCard(this.discardPileTopCard), this.getWidth()/2 - CARD_WIDTH/2,
                    this.getHeight()/2 - CARD_HEIGHT/2,
                    CARD_WIDTH, CARD_HEIGHT, null);
            repaint();
        }
    }
}