package is.iclt.icenlp.core.utils;

import is.iclt.icenlp.core.tokenizer.Token.MWECode;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * MappingLexicon reads a mapping file that contains tag-mapping rules, lemma
 * exception rules lexeme exception rules, MWE rules and rename-MWE rules. These
 * rules can then be applied to a collection of word objects.
 * 
 * @author hlynurs
 */
public class MappingLexicon {
	// Enumeration for block sections in the mapping file.
	public enum BLOCK_TYPE {
		not_set, tagmaps, mwe, tagmapping, lemma, mwe_rename, lexeme, lexemepattern
	}

	// Hash map for direct tags mappings.
	private HashMap<String, String> tagMaps;
	
	// Hash map for inverted tag mappings
	private HashMap<String, String> invertedTagMaps;

	// Hash map for exceptions.
	private HashMap<String, List<Pair<String, String>>> lemmaExceptionRuleMap;

	// Hash map for lexeme exceptions.
	private HashMap<String, String> mweRuleMap;

	// Hash map for MWE
	private HashMap<String, List<Pair<String, String>>> lexemeExceptionRuleMap;

	// Hash map for MWE rename rules.
	private HashMap<String, Pair<String, String>> mweRenameRuleMap;

	// Hash map for Lexeme-pattern tag rules.
	private HashMap<String, String> lexemePatternMap;

	// Boolean flag that is used to decide whether tags that do not have
	// any mapping are left unchanged or are marked with "notFoundMappingTag".
	private boolean leaveNotFoundTagUnchanged;

	// Boolean flag to controls whether we output what are rules are applied.
	// used in debug mode.
	private boolean showAppliedActions;
	
	// Inverse the tag mapping
	private boolean useInverseMapping = false;

	// String that contains the tag that is used when no mappings are found
	// for a given tag.
	private String notFoundMappingTag;

	// Boolan variable that is used when a lexeme of length one has no
	// tagmapping.
	// If this variable is set then the lexeme will be returned without adding
	// tag, etc
	// with that word in the output.
	private boolean leave_lexemes_of_length_one_unchanged = false;

	protected MappingLexicon(boolean leaveNotFoundTagUnchanged,
			boolean showAppliedActions, String notFoundMappingTag) {
		this.tagMaps = new HashMap<String, String>();
		this.invertedTagMaps = new HashMap<String, String>();
		this.lemmaExceptionRuleMap = new HashMap<String, List<Pair<String, String>>>();
		this.lexemeExceptionRuleMap = new HashMap<String, List<Pair<String, String>>>();
		this.mweRuleMap = new HashMap<String, String>();
		this.mweRenameRuleMap = new HashMap<String, Pair<String, String>>();
		this.lexemePatternMap = new HashMap<String, String>();
		this.leaveNotFoundTagUnchanged = leaveNotFoundTagUnchanged;
		this.showAppliedActions = showAppliedActions;
		this.notFoundMappingTag = notFoundMappingTag;
	}

	/**
	 * Constructor for the MapperLexicon class. This constructor initializes all
	 * the member variables and sets the to the values that are passed via the
	 * constructor. Then the mapping file is read and rules are added to the
	 * rule maps that are kept in memory.
	 * 
	 * @param mappingFile
	 *            A full path to the mapping file.
	 * @param showLexiconStatusOutput
	 *            Flag to controls whether we print overview of the rules that
	 *            were read from the mapping file.
	 * @param leaveNotFoundTagUnchanged
	 *            Flag to controls whether tag that do not have any mappings are
	 *            left unchanged or changed to notFoundMappingTag.
	 * @param showAppliedActions
	 *            Flag to controls whether we print applied rules to standard
	 *            output.
	 * @param notFoundMappingTag
	 *            The mapping tag that is used if leaveNotFoundTagUnchanged is
	 *            false and a given tag does not have any mapping tag.
	 * @throws Exception
	 *             If mapping file is not found.
	 */
	public MappingLexicon(String mappingFile, boolean showLexiconStatusOutput,
			boolean leaveNotFoundTagUnchanged, boolean showAppliedActions,
			String notFoundMappingTag) throws Exception {
		this(leaveNotFoundTagUnchanged, showAppliedActions, notFoundMappingTag);

		FileInputStream fstream = new FileInputStream(mappingFile);
		readConfigFile(fstream);
	}
	
	/**
	 * Constructor for the MapperLexicon class. This constructor initializes all
	 * the member variables and sets the to the values that are passed via the
	 * constructor. Then the mapping file is read and rules are added to the
	 * rule maps that are kept in memory.
	 * 
	 * @param mappingFile
	 *            A full path to the mapping file.
	 * @param showLexiconStatusOutput
	 *            Flag to controls whether we print overview of the rules that
	 *            were read from the mapping file.
	 * @param leaveNotFoundTagUnchanged
	 *            Flag to controls whether tag that do not have any mappings are
	 *            left unchanged or changed to notFoundMappingTag.
	 * @param showAppliedActions
	 *            Flag to controls whether we print applied rules to standard
	 *            output.
	 * @param notFoundMappingTag
	 *            The mapping tag that is used if leaveNotFoundTagUnchanged is
	 *            false and a given tag does not have any mapping tag.
	 * @param inverseMapping
	 * 			  Flag to tell the mapper to inverse the tagMap
	 * @throws Exception
	 *             If mapping file is not found.
	 */
	public MappingLexicon(String mappingFile, boolean showLexiconStatusOutput,
			boolean leaveNotFoundTagUnchanged, boolean showAppliedActions,
			String notFoundMappingTag, boolean inverseMapping) throws Exception {
		this(leaveNotFoundTagUnchanged, showAppliedActions, notFoundMappingTag);
		useInverseMapping = inverseMapping;
		FileInputStream fstream = new FileInputStream(mappingFile);
		readConfigFile(fstream);
	}

	/**
	 * Constructor for the MapperLexicon class. This constructor initializes all
	 * the member variables and sets the to the values that are passed via the
	 * constructor. With this constructor the mapping file is read from the
	 * IceNLPCore jar file.
	 * 
	 * @param showLexiconStatusOutput
	 *            Flag to controls whether we print overview of the rules that
	 *            were read from the mapping file.
	 * @param leaveNotFoundTagUnchanged
	 *            Flag to controls whether tag that do not have any mappings are
	 *            left unchanged or changed to notFoundMappingTag.
	 * @param showAppliedActions
	 *            Flag to controls whether we print applied rules to standard
	 *            output.
	 * @param notFoundMappingTag
	 *            The mapping tag that is used if leaveNotFoundTagUnchanged is
	 *            false and a given tag does not have any mapping tag.
	 * @throws Exception
	 *             If mapping file is not found.
	 */
	public MappingLexicon(boolean showLexiconStatusOutput,
			boolean leaveNotFoundTagUnchanged, boolean showAppliedActions,
			String notFoundMappingTag) throws Exception {
		this(leaveNotFoundTagUnchanged, showAppliedActions, notFoundMappingTag);

		InputStream fstream = getClass().getResourceAsStream(
				"/dict/icetagger/otb.apertium.dict");
		readConfigFile(fstream);
	}

	private void showStatusOutput() {
		System.out.println("[i] Number of mapping rules: "
				+ this.tagMaps.size());
		System.out.println("[i] Number of lexeme exception rules: "
				+ this.lexemeExceptionRuleMap.size());
		System.out.println("[i] Number of lemma exception rules: "
				+ this.lemmaExceptionRuleMap.size());
		System.out.println("[i] Number of lexeme-patterns rules: "
				+ this.lexemePatternMap.size());
		System.out
				.println("[i] Number of MWE rules: " + this.mweRuleMap.size());
		System.out.println("[i] Number of MWE rename rules: "
				+ this.mweRenameRuleMap.size());
	}

	/**
	 * Reads the rules from the mapping file.
	 * 
	 * @param mappingFile
	 *            A full path to the mapping file.
	 * @throws FileNotFoundException
	 *             If the mapping file cannot be found.
	 * @throws IOException
	 *             If the mapping file cannot be read.
	 */
	private void readConfigFile(InputStream fstream)
			throws FileNotFoundException, IOException {
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine = null;
		int lineNum = 1;
		BLOCK_TYPE type = BLOCK_TYPE.not_set;

		while ((strLine = br.readLine()) != null) {
			if (strLine.length() != 0 && !strLine.startsWith("#")) {
				if (type == BLOCK_TYPE.not_set
						&& !strLine.matches("\\[[a-zA-Z-]+\\]")) {
					System.out.println(">> Error in config file, line: "
							+ lineNum + ". Entry is not under any section.");
					System.err.println(strLine + ": " + strLine);
					break;
				}

				// are we getting a new block?
				else if (strLine.matches("\\[[a-zA-Z-]+\\]")) {
					String entryType = strLine.substring(1,
							strLine.length() - 1);

					if (entryType.toLowerCase().equals("tagmapping"))
						type = BLOCK_TYPE.tagmapping;

					else if (entryType.toLowerCase().equals("lemma"))
						type = BLOCK_TYPE.lemma;

					else if (entryType.toLowerCase().equals("lexeme"))
						type = BLOCK_TYPE.lexeme;

					else if (entryType.toLowerCase().equals("mwe"))
						type = BLOCK_TYPE.mwe;

					else if (entryType.toLowerCase().equals("mwe-rename"))
						type = BLOCK_TYPE.mwe_rename;

					else if (entryType.toLowerCase().equals("lexeme-pattern"))
						type = BLOCK_TYPE.lexemepattern;

					else {
						System.out
								.println(">> Error in config file, unknow block name "
										+ entryType);
						break;
					}
				}

				else {
					switch (type) {
					case tagmapping:
						if (strLine.matches("\\S+\\s+\\S+"))
						{
							String key = strLine.split("\\s+")[0];
							String value = strLine.split("\\s+")[1];
							
							// Without inverse mapping
							if(useInverseMapping == false)
							{
								this.tagMaps.put(key, value);
							}
							// With inverse mapping
							else
							{
								// Add to the regular mapping
								this.tagMaps.put(key, value);
								
								// Add to the inverted map
								
								// There are a few duplicate values
								// Some logic to handle them.
								if(value.equals("<num>"))
								{
									this.invertedTagMaps.put(value, "ta");
								}
								else if(value.equals("<vblex><actv><imp><p2><sg>"))
								{
									this.invertedTagMaps.put(value, "sbg2en");
								}
								else if(value.equals("<vblex><actv><inf>"))
								{
									this.invertedTagMaps.put(value, "sng");
								}
								else
								{
									this.invertedTagMaps.put(value, key);
								}
							}
							
							break;
						} else {
							System.out
									.println("[!!!]: Error in mapping file at line "
											+ lineNum + ". Invalid tagmapping.");
							System.out.println(lineNum + ": " + strLine);
							break;
						}

					case lemma:
						if (strLine.matches("\\S+\\s+\\S+\\s+\\S+")) {
							String[] str = strLine.split("\\s+");
							if (!this.lemmaExceptionRuleMap.containsKey(str[0])) {
								List<Pair<String, String>> emptyPairList = new LinkedList<Pair<String, String>>();
								this.lemmaExceptionRuleMap.put(str[0],
										emptyPairList);
							}
							Pair<String, String> pair = new Pair<String, String>(
									str[1], str[2]);
							this.lemmaExceptionRuleMap.get(str[0]).add(pair);
							break;
						} else {
							System.out
									.println("[!!!]: Error in mapping file at line "
											+ lineNum
											+ ". Invalid lemma entry.");
							System.out.println(lineNum + ": " + strLine);
							break;
						}

					case lexeme:
						if (strLine.matches("\\S+\\s+\\S+\\s+\\S+")) {
							String[] str = strLine.split("\\s+");
							if (!this.lexemeExceptionRuleMap
									.containsKey(str[0])) {
								List<Pair<String, String>> emptyPairList = new LinkedList<Pair<String, String>>();
								this.lexemeExceptionRuleMap.put(str[0],
										emptyPairList);
							}
							Pair<String, String> pair = new Pair<String, String>(
									str[1], str[2]);
							this.lexemeExceptionRuleMap.get(str[0]).add(pair);
							break;
						} else {
							System.out
									.println("[!!!]: Error in config at line "
											+ lineNum
											+ ". Invalid lexeme entry.");
							System.out.println(lineNum + ": " + strLine);
							break;
						}

					case mwe:
						if (strLine.matches("\\S+\\s+\\S+")) {
							String[] str = strLine.split("\\s+");
							this.mweRuleMap.put(str[0], str[1]);
							break;
						}

						else {
							System.out
									.println("[!!!]: Error in config at line "
											+ lineNum + ". Invalid MWE entry.");
							System.out.println(lineNum + ": " + strLine);
							break;
						}

					case mwe_rename:
						if (strLine.matches("\\S+\\s+\\S+\\s+\\S+")) {
							String[] strings = strLine.split("\\s+");
							this.mweRenameRuleMap.put(strings[0],
									new Pair<String, String>(strings[1],
											strings[2]));
							break;
						} else {
							System.out
									.println("[!!!]: Error in config at line "
											+ lineNum
											+ ". Invalid MWE-RENAME entry.");
							System.out.println(lineNum + ": " + strLine);
							break;
						}

					case lexemepattern:
						if (strLine.matches("\\S+\\s+\\S+")) {
							String[] str = strLine.split("\\s+");
							this.lexemePatternMap.put(str[0], str[1]);
							break;
						}

						else {
							System.out
									.println("[!!!]: Error in config at line "
											+ lineNum + ". Invalid ");
							System.out.println(lineNum + ": " + strLine);
							break;
						}
					}
				}
			}
			lineNum += 1;
		}

		if (this.showAppliedActions)
			this.showStatusOutput();
	}

	/**
	 * Goes through the incoming word list and applies relevant rules.
	 * 
	 * @param wordList
	 *            List of word objects.
	 */
	public void processWordList(List<Word> wordList) {
		// let's go through the tag mapping and check if there is any TAGMAPPING
		// for that word.
		// If there are no tag mapping rules then we skip this part.

		// If there is no mapping tag found, and the word is a single char..
		// then we want to dump it out without modifying it...
		// we need a new option fo this: IF_NOT_FOUND

		if (this.tagMaps.size() > 0) {
			
			for (Word word : wordList) {
				//System.out.println(word.getLexeme() + " - " + word.getTag());
				String mappedTag = this.lookupTagmap(word.getTag(), false);
				if(word.getLexeme().equals(word.getTag()) && mappedTag == null){
					word.setOnlyOutputLexeme(true);
					word.setTag("");
					continue;
				}
				
				if (mappedTag != null) {
					if (this.showAppliedActions) 
						System.out.println("[debug] tagmapping rule applied: " + word.getTag() + " -> " + mappedTag + ", on " + word.getLexeme());
					word.setTag(mappedTag);
				}

				else 
				{
					if (!this.leaveNotFoundTagUnchanged) {
						if (this.showAppliedActions)
							System.out.println("[debug] no tagmapping rule found for " + word.getTag()+ " mapped to unknown.");
						word.setTag(this.notFoundMappingTag);
					} else {
						if (this.showAppliedActions)
							System.out.println("[debug] no tagmapping rule found for " + word.getTag()+ " tag left unchanged.");
					}
				}
			}
		}
		
		// Go over Lemma Exception rules.
		for (Word word : wordList) {
			String lookupWordLemma = word.getLemma();
			String lookupWordLexeme = word.getLexeme().toLowerCase();

			if (this.hasExceptionRulesForLemma(lookupWordLemma)) 
			{
				List<Pair<String, String>> rules = this.getExceptionRulesForLemma(lookupWordLemma);
				for (Pair<String, String> pair : rules) 
				{
					if (word.getTag().matches(".*" + pair.one + ".*")) {
						if (this.showAppliedActions)
							System.out.println("[debug] applied Lemma exception rule for the lemma "+ word.getLemma());

						word.setTag(word.getTag().replaceFirst(pair.one, pair.two));
					}
				}
			}
			if (!word.isOnlyOutputLexeme()
					&& this.hasExceptionRulesForLexeme(lookupWordLexeme)) {
				List<Pair<String, String>> rules = this
						.getExceptionRulesForLexeme(lookupWordLexeme);
				for (Pair<String, String> pair : rules) {
					if (word.getTag().matches(".*" + pair.one + ".*")) {
						if (this.showAppliedActions)
							System.out.println("[debug] applied Lexeme exception rule for the lexeme "+ word.getLexeme());

						word.setTag(word.getTag().replaceFirst(pair.one, pair.two));
					}
				}
			}
		}


		// Go over the MWE expression
		for (int i = 0; i < wordList.size(); i++) {
			if (wordList.get(i).mweCode == MWECode.begins) {
				// the index of the words begins at index begins:
				int begins = i;
				int ends = 0;

				String mweStr = wordList.get(i).getLexeme();
				int j = i;
				while (j < wordList.size()) {
					if (wordList.get(j).mweCode == MWECode.ends) {
						// The words ends at there.
						ends = j;
						i = j;
						break;
					}
					if (j + 1 < wordList.size())
						mweStr += "_" + wordList.get(j + 1).getLexeme();

					j += 1;
				}
				mweStr = mweStr.toLowerCase();
				if (this.hasMapForMWE(mweStr)) 
				{
					if (this.showAppliedActions)
						System.out.println("[debug] applied MWE rule for the mwe " + mweStr);

					String lexeme = "";
					
					// We keep the first word in the mwe.
					Word first_mwe_word = wordList.get(begins);
					
					// remove the old words from the list.
					for (i = (ends - begins); i >= 0; i--) {
						lexeme += wordList.get(begins).getLexeme() + " ";
						wordList.remove(begins);
					}
					
					// Where we are working with MWE, we overwrite the lemma
					// with the lexeme.
					// lexeme.length()-1 because of the additional space at then end
					Word w = new Word(lexeme.substring(0, lexeme.length() - 1), this.getMapForMWE(mweStr), MWECode.none, null, false);
					w.setLemma(lexeme.substring(0, lexeme.length() - 1));
					
					// We add the same prespace as the first word had.
					w.preSpace = first_mwe_word.preSpace;
					
					// add the new word which contains the mwe.
					wordList.add(begins, w);
				}
			}
		}

		// Go over MWE-RENAME rules.
		for (Word word : wordList) {
			if (!word.isOnlyOutputLexeme() && this.hasRenameRuleForLexeme(word.getLexeme())) {
				Pair<String, String> pair = this.getRenameRuleForLexeme(word.getLexeme());
				word.setLemma(pair.one.replace('_', ' '));
				word.setLexeme(pair.one.replace('_', ' '));
				word.setTag(pair.two);
				if (this.showAppliedActions)
					System.out.println("[debug] applied MWE-RENAME rule to word " + word.getLexeme());
			}
		}

		// Go over Lexeme-pattern rules.
		for (Word word : wordList) {
			for (String key : this.lexemePatternMap.keySet()) {
				if (!word.isOnlyOutputLexeme() && word.getLexeme().matches(key)) {
					word.setTag(this.lexemePatternMap.get(key));
					if (this.showAppliedActions)
						System.out.println("[debug] applied Lexeme-pattern rule to word "+ word.getLexeme());
				}
			}
		}
	}

	private String lookupTagmap(String tag, boolean ignoreCase) {
		if (ignoreCase)
			tag = tag.toLowerCase();

		return this.tagMaps.get(tag);
	}
	
	/**
	 * This is only used when the inverted tag map is in use
	 * This will return null if inverted tag map is not set
	 * 
	 * @param tag
	 * @return The inverse mapping
	 */
	public String getInvertedTagMap(String tag, String word)
	{
		if(!useInverseMapping)
		{
			return null;
		}
		
		// Remove the spaces from the word while checking since the exception list has it setup that way
		String fixedWord = word.replace(" ", "").toLowerCase();
		
		// We have one lemma exception rule
		if(hasExceptionRulesForLemma(fixedWord))
		{
			List<Pair<String, String>> ex = getExceptionRulesForLemma(fixedWord);
			
			String correct = ex.get(0).one;
			String replace = ex.get(0).two;
			
			return this.invertedTagMaps.get(tag.replaceAll(replace, correct));
		}
		
		// We have one lexeme exception rule
		if(hasExceptionRulesForLexeme(fixedWord))
		{
			List<Pair<String, String>> ex = getExceptionRulesForLexeme(fixedWord);
			
			String correct = ex.get(0).one;
			String replace = ex.get(0).two;
			
			return this.invertedTagMaps.get(tag.replaceAll(replace, correct));
		}
		
		// Sentance endings become the word itself
		if(tag.equals("<sent>"))
		{
			return word;
		}
		
		return this.invertedTagMaps.get(tag);
	}

	private List<Pair<String, String>> getExceptionRulesForLemma(String lemma) {
		if (!this.lemmaExceptionRuleMap.containsKey(lemma))
			return null;

		return this.lemmaExceptionRuleMap.get(lemma);
	}

	private boolean hasExceptionRulesForLemma(String lemma) {
		return this.lemmaExceptionRuleMap.containsKey(lemma);
	}

	private boolean hasExceptionRulesForLexeme(String lexeme) {
		return this.lexemeExceptionRuleMap.containsKey(lexeme);
	}

	private List<Pair<String, String>> getExceptionRulesForLexeme(String lexeme) {
		if (!this.lexemeExceptionRuleMap.containsKey(lexeme))
			return null;

		return this.lexemeExceptionRuleMap.get(lexeme);
	}

	private boolean hasMapForMWE(String mweString) {
		return this.mweRuleMap.containsKey(mweString);
	}

	private String getMapForMWE(String mweString) {
		return this.mweRuleMap.get(mweString);
	}

	private boolean hasRenameRuleForLexeme(String lexeme) {
		return this.mweRenameRuleMap.containsKey(lexeme);
	}

	private Pair<String, String> getRenameRuleForLexeme(String lexeme) {
		return this.mweRenameRuleMap.get(lexeme);
	}

	public boolean isLeave_lexemes_of_length_one_unchanged() {
		return leave_lexemes_of_length_one_unchanged;
	}

	public void setLeave_lexemes_of_length_one_unchanged(
			boolean leaveLexemesOfLengthOneUnchanged) {
		leave_lexemes_of_length_one_unchanged = leaveLexemesOfLengthOneUnchanged;
	}
}
