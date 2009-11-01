package is.ru.icenlpserver.runner;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import is.ru.icenlpserver.common.Configuration;
import is.ru.icenlpserver.icenlp.IceNLPSingletonService;
import is.ru.icenlpserver.icenlp.icetagger.IceTaggerConfigrationException;
import is.ru.icenlpserver.network.NetworkThread;

/**
 * Runner contains the main function for the program.
 * @author hlynurs
 */
public class Runner 
{	
	public static boolean loadConfig(String location)
	{
		FileInputStream fstream = null;
		DataInputStream in = null;
		BufferedReader br = null;
		
		// Open the configuration file and read through it.
		try
		{
			fstream = new FileInputStream(location);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));			
		}
		catch (Exception e)
		{
			System.out.println("[x] Error while opening configuration file: " + e.getMessage());
			return false;
		}
		
		String strLine;
		try 
		{
			int lineNumber = 1;
			while ((strLine = br.readLine()) != null)
			{	
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
					    	return false;
					    }
					    
						String key = strLine.substring(0,strLine.indexOf("=")).replaceAll("\\s", "");

						Configuration.getInstance().addConfigEntry(key, value);
					}
					else
					{
						System.out.println("[x] Error in configuration file in line " + lineNumber);
						return false;
					}
				}
				
				lineNumber += 1;
			}
		} 
		catch (IOException e) 
		{
			System.out.println("[x] Error while reading configuration file: " + e.getMessage());
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) 
	{		
		// Announce the name of the server.
		System.out.println(">> IceNLPServer 1.0");
		
		// If there is no argument in the argument array then we will try
		// to read the default configuration file which is located in the
		// same folder as the JAR.
		if(args.length == 0)
		{
			System.out.println("[i] using default config file: IceNLPServer.conf");
			if(!loadConfig("IceNLPServer.conf"))
				return;
		}
		
		// We will read the configuration file that is passed in through args.
		else if(args.length == 1)
		{
			if(args[0].matches("(?i)--(help|h)"))
			{
				printHelp();
				return;
			}
			else if(args[0].matches("(?i)--(config|c)=.+"))
			{
				System.out.println("[i] using config file: IceNLPServer.conf");
				if(!loadConfig(args[0].split("=")[1]))
					return;
			}
			else
			{
				printHelp();
				return;
			}
		}
		else
		{
			printHelp();
			return;
		}
		
		final NetworkThread nt;
		Thread thread;
				
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
	
		// Start the network thread.
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
	
	public static void printHelp()
	{
		System.out.println("to start server: java -jar IceNLPServer.jar");
		System.out.println("arguments:");
		System.out.println("  --c=, --config=            configuration file to use.");
		System.out.println("  --h=, --help=              displays help.");
	}
}