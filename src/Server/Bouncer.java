package Server;

import common.SocketWrapper;

import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Bouncer {

    // When to release people to a game
    private final Semaphore people = new Semaphore(0);
    //The queue that it is watching
    private Queue<SocketWrapper> queue;
    //Number of people
    private int lobbySize;

}
