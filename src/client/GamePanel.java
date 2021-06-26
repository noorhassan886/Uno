package client;

import common.PlayerHand;
import common.UnoCard;
import common.WildCard;

import javax.swing.*;
import java.awt.*;
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
    private JLabel deckButton, unoButton;
    private int id;
    private int[] handsizes;

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

        // Add click on listener to each button to send a colored Wild Card
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

        // Show the deck button
        deckButton = new JLabel(new ImageIcon(UnoCard.getImageForCard("Card Back")
                .getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH)));
        deckButton.setBounds(
                1280 / 2 + 8,
                695 / 2 - CARD_HEIGHT / 2,
                CARD_WIDTH, CARD_HEIGHT);
        deckButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    ConnectionThread.getServerConnection().send("DRAW_CARD//kjdhfks");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        deckButton.setVisible(true);
        add(deckButton);

        // Show the Uno button
        unoButton = new JLabel(new ImageIcon(UnoCard.getImageForCard("Card Back")
                .getScaledInstance(CARD_WIDTH / 2, CARD_HEIGHT / 2, Image.SCALE_SMOOTH)));
        unoButton.setBounds(1200 - CARD_WIDTH / 2 - 20, 20,  CARD_WIDTH / 2, CARD_HEIGHT / 2);
        unoButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    ConnectionThread.getServerConnection().send("CALL_UNO//kjdhfks");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        unoButton.setVisible(true);
        add(unoButton);

    }

    public void initialize() {
        ConnectionThread connectionThread = ConnectionThread.getConnectionThread();

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
        connectionThread.subscribe(new UnoEvent() {
            @Override
            public void run() {
                if(this.message.startsWith("PLAYER_ID//")) {
                    id = Integer.parseInt(this.message.split("//")[1]);
                    System.out.println("id = " + id);
                }
            }
        });
        connectionThread.subscribe(new UnoEvent() {
            @Override
            public void run() {
                if(this.message.startsWith("DISPLAYING_PLAYER_HANDS")) {
                    String payload = this.message.split("//")[1];
                    String[] sizes = payload.split(",");

                    int currentHand = 0;
                    handsizes = new int[sizes.length - 1];
                    for (int i = 0; i < sizes.length; i++) {
                        if (currentHand != id) {
                            handsizes[currentHand] = Integer.parseInt(sizes[i]);
                            currentHand++;
                        }
                    }

                    System.out.println("handsizes = " + handsizes);
                    repaint();
                }
            }
        });
    }

    public void initCardLabels() {
        int totalWidth = this.getWidth() - SIDE_BUFFER * 2;
        int gap = this.hand.getCards().size() < 2
            ? 0
            : totalWidth / (this.hand.getCards().size() - 1);
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
            g.drawImage(UnoCard.getImageForCard(this.discardPileTopCard),
                    this.getWidth() / 2 - CARD_WIDTH - 7,
                    this.getHeight() / 2 - CARD_HEIGHT/2,
                    CARD_WIDTH, CARD_HEIGHT, null);
        }

        if (this.handsizes != null) {
            if (this.handsizes.length == 1) {
                int totalWidth = this.getWidth() - SIDE_BUFFER * 2;
                int gap = this.handsizes[0] < 2
                        ? 0
                        : totalWidth / (this.handsizes[0] - 1);
                gap = Math.min(gap, 120);
                int neededWidth = gap * (this.handsizes[0] - 1);
                int startX = this.getWidth() / 2 - neededWidth / 2;
                for (int i = 0; i < this.handsizes[0]; i++) {
                    g.drawImage(UnoCard.getImageForCard("Card Back"),
                            startX + gap * i - CARD_WIDTH / 2, 50,
                            CARD_WIDTH, CARD_HEIGHT, null);
                }
            } else {
                // TODO: Draw handSizes[1] card on the top of the screen
                int totalWidth = this.getWidth() - SIDE_BUFFER * 2;
                int gap = this.handsizes[1] < 2
                        ? 0
                        : totalWidth / (this.handsizes[1] - 1);
                gap = Math.min(gap, 120);
                int neededWidth = gap * (this.handsizes[1] - 1);
                int startX = this.getWidth() / 2 - neededWidth / 2;
                for (int i = 0; i < this.handsizes[1]; i++) {
                    g.drawImage(UnoCard.getImageForCard("Card Back"),
                            startX + gap * i - CARD_WIDTH / 2, 50,
                            CARD_WIDTH, CARD_HEIGHT, null);
                }

                // TODO: Draw handSizes[0] card on the left of the screen
                // TODO: Draw handSizes[2] card on the right of the screen
                int totalHeight = this.getHeight() - SIDE_BUFFER * 2;
                gap = this.handsizes[0] < 2
                        ? 0
                        : totalHeight / (this.handsizes[0] - 1);
                gap = Math.min(gap, 120);
                int neededHeight = gap * (this.handsizes[0] - 1);
                int startY = this.getHeight() / 2 - neededHeight / 2;
                for (int i = 0; i < this.handsizes[0]; i++) {
                    g.drawImage(UnoCard.getImageForCard("Card Back"),
                            SIDE_BUFFER, startY + gap * i - CARD_HEIGHT / 2,
                            CARD_WIDTH, CARD_HEIGHT, null);
                }
                gap = this.handsizes[2] < 2
                        ? 0
                        : totalHeight / (this.handsizes[2] - 1);
                gap = Math.min(gap, 120);
                neededHeight = gap * (this.handsizes[2] - 1);
                startY = this.getHeight() / 2 - neededHeight / 2;
                for (int i = 0; i < this.handsizes[2]; i++) {
                    g.drawImage(UnoCard.getImageForCard("Card Back"),
                            this.getWidth() - SIDE_BUFFER - CARD_WIDTH, startY + gap * i - CARD_HEIGHT / 2,
                            CARD_WIDTH, CARD_HEIGHT, null);
                }

            }

        }

    }

}