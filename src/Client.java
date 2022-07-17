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
		int segment = 1;
		int segCount = 0;
		int windowSize = 1;
		int count = 0;
		Boolean isfirstlost = false;
		try {
			socket = new Socket(address, port);
			// sends/receive data to the server
			input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			output = new DataOutputStream(socket.getOutputStream());
			output.writeUTF("network");
			String response = "";
			response = input.readUTF();
			int ack = 0;
			System.out.println(response);

			while (segCount < 100000) {

				for (int i = 0; i < windowSize; i++) {
					output.writeInt(segment);
					count++;
					segCount++;
					if (segCount > 100000) {
						break;
					} else if (segment < Math.pow(2, 32)) {
						segment = getNextSeg(count); // next segment number
					} else {
						segment = 1; // wrap around if the sequence reach max number 2^32
					}
					ack = input.readInt();
				}
				// get the ACK number from the server

				if (ack < ((windowSize * 1024) + 1)) {
					output.writeInt(ack);
					isfirstlost = true;
					windowSize /= 2;
					ack = input.readInt();
				} else {
					if (windowSize < Math.pow(2, 16)) {
						if (isfirstlost) {
							windowSize++;
						} else {
							windowSize *= 2;
						}

					}
				}
				System.out.println("segCount: = " + segCount);
			}

			// keep reading until -1 is input
			output.writeInt(-1);
			output.writeInt(segCount);
			// done with sending segments, send "Over" to end the connection
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
		return (count + 1) * 1024 + 1;
	}

	public static void main(String args[]) {
		Client client = new Client("localhost", 4001);
	}
}