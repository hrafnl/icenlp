package is.ru.icenlp.router.common.network;

public class Packet {
	private byte[] data;

	public Packet() {
		this.data = new byte[512];
	}

	public Packet(byte[] data) {
		this.data = data;
	}

	public Packet(int opcode) {
		this.data = new byte[512];
		this.setOpcode(opcode);
	}

	public byte[] getData() {
		return this.data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public int getOpcode() {
		return ByteConverter.bytesToInt(this.data, 0);
	}

	public void setOpcode(int opcode) {
		byte[] opcodedata = ByteConverter.itToByte(opcode);
		for (int i = 0; i < 4; i++)
			this.data[i] = opcodedata[i];
	}

	public int getInteger(int begins) {
		return ByteConverter.bytesToInt(this.data, begins);
	}

	public void setInteger(int begin, int value) {
		byte[] valueData = ByteConverter.itToByte(value);
		for (int i = 0; i < 4; i++) {
			this.data[i + begin] = valueData[i];
		}
	}
}
