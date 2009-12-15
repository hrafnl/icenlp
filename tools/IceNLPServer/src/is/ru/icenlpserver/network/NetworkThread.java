package is.ru.icenlpserver.network;

import is.ru.icenlpserver.common.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkThread implements Runnable 
{
	// Variable that is used to keep the thread
	// alive and to shut him down.
	private boolean alive;
	
	// Member variable for socket listener.
	private ServerSocket serverSocket;
	
	/**
	 * Constructor for the NetworkThread class.
	 * @param host
	 * @param port
	 */
	public NetworkThread()
	{	
		this.alive = true;
		
		try 
		{
			// Find the host name that the server will use.
			String host = "localhost";
			if(Configuration.getInstance().containsKey("host"))
				host = Configuration.getInstance().getValue("host");
			
			// Find the network port that the server will use.
			String port = "1234";
			if(Configuration.getInstance().containsKey("port"))
				port = Configuration.getInstance().getValue("port");
			
			
			InetSocketAddress address = new InetSocketAddress(host, Integer.parseInt(port));
			this.serverSocket = new ServerSocket(Integer.parseInt(port), 10,address.getAddress());
		
			System.out.println("[i] Server hostname: " + host);
			System.out.println("[i] Server port: " + port);
		}
		
		catch (Exception e)
		{
			System.out.println("[x] Error in binding host/port to server: " + e.getMessage());
		}
	}
	
	public void run() 
	{
		System.out.println("[i] ready.");
		
		while(this.alive)
		{
			try 
			{
				Socket socket = this.serverSocket.accept();
				if(Configuration.getInstance().debugMode())
					System.out.println("[debug] connection from host " +  socket.getInetAddress().getCanonicalHostName());
				
				// Let's create a new thread for the connection.
				new Thread(new ClientThread(socket)).start();
			} 
			
			catch (IOException e) 
			{
				System.out.println("[x] " + e.getMessage());
			}
		}
		
		System.out.println("[i] network thread stopped.");
		
	}
	
	public void cleanUp()
	{
		System.out.println("[i] stopping network thread.");
		this.alive = false;
	}
}
