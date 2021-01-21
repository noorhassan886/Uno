import common.UnoCard;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        /*
        2-4 player lobbies created by the player
        present player w a menu
        length defined messages
        players land in first available lobby
        time input
        usernames
        history
        need a way for players to call uno
        support 2 or 4 player lobbies
         */

        try {
            UnoCard.loadCardImage();
        } catch (IOException e) {
            System.out.println("Image failed to load: " + e.getMessage());
        }
    }
}

