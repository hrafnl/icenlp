package is.iclt.icenlp.icetagger;


import is.iclt.icenlp.core.utils.Word;
import java.util.List;

public interface IIceTagger {
	// TODO: comment
	List<Word> tag(String text) throws IceTaggerException;
	
	// TODO: comment
	void lemmatize(boolean value);	
}
