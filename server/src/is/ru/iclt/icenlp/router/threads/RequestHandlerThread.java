package is.ru.iclt.icenlp.router.threads;



import is.ru.iclt.icenlp.router.common.ISlave;
import is.ru.iclt.icenlp.router.common.SlaveCollection;
import is.ru.iclt.icenlp.router.common.network.ByteConverter;
import is.ru.iclt.icenlp.router.common.network.Packet;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;

public class RequestHandlerThread implements Runnable {
	private Socket socket;
	private ISlave slave;
	private String prefix = "[RequestHandlerThread]: ";

	public RequestHandlerThread(Socket socket) {
		this.socket = socket;
		try {
			this.socket.setSoTimeout(60000);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			// Lets get a new slave from the slave collection.
			this.slave = SlaveCollection.getInstance().getSlave();

			// If we don't have any slave to work with we will send
			// an error will then be sent to the client.
			if (this.slave == null)
				this
						.sendReplyToClient("Unable to serve request, try again later.");
			else {
				this.slave.increseLoad();

				// Read the request from the client.
				String request = this.readRequestFromClient();

				// Translate the request from client.
				System.out.println(prefix + request);

				String reply = this.slave.transle(request);

				if (reply == null)
					this
							.sendReplyToClient("Unable to serve request, try again later.");

				else
					this.sendReplyToClient(reply);

				this.slave.decreaseLoad();

				// Close the socket!
				this.socket.close();
			}
		}

		catch (Exception e) {
			System.out.println(prefix + "error in request thread: "
					+ e.getMessage());
		}
	}

	private String readRequestFromClient() throws IOException {
		byte[] data = new byte[512];
		this.socket.getInputStream().read(data);
		Packet packet = new Packet(data);
		String returnString = null;

		// If the first packet from the client has the opcode 3.
		// else we don't care and stop the connection.
		if (packet.getOpcode() == 3) {
			// Let's read the string that the client is sending
			// to us.
			int numpackets = packet.getInteger(4);

			if (numpackets >= 0 && numpackets < Integer.MAX_VALUE) {
				byte[] dataFromWeb = new byte[numpackets * 512];
				int byteCounter = 0;
				int size = 0;

				for (int i = 0; i < numpackets; i++) {
					data = new byte[512];
					this.socket.getInputStream().read(data);
					packet = new Packet(data);
					int dataSize = packet.getInteger(4);
					size = size + dataSize;

					for (int j = 0; j < dataSize; j++) {
						dataFromWeb[byteCounter++] = data[j + 8];
					}
				}
				returnString = new String(dataFromWeb, 0, size);
			}
		}
		return returnString;
	}

	private void sendReplyToClient(String reply) throws IOException {
		byte[] strBytes = reply.getBytes();
		int numberOfpackets = ((int) Math.floor((strBytes.length / 504))) + 1;

		// byte counter.
		int dataByteCounter = 0;

		// Collection of packets that we will send to the client.
		List<Packet> replyPackets = new LinkedList<Packet>();

		// Create the first packet with opcode 5.
		Packet p = new Packet(5);
		p.setInteger(4, numberOfpackets);
		replyPackets.add(p);

		for (int i = 0; i < numberOfpackets; i++) {
			byte[] packetData = new byte[512];
			int dataSize = 0;

			for (int j = 0; j < 504 && dataByteCounter < strBytes.length; j++) {
				packetData[j + 8] = strBytes[dataByteCounter];
				dataByteCounter++;
				dataSize++;
			}

			// Add the opcode and the data size to the data.
			byte[] opcodeData = ByteConverter.itToByte(6);
			byte[] sizeData = ByteConverter.itToByte(dataSize);

			for (int j = 0; j < 4; j++) {
				packetData[j] = opcodeData[j];
				packetData[j + 4] = sizeData[j];
			}

			replyPackets.add(new Packet(packetData));
		}

		// Send the packets to the client.
		OutputStream ostream = socket.getOutputStream();
		for (Packet packet : replyPackets)
			ostream.write(packet.getData());
	}
}
