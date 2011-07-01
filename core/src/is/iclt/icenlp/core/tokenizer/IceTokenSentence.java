package is.iclt.icenlp.core.tokenizer;

import java.util.ArrayList;

// Extends Sentence but uses IceTokenTag instead of Token
// Because there is data within IceTokenTag that get's cut off
// Like Lemma information
public class IceTokenSentence
{
	private ArrayList<IceTokenTags> myTokens;

    public IceTokenSentence()
    {
        myTokens = new ArrayList<IceTokenTags>();
	}

    public IceTokenSentence(ArrayList<IceTokenTags> tokens)
	{
        myTokens = new ArrayList<IceTokenTags>();
        
        for( IceTokenTags tok : tokens )
        {
            myTokens.add(tok);
        }
    }

    public ArrayList<IceTokenTags> getTokens()
	{
		return myTokens;
	}

    public void add (IceTokenTags tok)
    {
        myTokens.add(tok);
    }

    public String toString()
	{
		StringBuilder b = new StringBuilder( );

		for(IceTokenTags token : myTokens )
		{
            String str = token.toString();
            b.append( str );
			b.append( " " );
        }

		return b.toString();
	}

    // New line between each segment
    public String toStringNewline(boolean markUnknown)
	{
		StringBuilder b = new StringBuilder( );

		for(IceTokenTags token : myTokens )
		{
            String str = token.toString();
            b.append( str );
            if (markUnknown && token.isUnknown())
               b.append(" *");  // Means unknown word
            b.append("\n");
		}

		return b.toString();
	}
}
