package Server;


import common.PlayerEvent;
import common.SocketWrapper;

import java.util.concurrent.BlockingQueue;

public class PlayerListeningThread extends Thread {

    private final BlockingQueue<PlayerEvent> queue;
    private final SocketWrapper socketwrapper;
    private final int playerID;


    public PlayerListeningThread(BlockingQueue<PlayerEvent> queue, SocketWrapper sw, int id) {
        this.queue = queue;
        this.socketwrapper = sw;
        this.playerID = id;
    }

}
