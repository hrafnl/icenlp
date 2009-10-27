package is.ru.icenlpserver.network;

public class Packet 
{
	private byte[] data;
	private int dateSize;

	public Packet(byte[] data)
	{
		this.data = data;
	}
	public Packet(byte[] data, int size)
	{
		this.data = data;
		this.dateSize = size;
	}
	
	public byte[] getData()
	{
		return this.data;
	}
	
	public int getDataSize()
	{
		return this.dateSize;
	}
	
	public int getOpcode()
	{
		int opCode = 0; 
		int pos = 0; 
		opCode += ((int) data[pos++] & 0xFF) << 24; 
		opCode += ((int) data[pos++] & 0xFF) << 16; 
		opCode += ((int) data[pos++] & 0xFF) << 8; 
		opCode += ((int) data[pos++] & 0xFF) << 0; 
		return opCode; 
	}
}
