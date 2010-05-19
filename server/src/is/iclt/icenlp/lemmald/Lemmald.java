package is.iclt.icenlp.lemmald;

import is.iclt.icenlp.core.tokenizer.IceTokenTags;



public class Lemmald 
{
	private is.iclt.icenlp.core.lemmald.Lemmald lemmald = null;
	private static Lemmald instance_; 
	
	public static Lemmald instance(){
		if(instance_ == null)
			instance_ = new Lemmald();
		return instance_; 
	}
	
	protected Lemmald(){
		System.out.println("[i] Loading Lemmald.");
		this.lemmald = is.iclt.icenlp.core.lemmald.Lemmald.getInstance();
	}
	
	public String getLemma(IceTokenTags t){
		// Make sure we provide the lemmatizer with a lower case lexeme
		String lexeme, lemma;

		// The first letter is often capitalised at the beginning of a sentence
		if (!t.isProperNoun() && Character.isUpperCase(t.lexeme.charAt(0))) {
			lexeme = t.lexeme.toLowerCase();
		} else
			lexeme = t.lexeme;

		lemma = this.lemmald.lemmatize(lexeme, t.getFirstTagStr()).getLemma();
		return lemma;
	}	
}
