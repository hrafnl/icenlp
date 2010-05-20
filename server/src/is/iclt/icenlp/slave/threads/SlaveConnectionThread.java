package is.iclt.icenlp.slave.threads;



import is.iclt.icenlp.common.apertium.ApertiumWrapper;
import is.iclt.icenlp.common.network.Packet;
import is.iclt.icenlp.router.common.network.ByteConverter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class SlaveConnectionThread implements Runnable 
{
	private Socket socket;
	private InputStream istream;
	private boolean alive = true;
	private String scriptLocation;
	
	public SlaveConnectionThread(Socket socket, String scriptLocation)
	{
		this.scriptLocation = scriptLocation;
		this.socket = socket;
		try 
		{
			istream = socket.getInputStream();
		}
		catch (IOException e) 
		{
			System.out.println("[SlaveConnectionThread]: unable to get input stream from socket.");
			this.alive = false;
		}
	}
	
	public void run() 
	{
		while(alive)
		{
			try 
			{
				byte[] rdata = new byte[512];
				istream.read(rdata);
				int op = ByteConverter.bytesToInt(rdata,0);
				int numPackets = ByteConverter.bytesToInt(rdata,4);
				
				if(op == 10)
				{
					byte[] dataFromRouter = new byte[numPackets * 512];
					int byteCounter = 0;
					int size = 0;
					while(numPackets > 0)
					{
						byte[] data = new byte[512];
						try 
						{
							istream.read(data);
						}
						catch (IOException e) 
						{
							e.printStackTrace();
						}
						
						int packetOpcode = ByteConverter.bytesToInt(data, 0);
						if(packetOpcode != 0){							
							int dataSize = ByteConverter.bytesToInt(data, 4);
							size = size + dataSize;
							for(int j = 0; j<dataSize; j++){
								dataFromRouter[byteCounter++] = data[j+8];
							}
							numPackets -= 1;
						}
						else{
							System.out.println("[SlaveConnectinoThread]: received ping while getting data!");
						}
					}
					
					String stringFromRouter = new String(dataFromRouter, 0, size);
					System.out.println("[SlaveConnectinoThread]: string from router: " + stringFromRouter);
					
					
					// Here we must run Apertium.
					


		            String output = ApertiumWrapper.translate(scriptLocation, stringFromRouter);


		            
		            System.out.println("[SlaveConnectionThread]: translation sent to client " + output );
		            sendReply(output, socket);
		    		
				}
				
				else if(op == 0){/* we ignore the ping packets.*/}
				
				else
				{
					System.out.println("[SlaveConnectionThread]: recevied bogus opcode. Shuting down.");
					alive = false;
					socket.close();
				}
			} 
			catch (IOException e) 
			{
				this.alive = false;
			}
		}
	}
	
	/**
	 * Function for shutting down the thread. The socket
	 * to the router will be closed as well.
	 */
	public void shutdown()
	{
		try 
		{
			this.socket.close();
			System.out.println("thread shutting down.");
			this.alive = false;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void sendReply(String string, Socket socket) throws IOException
	{
		// Now we will send the replay back.
        List<Packet> replyPackets = new LinkedList<Packet>();
        byte[] outputBytes = string.getBytes();
        int numberOfpackets = ((int) Math.floor((outputBytes.length/504)))+1;
	
        // Let's create the packet that we will send first
		byte[] opcodeData = ByteConverter.itToByte(12);
		byte[] sizeData = ByteConverter.itToByte(numberOfpackets);
		byte[] data = new byte[512];
		int p1counter = 0;
		
		for(int i = 0; i<4; i++)
		{
			data[p1counter] = opcodeData[i];
			p1counter++;
		}
		
		for(int i=0; i<4; i++)
		{
			data[p1counter] = sizeData[i];
			p1counter++;	
		}
		
		replyPackets.add(new Packet(data));
		
		int dataByteCounter = 0;
		for(int i = 0; i<numberOfpackets; i++)
		{
			byte[] packetData = new byte[512];
			int dataSize = 0;
			
			for(int j = 0; j<504 && dataByteCounter<outputBytes.length; j++)
			{
				packetData[j+8] = outputBytes[dataByteCounter];
				dataByteCounter++;
				dataSize++;
			}
			
			opcodeData = ByteConverter.itToByte(13);
			sizeData = ByteConverter.itToByte(dataSize);
			for(int j=0; j<4; j++)
			{
				packetData[j] = opcodeData[j];
				packetData[j+4] = sizeData[j];
			}
			
			replyPackets.add(new Packet(packetData));
		}
		
	
		OutputStream ostream = socket.getOutputStream();
		for(Packet p : replyPackets)
			ostream.write(p.getData());
	}
}