package is.iclt.icenlp.client.runner;

import is.iclt.icenlp.client.network.ClientNetworkHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Runner {

    public static void printHelp() {
        System.out.println("IceNLPClient");
        System.out.println("\t --host|h= \t Connection host.");
        System.out.println("\t --port|p= \t Connection port.");
    }

    public static void main(String[] args) {
        String host = "localhost";
        String port = "1234";

        if (args.length > 0) {
            for (String arg : args) {
                if (arg.matches("(?i)--(port|p)=.+"))
                    port = arg.split("=")[1];
                else if (arg.matches("(?i)--(host|h)=.+"))
                    host = arg.split("=")[1];
                else {
                    printHelp();
                    return;
                }

            }
        }
        try {
            // Let's read from the std.
            String inLine;

            InputStreamReader reader = new InputStreamReader(System.in, "UTF8");
            BufferedReader br = new BufferedReader(reader);

            String inString = "";

            while ((inLine = br.readLine()) != null)
                inString += inLine + "\n";

            if (inString.length() >= 1)
                inString = inString.substring(0, inString.length() - 1);

            if (inString.length() == 0) {
                printHelp();
                return;
            }

            ClientNetworkHandler handler = new ClientNetworkHandler(host, port);
            
            // Fix for Apertium text processor. We must find a workaround for this problem.
            inString = inString.substring(0, inString.length()-6);
            //System.out.println("FOOO");
                        
            
            System.out.println(handler.tagString(inString));
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
    }
}