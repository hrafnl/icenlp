package is.iclt.icenlp.IceParser;

import is.iclt.icenlp.core.utils.Word;
import java.util.List;

public interface IIceParser {
    String parse(String text);
 //   String parse(List<Word> words);
	String parse(String taggedString, String outputFormat, Boolean markerrors, Boolean mergeFunctions);
}