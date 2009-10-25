package is.ru.icecache.icenlp;

import is.ru.icecache.common.Pair;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MapperLexicon 
{
	// Hash map for direct tags mappings.
	private HashMap<String, String> tagMaps;

	// Hash map for exceptions.
	private HashMap<String,  List<Pair<String, String>>> expcetionMaps;
	
	public MapperLexicon(String mapperFile) throws IOException 
	{
		
		// Let's initialize the maps.
		this.tagMaps = new HashMap<String, String>();
		this.expcetionMaps = new HashMap<String, List<Pair<String, String>>>();

		// Let's read in the mapperFile
		FileInputStream fstream = new FileInputStream(mapperFile);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;

		while ((strLine = br.readLine()) != null) 
		{
			try
			{
				// We found a exception rule in the lexicon file.
				if(strLine.toLowerCase().startsWith("lemma="))
				{
					String[] str = strLine.substring(6).split("\t");
					if(!this.expcetionMaps.containsKey(str[0]))
					{
						List< Pair<String, String> > emptyPairList = new LinkedList< Pair<String, String> >();
						this.expcetionMaps.put(str[0], emptyPairList);
					}
					Pair<String, String> pair = new Pair<String, String>(str[1], str[2]);
					this.expcetionMaps.get(str[0]).add(pair);
				
				}
				else
				{
					String key = strLine.split("\t")[0];
					String value = strLine.split("\t")[1];
					this.tagMaps.put(key, value);
				}
			}
			catch (Exception e) 
			{
				System.out.println(e);
			}
		}
	}

	public String lookupTagmap(String tag, boolean ignoreCase) 
	{
		if(ignoreCase)
			tag = tag.toLowerCase();
		
		if(this.tagMaps.containsKey(tag))
			return this.tagMaps.get(tag);
		
		return null;
	}
	
	
	
	public List<Pair<String, String>> getExceptionRuleForLexeme(String lexeme)
	{
		if(!this.expcetionMaps.containsKey(lexeme))
			return null;
		
		return this.expcetionMaps.get(lexeme);
	}
	
	
	public boolean hasExceptionRuleForLemma(String lexeme)
	{
		return this.expcetionMaps.containsKey(lexeme);
	}
	
	public boolean hasMapForLexeme(String lexeme)
	{
		return this.tagMaps.containsKey(lexeme);
	}
}
