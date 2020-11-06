package Server;

import common.SocketWrapper;
import common.UnoCard;

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

    public void release() {
        this.people.release();
    }

    @Override
    public void run() {
        System.out.println("Bouncer is waiting for " + lobbySize + "-player lobbies");

        while (true) {

            // Wait for enough people
            try {
                this.people.acquire(lobbySize);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Create a new game
            UnoGameThread thread;
            if (this.lobbySize == 2)
                thread = new UnoGameThread(this.queue.remove(), this.queue.remove());
            else
                thread = new UnoGameThread(this.queue.remove(), this.queue.remove(),
                         this.queue.remove(), this.queue.remove() );
            thread.start();
        }

    }

}
