package Server;


import common.PlayerEvent;
import common.SocketWrapper;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class PlayerListeningThread extends Thread {

    private final BlockingQueue<PlayerEvent> queue;
    private final SocketWrapper socketWrapper;
    private final int playerID;


    public PlayerListeningThread(BlockingQueue<PlayerEvent> queue, SocketWrapper sw, int id) {
        this.queue = queue;
        this.socketWrapper = sw;
        this.playerID = id;
    }

    public void run() {
        while (true) {
            String message = null;
            try {
                message = socketWrapper.read();
                PlayerEvent event = new PlayerEvent(this.playerID, message.split("//")[0], message.split("//")[1]);
                this.queue.add(event);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}
