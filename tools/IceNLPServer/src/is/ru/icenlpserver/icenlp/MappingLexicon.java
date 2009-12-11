package is.ru.icenlpserver.icenlp;

import is.ru.icenlpserver.common.Pair;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MappingLexicon 
{
	// Enumeration for block sections in the mapping file.
	public enum BLOCK_TYPE {not_set, tagmaps, mwe, tagmapping, lemma, mwe_rename, lexeme, test}
	
	// Hash map for direct tags mappings.
	private HashMap<String, String> tagMaps;

	// Hash map for exceptions.
	private HashMap<String,  List<Pair<String, String>>> lemmaExceptionRuleMap;
	
	// Hash map for lexeme exceptions.
	private HashMap<String, String> mweRuleMap;
	
	// Hash map for MWE
	private HashMap<String, List<Pair<String, String>>> lexemeExceptionRuleMap;
	
	// Hash map for MWE rename rules.
	private HashMap<String, Pair<String, String>> mweRenameRuleMap;
	
	
	/***
	 * Constructor for MappingLexicon.
	 * @param mapperFile Location to a mapping file that the class
	 * reads from.
	 * @throws Exception
	 */
	public MappingLexicon(String mapperFile) throws Exception 
	{	
		// Initialize the maps.
		this.tagMaps = new HashMap<String, String>();
		this.lemmaExceptionRuleMap = new HashMap<String, List<Pair<String, String>>>();
		this.lexemeExceptionRuleMap = new HashMap<String, List<Pair<String, String>>>();
		this.mweRuleMap = new HashMap<String, String>();
		this.mweRenameRuleMap = new HashMap<String, Pair<String, String>>();
		
		// Read in the mapperFile
		FileInputStream fstream = new FileInputStream(mapperFile);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine = null;
		int lineNum = 1;
		BLOCK_TYPE type = BLOCK_TYPE.not_set;
		
		while ((strLine = br.readLine()) != null) 
		{
			if(strLine.length() != 0 && !strLine.startsWith("#"))
			{
				if(type == BLOCK_TYPE.not_set && !strLine.matches("\\[[a-zA-Z-]+\\]"))
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

					
					else if(entryType.toLowerCase().equals("lexeme"))
						type = BLOCK_TYPE.lexeme;
	
					
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
							if(strLine.matches("\\S+\\s+\\S+"))
							{
								String key = strLine.split("\\s+")[0];
								String value = strLine.split("\\s+")[1];
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
							if(strLine.matches("\\S+\\s+\\S+\\s+\\S+"))
							{	
								String[] str = strLine.split("\\s+");
								if(!this.lemmaExceptionRuleMap.containsKey(str[0]))
								{
									List< Pair<String, String> > emptyPairList = new LinkedList< Pair<String, String> >();
									this.lemmaExceptionRuleMap.put(str[0], emptyPairList);
								}
								Pair<String, String> pair = new Pair<String, String>(str[1], str[2]);
								this.lemmaExceptionRuleMap.get(str[0]).add(pair);
								break;
							}
							else
							{
								System.out.println("[!!!]: Error in config at line " + lineNum + ". Invalid lemma entry.");
								System.out.println(lineNum + ": " + strLine);
								break;	
							}
						
						case lexeme:
							if(strLine.matches("\\S+\\s+\\S+\\s+\\S+"))
							{
								String[] str = strLine.split("\\s+");
								if(!this.lexemeExceptionRuleMap.containsKey(str[0]))
								{
									List< Pair<String, String> > emptyPairList = new LinkedList< Pair<String, String> >();
									this.lexemeExceptionRuleMap.put(str[0], emptyPairList);
								}
								Pair<String, String> pair = new Pair<String, String>(str[1], str[2]);
								this.lexemeExceptionRuleMap.get(str[0]).add(pair);
								break;
							}
							else
							{
								System.out.println("[!!!]: Error in config at line " + lineNum + ". Invalid lexeme entry.");
								System.out.println(lineNum + ": " + strLine);
								break;		
							}
						
	
						case mwe:
							if(strLine.matches("\\S+\\s+\\S+"))
							{
								String[] str = strLine.split("\\s+");
								this.mweRuleMap.put(str[0], str[1]);
								break;
							}
							
							else
							{
								System.out.println("[!!!]: Error in config at line " + lineNum + ". Invalid MWE entry.");
								System.out.println(lineNum + ": " + strLine);
								break;
							}
						
						case mwe_rename:
							if(strLine.matches("\\S+\\s+\\S+\\s+\\S+"))
							{
								String[] strings = strLine.split("\\s+");
								this.mweRenameRuleMap.put(strings[0], new Pair<String, String>(strings[1], strings[2]));
								break;
							}
							else
							{
								System.out.println("[!!!]: Error in config at line " + lineNum + ". Invalid MWE-RENAME entry.");
								System.out.println(lineNum + ": " + strLine);
								break;
							}
					}
				}
			}
			lineNum +=1;
		}
	
		// Print out number of rules read.
		System.out.println("[i] Number of mapping rules: " + this.tagMaps.size());
		System.out.println("[i] Number of lexeme exception rules: " + this.lexemeExceptionRuleMap.size());
		System.out.println("[i] Number of lemma exception rules: " + this.lemmaExceptionRuleMap.size());
		System.out.println("[i] Number of MWE rules: " + this.mweRuleMap.size());
		System.out.println("[i] Number of MWE rename rules: " + this.mweRenameRuleMap.size());
	}

	/**
	 * Returns the mapping tag for a given tag.
	 * @param tag that will be searched for.
	 * @param ignoreCase ignore the case of the incoming tag.
	 * @return a mapping for tag if found, null otherwise.
	 */
	public String lookupTagmap(String tag, boolean ignoreCase) 
	{
		if(ignoreCase)
			tag = tag.toLowerCase();
		
		if(this.tagMaps.containsKey(tag))
			return this.tagMaps.get(tag);
		
		return null;
	}
	
	/**
	 * Checks if there exist tag exception rules for a given lemma.
	 * @param lemma Lemma that will be used to look up exceptions.
	 * @return List of rule pairs, the first part is the tag that must appear in the tag
	 * string the other part is the tag that will be used to override the part that
	 * was found.
	 */
	public List<Pair<String, String>> getExceptionRulesForLemma(String lemma)
	{
		if(!this.lemmaExceptionRuleMap.containsKey(lemma))
			return null;
		
		return this.lemmaExceptionRuleMap.get(lemma);
	}
		
	/**
	 * Checks if there exist an exception rule for a given lemma.
	 * @param lexeme Lemma that will be searched for.
	 * @return True if there exist rules for the lemma, false otherwise.
	 */
	public boolean hasExceptionRulesForLemma(String lexeme)
	{
		return this.lemmaExceptionRuleMap.containsKey(lexeme);
	}
	
	public boolean hasExceptionRulesForLexeme(String lexeme)
	{
		return this.lexemeExceptionRuleMap.containsKey(lexeme);
	}
	
	public List<Pair<String, String>> getExceptionRulesForLexeme(String lexeme)
	{
		if(!this.lexemeExceptionRuleMap.containsKey(lexeme))
			return null;
		
		return this.lexemeExceptionRuleMap.get(lexeme);
	}
	
	public boolean hasMapForMWE(String mweString)
	{
		return this.mweRuleMap.containsKey(mweString);
	}
	
	public String getMapForMWE(String mweString)
	{
		return this.mweRuleMap.get(mweString);
	}
	
	/***
	 * Check if a given lexeme contains a Lexeme rename rule.
	 * This is used to rename multi word expressions.
	 * @param lexeme
	 * @return true if the lexeme has a rename rule, false otherwise.
	 */
	public boolean hasRenameRuleForLexeme(String lexeme)
	{
		return this.mweRenameRuleMap.containsKey(lexeme);
	}
	
	/***
	 * Get the rename rule for a given lexeme.
	 * @param lexeme
	 * @return Pair<String, String> that contains the new lexeme/lemma and
	 * the new tag.
	 */
	public Pair<String, String> getRenameRuleForLexeme(String lexeme)
	{
		return this.mweRenameRuleMap.get(lexeme);
	}
}
