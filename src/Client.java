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
			input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			output = new DataOutputStream(socket.getOutputStream());
			output.writeUTF("network");
			String response = "";
			response = input.readUTF();
			System.out.println(response);

			int ack = 1;
			int segment = 1;
			int windowSize = 1;
			int segCount = 0;
			int resentCount = 0;
			boolean lostBefore = false;
			boolean hasLoss = false;
			int adjustWindow = 0; // 0 for doubles, 1 for half, 2 for +1;

			while (segCount < 1000) {
				if (ack == segment) {
					for (int i = 0; i < windowSize; i++) {
						output.writeInt(segment);
						segCount++;
						System.out.println("sending seg: " + segment);
						if (segCount > 100000) {
							break;
						} else {
							segment = getNextSeg(segment);
						}
						ack = input.readInt();
						System.out.println("Now " + ack + " is wanted");
					}
					adjustWindow = lostBefore ? 2 : 0;
				} else {
					segment = ack;
					output.writeInt(segment);
					segCount++;
					adjustWindow = 1;
					lostBefore = true;
					// if (ack < ((windowSize * 1024) + 1)) {
					// output.writeInt(ack);
					// resentCount++;
					// isfirstlost = true;
					// windowSize /= 2;
					// ack = input.readInt();
					// } else {
					// if (windowSize < Math.pow(2, 16)) {
					// if (isfirstlost) {
					// windowSize++;
					// } else {
					// windowSize *= 2;
					// }

					// }
					// }
					ack = input.readInt();
				}

				if (adjustWindow == 0 && windowSize * 2 < Math.pow(2, 16)) {
					windowSize *= 2;
					System.out.println("doubled windowSize = " + windowSize);
				} else if (adjustWindow == 1 && windowSize != 1) {
					windowSize /= 2;
					System.out.println("halfed windowSize = " + windowSize);
				} else {
					windowSize++;
					System.out.println("windowSize + 1 = " + windowSize);
				}

				// get the ACK number from the server
			}
			output.writeInt(-1);
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