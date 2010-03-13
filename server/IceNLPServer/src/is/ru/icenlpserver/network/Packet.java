package is.ru.icenlpserver.network;

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


	/**
	 * Getter for the data member.
	 * @return
	 */
	public byte[] getData() {
		return this.data;
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
	
	/**
	 * Function that returns the opcode of a packet. 
	 * @return Integer number that is read from the first
	 * fore bits in the network payload.
	 */
	public int getOpcode() {
		int opCode = 0;
		int pos = 0;
		opCode += ((int) data[pos++] & 0xFF) << 24;
		opCode += ((int) data[pos++] & 0xFF) << 16;
		opCode += ((int) data[pos++] & 0xFF) << 8;
		opCode += ((int) data[pos++] & 0xFF) << 0;
		return opCode;
	}
}