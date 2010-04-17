package is.ru.iclt.icenlp.router.common;


import is.ru.iclt.icenlp.common.network.Packet;
import is.ru.iclt.icenlp.router.common.network.ByteConverter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Slave implements ISlave {
	private int numberOfRequests;
	private Socket socket;
	private String md5;

	public Slave(Socket socket) {
		this.numberOfRequests = 0;
		this.socket = socket;
		this.startPingCheck();
	}

	public synchronized void increseLoad() {
		this.numberOfRequests += 1;
	}

	public synchronized void decreaseLoad() {
		if (this.numberOfRequests > 0)
			this.numberOfRequests -= 1;
	}

	@Override
	public String transle(String str) throws IOException {
		List<Packet> sendingPackets = new LinkedList<Packet>();
		byte[] strBytes = str.getBytes();
		int numberOfpackets = ((int) Math.floor((strBytes.length / 504))) + 1;

		// Let's create the packet that we will send first
		Packet packet = new Packet(10);
		packet.setInteger(4, numberOfpackets);
		sendingPackets.add(packet);

		int dataByteCounter = 0;

		for (int i = 0; i < numberOfpackets; i++) {
			byte[] packetData = new byte[512];
			int dataSize = 0;

			for (int j = 0; j < 504 && dataByteCounter < strBytes.length; j++) {
				packetData[j + 8] = strBytes[dataByteCounter];
				dataByteCounter++;
				dataSize++;
			}

			byte[] opcodeData = ByteConverter.itToByte(11);
			byte[] sizeData = ByteConverter.itToByte(dataSize);

			for (int j = 0; j < 4; j++) {
				packetData[j] = opcodeData[j];
				packetData[j + 4] = sizeData[j];
			}

			sendingPackets.add(new Packet(packetData));
		}

		InputStream istrem = socket.getInputStream();
		OutputStream ostream = socket.getOutputStream();

		for (Packet p : sendingPackets)
			ostream.write(p.getData());

		// Let's read back the reply from the slave.
		byte[] d = new byte[512];
		istrem.read(d);
		int opCode = ByteConverter.bytesToInt(d, 0);

		if (opCode != 12)
			return null;

		int numPackets = ByteConverter.bytesToInt(d, 4);
		byte[] strData = new byte[numPackets * 512];
		int byteCounter = 0;
		int size = 0;
		byte[] data;

		for (int i = 0; i < numPackets; i++) {
			data = new byte[512];
			try {
				istrem.read(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
			opCode = ByteConverter.bytesToInt(data, 0);
			if (opCode != 13)
				return null;
			int dataSize = ByteConverter.bytesToInt(data, 4);
			size = size + dataSize;
			for (int j = 0; j < dataSize; j++) {
				strData[byteCounter++] = data[j + 8];
			}
		}
		String translatedString = new String(strData, 0, size);
		return translatedString;
	}

	@Override
	public String getMD5() {
		return this.md5;
	}

	@Override
	public void setMD5(String md5) {
		this.md5 = md5;
	}

	@Override
	public String getHost() {
		return this.socket.getInetAddress().toString();
	}

	@Override
	public void startPingCheck() {
		SlavePingThread spt = new SlavePingThread(socket, this);
		Thread t = new Thread(spt);
		t.start();
	}

	@Override
	public void deleteThisSlave() {
		SlaveCollection.getInstance().removeSlave(this.md5);
		try {
			this.socket.close();
		} catch (IOException e) {
		}
	}

	@Override
	public int getLoad() {
		return this.numberOfRequests;
	}
}
