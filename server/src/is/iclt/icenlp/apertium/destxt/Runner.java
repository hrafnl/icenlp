package is.iclt.icenlp.apertium.destxt;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		line = line.replace("^$/$$", "\\$");
		line = line.replace("^//$", "\\/");
		//line = line.replace(",/,<cm>", ",<cm>");
		//line = line.replace("./.<sent>", ".<sent>");
		//line = line.replace("?/?<sent>", "?<sent>");
		//line = line.replace("!/!<sent>", "!<sent>");
		line = line.replace("^[/[$", "\\[");
		line = line.replace("^]/]$", "\\]");
		line = line.replace("^{/{$", "\\{");
		line = line.replace("^}/}$", "\\}");
		Pattern pattern = Pattern.compile("(\\^./.[<[a-zA-Z0-9]+>]?\\$)");
		Matcher matcher = pattern.matcher(line);
		List<String> changes = new LinkedList<String>();
		while(matcher.find()){
			int groupcount = matcher.groupCount();
			for (int i = 1; i <= groupcount; i++) {
				String m = line.substring(matcher.start(i),matcher.end(i));
				String[] msplit = m.split("/");
				if(msplit[0].charAt(1) == msplit[1].charAt(0))
					changes.add(m);				
			}
		}
		for(String s : changes){
			line = line.replace(s, ""+ s.charAt(1));
		}
		return line;
	}
}
