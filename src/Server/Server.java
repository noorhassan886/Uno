package Server;

import common.SocketWrapper;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.Queue;

public class Server {

    // lobbies that out players wait in
    public static final Queue<SocketWrapper>twoPlayerLobby = new LinkedList<>();
    public static final Queue<SocketWrapper>fourPlayerLobby = new LinkedList<>();

    public static void main(String[] args) {

        // opening a server for client connections
        final int SERVER_PORT = 5000;
        try(ServerSocket server = new ServerSocket(SERVER_PORT)) {

            // make bouncers watch the lobbies for new players
            Bouncer twoPlayer = new Bouncer(twoPlayerLobby, 2);
            Bouncer fourPlayer = new Bouncer(fourPlayerLobby, 4);
            twoPlayer.start();
            fourPlayer.start();

            // wait for clients to connect
            while (true) {
                SocketWrapper client = new SocketWrapper(server.accept());
                String gameMode = client.read();
                if (gameMode.equals("two")) {
                    System.out.println("New 2 player");
                    twoPlayerLobby.add(client);
                    twoPlayer.release();
                }
                else {
                    System.out.println("New 4 player");
                    fourPlayerLobby.add(client);
                    fourPlayer.release();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

}
