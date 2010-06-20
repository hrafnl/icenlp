package is.iclt.icenlp.client.runner;

import is.iclt.icenlp.client.network.ClientNetworkHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

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
            
        	ClientNetworkHandler handler = new ClientNetworkHandler(host, port);

            String inLine;

            InputStreamReader reader = new InputStreamReader(System.in, "UTF8");
            BufferedReader br = new BufferedReader(reader);

            List<String> strsin = new LinkedList<String>();

            
            while ((inLine = br.readLine()) != null){
            	if(!inLine.equals("]")){
            		strsin.add(inLine);
            		
            	}
            	
            }
            
            for(String s : strsin){
            	s = s.replace(".[][\n]", "");
            	System.out.print(handler.tagString(s));

            	
            }
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
    }
}