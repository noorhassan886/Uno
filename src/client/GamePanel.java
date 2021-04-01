package client;

import common.PlayerHand;
import common.UnoCard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GamePanel extends JPanel {

    private final JButton button1, button2;
    private PlayerHand hand;
    private UnoCard discardPileTopCard;
    private final int SIDE_BUFFER = 100;
    private final int CARD_WIDTH = 100;
    private final int CARD_HEIGHT = 150;
//    private JLabel deckButton;

    private ArrayList<JLabel> cardLabels;

    public GamePanel() {
        this.button1 = new JButton("text");
        this.button2 = new JButton("more text");
        this.hand = new PlayerHand();
        this.cardLabels = new ArrayList<>();

        add(this.button1);
        add(this.button2);
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
                    // TODO: remove old labels from container
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
                    System.out.println("Clicked on " + hand.getCards().get(finalI));
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

