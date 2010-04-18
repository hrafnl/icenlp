package is.iclt.icenlp.common.apertium;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ApertiumWrapper {


    public static String translate(String scriptLocation, String text) throws IOException {
        String[] passCmd = {"/bin/bash", "-c", "echo '" + text + "' | sh " + scriptLocation};
        Runtime run = Runtime.getRuntime();
        Process pr = run.exec(passCmd);
        try {
            pr.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));

        String output = "";
        String line;

        while ((line = buf.readLine()) != null) {
            output += line + "\n";
        }

        if (output.endsWith("\n"))
            output = output.substring(0, output.length() - 1);

        return output;
    }
}
