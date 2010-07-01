package is.iclt.icenlp.server.network;

import is.iclt.icenlp.common.configuration.Configuration;
import is.iclt.icenlp.server.output.OutputGenerator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkThread implements Runnable {
	// Member variables.
	private boolean alive; // indicates the status of the thread.
	private ServerSocket serverSocket;
    private boolean debugMode = false;
    private OutputGenerator outputGenerator;

	public NetworkThread() {
		this.debugMode = Configuration.getInstance().debugMode();
        this.alive = true;
        try 
        {
			this.outputGenerator = new OutputGenerator();
		} 
        catch (Exception e1) {
			System.out.println("[!!] error in creating output generator: " + e1.getMessage());
			System.exit(0);
		}
		try {
			// Find the host name that the server will use.
			String host = "localhost";
			if (Configuration.getInstance().containsKey("host"))
				host = Configuration.getInstance().getValue("host");

			// Find the network port that the server will use.
			String port = "1234";
			if (Configuration.getInstance().containsKey("port"))
				port = Configuration.getInstance().getValue("port");

            String backlogSize = "100";
            if (Configuration.getInstance().containsKey("backlogSize"))
				backlogSize = Configuration.getInstance().getValue("backlogSize");

			InetSocketAddress address = new InetSocketAddress(host, Integer.parseInt(port));
			this.serverSocket = new ServerSocket(Integer.parseInt(port), Integer.parseInt(backlogSize), address.getAddress());

			System.out.println("[i] Server hostname: " + host);
			System.out.println("[i] Server port: " + port);
            System.out.println("[i] Connection backlog size: " + backlogSize);
		}

		catch (Exception e) {
			System.out.println("[!!] Error in binding host/port to server: " + e.getMessage());
		}
	}
	/**
	 * Run is called when the thread is spawned.
	 * This functions loops until alive becomes false.
	 * During the loop the function waits for new
	 * incoming requests.
	 */
	public void run() {
		System.out.println("[i] Ready.");

		while (this.alive) {
			try {
				Socket socket = this.serverSocket.accept();
				if (this.debugMode)
					System.out.println("[debug] Connection from host " + socket.getInetAddress().getCanonicalHostName());

				// Let's create a new thread for the connection.
				new Thread(new ClientThread(socket, this.outputGenerator)).start();
			}

			catch (IOException e) {
				System.out.println("[!!] " + e.getMessage());
			}
		}

		System.out.println("[i] Network thread stopped.");
	}

	/**
	 * Function to kill the thread.
	 */
	public void cleanUp() {
		System.out.println("[i] Stopping network thread.");
		this.alive = false;
	}
}
