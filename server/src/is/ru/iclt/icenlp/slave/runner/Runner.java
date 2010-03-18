package is.ru.iclt.icenlp.slave.runner;



import is.ru.iclt.icenlp.slave.threads.SlaveConnectionThread;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Runner class for the slave.
 * @author hlynur
 */
public class Runner {
	public static void main(String[] args) {
		// If there are no parameters we print help.
		if (args.length == 0) {
			printHelp();
			return;
		}

		String scriptLoaction = null;

		// default host to connect to is "localhost"
		// this can be changed with an argument,namely
		// with the -hostname argument.
		String routerHost = "localhost";

		// default port to connect to is "2525"
		// this can be changed with an argument,namely
		// with the -port argument.
		String routerPort = "2525";

		for (String string : args) {
			if (string.matches("-host=[a-zA-Z0-9.]+"))
				routerHost = string.replace("-host=", "");

			else if (string.matches("-h=[a-zA-Z0-9.]+"))
				routerHost = string.replace("-h=", "");

			else if (string.matches("-port=[0-9]+"))
				routerPort = string.replace("-port=", "");

			else if (string.matches("-p=[0-9]+"))
				routerPort = string.replace("-p=", "");

			else if (string.matches("-sh=.+"))
				scriptLoaction = string.replace("-sh=", "");

			else {
				System.out.println("[SlaveRunner]: unknown parameter: " + string);
				return;
			}
		}

		// If there is no script location set we must
		// stop the slave.
		if (scriptLoaction == null) {
			printHelp();
			return;
		}

		// Lets check out if the script exists and is executable.
		File f = new File(scriptLoaction);
		if (!f.exists()) {
			System.out.println("[SlaveRunner]: File " + scriptLoaction + " does not exists.");
			return;
		}

		if (!f.canExecute()) {
			System.out.println("[SlaveRunner]: File " + scriptLoaction + " is not executiable.");
			return;
		}

		Socket socket = null;
		try {
			System.out.println("[SlaveRunner]: connecting to " + routerHost
					+ ":" + routerPort);
			int irouterPort = Integer.parseInt(routerPort);
			socket = new Socket(routerHost, irouterPort);
		} catch (UnknownHostException e) {
			System.out.println("Unable to connect to host " + routerHost);
			return;
		} catch (IOException e) {
			System.out.println("Unable to connect to host " + routerHost);
			return;
		}

		// Let's start the connection thread and initialize the communications
		// with the rotuer.
		final SlaveConnectionThread ct = new SlaveConnectionThread(socket,
				scriptLoaction);
		final Thread t = new Thread(ct);
		t.start();
		System.out.println("[SlaveRunner]: ready.");

		// Shutdown hook so users can kill the server
		// with shutdown signal (CTRL+C)
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("\nShutting server down..");
				ct.shutdown();
			}
		});
	}

	public static void printHelp() {
		System.out.println(">> IceNLP Slave");
		System.out.println("Usage: java -jar IceNLPSlave.jar --sh=~/translation/apertium.sh");
		System.out.println("");
		System.out.println("Other parameters:");
		System.out.println("-host \t The hostname of the router.");
		System.out.println("-port \t The port of the router.");
	}
}
