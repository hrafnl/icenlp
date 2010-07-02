package is.iclt.icenlp.aperitum.destxt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Runner {

	public static void main(String[] args) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String str = "";
			str = in.readLine();
			while (str != null) {
				System.out.println(processLine(str));
				str = in.readLine();	
			}
		} 
		catch (IOException e) {
			System.out.println("error> " +e.getMessage());
			System.exit(0);
		}
	}
	
	public static String processLine(String line) {
		line = line.replace("^</<$", "\\<");
		line = line.replace("^>/>$", "\\>");
		line = line.replace("^{/{$", "\\{");
		line = line.replace("^}/}$", "\\}");
		line = line.replace("^[/[$", "\\[");
		line = line.replace("^]/]$", "\\]");
		line = line.replace(",/,<cm>", ",<cm>");
		line = line.replace("./.<sent>", ".<sent>");
		line = line.replace("?/?<sent>", "?<sent>");
		line = line.replace("!/!<sent>", "!<sent>");
		line = line.replace("^(/($", "(");
		line = line.replace("^)/)$", ")");
		//line = line.replace("$", "");
		
		
		return line;
	}
}
