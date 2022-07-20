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
                // read string from the client and confirm connection
                String inputData = input.readUTF();
                if (inputData.equals("network")) {
                    System.out.println("Connection request received, client is now connected");
                    output.writeUTF("Success");

                    // initialize variables
                    int ack = 1;
                    int segRecCount = 0;
                    TreeSet<Integer> buffer = new TreeSet<>(); // store the correct segment
                    // List<Double> goodPutList = new ArrayList<>();
                    int resentCount = 0;

                    // start to receive segments from the client
                    int seg = input.readInt();
                    while (seg != -1) {
                        System.out.println("seg received: " + seg);

                        // buffer the segment
                        if (buffer.add(seg)) {
                            System.out.println("seg count increased");
                            segRecCount++;
                        }

                        // search in the buffer, find the next needed segment
                        while (buffer.contains(ack) && seg <= buffer.last()) {
                            // when wrap around happens, clear the buffer so that new numbers can be loaded
                            // in the buffer
                            if (getNextSeg(ack) == 1) {
                                buffer.clear();
                                buffer.add(1);
                                System.out.println("wrap around, buffer cleared");
                            }
                            ack = getNextSeg(ack);
                        }

                        // send ack(next seg wanted) to the client
                        output.writeInt(ack);
                        // receive the next incoming segment
                        seg = input.readInt();
                        // calculate good-put periodically
                        // if (segRecCount % 1000 == 0) {
                        // output.writeInt(-2);
                        // resendCount = input.readInt();
                        // double res = 1000.0 / (resendCount + 1000);
                        // System.out.println("The good-put of the last 1000 segments received = " +
                        // res);
                        // goodPutList.add(res);
                        // }

                    }

                    // calcualte good-put
                    resentCount = input.readInt();
                    System.out.println("\n ========================= \n");
                    double res = 10000.0 / (10000.0 + resentCount);
                    System.out.println("Total number of segment resent: " + resentCount);
                    System.out.println("Avg good-put: " + res);
                    System.out.println("total seg received: " + segRecCount);

                    // double sum = 0;
                    // for (double d : goodPutList) {
                    // sum += d;
                    // }
                    // System.out.println("resendCount: " + resendCount);
                    // System.out.println("Avg good-put: " + sum / goodPutList.size());
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

    // calculate the next segment based on the current one, perform wrap around when
    // it reaches the maximum number 2^17
    private static int getNextSeg(int seg) {
        int count = (seg - 1) / 1024;
        return seg > Math.pow(2, 17) ? 1 : (count + 1) * 1024 + 1;
    }

    public static void main(String args[]) {
        Server server = new Server(4001);
    }

}