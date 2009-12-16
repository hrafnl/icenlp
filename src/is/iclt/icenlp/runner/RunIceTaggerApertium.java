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

import is.iclt.icenlp.core.lemmald.LemmaResult;
import is.iclt.icenlp.core.lemmald.Lemmald;
import is.iclt.icenlp.core.tokenizer.IceTokenTags;
import is.iclt.icenlp.core.tokenizer.Segmentizer;
import is.iclt.icenlp.core.tokenizer.Token;
import is.iclt.icenlp.core.tokenizer.Token.MWECode;
import is.iclt.icenlp.core.utils.FileEncoding;
import is.iclt.icenlp.core.utils.MapperLexicon;
import is.iclt.icenlp.core.utils.Pair;
import is.iclt.icenlp.core.utils.Word;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Runs IceParser producing Apertium output format.
 * @author Hrafn Loftsson
 */
public class RunIceTaggerApertium extends RunIceTagger
{
	private MapperLexicon mappingLexicon;
	

	public static void main( String args[] ) throws Exception
	{
        RunIceTaggerApertium runner = new RunIceTaggerApertium();
        Date before = runner.initialize(args);
        runner.mappingLexicon = new MapperLexicon(runner.tagMapFile, false);

        // Perform the tagging
        runner.performTagging();
		runner.finish(before);
	}

	// we override printResults.
	protected void printResults(BufferedWriter outFile) throws IOException 
	{
		List<Word> wordList = new LinkedList<Word>();
		
		// Get instance of lemmald
		Lemmald myLemmald = Lemmald.getInstance();
	
		// create word objects and add them to the wordlist.
		for(Object o : tokenizer.tokens)
		{
			IceTokenTags t = ((IceTokenTags)o);
			wordList.add(new Word(t.lexeme, myLemmald.lemmatize(t.lexeme, t.getFirstTagStr()).getLemma(), t.getFirstTagStr(), t.mweCode));
		}
		
		// Lets look for tag mappings in the wordList.
		for(Word word : wordList)
		{
			String mappedTag = mappingLexicon.lookupTagmap(word.getTag(), false);
			if(mappedTag != null)
				word.setTag(mappedTag);
			else
				word.setTag("<NOT MAPPED>");
		}
		
        // Go over Lemma Exception rules.
        for(Word word : wordList)
        {
            String lookupWord = word.getLemma();

            if(this.mappingLexicon.hasExceptionRulesForLemma(lookupWord))
            {
                List<Pair<String, String>> rules = this.mappingLexicon.getExceptionRulesForLemma(lookupWord);
                for(Pair<String, String> pair : rules)
                {
                    if(word.getTag().matches(".*" +pair.one +".*"))
                    {
                        word.setTag(word.getTag().replaceFirst(pair.one, pair.two));
                    }
                }
            }
        }
        
        // Go over Lexeme Exception rules.
        for(Word word : wordList)
        {
            String lookupWord = word.getLexeme();

            if(this.mappingLexicon.hasExceptionRulesForLexeme(lookupWord))
            {
                List<Pair<String, String>> rules = this.mappingLexicon.getExceptionRulesForLexeme(lookupWord);
                for(Pair<String, String> pair : rules)
                {
                    if(word.getTag().matches(".*" +pair.one +".*"))
                    {
                        word.setTag(word.getTag().replaceFirst(pair.one, pair.two));
                    }
                }
            }
        }
        // Go over the MWE expression
        for(int i = 0; i < wordList.size(); i++)
        {
            if(wordList.get(i).mweCode == MWECode.begins)
            {
                // the index of the words begins at index begins:
                int begins = i;
                int ends = 0;

                String mweStr = wordList.get(i).getLexeme();
                int j = i;
                while(j < wordList.size())
                {
                    if(wordList.get(j).mweCode == MWECode.ends)
                    {
                        // The words ends at there.
                        ends = j;
                        i = j;
                        break;
                    }
                    if(j+1 < wordList.size())
                        mweStr += "_" + wordList.get(j+1).getLexeme();

                    j += 1;
                }

                if(this.mappingLexicon.hasMapForMWE(mweStr))
                {
                    String lemma = "";
                    String lexeme = "";

                    for(i = (ends - begins); i>= 0; i--)
                    {
                        lemma += wordList.get(begins).getLexeme() + " ";
                        wordList.remove(begins);
                    }

                    // Where we are working with MWE, we overwrite the lemma with the lexeme.
                    Word w = new Word(lexeme.substring(0,lexeme.length()-1), this.mappingLexicon.getMapForMWE(mweStr), MWECode.none);
                    w.setLemma(lexeme.substring(0,lexeme.length()-1));
                    wordList.add(begins, w);
                }
            }
        }
        
        // Go over MWE-RENAME rules.
        for(Word word : wordList)
        {
            if(this.mappingLexicon.hasRenameRuleForLexeme(word.getLexeme()))
            {
                Pair<String, String> pair = this.mappingLexicon.getRenameRuleForLexeme(word.getLexeme());
                word.setLemma(pair.one.replace('_', ' '));
                word.setLemma(pair.one.replace('_', ' '));
                word.setTag(pair.two);
            }
        }
        
        // Create output string that will be sent to the client.
        String output = "";

        for(Word word: wordList)
            output = output + "^" + word.getLemma() + word.getTag() + "$ ";

		outFile.write( output );
		outFile.newLine();
	}
}
