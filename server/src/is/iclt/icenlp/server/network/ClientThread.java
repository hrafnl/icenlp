package is.iclt.icenlp.server.network;

import is.iclt.icenlp.common.network.ByteConverter;
import is.iclt.icenlp.common.network.Packet;
import is.iclt.icenlp.common.configuration.Configuration;
import is.iclt.icenlp.core.utils.Pair;
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
    private Configuration configuration;
    private String defaultFormatString = "txt";

	public ClientThread(Socket socket, OutputGenerator generator)
	{
		this.configuration = Configuration.getInstance();
		this.outputGenerator = generator;
		this.socket = socket;
		this.alive = true;
        this.debugMode = configuration.debugMode(); 

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
        Pair<String, String> formatAndString = new Pair<String, String>();

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

                    if (!strFromClient.equals(""))
                    {
					    //strFromClient = removeAltBrackets(strFromClient);
                        formatAndString = removeAltBrackets((strFromClient));
                    }
                    else
                    {
                        /*TODO : Handle this special case of empty string */
                        System.out.println("gDB>> empty line!");
                    }

					if (this.debugMode) {
						System.out.println("[debug] Cropped string: " + strFromClient);
					}
					
					// Let's check out the output that the clients will be
					// receiving and let's create a replay for the client.
					String taggedString = "";

					try 
					{
                        String format = formatAndString.one;
                        String stringToAnalyze = formatAndString.two;

						if(this.configuration.containsKey("ExternalMorpho") && this.configuration.getValue("ExternalMorpho").equals("apertium"))
						{
							//taggedString = this.outputGenerator.generateExternalOutput(strFromClient) + "\n";
                            taggedString = this.outputGenerator.generateExternalOutput(stringToAnalyze) + "\n";
						}
						else
						{
							// wrap to function.
							java.lang.StringBuilder b = new StringBuilder();

							String[] lines = stringToAnalyze.split("\n");
                            //String[] lines = strFromClient.split("\n");

							for(String s : lines)
							{
								String strOut = this.outputGenerator.generateOutput(format, s);
								b.append(strOut+"\n");
							}
					
							taggedString = b.toString();
						}
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

	// if we find [ xml ] or [ tcf ] we set the configs according to the string
	private Pair<String,String> removeAltBrackets(String strFromClient)
	{
        /* result.one is the format string, result.two is the string to be analyzed
            For example, strFromClient="[txt][merge]Hann er góður" =>
            result.one = "[txt][merge]", result.two = "Hann er góður"
        */
        Pair result = new Pair<String, String>();

		// if we have anything else than alt, we just return the string back
		if (Configuration.getInstance().getValue("IceParserOutput") == null || !Configuration.getInstance().getValue("IceParserOutput").equals("alt"))
		{
		    result.two = strFromClient;
            return result;
		}

        // if the first char isn't [ then we will return the text unaltered straight away with default format string
       if (strFromClient.charAt(0) != '[')
        {
            result.one = defaultFormatString;
            result.two = strFromClient;
            return result;
        }

		// GÖL
		// Split the incoming message into two, format string and the text to be analyzed.
		// We check out which tags are sent, and do the appropriate flagging.
		// Then we return the rest of the message.
		String strFromClientWithoutFormat = strFromClient.replaceFirst("^[\\S\\[\\]]*\\]","");
		String formatString = strFromClient.replaceFirst("^([\\S\\[\\]]*\\]).*","$1");
        result.one = formatString;
        result.two = strFromClientWithoutFormat;
        return result;
	}
}
