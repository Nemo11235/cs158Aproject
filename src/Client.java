import java.net.*;
import java.util.*;
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

			// initialize the socket and input/output stream
			socket = new Socket(address, port);
			input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			output = new DataOutputStream(socket.getOutputStream());

			// send a string to server and wait for ack to confirm connection
			output.writeUTF("network");
			String response = "";
			response = input.readUTF();
			System.out.println(response);

			// initialize variables
			int ack = 1;
			int segment = 1;
			int windowSize = 1;
			int segCount = 0;
			int resentCount = 0;
			boolean lostBefore = false;
			int adjustWindow = 0; // 0 for doubles, 1 for half, 2 for +1;
			Random rand = new Random();
			List<Integer> windowSizes = new ArrayList<>();
			List<Integer> lostSegs = new ArrayList<>();
			int totalSegToSend = 100;

			// keep sending until we sent all segments
			while (segCount < totalSegToSend) {
				System.out.println("\n ========================= \n");
				if (ack == segment) {
					for (int i = 0; i < windowSize; i++) {
						// randomly loss some segments
						int send = rand.nextInt(10);
						if (send == 0) {
							System.out.println(segment + " will be lost");
							lostSegs.add(segment);
						}
						System.out.println("sending: " + segment);
						// send the segment to server if we choose not to lose it
						if (send != 0) {
							output.writeInt(segment);
							ack = input.readInt();
							System.out.println("ack: " + ack);
							segCount++;
						}

						// if we have more segments to send, calculate the next segment to send,
						// otherwise jump out from the loop
						if (segCount >= totalSegToSend) {
							break;
						} else {
							segment = getNextSeg(segment);
						}

					}
					adjustWindow = lostBefore ? 2 : 0;
				} else {
					// retransmit the missing segment
					segment = ack;
					output.writeInt(segment);
					System.out.println("resending: " + segment);
					adjustWindow = 1;
					lostBefore = true;
					resentCount++;
					segCount++;
					ack = input.readInt();
					segment = ack;
					System.out.println("ack from resending: " + ack);
				}

				// adjust the window size according to the ack
				if (adjustWindow == 0 && windowSize * 2 < Math.pow(2, 16)) {
					windowSize *= 2;
					System.out.println("doubled windowSize = " + windowSize);
				} else if (adjustWindow == 1) {
					if (windowSize != 1) {
						windowSize /= 2;
					}
					System.out.println("halfed windowSize = " + windowSize);
				} else if (adjustWindow == 2 && windowSize < Math.pow(2, 16)) {
					windowSize++;
					System.out.println("windowSize + 1 = " + windowSize);
				}
				windowSizes.add(windowSize);
			}

			// print out the data we want to see
			System.out.println("total seg sent: " + segCount);
			System.out.println("resent: " + resentCount);
			System.out.println("\n ========================= \n");
			System.out.println("Window size changes over time: ");
			windowSizes.add(0, 1);
			for (Integer i : windowSizes) {
				System.out.println(i);
			}
			System.out.println("\n ========================= \n");
			System.out.println("Lost segments: ");
			for (Integer i : lostSegs) {
				System.out.println(i);
			}
			output.writeInt(-1);
			output.writeInt(resentCount);
			input.close();
			output.close();
			socket.close();

		} catch (UnknownHostException u) {
			System.out.println(u);
		} catch (IOException i) {
			System.out.println(i);
		}

	}

	private static int getNextSeg(int seg) {
		int count = (seg - 1) / 1024;
		return seg > Math.pow(2, 17) ? 1 : (count + 1) * 1024 + 1;
	}

	public static void main(String args[]) {
		Client client = new Client("localhost", 4001);
	}
}

// 10.0.0.17
// localhost
// 172.20.10.2