package client;

import common.SocketWrapper;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionThread extends Thread{

    private static SocketWrapper serverConnection;
    private static ConnectionThread connectionThread;
    private ArrayList<UnoEvent> callbacks;


    public static SocketWrapper getServerConnection () {
        return serverConnection;
    }

    public static SocketWrapper makeServerConnection () {
        if (serverConnection == null) {
            try {
                serverConnection = new SocketWrapper(new Socket("34.68.106.60", 5000));
                connectionThread = new ConnectionThread();
                connectionThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return serverConnection;
    }

    public static ConnectionThread getConnectionThread() { return connectionThread; }

    private ConnectionThread() {
        callbacks = new ArrayList<>();
    }

    public void subscribe(UnoEvent callback) {
        this.callbacks.add(callback);
    }

    public void run() {
        // TODO: read new messages from the socket continuously
        // TODO: on each message, execute any subscriptions

        while (true) {
            String message = null;
            try {
                message = serverConnection.read();
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (UnoEvent callback : this.callbacks) {
                callback.run(message);
            }
        }
    }
}
