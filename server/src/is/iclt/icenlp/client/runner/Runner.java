package is.iclt.icenlp.client.runner;




import is.iclt.icenlp.common.network.ByteConverter;
import is.iclt.icenlp.common.network.Packet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Runner 
{	
	
	
	public static void printHelp()
	{
		System.out.println("IceNLPClient");
		System.out.println("\t --host|h= \t Connection host.");
		System.out.println("\t --port|p= \t Connection port.");
	}
	
	
	
	public static void main(String[] args) 
	{		
		String host = "localhost";
		int port = 1234;
		
		if(args.length > 0)
		{	
			for(String arg : args)
			{
				if(arg.matches("(?i)--(port|p)=.+"))
					port = Integer.parseInt(arg.split("=")[1]);
				else if(arg.matches("(?i)--(host|h)=.+"))
					host = arg.split("=")[1];
				else
				{
					printHelp();
					return;
				}
					
			}
		}
		try
		{
			// Let's read from the std.
			String inLine;

			InputStreamReader reader = new InputStreamReader(System.in, "UTF8");
			BufferedReader br = new BufferedReader(reader);
			
			String inString = "";

			while ((inLine = br.readLine()) != null)
				inString += inLine + "\n";
			
			if(inString.length()>=1)
				inString = inString.substring(0, inString.length()-1);

			// Do we need this check here?
			if (inString.length() == 0) 
			{
				printHelp();
				return;
			}
			
			Socket socket = null;
			socket = new Socket(host, port);
			System.out.println(tagString(inString, socket));

		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}
	
	
	
	
	
	
	
	public static String tagString(String lines, Socket socket) throws IOException
	{
		InputStream inStream = socket.getInputStream();
		OutputStream outStream = socket.getOutputStream();
		

		
		createSentecePackets(outStream, lines);
		Packet p = readFromStream(inStream);
		if(p.getOpcode() == 3)
		{
			int size = 0;
			int numOfPacks = is.iclt.icenlp.common.network.ByteConverter.bytesToInt(p.getData(), 4);
			byte[] dataFromServer = new byte[numOfPacks * 512];
			int byteCounter = 0;
			
			for(int i = 0; i<numOfPacks; i++)
			{
				p = readFromStream(inStream);
				int dataSize = is.iclt.icenlp.common.network.ByteConverter.bytesToInt(p.getData(), 4);
				size = size + dataSize;
				for(int j = 0; j<dataSize; j++)
				{
					dataFromServer[byteCounter++] = p.getData()[j+8];
				}	
			}
			
			String strServer = new String(dataFromServer, 0, size);
			return strServer;
		}
		else
		{
			System.out.println(">> Error, reply from server was wrong.");
			return "";
		}
	}
		
	
	public static Packet readFromStream(InputStream stream)
	{
		byte[] data = new byte[512];
		Packet packet = null;
		try 
		{
			int readSize = stream.read(data, 0, 512);
			packet = new Packet(data, readSize);
		} 
		
		catch (IOException e) 
		{
			System.out.println("error: " + e.getMessage());
		}
		
		return packet;
	}
	
	public static void createSentecePackets(OutputStream stream, String sentence)
	{
		try 
		{
			// list of packets that we will send.
			List<Packet> packets = new LinkedList<Packet>();
			
			byte[] strBytes = sentence.getBytes("UTF8");
			int numberOfpackets = ((int) Math.floor((strBytes.length/504)))+1;
			
			// Let's create the packet that we will send first
			byte[] opcodeData = ByteConverter.itToByte(1);
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
			
			packets.add(new Packet(data));
			
			
			int dataByteCounter = 0;
			for(int i = 0; i<numberOfpackets; i++)
			{
				byte[] packetData = new byte[512];
				int dataSize = 0;
				
				for(int j = 0; j<504 && dataByteCounter<strBytes.length; j++)
				{
					packetData[j+8] = strBytes[dataByteCounter];
					dataByteCounter++;
					dataSize++;
				}
				
				opcodeData = ByteConverter.itToByte(2);
				sizeData = ByteConverter.itToByte(dataSize);
				for(int j=0; j<4; j++)
				{
					packetData[j] = opcodeData[j];
					packetData[j+4] = sizeData[j];
				}
				packets.add(new Packet(packetData));

			}
			for(Packet pack: packets)
			{
				stream.write(pack.getData());
			}
		} 
		
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}		
}
