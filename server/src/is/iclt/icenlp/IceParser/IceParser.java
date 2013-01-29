package is.iclt.icenlp.IceParser;

import is.iclt.icenlp.common.configuration.Configuration;
import is.iclt.icenlp.core.utils.Word;
import is.iclt.icenlp.facade.IceParserFacade;

import java.io.IOException;
import java.util.List;


public class IceParser {

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
    private static final String formatTxt = "txt";
    private static final String formatJson = "json";     
    private static final String formatXml = "xml";
    private static final String formatPpl = "ppl";     
    private static final String formatTcf = "tcf";     
    private static final String formatTag = "tag";     
    private static final String formatMerge = "merge";     
    private static final String formatError = "error";     
    private static final String formatAlt = "alt";     

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

        if (Configuration.getInstance().getValue("IceParserOutput").toLowerCase().equals(formatTcf)) {
			value_IceParserOutput = formatTcf;
		} 
        else if (Configuration.getInstance().getValue("IceParserOutput").toLowerCase().equals(formatXml)) {
			value_IceParserOutput = formatXml;
		}
        else if (Configuration.getInstance().getValue("IceParserOutput").toLowerCase().equals(formatTxt)) {
            value_IceParserOutput = formatTxt;
        }
        else if (Configuration.getInstance().getValue("IceParserOutput").toLowerCase().equals(formatPpl)) {
            value_IceParserOutput = formatPpl;
        }
        else if (Configuration.getInstance().getValue("IceParserOutput").toLowerCase().equals(formatJson)) {
            value_IceParserOutput = formatJson;
        }
        // Special case: If alternate output format and the user does not specify format string in the query
        // then use txt format
        else if (Configuration.getInstance().getValue("IceParserOutput").toLowerCase().equals(formatAlt)) {
            value_IceParserOutput = formatTxt;
        }

        System.out.println("[i] IceParser output format: " + value_IceParserOutput);
        System.out.println("[i] IceParser instance created.");
        parser = new IceParserFacade();
    }

    public String parse(String text) {
        try {
            return parser.parse(text, include_functions, "one_phrase_per_line", false, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

	public void parse(String format, List<Word> words) {
        // format is a format string like "[txt]"

		String taggedString = this.getTagStr(words);
		
		try {
            boolean merge = false;
            boolean error = false;
			if (Configuration.getInstance().getValue("IceParserOutput").equals(formatAlt))
			{
                String newFormat = format;
		        if (newFormat.contains("["+formatMerge+"]")) {
                    merge = true;
                    newFormat = newFormat.replace("["+formatMerge+"]", "");
                }
                if (newFormat.contains("["+formatError+"]")) {
                    error = true;
                    newFormat = newFormat.replace("["+formatError+"]", "");
                }
                // We are only interested in the text representing the format, within the brackets, e.g. bla[txt]bla
                value_IceParserOutput = newFormat.replaceAll(".*\\[(\\S+)\\].*","$1");
			}

			String strParse = this.parser.parse(taggedString, include_functions, value_IceParserOutput, error, merge);

            parsedString = strParse;
		}
		catch (IOException e) {

			e.printStackTrace();
		}
	}

	private String getTagStr(List<Word> wordList)
	{	
		StringBuilder strBuilder = new StringBuilder();
		
		for(Word w : wordList)
        {
			strBuilder.append(w.getLexeme() + " " + w.getTag() + " ");
        }
        return strBuilder.toString();
	}
}
