package is.iclt.icenlp.common.network;

import is.iclt.icenlp.router.common.network.ByteConverter;

/**
 * Class that represents a network packet. The packet contains the payload that
 * is read from a socket.
 * 
 * @author hlynur
 */
public class Packet {
	private byte[] data;
	private int dateSize;


	

	
	
	/**
	 * Constructor for the class.
	 * @param data
	 * @param size
	 */
	public Packet(byte[] data, int size) {
		this.data = data;
		this.dateSize = size;
	}
	
	/**
	 * Setter for the data member
	 * variable.
	 * @param data
	 */
	public Packet(byte[] data) {
		this.data = data;
	}
	
	public Packet(int opcode) {
		this.data = new byte[512];
		this.setOpcode(opcode);
	}


	/**
	 * Getter for the data member.
	 * @return
	 */
	public byte[] getData() {
		return this.data;
	}
	
	
	public void setData(byte[] data) {
		this.data = data;
	}
	
	

	/**
	 * Function that returns the size of
	 * the packet.
	 * @return Integer number that is the size of the
	 * payload.
	 */
	public int getDataSize() {
		return this.dateSize;
	}
	
	public int getOpcode() 
	{
		return ByteConverter.bytesToInt(this.data, 0);
	}
	
	public void setOpcode(int opcode) {
		byte[] opcodedata = ByteConverter.itToByte(opcode);
		for (int i = 0; i < 4; i++)
			this.data[i] = opcodedata[i];
	}
	
	public void setInteger(int begin, int value) {
		byte[] valueData = ByteConverter.itToByte(value);
		for (int i = 0; i < 4; i++) {
			this.data[i + begin] = valueData[i];
		}
	}
	
	public int getInteger(int begins) {
		return ByteConverter.bytesToInt(this.data, begins);
	}
}