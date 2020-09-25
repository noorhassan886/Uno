package Server;

import common.SocketWrapper;

import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Bouncer extends Thread {

    // when to release people to a game
    private final Semaphore people = new Semaphore(0);

    // the queue that it is watching
    private Queue<SocketWrapper> queue;

    // number of people
    private int lobbySize;

    // constructor
    public Bouncer(Queue<SocketWrapper> queue, int gameSize) {
        this.queue = queue;
        this.lobbySize = gameSize;
    }

    @Override
    public void run() {
        System.out.println("Bouncer is waiting for " + lobbySize + "-player lobbies");
    }
}
