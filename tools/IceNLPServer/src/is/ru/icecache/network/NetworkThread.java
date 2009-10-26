package is.ru.icecache.network;

import is.ru.icecache.common.Configuration;

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
			InetSocketAddress address = new InetSocketAddress(Configuration.host, 1234);
			this.serverSocket = new ServerSocket(Integer.parseInt(Configuration.port),12,address.getAddress());
		}
		
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() 
	{
		while(this.alive)
		{
			try 
			{
				Socket socket = this.serverSocket.accept();
				System.out.println("[i] incoming connection from a client " +  socket.getInetAddress().getCanonicalHostName());
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
