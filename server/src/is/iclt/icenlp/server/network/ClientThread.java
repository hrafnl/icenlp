package is.iclt.icenlp.server.network;

import is.iclt.icenlp.common.network.ByteConverter;
import is.iclt.icenlp.common.network.Packet;
import is.iclt.icenlp.common.configuration.Configuration;
import is.iclt.icenlp.server.output.OutputGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

/**
 * ClientThread handles the network communications between connected client and
 * the server.
 */
public class ClientThread implements Runnable {
	// Private member variable for the client
	private Socket socket;
	private boolean alive;
	private OutputStream ostream;
	private InputStream istream;
    private boolean debugMode;
    private OutputGenerator outputGenerator;

	public ClientThread(Socket socket, OutputGenerator generator) {
		this.outputGenerator = generator;
		this.socket = socket;
		this.alive = true;
        this.debugMode = Configuration.getInstance().debugMode(); 

		try {
			this.ostream = socket.getOutputStream();
			this.istream = socket.getInputStream();
		}

		catch (IOException e) {
			this.alive = false;
			e.printStackTrace();
		}
	}
	/**
	 * Run is called when the thread is spawned.
	 */
	public void run() {
		while (this.alive) 
		{
			Packet pack = this.readFromClient();
			if (pack.getDataSize() == -1) {
                this.alive = false;
				break;
			} else {
				int opcode = pack.getOpcode();
                if (opcode == 1) { 
                    int numberOfPackets = ByteConverter.bytesToInt(pack.getData(), 4);
					if(numberOfPackets <= 0){
                        this.alive = false;
                        break;
                    }
                    List<Packet> packets = new LinkedList<Packet>();

					for (int i = 0; i < numberOfPackets; i++) {
						Packet p = readFromClient();
                        if(p.getOpcode() != 2){
                            if(this.debugMode)
                                System.out.println("[debug] invalid data packet from client.");
                            this.alive = false;
                            break;
                        }
                        packets.add(p);
					}

                    // if we got some bogus packets while fetching the data, then we will
                    // stop this thread.
                    if(!this.alive)
                        break;

					byte[] stringData = new byte[numberOfPackets * 512];
					int byteCounter = 0;
					int stringSize = 0;

					for (Packet p : packets) {
						stringSize = stringSize + ByteConverter.bytesToInt(p.getData(), 4);
						for (int i = 0; i < ByteConverter.bytesToInt(p.getData(), 4); i++) {
							stringData[byteCounter++] = p.getData()[i + 8];
						}
					}

					String strFromClient = null;
					try {
						strFromClient = new String(stringData, 0, stringSize, "UTF8");
					}

					catch (UnsupportedEncodingException e2) {
                        System.out.println("[!!] Error while generating return string: " + e2.getMessage());
					}

					if (this.debugMode) {
						System.out.println("[debug] String from client: " + strFromClient);
					}
					// Let's check out the output that the clients will be
					// receiving and let's create a replay for the client.
					String taggedString = null;
					try {
						// wrap to function.
						java.lang.StringBuilder b = new StringBuilder();
						
						String[] lines = strFromClient.split("\n");
						String res = "";
						for(String s : lines){
							String strOUt = this.outputGenerator.generateOutput(s);
							res += this.outputGenerator.generateOutput(s) + "\n";
							b.append(strOUt+"\n");
						}
				
						//taggedString = b.toString();
						taggedString = res;
						taggedString = taggedString.substring(0, taggedString.length()-1);
						taggedString = taggedString + "[][\n]";
						
						if (this.debugMode)
							System.out.println("[debug] Reply string from IceNLP that will be sent to client is: " + taggedString);

					} 
					catch (Exception e) {
						System.out.println(e.getCause());
						System.out.println("[!!] Error in thread while getting IceNLP singleton instance");
					}

					// Send the tagged string back to the client.
					try {
						writeReplyString(taggedString);
					} catch (IOException e) {
						System.out.println("Could not write to client");
						this.alive = false;
						break;
					}
				}
                
				else {
				    if(this.debugMode){
                        System.out.println("[debug] invalid initial packet from client.");
                    }
                    this.alive = false;
					break;
				}
			}
		}

		if (this.debugMode) {
			System.out.println("[debug] Client thread is shutting down");
		}

		try {
			this.socket.close();
		} catch (IOException e) {
			System.out.println("[!!] " + e.getMessage());
		}
	}

	public void writeToClient(List<Packet> packets) throws IOException {
		for (Packet p : packets)
			this.ostream.write(p.getData());
	}

	// Function for sending the replay to the client.
	private void writeReplyString(String reply) throws IOException {
		List<Packet> packets = new LinkedList<Packet>();
		byte[] strBytes = reply.getBytes("UTF8");
		int numberOfpackets = ((int) Math.floor((strBytes.length / 504))) + 1;

		// Let's add the initial packet to the collection
		packets.add(PacketManager.createTagAnswerSizePacket(numberOfpackets));

		// Let's split the string into packets and add them to our collection.
		for (Packet p : PacketManager.createStringPackets(4, strBytes, numberOfpackets))
			packets.add(p);

		// Let's send the packets to the client.
		if (this.debugMode)
			System.out.println("[debug] sending " + packets.size() + " packets to client.");
		this.writeToClient(packets);

		if (this.debugMode)
			System.out.println("[debug] data sent to client.");
	}
    
    private Packet readFromClient() {
		byte[] data = new byte[512];
		Packet packet = null;
		try {
			int readSize = this.istream.read(data, 0, 512);
			packet = new Packet(data, readSize);
		}

		catch (IOException e) {
			this.alive = false;
		}

		return packet;
	}
}
