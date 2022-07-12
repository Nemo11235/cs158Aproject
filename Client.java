import java.net.*;
import java.io.*;

public class Client {
    // initialize socket and input output streams
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    // constructor to put IP address and port
    public Client(String address, int port) {
        // establish a connection
        try {
            socket = new Socket(address, port);
            // sends/receive data to the server
            input = new DataInputStream(System.in);
            output = new DataOutputStream(socket.getOutputStream());

            output.writeUTF("connect");
            output.writeUTF("connect");

        } catch (UnknownHostException u) {
            System.out.println(u);
        } catch (IOException i) {
            System.out.println(i);
        }

        // string to read message from input
        int segment = 1024;

        // keep reading until "Over" is input
        while (segment < Math.pow(2, 16)) {
            // TODO: we should multiply segment by 2 everytime
            segment *= 2;
            // TODO: we should also check for packet loss and resend the oldest missing
            // packet
            try {
                String line = "Segment #: " + segment + " sent to the server.\n";
                output.writeUTF(line);
            } catch (IOException i) {
                System.out.println(i);
            }
        }

        // done with sending segments, send "Over" to end the connection
        try {
            String line = "Over";
            output.writeUTF(line);
        } catch (IOException i) {
            System.out.println(i);
        }

        // close the connection
        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException i) {
            System.out.println(i);
        }
    }

    public static void main(String args[]) {
        Client client = new Client("localhost", 4001);
    }
}
