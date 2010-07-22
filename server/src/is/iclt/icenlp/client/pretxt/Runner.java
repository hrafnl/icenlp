package is.iclt.icenlp.client.pretxt;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Runner {
	public static void main(String[] args) {
		try{
			String inLine;
			InputStreamReader reader = new InputStreamReader(System.in, "UTF8");
			BufferedReader br = new BufferedReader(reader);
			
			List<String> lines = new LinkedList<String>();
			while ((inLine = br.readLine()) != null)
				lines.add(inLine.trim());
		
			for(String s : lines)
			{
				String[] cmd = {
						"/bin/sh",
						"-c",
						"echo \"" + s + "\" | /usr/local/bin/apertium-destxt"
						};

	
	    		Scanner sc = new Scanner(Runtime.getRuntime().exec(cmd).getInputStream());    		
	    		String l = "";
	    		
	    		while (sc.hasNext()){
	    			l += sc.next() + " ";
	    			
	    		}
	    
	    		System.out.println(l.replace(".[][ ]" , ""));
	    		
			}
			//System.out.println(aperttxts.size()); 
			
		}
		catch (Exception e) 
		{
			System.out.println("error" + e.getMessage());
		}
	}
}
