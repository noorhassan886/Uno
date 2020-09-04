package Server;

import common.SocketWrapper;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class Server {

    //Lobbies that out players wait in
    public static final Queue<SocketWrapper>twoPlayerLobby = new LinkedList<>();
    public static final Queue<SocketWrapper>fourPlayerLobby = new LinkedList<>();

    public static void main(String[] args) {

        //opening a server for client connections
        final int SERVER_PORT = 5000;
        try(ServerSocket server = new ServerSocket(SERVER_PORT)) {

            // wait for clients to connect
            while (true) {
                SocketWrapper client = new SocketWrapper(server.accept());
                if (true) {
                    twoPlayerLobby.add(client);
                }
                else {
                    fourPlayerLobby.add(client);
                }
            }

                //TODO: show them a menu for 2 4 player lobby
                //TODO: get their response
                //TODO: put them into matching lobby
                //TODO: run that lobby's game on another thread

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

}
