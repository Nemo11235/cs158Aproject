import java.net.*;
import java.util.*;
import java.io.*;

public class Server {
    Socket socket;
    ServerSocket server;
    DataInputStream input;
    DataOutputStream output;

    public Server(int port) {
        try {
            // start server and wait for connection
            server = new ServerSocket(port);
            System.out.println("Server started, waiting for client...");
            socket = server.accept(); // sit until there's someone try to connect
            System.out.println("Client accepted");

            // variables for input/output stream
            input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            output = new DataOutputStream(socket.getOutputStream());

            try {

                String inputData = input.readUTF();

                if (inputData.equals("network")) {
                    System.out.println("Connection request received, client is now connected");
                    // notify the client that the connection is successful
                    output.writeUTF("Success");

                    // start accepting segments and send acks
                    int ack = 1; // ack to sent
                    double sentSeg = 10000000; // number of segment sent by the client
                    int receivedSeg = 0; // number of segment received
                    int lastSeg = 0;
                    List<Integer> buffer = new ArrayList<>(); // store the correct segment
                    int segment; // current segment recieved
                    int maxSegReceived = 0;
                    segment = input.readInt();
                    while (segment != -1) {
                        // if received the wrong segment, send the ack of the previous wanted segment
                        if (segment == ack) {
                            // buffer the correct segment received and determin the next segment wanted
                            buffer.add(segment);
                            System.out.println(segment);
                            ack = getNextSeg(segment); // update ack to the next expected segment number
                        }
                        output.writeInt(ack);
                        receivedSeg++;
                        segment = input.readInt();
                        // System.out.println("received: " + receivedSeg);
                        maxSegReceived = Math.max(maxSegReceived, segment);
                        // calculate good-put periodically
                        if (receivedSeg == 1000) {
                            double res = (maxSegReceived - lastSeg) / 1000;
                            System.out.println("The good-put of the last 1000 segments received = " + res);
                            maxSegReceived = 0;
                            lastSeg = segment;
                        }

                    }
                    for (Integer i : buffer) {
                        System.out.println(i);
                    }
                    System.out.println("Size: " + buffer.size());

                } else {
                    System.out.println("Connection failed, please try again.");
                }
            } catch (IOException i) {
                System.out.println(i);
            }

            System.out.println("Closing Connection");
        } catch (IOException i) {
            System.out.println(i);
        }

    }

    private static int getNextSeg(int seg) {
        int count = (seg - 1) / 1024;
        return (count + 1) * 1024 + 1;
    }

    public static void main(String args[]) {
        Server server = new Server(4001); // my port number
    }

}