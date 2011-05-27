package is.iclt.icenlp.core.apertium;

// Stores one lexical unit
public class LexicalUnit
{
	private String lemma;
	private String symbols;
	private boolean unknown;
	
	public LexicalUnit(String lemma, String symbols)
	{
		this.lemma = lemma;
		this.symbols = symbols;
		this.unknown = false;
	}
	
	public LexicalUnit(String lemma, String symbols, boolean unknown)
	{
		this.lemma = lemma;
		this.symbols = symbols;
		this.unknown = unknown;
	}
	
	public String getLemma()
	{
		return lemma;
	}
	
	public String getSymbols()
	{
		return symbols;
	}
	
	public void setSymbols(String symbols)
	{
		this.symbols = symbols;
	}
	
	public boolean isUnknown()
	{
		return unknown;
	}
	
	public boolean isPreposition()
	{
		return symbols.equals("<pr>");
	}
	
	public boolean isVerb()
	{
		return symbols.contains("<vblex>") || 
			   symbols.contains("<vbser>") ||
			   symbols.contains("<vbhaver>") ||
			   symbols.contains("<vaux>");
	}
}
