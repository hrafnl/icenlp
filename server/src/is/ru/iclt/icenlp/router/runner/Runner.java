package is.ru.iclt.icenlp.router.runner;

import is.ru.iclt.icenlp.router.threads.RequestListneningThread;
import is.ru.iclt.icenlp.router.threads.RequestListneningThreadException;
import is.ru.iclt.icenlp.router.threads.SlaveListeningThread;
import is.ru.iclt.icenlp.router.threads.SlaveListeningThreadException;

/**
 * Runner class for the router.
 * @author hlynur
 */
public class Runner {
	public static String prefix = "[RouterRunner]: ";

	public static void main(String[] args) {
		System.out.println();
		System.out.println(">> IceNLP Router");

		// The hostname for the router server.
		// default hostname is "localhost".
		String hostname = "localhost";

		// The port that the server listens to for
		// incoming slaves. Default port is 2525.
		String slavePort = "2525";

		// The port that the server listens to for
		// incoming translation requests. Default port
		// is 2526.
		String requestport = "2526";

		// let's look through the arguments that
		// are passed by the user.
		for (String arg : args) {
			// argument check for --slaveport (-s)
			if (arg.matches("-slaveport=[0-9]+|-s=[0-9]+")) {
				if (arg.startsWith("-slaveport="))
					arg = arg.replace("-slaveport=", "");
				else
					arg = arg.replace("-s=", "");

				slavePort = arg;
			}

			// argument check for --request-port (-r)
			else if (arg.matches("-request-port=[0-9]+|-r=[0-9]+")) {
				if (arg.startsWith("-request-port="))
					arg = arg.replace("-request-port=", "");
				else
					arg = arg.replace("-r=", "");

				requestport = arg;
			}

			// argument check for --host (-h)
			else if (arg.matches("-host=[a-zA-Z0-9.]+|-h=[a-zA-Z0-9.]+")) {
				if (arg.startsWith("-host="))
					arg = arg.replace("-host=", "");
				else
					arg = arg.replace("-h=", "");

				hostname = arg;
			} else if (arg.matches("-help")) {
				help();
				return;
			}

			else {
				help();
				System.out.println(prefix + "unknown parameter " + arg);
				return;
			}
		}

		// Let's display all the settings.
		System.out.println(prefix + "hostname set to " + hostname);

		// Let's start the slave listening thread.
		SlaveListeningThread slaveListeningThread = null;
		try {
			slaveListeningThread = new SlaveListeningThread(hostname, slavePort);
		} catch (SlaveListeningThreadException e) {
			System.out.println(prefix
					+ "unable to start Slave Listening thread: "
					+ e.getLocalizedMessage() + ". Shutting down.");
			return;
		}

		new Thread(slaveListeningThread).start();

		// Let's start the request listening thread.
		RequestListneningThread requestThread = null;
		try {
			requestThread = new RequestListneningThread(hostname, requestport);
		} catch (RequestListneningThreadException e) {
			System.out.println(prefix
					+ "unable to start Request Listening thread: "
					+ e.getLocalizedMessage() + ". Shutting down.");
			return;
		}

		new Thread(requestThread).start();
	}

	public static void help() {
		System.out.println("parameters:");
		System.out.println("-slaveport|-s         The port that the incoming slaves connects to.");
		System.out.println("-request-port|-r      The port that clients use to make translation requests.");
		System.out.println("-host|-h              Hostname for the server. Used to bind the socket.");
		System.out.println("-help                 Prints this menu.");
	}
}
