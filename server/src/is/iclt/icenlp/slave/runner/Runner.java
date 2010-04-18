package is.iclt.icenlp.slave.runner;

import is.iclt.icenlp.common.configuration.Configuration;
import is.iclt.icenlp.slave.threads.SlaveConnectionThread;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Runner class for the slave.
 * @author hlynur
 */
public class Runner {
	private static String prefix = "[SlaveRunner]: ";
    public static void main(String[] args)
    {

        if(args.length >= 2){
            System.out.println(prefix + "error in parameters.");
            System.out.println(prefix + "stopping.");
            help();
            System.exit(0);
        }

        else if(args.length == 1){

            if(args[0].equals("--help")){
                help();
                System.exit(0);
            }

            String configFileLocation = args[0];
            File f = new File(configFileLocation);
            if(f.exists() && f.canRead()){
                System.out.println(prefix + "reading configuration file from: " + configFileLocation);
                Configuration.loadConfig(args[0]);
            }
            else{
                System.out.println(prefix + "unable to read configuration file from: " + configFileLocation);
                System.out.println(prefix + "stopping.");
                System.exit(0);
            }
        }

        else{
            File f = new File("slave.conf");
            System.out.println(prefix + "Reading default configuration file: " + f.getAbsolutePath());
            if(f.exists() && f.canRead())
                Configuration.loadConfig("slave.conf");
            else{
                System.out.println(prefix + "unable to read default configuration file: " + f.getAbsolutePath());
                System.out.println(prefix + "stopping.");
                System.exit(0);
            }
        }

        String scriptLocation = Configuration.getInstance().getValue("ApertiumRunScript");
		String routerHost = Configuration.getInstance().getValue("routerHost");
		String routerPort = Configuration.getInstance().getValue("routerPort");


        // Lets check out if the script exists and is executable.
		File f = new File(scriptLocation);
		if (!f.exists()) {
			System.out.println("[SlaveRunner]: File " + scriptLocation + " does not exists.");
			return;
		}

		if (!f.canExecute()) {
			System.out.println("[SlaveRunner]: File " + scriptLocation + " is not executiable.");
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
				scriptLocation);
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

	public static void help() {
		System.out.println("parameters:");
		System.out.println("[configuration file]      Path to configuration file to load.");
		System.out.println("--help                    Prints this menu.");
	}
}
