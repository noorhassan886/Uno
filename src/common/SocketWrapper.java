package common;

import java.io.*;
import java.net.Socket;

public class SocketWrapper {

    // raw socket connection
    private Socket connection;
    // stream sending data to the other end
    private BufferedWriter out;
    // stream receiving data from the other end
    private BufferedReader in;

    // constructor
    public SocketWrapper(Socket connection) throws IOException {
        this.connection = connection;
        this.out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        this.in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    }

    public void send(String message) throws IOException {
        // Create a string (4 characters long) represents the length of the following message
        String messageLength = String.valueOf(message.length());
        // Sometimes we'll need to pad it with zeroes at the start
        String padding = "";
        for (int i = 0; i < 4 - messageLength.length(); i++) {
            padding += "0" ;
        }
        //Send the message length
        this.out.write(padding + messageLength);
        //Send the message itself
        this.out.write(message);
        // figures it isn't kept in some internal buffer
        this.out.flush();
    }

    public String read() throws IOException{
        // Reads 4 characters for the message length
        String messageLength = "";
        for (int i = 0; i < 4; i++) {
            messageLength += (char) this.in.read();

        }
        // Converts that to an integer
        int length = Integer.parseInt(messageLength);

        // reads that many characters after
        char[] message = new char[length];
        int actual = this.in.read(message);
        if (length != actual)
            throw new IOException("Unexpected message length. Expected " + length + ", got " + actual);

        // Returns a built string using those characters
        return new String(message);
    }

}
