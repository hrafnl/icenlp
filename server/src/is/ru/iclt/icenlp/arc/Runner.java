package is.ru.iclt.icenlp.arc;

import is.ru.iclt.icenlp.common.network.Packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

public class Runner 
{
	public static void main(String[] args) 
	{	
		String host = "localhost";
		String port = "2525";
		String text = "";
		
		// Go through the parameters.
		for(String arg : args)
		{
			if(arg.startsWith("-host="))
				host = arg.replace("-host=", "");
			
			else if(arg.startsWith("-port="))
				port = arg.replace("-port=", "");
			
			else if(arg.startsWith("-text="))
				text = arg.replace("-text=", "");
			
			else if(arg.equals("-help"))
			{
				System.out.println("IRW - IceNLP router webclient");
				System.out.println();
				System.out.println("usage: java -jar irw.jar [arguments]");
				System.out.println();
				System.out.println("Arguments:");
				System.out.println("  -host=[host]          Hostname where the router is running.");
				System.out.println("  -port=[port]          The port that the router is listnening on.");
				System.out.println("  -text=[text]          Text that will be translated.");
				System.out.println("  -help                 Prints this help menu.");
				return;
			}
			
			else
			{
				System.out.println("Unknown parameter '" + arg + "'");
				return;
			}
		}
		
		try 
		{
			Socket socket = new Socket(host, Integer.parseInt(port));
			
			sendSentence(text, socket);
			String translation = readReply(socket);
			socket.close();
			
			if(translation == null)
				System.out.println("unable to serve request.");
			else
				System.out.println(translation);
			
		} 
		catch (NumberFormatException e) 
		{
			System.out.println("Error using port " + port);
			return;
		} 
		catch (UnknownHostException e) 
		{
			System.out.println("Unable to find host " + host);
			return;
		} 
		catch (IOException e) 
		{
			System.out.println("Unable to connect to host " + host + ":"+ port);
			return;
		}
	}
	
	public static int bytesToInt(byte[] data, int begin)
	{
		int opCode = 0;
		int pos = begin;
		opCode += ((int) data[pos++] & 0xFF) << 24;
		opCode += ((int) data[pos++] & 0xFF) << 16;
		opCode += ((int) data[pos++] & 0xFF) << 8;
		opCode += ((int) data[pos++] & 0xFF) << 0;
		return opCode;
	}
	
	public static byte[] itToByte(int value)
	{
		byte[] bytes = new byte[4];
		for (int i = 0; i < 4; i++) 
		{
			int offset = (bytes.length - 1 - i) * 8;
			bytes[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return bytes;
	}
	
	public static String readReply(Socket socket) throws IOException
	{
		InputStream instream = socket.getInputStream();
		String replyString = "";
		byte[] data = new byte[512];
		
		instream.read(data);
		int op = bytesToInt(data, 0);
		int numPack = bytesToInt(data, 4);
		
		if(op != 5)
		{
			return null;
		}
		else
		{
			byte[] dataFromRouter = new byte[numPack * 512];
			int byteCounter = 0;
			int size = 0;
			
			for(int i = 0; i<numPack; i++)
			{
				data = new byte[512];
				try 
				{
					instream.read(data);
				}
				catch (IOException e) 
				{
					return null;
				}
				int dataSize = bytesToInt(data, 4);
				size = size + dataSize;
				for(int j = 0; j<dataSize; j++)
				{
					dataFromRouter[byteCounter++] = data[j+8];
				}	
			}
			
			replyString = new String(dataFromRouter, 0, size);
		}
		return replyString;
	}
	
	
	
	public static void sendSentence(String string, Socket socket) throws IOException
	{
		
		List<Packet> byte_to_send = new LinkedList<Packet>();
		
		byte[] strBytes = string.getBytes();
		int numberOfpackets = ((int) Math.floor((strBytes.length/504)))+1;
		
		// Let's create the packet that we will send first
		byte[] opcodeData = itToByte(3);
		byte[] sizeData = itToByte(numberOfpackets);
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
		
		byte_to_send.add(new Packet(data));
		
		
		int dataByteCounter = 0;
		for(int i = 0; i< (numberOfpackets); i++)
		{
			byte[] packetData = new byte[512];
			int dataSize = 0;
			
			for(int j = 0; j<504 && dataByteCounter<strBytes.length; j++)
			{
				packetData[j+8] = strBytes[dataByteCounter];
				dataByteCounter++;
				dataSize++;
			}
			
			opcodeData = itToByte(4);
			sizeData = itToByte(dataSize);
			for(int j=0; j<4; j++)
			{
				packetData[j] = opcodeData[j];
				packetData[j+4] = sizeData[j];
			}
			
			byte_to_send.add(new Packet(packetData));
		}
		
		
		OutputStream ostream = socket.getOutputStream();
		for(Packet p : byte_to_send)
		{
			ostream.write(p.getData());
		}
	}
}
