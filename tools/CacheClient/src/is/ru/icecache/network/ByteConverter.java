package is.ru.icecache.network;

public class ByteConverter 
{
	/***
	 * Function for converting integer to 4bytes.
	 * @param value Number that will be converted.
	 * @return Byte array the contains 4 byte.
	 */
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
}
