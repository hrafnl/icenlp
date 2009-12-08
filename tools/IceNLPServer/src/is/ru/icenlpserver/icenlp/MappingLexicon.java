package is.ru.icenlpserver.icenlp;

import is.ru.icenlpserver.common.Pair;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MappingLexicon 
{
	// Enumeration for block sections in the mapping file.
	public enum BLOCK_TYPE {not_set, tagmaps, mwe, tagmapping, lemma, mwe_rename}
	
	
	// Hash map for direct tags mappings.
	private HashMap<String, String> tagMaps;

	// Hash map for exceptions.
	private HashMap<String,  List<Pair<String, String>>> lemmaExceptionMap;
	
	// Hash map for lexeme exceptions.
	private HashMap<String, String> MWEMap;
	
	// Hash map for MWE
	private HashMap<String, List<Pair<String, String>>> lexemeExceptionMap;
	
	// Hash map for mwe rename rules.
	private HashMap<String, Pair<String, String>> mweRenameMap;
	
	
	public MappingLexicon(String mapperFile) throws Exception 
	{	

		// Let's initialize the maps.
		
		
		// This map is used for the tag mapping.
		this.tagMaps = new HashMap<String, String>();
		this.lemmaExceptionMap = new HashMap<String, List<Pair<String, String>>>();
		this.lexemeExceptionMap = new HashMap<String, List<Pair<String, String>>>();
		this.MWEMap = new HashMap<String, String>();
		this.mweRenameMap = new HashMap<String, Pair<String, String>>();
		
		// test for MWE.
		//this.MWEMap.put("að_fjalla_um", "<MAJOR_TEST>");

		// Let's read in the mapperFile
		FileInputStream fstream = new FileInputStream(mapperFile);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		int lineNum = 1;
		
		//String entryType = null;
		BLOCK_TYPE type = BLOCK_TYPE.not_set;
		
		while ((strLine = br.readLine()) != null) 
		{
			
			if(strLine.length() != 0 && !strLine.startsWith("#"))
			{
				if(type == BLOCK_TYPE.not_set && !strLine.matches("\\[[a-zA-Z]+\\]"))
				{
					System.out.println(">> Error in config file, line: " + lineNum + ". Entry is not under any section.");
					System.err.println(strLine + ": " + strLine);
					break;
				}
				
				// are we getting a new block? 
				else if(strLine.matches("\\[[a-zA-Z-]+\\]"))
				{
					String entryType = strLine.substring(1, strLine.length()-1);
					
					if(entryType.toLowerCase().equals("tagmapping"))
						type = BLOCK_TYPE.tagmapping;
					
					else if(entryType.toLowerCase().equals("lemma"))
						type = BLOCK_TYPE.lemma;	
					
					else if(entryType.toLowerCase().equals("mwe"))
						type = BLOCK_TYPE.mwe;	
					
					else if(entryType.toLowerCase().equals("mwe-rename"))
						type = BLOCK_TYPE.mwe_rename;
					
					else
					{
						System.out.println(">> Error in config file, unknow block name " + entryType);
						break;
					}
				}
				
				else
				{
					switch (type) 
					{
						case tagmapping:
							if(strLine.matches("[^@]+@[^@]+"))
							{
								String key = strLine.split("@")[0];
								String value = strLine.split("@")[1];
								this.tagMaps.put(key, value);
								break;
							}
							else
							{
								System.out.println("[!!!]: Error in config at line " + lineNum + ". Invalid tagmapping.");
								System.out.println(lineNum + ": " + strLine);
								break;
							}


						case lemma:
							if(strLine.matches("[^@]+@[^@]+@[^@]+"))
							{	
								String[] str = strLine.split("@");
								if(!this.lemmaExceptionMap.containsKey(str[0]))
								{
									List< Pair<String, String> > emptyPairList = new LinkedList< Pair<String, String> >();
									this.lemmaExceptionMap.put(str[0], emptyPairList);
								}
								Pair<String, String> pair = new Pair<String, String>(str[1], str[2]);
								this.lemmaExceptionMap.get(str[0]).add(pair);
								break;
							}
							else
							{
								System.out.println("[!!!]: Error in config at line " + lineNum + ". Invalid lemma entry.");
								System.out.println(lineNum + ": " + strLine);
								break;	
							}
						
						case mwe:
							if(strLine.matches("[^@]+@[^@]+"))
							{
								String[] str = strLine.split("@");
								this.MWEMap.put(str[0], str[1]);
								break;
							}
							
							else
							{
								System.out.println("[!!!]: Error in config at line " + lineNum + ". Invalid MWE entry.");
								System.out.println(lineNum + ": " + strLine);
								break;
							}
						
						case mwe_rename:
							if(strLine.matches("[^@]+@[^@]+@[^@]+"))
							{
								String[] strings = strLine.split("@");
								this.mweRenameMap.put(strings[0], new Pair<String, String>(strings[1], strings[2]));
								break;
							}
							else
							{
								System.out.println("[!!!]: Error in config at line " + lineNum + ". Invalid MWE-RENAME entry.");
								System.out.println(lineNum + ": " + strLine);
								break;
							}
							

						default:
							break;
					}
				}
			}
			
			// Increase the line counter by one.
			lineNum +=1;
		}
	
		// Print out number of rules read.
		System.out.println("[i] Number of mapping rules: " + this.tagMaps.size());
		System.out.println("[i] Number of lexeme exception rules: " + this.lexemeExceptionMap.size());
		System.out.println("[i] Number of lemma exception rules: " + this.lemmaExceptionMap.size());
		System.out.println("[i] Number of MWE rules: " + this.MWEMap.size());
		System.out.println("[i] Number of MWE rename rules: " + this.mweRenameMap.size());

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
	
	
	
	/***
	 * Check if a given lexeme contains a Lexeme rename rule.
	 * This is used to rename multi word expressions.
	 * @param lexeme
	 * @return true if the lexeme has a rename rule, false otherwise.
	 */
	public boolean hasRenameRuleForLexeme(String lexeme)
	{
		return this.mweRenameMap.containsKey(lexeme);
	}
	
	/***
	 * Get the rename rule for a given lexeme.
	 * @param lexeme
	 * @return Pair<String, String> that contains the new lexeme/lemma and
	 * the new tag.
	 */
	public Pair<String, String> getRenameRuleForLexeme(String lexeme)
	{
		return this.mweRenameMap.get(lexeme);
	}
}
