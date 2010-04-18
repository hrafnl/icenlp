package is.iclt.icenlp.router.threads;

import is.iclt.icenlp.router.common.Slave;
import is.iclt.icenlp.router.common.SlaveCollection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SlaveListeningThread implements Runnable {
	private String port;
	private String host;
	private ServerSocket serverSocket;
	private boolean alive = false;
	private String prefix = "[SlaveListeningThread]: ";

	public SlaveListeningThread(String host, String port) throws SlaveListeningThreadException {
		this.port = port;
		this.host = host;

		try {
			InetSocketAddress address = new InetSocketAddress(this.host, Integer.parseInt(this.port));
			this.serverSocket = new ServerSocket(Integer.parseInt(port), 10, address.getAddress());
		} catch (NumberFormatException e) {
			throw new SlaveListeningThreadException(e.getMessage());
		} catch (IOException e) {
			throw new SlaveListeningThreadException(e.getMessage());
		}

		this.alive = true;
		System.out.println("[SlaveListeningThread]: network is up. Listening on port "+ this.port);
	}

	public void run() {
		while (this.alive) {
			try {
				Socket s = this.serverSocket.accept();
				System.out.println(prefix + "connection from slave " + s.getInetAddress());
				SlaveCollection.getInstance().addSlave(new Slave(s));

			} catch (IOException e) {
				System.out.println(prefix + "Exception while accepting new sockets.");
				this.alive = false;
			}
		}
	}
}
