package is.ru.icenlpserver.network;

import java.util.LinkedList;
import java.util.List;

public class PacketManager 
{
	private PacketManager(){}
	
	public static Packet createQuitPacket()
	{
		byte[] data = new byte[512];
		byte[] opcodeData = ByteConverter.itToByte(5);
		
		for(int i = 0; i<4; i++)
		{
			data[i] = opcodeData[i];
		}		
		
		return new Packet(data);
	}
	
	public static Packet createTagAnswerSizePacket(int numPackets)
	{
		byte[] data = new byte[512];
		
		byte[] opcodeData = ByteConverter.itToByte(3);
		byte[] sizeData = ByteConverter.itToByte(numPackets);
		
		for(int i = 0; i<4; i++)
		{
			data[i] = opcodeData[i];
			data[i+4] = sizeData[i];
		}
		
		return new Packet(data);
	}
	
	public static List<Packet> createStringPackets(int opcode, byte[] data, int numPackets)
	{
		LinkedList<Packet> packets = new LinkedList<Packet>();
		int dataByteCounter = 0;
		
		for(int i = 0; i<numPackets; i++)
		{
			byte[] packetData = new byte[512];
			int dataSize = 0;
			
			for(int j = 0; j<504 && dataByteCounter<data.length; j++)
			{
				packetData[j+8] = data[dataByteCounter];
				dataByteCounter++;
				dataSize++;
			}
			
			byte[] opcodeData = ByteConverter.itToByte(4);
			byte[] sizeData = ByteConverter.itToByte(dataSize);
			
			for(int j=0; j<4; j++)
			{
				packetData[j] = opcodeData[j];
				packetData[j+4] = sizeData[j];
			}
			
			packets.add(new Packet(packetData));
		}
		
		return packets;
	}
}
