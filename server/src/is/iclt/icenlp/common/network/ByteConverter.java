package is.ru.iclt.icenlp.common.network;

/**
 * ByteConverter contains methods that allows us to convert primitive types
 * to/from byte arrays. Used in the network communications.
 * 
 * @author hlynurs
 * 
 */
public class ByteConverter {
	/***
	 * Function for converting integer to 4 bytes.
	 * 
	 * @param value Number that will be converted.
	 * @return Byte array the contains 4 byte.
	 */
	public static byte[] itToByte(int value) {
		byte[] bytes = new byte[4];
		for (int i = 0; i < 4; i++) {
			int offset = (bytes.length - 1 - i) * 8;
			bytes[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return bytes;
	}

	/***
	 * Function for converting byte array to int.
	 * 
	 * @param data Byte array
	 * @param begin position where the integer begins in the byte array.
	 * @return Integer.
	 */
	public static int bytesToInt(byte[] data, int begin) {
		int opCode = 0;
		int pos = begin;
		opCode += ((int) data[pos++] & 0xFF) << 24;
		opCode += ((int) data[pos++] & 0xFF) << 16;
		opCode += ((int) data[pos++] & 0xFF) << 8;
		opCode += ((int) data[pos++] & 0xFF) << 0;
		return opCode;
	}
}
