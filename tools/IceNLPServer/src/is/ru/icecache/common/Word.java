package is.ru.icecache.common;

public class Word 
{
	private String lexeme;
	private String tag;
	private String lemma;
	
	public Word(String lexeme, String tag) 
	{
		this.lexeme = lexeme;
		this.tag = tag;
	}
	public String getLexeme() {
		return lexeme;
	}
	public void setLexeme(String lexeme) {
		this.lexeme = lexeme;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getLemma() {
		return lemma;
	}
	public void setLemma(String lemma) {
		this.lemma = lemma;
	}
	
	
}
