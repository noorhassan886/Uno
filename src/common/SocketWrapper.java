package common;

import java.io.*;
import java.net.Socket;

public class SocketWrapper {

    //raw socket connection
    private Socket connection;
    //Stream sending data to the other end
    private BufferedWriter out;
    // Stream receiving data from the other end
    private BufferedReader in;

    // constructor
    public SocketWrapper(Socket connection) throws IOException {
        this.connection = connection;
        this.out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        this.in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

    }

}
