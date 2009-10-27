package is.ru.icecache.runner;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import is.ru.icecache.common.Configuration;
import is.ru.icecache.icenlp.IceNLPSingletonService;
import is.ru.icecache.icenlp.icetagger.IceTaggerConfigrationException;
import is.ru.icecache.network.NetworkThread;

public class Runner 
{	
	public static void loadConfig(String location)
	{
		
		FileInputStream fstream = null;
		DataInputStream in = null;
		BufferedReader br = null;
		
		try
		{
			// Let's open up the configuration file and read
			// through it.
			fstream = new FileInputStream(location);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));			
		}
		catch (Exception e)
		{
			System.out.println("[x] Error while opening configuration file: " + e.getMessage());
			return;
		}
		
		// Let's go through the configuration file.
		String strLine;
		try 
		{
			int lineNumber = 1;
			while ((strLine = br.readLine()) != null)
			{	
				// We don't want to look at comments
				if(!strLine.startsWith("#") && !strLine.startsWith("//") && strLine.length() > 0)
				{
					if(strLine.length() >= 3 && strLine.contains("="))
					{ 
					    Pattern p = Pattern.compile("\"[^\"]+\"");
					    
					    Matcher matcher = p.matcher(strLine);
					    String value;
					    if(matcher.find())
					    {
					    	value = matcher.group().replace("\"", "");
					    }
					    else
					    {
					    	System.out.println("[x] Error in configuration file in line " + lineNumber);
					    	return;
					    }
					    
						String key = strLine.substring(0,strLine.indexOf("=")).replaceAll("\\s", "");

						Configuration.getInstance().addConfigEntry(key, value);
					}
					else
					{
						System.out.println("[x] Error in configuration file in line " + lineNumber);
						return;
					}
				}
				
				lineNumber += 1;
			}
		} 
		catch (IOException e) 
		{
			System.out.println("[x] Error while reading configuration file: " + e.getMessage());
			return ;
		}
	}
	
	public static void main(String[] args) 
	{		
		// We will read the default config file.
		if(args.length == 0)
		{
			System.out.println("[i] using default config file: IceNLPServer.conf");
			loadConfig("IceNLPServer.conf");
		}
		
		// We will read the config file that is passed in through args.
		else if(args.length == 1)
		{
			if(args[0].matches("(?i)--(help|h)"))
			{
				System.out.println("Help menu");
				return;
			}
			else if(args[0].matches("(?i)--(config|c)=.+"))
			{
				System.out.println("[i] using config file: IceNLPServer.conf");
				loadConfig(args[0]);
			}
			else
			{
				System.out.println("Help menu");
				return;
			}
		}
		else
		{
			System.out.println("Help menu");
			return;
		}
		
		final NetworkThread nt;
		Thread thread;
		
		// Let's announce the name of the server.
		System.out.println(">> IceNLPServer 1.0");
				
		// Lets create the first instance of the singleton service.
		try
		{
			IceNLPSingletonService.getInstance();
		}
		catch (IceTaggerConfigrationException e) 
		{
			System.out.println("[x] configuration error: " + e.getMessage());
			return;
		}
	
		System.out.println("[i] ready.");
		
		// Let's start the network thread.
		nt = new NetworkThread();
		thread = new Thread(nt);
		thread.start();
		
		// Shutdown hook so users can kill the server
		// with shutdown signal (CTRL+C)
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
		             System.out.println("\nShuting server down..");
		             nt.cleanUp();
		    }
		});
	}
}