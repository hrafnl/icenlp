package is.runner;

import is.ru.icecache.network.ByteConverter;
import is.ru.icecache.network.Packet;
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
	public static void main(String[] args) 
	{		
		try
		{
			// Let's read from the std-in
			String in;
			InputStreamReader reader = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(reader);
			
			in = br.readLine();
			
			Socket socket = null;
			socket = new Socket("localhost", 1234);
			String out = tagString(in, socket);
			System.out.println(out);
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}
	
	public static String tagString(String s, Socket socket) throws IOException
	{
		InputStream inStream = socket.getInputStream();
		OutputStream outStream = socket.getOutputStream();
		createSentecePackets(outStream, s);
		Packet p = readFromStream(inStream);
		if(p.getOpcode() == 3)
		{
			
			int size = 0;
			int numOfPacks = ByteConverter.bytesToInt(p.getData(), 4);
			byte[] dataFromServer = new byte[numOfPacks * 512];
			int byteCounter = 0;
			
			for(int i = 0; i<numOfPacks; i++)
			{
				p = readFromStream(inStream);
				int dataSize = ByteConverter.bytesToInt(p.getData(), 4);
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
			System.out.println(">> error replay from server was wrong.");
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
			
			byte[] strBytes = sentence.getBytes();
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
