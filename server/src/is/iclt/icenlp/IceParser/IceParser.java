package is.iclt.icenlp.IceParser;

import is.iclt.icenlp.common.configuration.Configuration;
import is.iclt.icenlp.core.utils.Word;
import is.iclt.icenlp.facade.IceParserFacade;

import java.io.IOException;
import java.util.List;


public class IceParser implements IIceParser{

    // Private static variable that contains the singleton
    // instance of this class.
    private static IceParser instance_ = null;

    // Private member variable that holds an instance of the
    // IceParser facade.
    private IceParserFacade parser;
    private boolean include_functions = true;
    private boolean phrase_per_line = true;
    private String mark_subject_left = "<@←SUBJ>";
    private String mark_subject_right = "<@SUBJ→>";
    
    private String mark_obj_left = "<@←OBJ>";
    private String mark_obj_right = "<@OBJ→>";
    private String parsedString;
	private String value_IceParserOutput = "";

    
    public synchronized static IceParser instance(){
        if(instance_ == null)
            instance_ = new IceParser();
        return instance_;
    }
    
    public String getParsedString() {
		return parsedString;
	}
    
    protected IceParser(){
        String value_mark_subject_left = Configuration.getInstance().getValue("mark_subject_left");
        if(value_mark_subject_left != null){
        	this.mark_subject_left = value_mark_subject_left;	
        }
        String value_mark_subject_right = Configuration.getInstance().getValue("mark_subject_right");
        if(value_mark_subject_right != null){
        	this.mark_subject_right = value_mark_subject_right;	
        }
        
        String value_mark_obj_right = Configuration.getInstance().getValue("mark_obj_right");
        if(value_mark_subject_right != null){
        	this.mark_obj_right = value_mark_obj_right;	
        }
        
        String value_mark_obj_left = Configuration.getInstance().getValue("mark_obj_left");
        if(value_mark_subject_right != null){
        	this.mark_obj_left = value_mark_obj_left;	
        }

        if (Configuration.getInstance().getValue("IceParserOutput").toLowerCase().equals("tcf")) {
			value_IceParserOutput = "tcf";
		} 
        else if (Configuration.getInstance().getValue("IceParserOutput").toLowerCase().equals("xml")) {
			value_IceParserOutput = "xml";
		} 
                
        System.out.println("[i] IceParser instance created.");
        parser = new IceParserFacade();
    }

    public String parse(String text) {
        try {
            return parser.parse(text, include_functions, "one_phrase_per_line");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

	public String parse(List<Word> words) {
		String taggedString = this.getTagStr(words);
		try {
            String strParse = this.parser.parse(taggedString, include_functions, value_IceParserOutput);
			int i = 0;
			int lastIndex = 0;
			for (Word w : words)
			{
				int index = strParse.indexOf(w.getLexeme(), lastIndex);
				lastIndex = index;
				
				// if IceParserOutput is set to true in the config
				// then we do not number the words (word_3)
				if (value_IceParserOutput.equals("tcf")||value_IceParserOutput.equals("xml"))
				{
					strParse = strParse.substring(0, index + w.getLexeme().length())+ " "+ strParse.substring(index + w.getLexeme().length() + 1);
				}
				else
				{
					strParse = strParse.substring(0, index + w.getLexeme().length())+ "_"+ i+ " "+ strParse.substring(index + w.getLexeme().length() + 1);
				}
				i = i + 1;
			}
			
			// checks the config to see if the user wants IceParserOutput
			// and sets the parsed string into an accessable variable
			if (value_IceParserOutput.equals("tcf")||value_IceParserOutput.equals("xml"))
			{
				// reset the parsedString before appending to it
				parsedString = "";


				// append strParse one line at a time into parsedString
				// and add \n after each line
				// add another \n after a line of dots
				for (String tmpString : strParse.split("\n"))
				{
					parsedString += tmpString + "\n";
				}
				parsedString.replaceAll(". .", ". .\n");

				return null;
			}

			// Let's add the subj to correct words.
			for (String parseLine : strParse.split("\n")) 
			{
				// System.out.println("parser line: " + parseLine);
				if (parseLine.contains("{*SUBJ")) 
				{
					char arrow = parseLine.charAt(6);
					String[] parseLineTokens = parseLine.split(" ");
					// Search for the last word in the subj, that is the one
					// that will get the subj to its tag.
					for (int j = parseLineTokens.length - 1; j >= 0; j--) 
					{
						if (parseLineTokens[j].split("_").length >= 2) {

							String[] d = parseLineTokens[j].split("_");
							String wordIndexStr = d[d.length - 1];
							if (wordIndexStr.matches("[0-9]+")) {
								int ind = Integer.parseInt(wordIndexStr);
								if (ind > words.size())
									continue;
								if (arrow == '>'){
									words.get(Integer.parseInt(d[d.length - 1])).parseString = this.mark_subject_right;
								}
								else{
									words.get(Integer.parseInt(d[d.length - 1])).parseString = mark_subject_left;
								}
								break;
							}
						}
					}
				}
				if (parseLine.contains("{*OBJ"))
				{
					char arrow = parseLine.charAt(5);
					String[] parseLineTokens = parseLine.split(" ");
					for (int j = parseLineTokens.length - 1; j >= 0; j--) 
					{
						if (parseLineTokens[j].split("_").length >= 2) {

							String[] d = parseLineTokens[j].split("_");
							String wordIndexStr = d[d.length - 1];
							if (wordIndexStr.matches("[0-9]+")) {
								int ind = Integer.parseInt(wordIndexStr);
								if (ind > words.size())
									continue;
								if (arrow == '>'){
									words.get(Integer.parseInt(d[d.length - 1])).parseString = this.mark_obj_right;
								}
								else{
									words.get(Integer.parseInt(d[d.length - 1])).parseString = this.mark_obj_left;
								}
								break;
							}
						}
					}
					
				}	
			}
		} 
		catch (IOException e) {

			e.printStackTrace();
		}
		return null;
	}
	
	private String getTagStr(List<Word> wordList){
		StringBuilder strBuilder = new StringBuilder();
		for(Word w : wordList)
			strBuilder.append(w.getLexeme() + " " + w.getTag() + " ");
		return strBuilder.toString();
	}
}
