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
			System.out.println(response);

			while (segCount < 10000000) {

				for (int i = 0; i < windowSize; i++) {
					output.writeInt(segment);
					count++;
					if (segment < Math.pow(2, 32)) {
						segment = (1024 * count) + 1; // next segment number
					} else {
						segment = 1; // wrap around if the sequence reach max number 2^32
					}

				}
				segCount += count;

				// get the ACK number from the server
				int ack = input.readInt();
				if (ack < ((windowSize * 1024) + 1)) {
					output.writeInt(ack);
					isfirstlost = true;
					windowSize /= 2;
					if (windowSize < 1) {
						windowSize = 1;
					}
				} else {
					if (windowSize < Math.pow(2, 16)) {
						if (isfirstlost) {
							windowSize++;
						} else {
							windowSize *= 2;
						}

					}
				}
				System.out.println(segCount);
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

	public static void main(String args[]) {
		Client client = new Client("localhost", 4001);
	}
}