package is.iclt.icenlp.server.runner;

import is.iclt.icenlp.IceNLPSingletonService;
import is.iclt.icenlp.icetagger.IceTaggerConfigrationException;
import is.iclt.icenlp.common.configuration.Configuration;
import is.iclt.icenlp.server.network.NetworkThread;


/**
 * Runner class for the application. This class contains the main function.
 * 
 * @author hlynurs
 */
public class Runner {


	public static void main(String[] args) {
		// Announce the name of the server.
		System.out.println(">> IceNLPServer");

		// If there is no argument in the argument array then we will try
		// to read the default configuration file which is located in the
		// same folder as the JAR.
		if (args.length == 0) {
			System.out
					.println("[i] Using default config file: server.conf");
			if (!Configuration.loadConfig("server.conf"))
				return;
		}

		// We will read the configuration file that is passed in through args.
		else if (args.length == 1) {
			if (args[0].matches("(?i)--(help|h)")) {
				printHelp();
				return;
			} else if (args[0].matches("(?i)--(config|c)=.+")) {
				String configFilePath = args[0].split("=")[1];	
				System.out.println("[i] Using config file: " + configFilePath);
				if (!Configuration.loadConfig(configFilePath))
					return;
			} else {
				printHelp();
				return;
			}
		} else {
			printHelp();
			return;
		}

		final NetworkThread nt;
		Thread thread;

		// Lets create the first instance of the singleton service.
		try {
			IceNLPSingletonService.getInstance();
		} catch (IceTaggerConfigrationException e) {
			System.out.println("[X] Configuration error: " + e.getMessage());
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
				System.out.println("\nShutting server down..");
				nt.cleanUp();
			}
		});
	}

	/**
	 * Prints out help for application usage and arguments that the program
	 * recognizes.
	 */
	public static void printHelp() {
		System.out.println("to start server: java -jar IceNLPServer.jar");
		System.out.println("arguments:");
		System.out.println("  --c=, --config=            configuration file to use.");
		System.out.println("  --h=, --help=              displays help.");
	}
}
