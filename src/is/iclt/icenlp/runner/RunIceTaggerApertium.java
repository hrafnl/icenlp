/*
 * Copyright (C) 2009 Hrafn Loftsson
 *
 * This file is part of the IceNLP toolkit.
 * IceNLP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * IceNLP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with IceNLP. If not,  see <http://www.gnu.org/licenses/>.
 *
 * Contact information:
 * Hrafn Loftsson, School of Computer Science, Reykjavik University.
 * hrafn@ru.is
 */
package is.iclt.icenlp.runner;
import is.iclt.icenlp.core.lemmald.Lemmald;
import is.iclt.icenlp.core.tokenizer.IceTokenTags;
import is.iclt.icenlp.core.tokenizer.Segmentizer;
import is.iclt.icenlp.core.utils.MappingLexicon;
import is.iclt.icenlp.core.utils.Word;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Runs IceParser producing Apertium output format.
 * @author Hrafn Loftsson
 */
public class RunIceTaggerApertium extends RunIceTagger
{
	// Member variables.
	private MappingLexicon  mappingLexicon;
    private Lemmald lemmald;
    private boolean showSurfaceForm=false;
	

	public static void main( String args[] ) throws Exception
	{
        RunIceTaggerApertium runner = new RunIceTaggerApertium();
        Date before = runner.initialize(args);
        runner.lemmald = Lemmald.getInstance();
        
        // create new instance of the mapping lexicon.
        runner.mappingLexicon = new MappingLexicon(runner.tagMapFile, false, false, false, "<NOT MAPPED>");
        
        // Perform the tagging
        runner.performTagging();
		runner.finish(before);
	}

    // we override getParameters
    protected void getParameters( String args[] )
	{

        super.getParameters(args);
        // The parameter showSurfaceForm is only used in IceTaggerApertium
		for( int i = 0; i <= args.length - 1; i++ )
		{
            if( args[i].equals( "-sf" ) )
                showSurfaceForm = true;
        }
    }

    // we override showParameters
     protected void showParameters()
	{
		super.showParameters();
        System.out.println( "  -sf (print surface form)" );
    }

	// we override printResults.
	protected void printResults(BufferedWriter outFile) throws IOException 
	{
        String lexeme;
		List<Word> wordList = new LinkedList<Word>();
		
		// create word objects and add them to the wordlist.
		for(Object o : tokenizer.tokens)
		{
			IceTokenTags t = ((IceTokenTags)o);
            // Strange place to count this.  Can we move this somewhere else?
            numTokens++;
            if( t.isUnknown() )
			    numUnknowns++;

            // Make sure we use lower case for lexemes before we ask for the lemma
            if (!t.isProperNoun() && Character.isUpperCase( t.lexeme.charAt( 0 ) ))
                lexeme = t.lexeme.toLowerCase();
            else
                lexeme = t.lexeme;
            
            wordList.add(new Word(t.lexeme, this.lemmald.lemmatize(lexeme, t.getFirstTagStr()).getLemma(), t.getFirstTagStr(), t.mweCode, t.tokenCode, t.linkedToPreviousWord));
			//wordList.add(new Word(t.lexeme, this.lemmald.lemmatize(t.lexeme, t.getFirstTagStr()).getLemma(), t.getFirstTagStr(), t.mweCode, t.tokenCode, t.linkedToPreviousWord));
		}
		
		this.mappingLexicon.processWordList(wordList);


        // Create output string that will be sent to the client.
        String output = "";

        for(Word word: wordList)  {
            if( outputFormat == Segmentizer.tokenPerLine ) {
                if (showSurfaceForm)
                    outFile.write("^" + word.getLexeme() + "/" + word.getLemma() + word.getTag() + "$");
                else
                    outFile.write("^" + word.getLemma() + word.getTag() + "$");
                outFile.newLine();
            }
            else {
                if (!word.linkedToPreviousWord)
                    output = output + " ";
                if (showSurfaceForm)
                    output = output + "^" + word.getLexeme() + "/" + word.getLemma() + word.getTag() + "$";
                else
                    output = output + "^" + word.getLemma() + word.getTag() + "$";
            }
        }

        if( outputFormat != Segmentizer.tokenPerLine ) {
            // Remove the first char if it is a space.
			if (output.charAt(0) == ' ')
				output = output.substring(1, output.length());
            outFile.write( output );
        }
		outFile.newLine();
	}
}
