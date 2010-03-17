package is.ru.iclt.icenlp.router.threads;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class RequestListneningThread implements Runnable {

	private String port;
	private String host;
	private ServerSocket serverSocket;
	private boolean alive = false;
	private String prefix = "[RequestListneningThread]: ";

	public RequestListneningThread(String host, String port)
			throws RequestListneningThreadException {
		this.port = port;
		this.host = host;

		try {
			InetSocketAddress address = new InetSocketAddress(this.host,
					Integer.parseInt(this.port));
			this.serverSocket = new ServerSocket(Integer.parseInt(port), 10,
					address.getAddress());
		}

		catch (NumberFormatException e) {
			throw new RequestListneningThreadException(e.getMessage());
		}

		catch (IOException e) {
			throw new RequestListneningThreadException(e.getMessage());
		}

		this.alive = true;
		System.out.println(this.prefix + "network is up. Listnening on port "
				+ this.port);
	}

	@Override
	public void run() {
		while (this.alive) {
			try {
				System.out.println(this.prefix + "waiting for connections.");
				Socket s = this.serverSocket.accept();
				System.out.println(this.prefix + "incoming request from "
						+ s.getInetAddress());

				// Lets start a handler thread for this connection.
				new Thread(new RequestHandlerThread(s)).start();

			} catch (IOException e) {
				System.out.println(this.prefix
						+ "unable to accept incoming socket.");
				e.printStackTrace();
			}
		}
	}
}
