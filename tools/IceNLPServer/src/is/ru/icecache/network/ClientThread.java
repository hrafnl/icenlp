package is.ru.icecache.network;

import is.ru.icecache.icenlp.IceNLPSingletonService;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

/**
 * ClientThread handles the network communications between
 * connected clients and the server.
 * @author hlynurs
 */
public class ClientThread implements Runnable
{
	// Private member variable for the client
	private Socket socket;
	private boolean alive;
	private OutputStream ostream;
	private InputStream istream;
		
	public ClientThread(Socket socket)
	{
		this.socket = socket;
		this.alive = true;
		
		try 
		{
			this.ostream = socket.getOutputStream();
			this.istream = socket.getInputStream();
		} 
		
		catch (IOException e) 
		{
			this.alive = false;
			e.printStackTrace();
		}
	}
	
	@Override
	
	// The client thread will work as follows:
	// 0 opcode means 
	public void run() 
	{			
		while(this.alive)
		{
			// Let's read backets from the client:
			Packet pack = this.readFromClient();
			if(pack.getDataSize() == -1)
			{	
				this.alive = false;
				break;
			}
			else
			{
				int opcode = pack.getOpcode();
				if(opcode == 1)
				{
					int numberOfPacksets = ByteConverter.bytesToInt(pack.getData(), 4);
					List<Packet> packets = new LinkedList<Packet>();
					
					for(int i = 0; i<numberOfPacksets; i++)
					{
						packets.add(readFromClient());
					}
					
					byte[] stringData = new byte[numberOfPacksets*512];
					int byteCounter = 0;
					int stringSize = 0;
					
					for(Packet p:packets)
					{
						stringSize = stringSize + ByteConverter.bytesToInt(p.getData(), 4);
						for(int i=0; i<ByteConverter.bytesToInt(p.getData(), 4); i++)
						{
							stringData[byteCounter++] = p.getData()[i+8]; 
						}
					}
					
					String strFromClient = new String(stringData, 0 ,stringSize);
					System.out.println(">> recived string from client: " + strFromClient);
					
					// Let's check out the output that the clients will be reciving and 
					// let's create a replay for the client.
					String taggedString = null;
					try
					{
						taggedString = IceNLPSingletonService.getInstance().tagText(strFromClient);
					}
					catch (Exception e) 
					{
						System.out.println("[x] error in thread while getting IceNLP singleton instance" );
					}
		
					// lets write the replay to the client.
					try 
					{
						writeReplyString(taggedString);
					} 
					catch (IOException e) 
					{
						System.out.println("Could not write to client");
						this.alive = false;
						break;
					}
				}
				
				else if(opcode == 5)
				{
					System.out.println(">> clients is closing the connection");
					this.alive = false;
					break;
				}
				
				else
				{
					this.alive = false;
					break;
				}
			}
		}
		
		System.out.println(">> Client thread is shuting down");
		try 
		{
			this.socket.close();
		} 
		catch (IOException e) 
		{
			System.out.println("[x] " + e.getMessage());
		}
	}
	
	public void writeToClient(List<Packet> packets) throws IOException
	{
		for(Packet p : packets)
			this.ostream.write(p.getData());
	}
	
	
	// Function for sending the replay to the client.
	private void writeReplyString(String replay) throws IOException
	{	
		List<Packet> packets = new LinkedList<Packet>();
		
		byte[] strBytes = replay.getBytes();
		int numberOfpackets = ((int) Math.floor((strBytes.length/504)))+1;
		
		// Let's add the initial packet to the collection
		packets.add(PacketManager.createTagAnswerSizePacket(numberOfpackets));
		
		// Let's split the string into packets and add them to our collection.
		for(Packet p : PacketManager.createStringPackets(4, strBytes, numberOfpackets))
			packets.add(p);
		
		// Let's send the packets to the client.
		this.writeToClient(packets);
	}
	
	
	public Packet readFromClient()
	{
		byte[] data = new byte[512];
		Packet packet = null;
		try 
		{
			int readSize = this.istream.read(data, 0, 512);
			packet = new Packet(data, readSize);
		} 
		
		catch (IOException e) 
		{
			this.alive = false;
		}
		
		return packet;
	}
}
