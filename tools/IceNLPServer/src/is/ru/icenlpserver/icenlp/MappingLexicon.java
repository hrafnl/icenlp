package is.ru.icenlpserver.icenlp;

import is.ru.icenlpserver.common.Pair;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MappingLexicon 
{
	// Hash map for direct tags mappings.
	private HashMap<String, String> tagMaps;

	// Hash map for exceptions.
	private HashMap<String,  List<Pair<String, String>>> lemmaExceptionMap;
	
	// Hash map for lexeme exceptions.
	private HashMap<String, String> MWEMap;
	
	// Hash map for MWE
	private HashMap<String, List<Pair<String, String>>> lexemeExceptionMap;
	
	
	public MappingLexicon(String mapperFile) throws IOException 
	{	
		// Let's initialize the maps.
		this.tagMaps = new HashMap<String, String>();
		this.lemmaExceptionMap = new HashMap<String, List<Pair<String, String>>>();
		this.lexemeExceptionMap = new HashMap<String, List<Pair<String, String>>>();
		this.MWEMap = new HashMap<String, String>();
		
		// test for MWE.
		//this.MWEMap.put("að_fjalla_um", "<MAJOR_TEST>");

		// Let's read in the mapperFile
		FileInputStream fstream = new FileInputStream(mapperFile);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		int lineNum = 1;
		
		while ((strLine = br.readLine()) != null) 
		{
			if(strLine.length() != 0)
				{
				try
				{
					// We found a exception rule in the lexicon file.
					if(strLine.toLowerCase().startsWith("lemma=") || strLine.toLowerCase().startsWith("lexeme=") || 
							strLine.toLowerCase().startsWith("mwe="))
					{
						if(strLine.matches("LEMMA=[^\t]+\t[^\t]+\t[^\t]+"))
						{
							String[] str = strLine.substring(6).split("\t");
							if(!this.lemmaExceptionMap.containsKey(str[0]))
							{
								List< Pair<String, String> > emptyPairList = new LinkedList< Pair<String, String> >();
								this.lemmaExceptionMap.put(str[0], emptyPairList);
							}
							Pair<String, String> pair = new Pair<String, String>(str[1], str[2]);
							this.lemmaExceptionMap.get(str[0]).add(pair);
						}
						
						else if(strLine.matches("LEXEME=[^\t]+\t[^\t]+\t[^\t]+"))
						{	
							String[] str = strLine.substring(6).split("\t");
							if(!this.lexemeExceptionMap.containsKey(str[0]))
							{
								List< Pair<String, String> > emptyPairList = new LinkedList< Pair<String, String> >();
								this.lexemeExceptionMap.put(str[0], emptyPairList);
							}
							Pair<String, String> pair = new Pair<String, String>(str[1], str[2]);
							this.lexemeExceptionMap.get(str[0]).add(pair);
						}
						
						// If the line contains a MWE rule.
						else if(strLine.matches("MWE=[^\t]+\t[^\t]+"))
						{
							String[] str = strLine.substring(4).split("\t");
							this.MWEMap.put(str[0], str[1]);
						}
						
						else
						{
							System.out.println("[x] Error in exception rule in mapping file " + mapperFile + " on line " + lineNum);
							System.out.println("Rule was: " + strLine);
						}
					}
					else
					{
						if(strLine.matches("[^\t]+\t[^\t]+"))
						{
							String key = strLine.split("\t")[0];
							String value = strLine.split("\t")[1];
							this.tagMaps.put(key, value);
						}
						else
						{
							System.out.println("[x] Error in mapping rule in mapping file " + mapperFile + " on ine " + lineNum);
						}
					}
					
				}
				catch (Exception e) 
				{
					System.out.println(e);
				}
			}
			lineNum +=1;
		}
	
		// Print out number of rules read.
		System.out.println("[i] Number of mapping rules: " + this.tagMaps.size());
		System.out.println("[i] Number of lexeme exception rules: " + this.lexemeExceptionMap.size());
		System.out.println("[i] Number of lemma exception rules: " + this.lemmaExceptionMap.size());
		System.out.println("[i] Number of MWE rules: " + this.MWEMap.size());
	}

	public String lookupTagmap(String tag, boolean ignoreCase) 
	{
		if(ignoreCase)
			tag = tag.toLowerCase();
		
		if(this.tagMaps.containsKey(tag))
			return this.tagMaps.get(tag);
		
		return null;
	}
	
	public List<Pair<String, String>> getExceptionRulesForLemma(String lemma)
	{
		if(!this.lemmaExceptionMap.containsKey(lemma))
			return null;
		
		return this.lemmaExceptionMap.get(lemma);
	}
		
	public boolean hasExceptionRulesForLemma(String lexeme)
	{
		return this.lemmaExceptionMap.containsKey(lexeme);
	}
	
	public boolean hasExceptionRulesForLexeme(String lexeme)
	{
		return this.lexemeExceptionMap.containsKey(lexeme);
	}
	
	public List<Pair<String, String>> getExceptionRulesForLexeme(String lexeme)
	{
		if(!this.lexemeExceptionMap.containsKey(lexeme))
			return null;
		
		return this.lexemeExceptionMap.get(lexeme);
	}
	
	public boolean hasMapForMWE(String mweString)
	{
		return this.MWEMap.containsKey(mweString);
	}
	
	public String getMapForMWE(String mweString)
	{
		return this.MWEMap.get(mweString);
	}
	
	
	
	
	
}
