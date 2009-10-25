package is.ru.icecache.runner;

import is.ru.icecache.common.Configuration;
import is.ru.icecache.icenlp.IceNLPSingletonService;
import is.ru.icecache.icenlp.icetagger.IceTaggerConfigrationException;
import is.ru.icecache.network.NetworkThread;

public class Runner 
{
	public static void printHelp()
	{
		System.out.println(">> IceCache v1 - program parameters");
		System.out.println("-port=[port-number]");
		System.out.println("-host=[hostname]");
		System.out.println("-tritagger=true|false");
		System.out.println("-icelexicondir=dir");
		System.out.println("-tokenizerlexicon=dir");
		System.out.println("-mapperlexicon=dir");
		System.out.println("-tritaggerlexicon=dir");
		
		// These are new features.
		System.out.println("-lemmatize=true|false");
		System.out.println("-mapperlexicon=dir");
	}
	
	public static void main(String[] args) 
	{
		if(args.length == 1)
		{
			if(args[0].matches("(?i)-(help|h)"))
			{
				printHelp();
				return;
			}
		}
		
		final NetworkThread nt;
		Thread thread;
		
		// Let's announce the name of the server.
		System.out.println(">> IceCache v1.");
		
		if(args.length != 0)
		{
			for(String s:args)
			{
				if(s.matches("(?i)-(port|host|tritagger|icelexicondir|tokenizerlexicon|mapperlexicon|tritaggerlexicon|lemmatize)=.+"))
				{
					// Lets remove the - and move everything to lower case.
					s = s.replace("-", "");
					//s = s.toLowerCase();
					String[] kv = s.split("=");
					
					if(kv[0].equals("port"))
						Configuration.port = kv[1];
					
					else if(kv[0].equals("host"))
						Configuration.host = kv[1];
					
					else if(kv[0].equals("tritagger"))
					{
						String tritaggerArgs = kv[1].toLowerCase();

						if(tritaggerArgs.equals("true") || tritaggerArgs.equals("false"))
							Configuration.tritagger = Boolean.parseBoolean(tritaggerArgs);
						else
						{
							System.out.println("[x] tritagger argument can only be true or false");
							return;				
						}
					}
					
					else if(kv[0].equals("icelexicondir"))
					{
						Configuration.iceLexiconsDir = kv[1] + '/';
					}
					
					else if(kv[0].equals("tokenizerlexicon"))
					{
						Configuration.tokenizerLexicon = kv[1] + '/';
					}
					
					else if(kv[0].equals("mapperlexicon"))
					{
						Configuration.mapperLexicon = kv[1] + '/';
					}
					
					else if(kv[0].equals("tritaggerlexicon"))
					{
						Configuration.tritaggerLexicon = kv[1] + '/';
					}
					
					else if(kv[0].equals("lemmatize"))
					{
						String lemmatizeArg = kv[1].toLowerCase();

						if(lemmatizeArg.equals("true") || lemmatizeArg.equals("false"))
							Configuration.lemmatize = Boolean.parseBoolean(lemmatizeArg);
						else
						{
							System.out.println("[x] lemmatize argument can only be true or false");
							return;				
						}						
					}

					else
					{
						System.out.println("[x] unknown parameter: " + s);
						return;
					}
				}
				
				else
				{
					System.out.println("[x] error in parameter: " + s);
					return;
				}
			}
		}
		
		// Let's print out the server information.
		System.out.println("[i] listning on host: " + Configuration.host + ".");
		System.out.println("[i] listning on port: " + Configuration.port + ".");	
		
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