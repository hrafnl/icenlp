package is.ru.icenlp.webclient;

public class Packet 
{
	private byte[] data;

	public Packet(byte[] data)
	{
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}
