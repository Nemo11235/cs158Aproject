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
		String sendpkt = "";
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
					sendpkt = String.valueOf(segment);
					output.writeUTF(sendpkt);
					count++;
					if (segment < Math.pow(2, 32)) {
						segment = (1024 * count) + 1; // next segment number

					} else {
						segment = 1; // wrap around if the sequence reach max number 2^32
					}
				}
				segCount += count;

				//get the ACK number from the server
				response = input.readUTF();
				System.out.println(response);
				String[] responseArray = response.split("\\s+");
				int ackNumber = Integer.valueOf(responseArray[1]);
				if (ackNumber < ((windowSize * 1024) + 1)) {
					sendpkt = String.valueOf(ackNumber);
					output.writeUTF(sendpkt);
					isfirstlost = true;
					windowSize /= 2;
				} else {
					if (windowSize < Math.pow(2, 16)) {
						if (isfirstlost) {
							windowSize++;
						} else {
							windowSize *= 2;
						}

					}
				}
			}
			// keep reading until "Over" is input
			String line = "Over";
			output.writeUTF(line);
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