package is.ru.iclt.icenlp.arc.network;

import is.ru.iclt.icenlp.common.network.ByteConverter;
import is.ru.iclt.icenlp.common.network.Packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class NetworkHandler {

    private Socket socket;

    public NetworkHandler(String host, String port) throws NetworkException {
        // Open socket connection to the router.
        try {
            this.socket = new Socket(host, Integer.parseInt(port));
        } catch (IOException e) {
            throw new NetworkException();
        }
    }


    public String translate(String text) throws NetworkException {
        if(text == null)
            return "";

        if(text.length() <= 0)
            return "";

        if(text.matches("\\s+"))
            return "";

        try {
            this.sendSentence(text);
            return this.readReply();
        } catch (IOException e) {
            throw new NetworkException();
        }
    }

    public void closeConnection() throws NetworkException {
        try {
            this.socket.close();
        } catch (IOException e) {
            throw new NetworkException();
        }
    }




    private void sendSentence(String string) throws IOException{

		List<Packet> byte_to_send = new LinkedList<Packet>();

		byte[] strBytes = string.getBytes();
		int numberOfpackets = ((int) Math.floor((strBytes.length/504)))+1;

		// Let's create the packet that we will send first
		byte[] opcodeData = ByteConverter.itToByte(3);
		byte[] sizeData = ByteConverter.itToByte(numberOfpackets);
		byte[] data = new byte[512];
		int p1counter = 0;

		for(int i = 0; i<4; i++){
			data[p1counter] = opcodeData[i];
			p1counter++;
		}

		for(int i=0; i<4; i++){
			data[p1counter] = sizeData[i];
			p1counter++;
		}

		byte_to_send.add(new Packet(data));


		int dataByteCounter = 0;
		for(int i = 0; i< (numberOfpackets); i++){
			byte[] packetData = new byte[512];
			int dataSize = 0;

			for(int j = 0; j<504 && dataByteCounter<strBytes.length; j++){
				packetData[j+8] = strBytes[dataByteCounter];
				dataByteCounter++;
				dataSize++;
			}

			opcodeData = ByteConverter.itToByte(4);
			sizeData = ByteConverter.itToByte(dataSize);
			for(int j=0; j<4; j++){
				packetData[j] = opcodeData[j];
				packetData[j+4] = sizeData[j];
			}

			byte_to_send.add(new Packet(packetData));
		}
        
		OutputStream ostream = this.socket.getOutputStream();
		for(Packet p : byte_to_send){
			ostream.write(p.getData());
		}
	}
    
    private String readReply() throws IOException {
		InputStream instream = socket.getInputStream();
		String replyString = "";
		byte[] data = new byte[512];

		instream.read(data);
		int op =  ByteConverter.bytesToInt(data, 0);
		int numPack = ByteConverter.bytesToInt(data, 4);

		if(op != 5){
			return null;
		}
		else{
			byte[] dataFromRouter = new byte[numPack * 512];
			int byteCounter = 0;
			int size = 0;

			for(int i = 0; i<numPack; i++)
			{
				data = new byte[512];
				instream.read(data);
				int dataSize = ByteConverter.bytesToInt(data, 4);
				size = size + dataSize;
				for(int j = 0; j<dataSize; j++){
					dataFromRouter[byteCounter++] = data[j+8];
				}
			}

			replyString = new String(dataFromRouter, 0, size);
		}
		return replyString;
	}
}
