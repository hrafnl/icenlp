package is.iclt.icenlp.core.apertium;

// Stores one lexical unit
public class LexicalUnit
{
	private String lemma;
	private String symbols;
	private boolean unknown;
	private boolean space;
	private boolean ignore;
	
	public LexicalUnit(String lemma, String symbols)
	{
		this.lemma = lemma;
		this.symbols = symbols;
		this.unknown = false;
		this.ignore = false;
	}
	
	public LexicalUnit(String lemma, String symbols, boolean unknown)
	{
		this(lemma, symbols);
		
		this.unknown = unknown;
	}
	
	public LexicalUnit(String lemma, String symbols, boolean unknown, boolean space)
	{
		this(lemma, symbols, unknown);
		
		this.space = space;
	}
	
	public String getLemma()
	{
		return lemma;
	}
	
	public void setLemma(String lemma)
	{
		this.lemma = lemma;
	}
	
	public String getSymbols()
	{
		return symbols;
	}
	
	public void setSymbols(String symbols)
	{
		this.symbols = symbols;
	}
	
	public void setIgnore(boolean ignore)
	{
		this.ignore = ignore;
	}
	
	public boolean isUnknown()
	{
		return unknown;
	}
	
	public boolean isSpace()
	{
		return space;
	}
	
	public boolean isIgnore()
	{
		return ignore;
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
	
	public boolean isDet()
	{
		return symbols.contains("<det>");
	}
}
