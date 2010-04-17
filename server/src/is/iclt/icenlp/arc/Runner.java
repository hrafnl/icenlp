package is.iclt.icenlp.arc;

import is.iclt.icenlp.arc.network.NetworkException;
import is.iclt.icenlp.arc.network.NetworkHandler;
import java.awt.*;

public class Runner 
{
    public static void main(String[] args){
        /* Host, port and and text are initialized with the following
           default values. These values are used if they are not changed
           with parameters from std-in.*/
        String host = "localhost";
        String port = "2526";
        String text = "";

        // Read through the parameters from std-in.
		for(String arg : args)
		{
            if(arg.startsWith("--host="))
				host = arg.replace("--host=", "");

			else if(arg.startsWith("--port="))
				port = arg.replace("--port=", "");

			else if(arg.startsWith("--text="))
				text = arg.replace("--text=", "");

			else if(arg.equals("--help")){
				System.out.println("ARC - Apertium RouterClient");
				System.out.println();
				System.out.println("Available arguments:");
				System.out.println("  --host=[host]          Hostname of the server which is running the router.");
				System.out.println("  --port=[port]          Por number of the server which is running the router.");
				System.out.println("  --text=[text]          Text that will be translated.");
				System.out.println("  --help                 Prints this help menu.");
				return;
			}

			else{
				System.out.println("Unknown parameter '" + arg + "'");
				return;
			}
		}

        /* Create instance of the network handler and send the
           the text to the router and print the result from
           the translation to std-out. */
        try {
            NetworkHandler handler = new NetworkHandler(host, port);
            System.out.println(handler.translate(text));
            handler.closeConnection();
        } catch (NetworkException e) {
            System.out.println("Error: unable to communicate with router");
        }


    }
}
