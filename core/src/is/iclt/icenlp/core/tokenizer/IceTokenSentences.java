package is.iclt.icenlp.core.tokenizer;

import java.util.ArrayList;

/**
 * Encapsulates sentences as a list of Sentence objects.
 * @author Sverrir Sigmundarson
 */
public class IceTokenSentences
{
    private ArrayList<IceTokenSentence> mySentences;

    public IceTokenSentences()
    {
        mySentences = new ArrayList<IceTokenSentence>();
	}

    public ArrayList<IceTokenSentence> getSentences()
	{
		return mySentences;
	}

    public void add (IceTokenSentence sent)
    {
        mySentences.add(sent);
    }

    public String toStringNewline(boolean markUnknown)
	{
		StringBuilder b = new StringBuilder( );

		for( IceTokenSentence sent : mySentences )
		{
            String str = sent.toStringNewline(markUnknown);
            b.append( str );
			b.append( "\n" );
        }
		return b.toString();
	}

    public String toString()
	{
		StringBuilder b = new StringBuilder( );

		for( IceTokenSentence sent : mySentences )
		{
            String str = sent.toString();
            b.append( str );
			b.append( "\n" );
        }
		return b.toString();
	}
    
    public int size()
    {
    	return mySentences.size();
    }
}
