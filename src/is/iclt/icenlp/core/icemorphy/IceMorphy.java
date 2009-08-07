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
package is.iclt.icenlp.core.icemorphy;

import is.iclt.icenlp.core.tokenizer.*;
import is.iclt.icenlp.core.icetagger.IceFrequency;
import is.iclt.icenlp.core.utils.*;

import java.util.ArrayList;

/**
 * An unknown word guesser (morphological analyzer) for Icelandic.
 * <p>IceMorphy is described in the paper:
 * <ul>
 * <li>Hrafn Loftsson. Tagging Icelandic text: A linguistic rule-based approach. Nordic Journal of Linguistics (2008), 31(1), 47-72.</li>
 * @author Hrafn Loftsson
 */
public class IceMorphy {
    private enum MorphoClass
    { NounMasculine1, NounMasculine2, NounMasculine3, NounMasculine4, NounMasculine5,
      NounMasculine6, NounMasculine7, NounMasculine8, NounMasculine9, NounMasculine10,
      NounFeminine1,  NounFeminine2, NounFeminine3,  NounFeminine4,  NounFeminine5, NounFeminine6,
      NounNeuter1,  NounNeuter2, NounNeuter3,  NounNeuter4,
      Adj1, Adj2, Adj3, Adj4, Adj5,
      VerbActive1, VerbActive2, VerbActive3, VerbActive4, VerbActive5,
      VerbMiddle1, VerbMiddle2, None
    }
    private enum Mood {Indicative, Subjunctive, Imperative, IndicativeSubjunctive};
    private enum Voice {Active, Middle};
    //private enum Person {First, Second, Third, FirstSecond, FirstThird, SecondThird};
    private IceLog logger = null;    // Logfile file
	private Lexicon dictionary;     // a big dictionaryOtb
    private Lexicon dictionaryBase; // base dictionary
    private Trie endingsBase;        // a list of endings and possible tags
	private Trie endingsDict;       // a list of endings and possible tags
	private Trie endingsProperDict;  // a list of endings and possible tags for proper nounss
    private IceFrequency tagFreq;      // frequency of each tag according to some corpus
	private WordList prefixes;         // a list of prefixes to look for
	private ArrayList tokens;
    private String[] searchStrings;
    private int searchStringSize=36;
    private IceTokenTags dummyToken;

    private static final int suffixLength = 5; // The "normal" length of suffixes used for lookup
	private static final int maxSuffixLength = 10; // The maxium length of suffixes used for lookup
	private static final int maxPersonNameLength = 10; // Maximum length of person names
	private static final char[] vowels = { 'a', 'A', 'á', 'Á', 'e', 'E', 'é', 'É', 'i', 'I', 'í', 'Í',
	                                       'o', 'O', 'ó', 'Ó', 'u', 'U', 'ú', 'Ú', 'y', 'Y', 'ý', 'Ý', 'æ', 'Æ', 'ö', 'Ö' };
    static final int nominative = 0;
	static final int accusative = 1;
	static final int dative = 2;
	static final int genitive = 3;

	static final int masculine = 0;
	static final int feminine = 1;
	static final int neuter = 2;

	static final int singular = 0;
	static final int plural = 1;

    public IceMorphy( Lexicon bigLexicon,
                      Lexicon baseLexicon,
                      Trie endingsBase,
                      Trie endingsDict,
                      Trie endingsProper,
                      WordList prefixes, IceFrequency tagfrequencey, IceLog log )
	{
		if( bigLexicon == null )
			throw new NullPointerException( "Lexicon was not initialized properly (null).");

		this.dictionary = bigLexicon;
        this.dictionaryBase = baseLexicon;
        this.logger = log;

		this.endingsBase = endingsBase;
		this.endingsDict = endingsDict;
		this.endingsProperDict = endingsProper;

		this.tagFreq = tagfrequencey;

		this.prefixes = prefixes;
		dummyToken = new IceTokenTags( "dymmyToken", Token.TokenCode.tcNone );
        searchStrings = new String[searchStringSize+1];
    }


	public void setTokens( ArrayList lis )
	{
		tokens = lis;
	}

 /**
  * Returns the tag with the highest frequency for the supplied token
  * @param tok The token
  * @return tag with highest frequency
  */
    public String maxFrequency( IceTokenTags tok )
	{
		if( tok.isNoun() )
			tok.removeAllBut( IceTag.WordClass.wcNoun );

		String[] tags = tok.allTagStrings().split( "_" ); // split the tags
        return tagFreq.maxFrequency( tags );   // get the tag with the maximum frequencey
	}

	private void setSearchStrings( String root, MorphoClass type )
	{
        for (int i=1; i<=searchStringSize; i++)
                searchStrings[i]=null;

        int len = root.length();
		switch( type )
		{
			case NounMasculine1:
				searchStrings[1] = root + "ur";    // hestur
				searchStrings[2] = root;
				searchStrings[3] = root + "i";
				if( root.endsWith( "ð" ) ) // iðnað
					searchStrings[4] = root + "ar";
				else
					searchStrings[4] = root + "s";
				searchStrings[5] = searchStrings[1] + "inn";
				searchStrings[6] = searchStrings[2] + "inn";
				searchStrings[7] = searchStrings[3] + "num";
				searchStrings[8] = searchStrings[4] + "ins";
				searchStrings[9] = root + "ar";    // hestar
				searchStrings[10] = root + "a";
				searchStrings[11] = root + "um";
				searchStrings[12] = root + "a";
				searchStrings[13] = searchStrings[9] + "nir";    // nominative + article
				searchStrings[14] = searchStrings[10] + "na";    // accusative + article
				searchStrings[15] = root + "unum";
				searchStrings[16] = searchStrings[12] + "nna";
				searchStrings[17] = root + "ir";    // gestir
				searchStrings[18] = root + "i";
				searchStrings[19] = searchStrings[17] + "nir";
				searchStrings[20] = searchStrings[18] + "na";
				break;
			case NounMasculine2:
				searchStrings[1] = root + "i";    // skóli
				searchStrings[2] = root + "a";
				searchStrings[3] = searchStrings[2];
				searchStrings[4] = searchStrings[2];
				searchStrings[5] = searchStrings[1] + "nn";
				searchStrings[6] = searchStrings[2] + "nn";
				searchStrings[7] = searchStrings[3] + "num";
				searchStrings[8] = searchStrings[4] + "ns";
				searchStrings[9] = root + "ar";    // skólar
				searchStrings[10] = root + "a";
				searchStrings[11] = root + "um";
				searchStrings[12] = root + "a";
				searchStrings[13] = searchStrings[9] + "nir";
				searchStrings[14] = searchStrings[10] + "na";
				searchStrings[15] = root + "unum";
				searchStrings[16] = searchStrings[12] + "nna";
				break;
			case NounMasculine3:
				searchStrings[1] = root + "ur";    // galdur
				searchStrings[2] = searchStrings[1];
				searchStrings[3] = root + "ri";
				searchStrings[4] = root + "urs";
				searchStrings[5] = searchStrings[1] + "inn";
				searchStrings[6] = searchStrings[2] + "inn";
				searchStrings[7] = searchStrings[3] + "num";
				searchStrings[8] = searchStrings[4] + "ins";
				searchStrings[9] = root + "rar";    // galdrar
				searchStrings[10] = root + "ra";
				searchStrings[11] = root + "rum";
				searchStrings[12] = root + "ra";
				searchStrings[13] = searchStrings[9] + "nir";
				searchStrings[14] = searchStrings[10] + "na";    // accusative + article
				searchStrings[15] = root + "unum";
				searchStrings[16] = searchStrings[12] + "nna";
				break;
			case NounMasculine4:
				searchStrings[1] = root;          // maur
				searchStrings[2] = searchStrings[1];
				searchStrings[3] = searchStrings[1];
				searchStrings[4] = root + "s";
				searchStrings[5] = searchStrings[1] + "inn";
				searchStrings[6] = searchStrings[2] + "inn";
				searchStrings[7] = searchStrings[3] + "num";
				searchStrings[8] = searchStrings[4] + "ins";
				searchStrings[9] = root + "ar";    // maurar
				searchStrings[10] = root + "a";
				searchStrings[11] = root + "um";
				searchStrings[12] = root + "a";
				searchStrings[13] = searchStrings[9] + "nir";
				searchStrings[14] = searchStrings[10] + "na";
				searchStrings[15] = root + "unum";
				searchStrings[16] = searchStrings[12] + "nna";
				break;
			case NounMasculine5:
				searchStrings[1] = root + "ur";    // hugur, fundur
				searchStrings[2] = root;
				searchStrings[3] = root + "i";
				searchStrings[4] = root + "ar";
				searchStrings[5] = searchStrings[1] + "inn";
				searchStrings[6] = searchStrings[2] + "inn";
				searchStrings[7] = searchStrings[3] + "num";
				searchStrings[8] = searchStrings[4] + "ins";
				searchStrings[9] = root + "ar";    // hugar
				searchStrings[10] = root + "a";
				searchStrings[11] = root + "um";
				searchStrings[12] = root + "a";
				searchStrings[13] = searchStrings[9] + "nir";
				searchStrings[14] = searchStrings[10] + "na";
				searchStrings[15] = root + "unum";
				searchStrings[16] = searchStrings[12] + "nna";
				break;
			case NounMasculine6:
				searchStrings[1] = root + "l";    // stóll
				searchStrings[2] = root;
				searchStrings[3] = root + "i";
				searchStrings[4] = root + "s";
				searchStrings[5] = searchStrings[1] + "inn";
				searchStrings[6] = searchStrings[2] + "inn";
				searchStrings[7] = searchStrings[3] + "num";
				searchStrings[8] = searchStrings[4] + "ins";
				searchStrings[9] = root + "ar";    // stólar
				searchStrings[10] = root + "a";
				searchStrings[11] = root + "um";
				searchStrings[12] = root + "a";
				searchStrings[13] = searchStrings[9] + "nir";
				searchStrings[14] = searchStrings[10] + "na";
				searchStrings[15] = root + "unum";
				searchStrings[16] = searchStrings[12] + "nna";
				break;
			case NounMasculine7:
				searchStrings[1] = root + "ir";    // hellir
				searchStrings[2] = root + "i";
				searchStrings[3] = root + "i";
				searchStrings[4] = root + "s";
				searchStrings[5] = searchStrings[1] + "inn";
				searchStrings[6] = searchStrings[2] + "nn";
				searchStrings[7] = searchStrings[3] + "num";
				searchStrings[8] = searchStrings[4] + "ins";
				searchStrings[9] = root + "ar";    // hellar
				searchStrings[10] = root + "a";
				searchStrings[11] = root + "um";
				searchStrings[12] = root + "a";
				searchStrings[13] = searchStrings[9] + "nir";
				searchStrings[14] = searchStrings[10] + "na";
				searchStrings[15] = root + "unum";
				searchStrings[16] = searchStrings[12] + "nna";
				break;
			case NounMasculine8:
				searchStrings[1] = root;    // gítar, Örvar
				searchStrings[2] = root;
				searchStrings[3] = root + "i";
				searchStrings[4] = root + "s";
				searchStrings[5] = searchStrings[1] + "inn";
				searchStrings[6] = searchStrings[2] + "inn";
				searchStrings[7] = searchStrings[3] + "num";
				searchStrings[8] = searchStrings[4] + "ins";
				searchStrings[9] = root + "ar";    // gítar-ar
				searchStrings[10] = root + "a";
				searchStrings[11] = root + "um";
				searchStrings[12] = root + "a";
				searchStrings[13] = searchStrings[9] + "nir";
				searchStrings[14] = searchStrings[10] + "na";
				searchStrings[15] = root + "unum";
				searchStrings[16] = searchStrings[12] + "nna";
				break;
			case NounMasculine9:
				searchStrings[1] = root + "ur";    // bekkur
				searchStrings[2] = root;
				searchStrings[3] = root;
				searchStrings[4] = root + "jar";
				searchStrings[5] = searchStrings[1] + "inn";
				searchStrings[6] = searchStrings[2] + "inn";
				searchStrings[7] = searchStrings[3] + "num";
				searchStrings[8] = searchStrings[4] + "ins";
				searchStrings[9] = root + "ir";    // bekkir
				searchStrings[10] = root + "i";
				searchStrings[11] = root + "jum";
				searchStrings[12] = root + "ja";
				searchStrings[13] = searchStrings[9] + "nir";
				searchStrings[14] = searchStrings[10] + "na";
				searchStrings[15] = root + "junum";
				searchStrings[16] = searchStrings[12] + "nna";
				break;
			case NounMasculine10:
				String newRoot = root.substring( 0, root.length() - 3 ) + "end"; // atvinnurekend
				searchStrings[1] = root + "i";    // atvinnurekand-i
				searchStrings[2] = root + "a";
				searchStrings[3] = root + "a";
				searchStrings[4] = root + "a";
				searchStrings[5] = root + "inn";
				searchStrings[6] = root + "ann";
				searchStrings[7] = root + "anum";
				searchStrings[8] = root + "ans";
				searchStrings[9] = newRoot + "ur";
				searchStrings[10] = searchStrings[5];
				searchStrings[11] = newRoot + "um";
				searchStrings[12] = newRoot + "a";
				searchStrings[13] = searchStrings[9] + "nir";
				searchStrings[14] = searchStrings[10] + "na";
				searchStrings[15] = newRoot + "unum";
				searchStrings[16] = searchStrings[12] + "nna";
				break;
			case NounFeminine1:
				searchStrings[1] = root + "a";    // kona
				searchStrings[2] = root + "u";
				searchStrings[3] = searchStrings[2];
				searchStrings[4] = searchStrings[2];
				searchStrings[5] = searchStrings[1] + "n";
				searchStrings[6] = searchStrings[2] + "na";
				searchStrings[7] = searchStrings[3] + "nni";
				searchStrings[8] = searchStrings[4] + "nnar";
				searchStrings[9] = root + "ur";    // konur
				searchStrings[10] = root + "ur";
				searchStrings[11] = root + "um";
				searchStrings[12] = root + "a";    // til súpa
				searchStrings[13] = searchStrings[9] + "nar";  // kon-ur-nar
				searchStrings[14] = searchStrings[10] + "nar";
				searchStrings[15] = root + "unum";
				searchStrings[16] = searchStrings[12] + "nna";
				searchStrings[17] = root + "na";    // til súpna, special genitive, plural
				searchStrings[18] = searchStrings[17] + "nna";    // til súpna, special genitive, plural
				break;
			case NounFeminine2:
				searchStrings[1] = root;          // von
				searchStrings[2] = searchStrings[1];
				searchStrings[3] = searchStrings[1];
				searchStrings[4] = root + "ar";
				searchStrings[5] = searchStrings[1] + "in";
				searchStrings[6] = searchStrings[2] + "ina";
				searchStrings[7] = searchStrings[3] + "inni";
				searchStrings[8] = searchStrings[4] + "innar";
				searchStrings[9] = root + "ir";    // vonir
				searchStrings[10] = searchStrings[9];
				searchStrings[11] = root + "um";
				searchStrings[12] = root + "a";
				searchStrings[13] = searchStrings[9] + "nar";    // nominative + article
				searchStrings[14] = searchStrings[10] + "nar";    // accusative + article
				searchStrings[15] = root + "unum";
				searchStrings[16] = searchStrings[12] + "nna";
				break;
			case NounFeminine3:
				searchStrings[1] = root;          // meining
				searchStrings[2] = root + "u";
				searchStrings[3] = searchStrings[2];
				searchStrings[4] = root + "ar";
				searchStrings[5] = searchStrings[1] + "in";
				searchStrings[6] = searchStrings[2] + "na";
				searchStrings[7] = searchStrings[3] + "nni";
				searchStrings[8] = searchStrings[4] + "innar";
				searchStrings[9] = root + "ar";    // meiningar
				searchStrings[10] = searchStrings[9];
				searchStrings[11] = root + "um";
				searchStrings[12] = root + "a";
				searchStrings[13] = searchStrings[9] + "nar";    // nominative + article
				searchStrings[14] = searchStrings[10] + "nar";    // accusative + article
				searchStrings[15] = root + "unum";
				searchStrings[16] = searchStrings[12] + "nna";
				break;
			case NounFeminine4:
				searchStrings[1] = root;          // kæti
				searchStrings[2] = root;
				searchStrings[3] = root;
				searchStrings[4] = root;
				searchStrings[5] = searchStrings[1] + "n";
				searchStrings[6] = searchStrings[2] + "na";
				searchStrings[7] = searchStrings[3] + "nni";
				searchStrings[8] = searchStrings[4] + "nnar";
				break;
			case NounFeminine5:
				searchStrings[1] = root + "un";          // vitj-un
				searchStrings[2] = searchStrings[1];
				searchStrings[3] = searchStrings[1];
				searchStrings[4] = searchStrings[1] + "ar";
				searchStrings[5] = searchStrings[1] + "in";
				searchStrings[6] = searchStrings[2] + "ina";
				searchStrings[7] = searchStrings[3] + "inni";
				searchStrings[8] = searchStrings[4] + "innar";
				searchStrings[9] = root + "anir";    // vitjanir
				searchStrings[10] = searchStrings[9];
				searchStrings[11] = root + "unum";
				searchStrings[12] = root + "anna";
				searchStrings[13] = searchStrings[9] + "nar";
				searchStrings[14] = searchStrings[10] + "nar";
				searchStrings[15] = root + "ununum";
				searchStrings[16] = root + "ananna";
				break;
			case NounFeminine6:
				searchStrings[1] = root;          // lif-ur
				searchStrings[2] = root;
				searchStrings[3] = root;
				searchStrings[4] = root + "rar";
				searchStrings[5] = searchStrings[1] + "rin";
				searchStrings[6] = searchStrings[2] + "rina";
				searchStrings[7] = searchStrings[3] + "rinni";
				searchStrings[8] = searchStrings[4] + "innar";
				break;
			case NounNeuter1:
				searchStrings[1] = root;         // svín
				searchStrings[2] = root;
				searchStrings[3] = root + "i";
				searchStrings[4] = root + "s";
				searchStrings[5] = searchStrings[1] + "ið";
				searchStrings[6] = searchStrings[2] + "ið";
				searchStrings[7] = searchStrings[3] + "nu";
				searchStrings[8] = searchStrings[4] + "ins";
				searchStrings[9] = root;           // svín
				searchStrings[10] = root;
				searchStrings[11] = root + "um";    //
				searchStrings[12] = root + "a";     //
				searchStrings[13] = searchStrings[9] + "in";    // nominative + article
				searchStrings[14] = searchStrings[10] + "in";    // accusative + article
				searchStrings[15] = root + "unum";
				searchStrings[16] = searchStrings[12] + "nna";
				break;
			case NounNeuter2:
				searchStrings[1] = root + "i";          // veski
				searchStrings[2] = searchStrings[1];
				searchStrings[3] = searchStrings[1];
				searchStrings[4] = root + "is";
				searchStrings[5] = searchStrings[1] + "ð";    // nominative + article
				searchStrings[6] = searchStrings[2] + "ð";    // accusative + article
				searchStrings[7] = searchStrings[3] + "nu";
				searchStrings[8] = searchStrings[4] + "ins";
				searchStrings[9] = root + "i";           // veski
				searchStrings[10] = root + "i";
				searchStrings[11] = root + "um";    //
				searchStrings[12] = root + "a";     //
				searchStrings[13] = searchStrings[9] + "n";
				searchStrings[14] = searchStrings[10] + "n";
				searchStrings[15] = root + "unum";
				searchStrings[16] = searchStrings[12] + "nna";
				break;
			case NounNeuter3:
				searchStrings[1] = root + "ur";   // fóð-ur
				searchStrings[2] = searchStrings[1];
				searchStrings[3] = root + "ri";
				searchStrings[4] = root + "urs";
				searchStrings[5] = root + "rið";
				searchStrings[6] = root + "rið";
				searchStrings[7] = searchStrings[3] + "nu";
				searchStrings[8] = searchStrings[4] + "ins";
				searchStrings[9] = searchStrings[1];       // fóður
				searchStrings[10] = searchStrings[2];
				searchStrings[11] = root + "rum";
				searchStrings[12] = root + "ra";
				searchStrings[13] = root + "rin";
				searchStrings[14] = root + "rin";
				searchStrings[15] = root + "runum";
				searchStrings[16] = searchStrings[12] + "nna";
				break;
			case NounNeuter4:
				searchStrings[1] = root + "a";          // hjarta, nýra
				searchStrings[2] = searchStrings[1];
				searchStrings[3] = searchStrings[1];
				searchStrings[4] = searchStrings[1];
				searchStrings[5] = searchStrings[1] + "að";
				searchStrings[6] = searchStrings[2] + "að";
				searchStrings[7] = searchStrings[3] + "anu";
				searchStrings[8] = searchStrings[4] + "ans";
				searchStrings[9] = root + "u";       // nýr-un
				searchStrings[10] = searchStrings[2];
				searchStrings[11] = root + "um";
				searchStrings[12] = root + "a";
				searchStrings[13] = root + "un";
				searchStrings[14] = root + "un";
				searchStrings[15] = root + "unum";
				searchStrings[16] = searchStrings[12] + "nna";
				break;

			case Adj1:
				if( root.endsWith( "á" ) )    // hár, grár, blár,
					searchStrings[1] = root + "r";
				else if( root.endsWith( "laus" ) )
					searchStrings[1] = root;
				else
					searchStrings[1] = root + "ur";   // þreyttur
				searchStrings[2] = root + "an";
				searchStrings[3] = root + "um";
				searchStrings[4] = root + "s";
				searchStrings[5] = root;          // þreytt
				searchStrings[6] = root + "a";
				searchStrings[7] = root + "ri";
				searchStrings[8] = root + "rar";
				if( root.endsWith( "ð" ) )
					searchStrings[9] = root.substring( 0, len - 1 ) + "tt"; // breið-ur, brei-tt
				else if( root.endsWith( "tt" ) )
					searchStrings[9] = root;                                // þreytt-ur, þreytt
				else
					searchStrings[9] = root + "t";
				searchStrings[10] = searchStrings[9];
				searchStrings[11] = root + "u";
				searchStrings[12] = root + "s";

				searchStrings[13] = root + "ir";    // þreyttir
				searchStrings[14] = root + "a";
				searchStrings[15] = root + "um";
				searchStrings[16] = root + "ra";
				searchStrings[17] = root + "ar";   // þreyttar
				searchStrings[18] = root + "ar";
				searchStrings[19] = root + "um";
				searchStrings[20] = root + "ra";
				searchStrings[21] = root;           // þreytt
				searchStrings[22] = root;
				searchStrings[23] = root + "um";
				searchStrings[24] = root + "ra";

				searchStrings[25] = root + "i";    // þreytt-i
				searchStrings[26] = root + "a";
				searchStrings[27] = root + "a";
				searchStrings[28] = root + "a";
				searchStrings[29] = root + "a";    // þreytt-a
				searchStrings[30] = root + "u";    // þreytt-u
				searchStrings[31] = root + "u";    // þreytt-u
				searchStrings[32] = root + "u";    // þreytt-u
				searchStrings[33] = root + "a";    // þreytt-a
				searchStrings[34] = root + "a";    // þreytt-a
				searchStrings[35] = root + "a";    // þreytt-a
				searchStrings[36] = root + "a";    // þreytt-a

				break;

			case Adj2:
				searchStrings[1] = root + "inn";   // þéttrið-inn, krist-inn
				searchStrings[2] = searchStrings[1];
				searchStrings[3] = root + "num";
				searchStrings[4] = root + "ins";
				searchStrings[5] = root + "in";    // þéttriðin
				searchStrings[6] = root + "na";
				searchStrings[7] = root + "inni";
				searchStrings[8] = root + "innar";
				searchStrings[9] = root + "ið";    // þéttrið-ið
				searchStrings[10] = searchStrings[9];
				searchStrings[11] = root + "nu";
				searchStrings[12] = root + "ins";

				searchStrings[13] = root + "nir";   // þéttrið-nir
				searchStrings[14] = root + "na";
				searchStrings[15] = root + "num";
				searchStrings[16] = root + "inna";
				searchStrings[17] = root + "nar";   // þéttrið-nar
				searchStrings[18] = root + "nar";
				searchStrings[19] = root + "num";
				searchStrings[20] = root + "inna";
				searchStrings[21] = root + "in";    // þéttrið-in
				searchStrings[22] = root + "in";
				searchStrings[23] = root + "num";
				searchStrings[24] = root + "inna";
				break;

			case Adj3:
				searchStrings[1] = root + "i";    // góði
				searchStrings[2] = root + "a";
				searchStrings[3] = searchStrings[2];
				searchStrings[4] = searchStrings[2];
				searchStrings[5] = root + "a";    // góða
				searchStrings[6] = root + "u";
				searchStrings[7] = searchStrings[6];
				searchStrings[8] = searchStrings[6];
				searchStrings[9] = root + "a";    // góða
				searchStrings[10] = searchStrings[9];
				searchStrings[11] = searchStrings[9];
				searchStrings[12] = searchStrings[9];

				searchStrings[13] = root + "u";
				searchStrings[14] = root + "u";
				searchStrings[15] = root + "u";
				searchStrings[16] = root + "u";
				searchStrings[17] = root + "u";
				searchStrings[18] = root + "u";
				searchStrings[19] = root + "u";
				searchStrings[20] = root + "u";
				searchStrings[21] = root + "u";
				searchStrings[22] = root + "u";
				searchStrings[23] = root + "u";
				searchStrings[24] = root + "u";
				break;

			case Adj4:
				searchStrings[1] = root + "aður";   // horaður
				searchStrings[2] = root + "aðan";
				searchStrings[3] = root + "uðum";
				searchStrings[4] = root + "aðs";
				searchStrings[5] = root + "uð";   // horuð
				searchStrings[6] = root + "aða";
				searchStrings[7] = root + "aðri";
				searchStrings[8] = root + "aðrar";
				searchStrings[9] = root + "að";   // horað
				searchStrings[10] = searchStrings[9];
				searchStrings[11] = root + "uðu";
				searchStrings[12] = root + "aðs";

				searchStrings[13] = root + "aðir"; // hor-aðir
				searchStrings[14] = root + "aða";
				searchStrings[15] = root + "uðum";
				searchStrings[16] = root + "aðra";
				searchStrings[17] = root + "aðar"; // hor-aðar
				searchStrings[18] = root + "aðar";
				searchStrings[19] = root + "uðum";
				searchStrings[20] = root + "aðra";
				searchStrings[21] = root + "uð";    // hor-uð
				searchStrings[22] = root + "uð";
				searchStrings[23] = root + "uðum";
				searchStrings[24] = root + "aðra";
				break;

			case Adj5:
				searchStrings[1] = root + "l";   // heil-l
				searchStrings[2] = root + "an";
				searchStrings[3] = root + "um";
				searchStrings[4] = root + "s";
				searchStrings[5] = root;        // heil
				searchStrings[6] = root + "a";
				searchStrings[7] = root + "ri";
				searchStrings[8] = root + "rar";
				searchStrings[9] = root + "t";   // heil-t
				searchStrings[10] = searchStrings[9];
				searchStrings[11] = root + "u";
				searchStrings[12] = root + "s";

				searchStrings[13] = root + "ir"; // heil-ir
				searchStrings[14] = root + "a";
				searchStrings[15] = root + "um";
				searchStrings[16] = root + "ra";
				searchStrings[17] = root + "ar"; // heil-ar
				searchStrings[18] = root + "ar";
				searchStrings[19] = root + "um";
				searchStrings[20] = root + "ra";
				searchStrings[21] = root;         // heil
				searchStrings[22] = root;
				searchStrings[23] = root + "um";
				searchStrings[24] = root + "ra";
				break;
			case VerbActive1:
				searchStrings[1] = root + "a";   // ég/þeir borð-a
				searchStrings[2] = root + "ar";  // þú/hann borð-ar
				searchStrings[3] = root + "um";  // við borð-um
				searchStrings[4] = root + "ið";  // þið borð-ið
				searchStrings[5] = root + "aði"; // ég/hann borð-aði
				searchStrings[6] = root + "aðir"; // þú borð-aðir
				searchStrings[7] = root + "uðum"; // við borð-uðum
				searchStrings[8] = root + "uðuð"; // þið borð-uðuð
				searchStrings[9] = root + "uðu"; //  þeir borð-uðuð
				searchStrings[10] = root + "að"; //  borð-að
				searchStrings[11] = root + "ist"; //  borð-ist
				searchStrings[12] = root + "aðist"; //  borð-aðist
				searchStrings[13] = root + "andi"; //  borð-andi

				break;
			case VerbActive2:
				searchStrings[1] = root + "i";   // ég reyn-i
				searchStrings[2] = root + "ir";  // þú/hann reyn-ir
				searchStrings[3] = root + "um";  // við reyn-um
				searchStrings[4] = root + "ið";  // þið reyn-ið
				searchStrings[5] = root + "a";   // þeir reyn-a
				searchStrings[6] = root + "di";  // ég/hann reyn-di
				searchStrings[7] = root + "ti";  // ég/hann fyll-ti
				searchStrings[8] = root + "ði";  // ég/hann lif-ði
				searchStrings[9] = root + "dir"; // þú reyn-dir
				searchStrings[10] = root + "tir"; // þú fyll-tir
				searchStrings[11] = root + "ðir"; // þú lif-ðir
				searchStrings[12] = root + "dum"; // við reyn-dum
				searchStrings[13] = root + "tum"; // við fyll-tum
				searchStrings[14] = root + "ðum"; // við lif-ðum
				searchStrings[15] = root + "duð"; // þið reyn-duð
				searchStrings[16] = root + "tuð"; // þið fyll-tuð
				searchStrings[17] = root + "ðuð"; // þið lif-ðuð
				searchStrings[18] = root + "du"; // þeir reyn-du
				searchStrings[19] = root + "tu"; // þeir fyll-tu
				searchStrings[20] = root + "ðu"; // þeir lif-ðu
				searchStrings[21] = root + "ist"; //  reyn-ist
				searchStrings[22] = root + "t";   // reyn-t
				searchStrings[23] = root + "andi";   // reyn-andi
				break;
			case VerbActive3:
				searchStrings[1] = root + "i";   // ég ben-di
				searchStrings[2] = root + "ir";  // þú/hann ben-dir
				searchStrings[3] = root + "dum";  // við ben-dum
				searchStrings[4] = root + "dið";  // þið ben-dið
				searchStrings[5] = root + "da";   // þeir ben-da
				searchStrings[6] = root + "ti";  // ég/hann ben-ti
				searchStrings[7] = root + "tir"; // þú ben-tir
				searchStrings[8] = root + "tum"; // við ben-tum
				searchStrings[9] = root + "tuð"; // þið ben-tuð
				searchStrings[10] = root + "tu"; // þeir ben-tu
				searchStrings[11] = root + "dist"; //  bend-ist
				searchStrings[12] = root + "andi";   // reyn-andi
				break;
			case VerbActive4:
				searchStrings[1] = root + "ði";   // ég bræ-ði
				searchStrings[2] = root + "ðir";  // þú/hann bræ-ðir
				searchStrings[3] = root + "ðum";  // við bræ-ðum
				searchStrings[4] = root + "ðið";  // þið bræ-ðið
				searchStrings[5] = root + "ða";   // þeir bræ-ða
				searchStrings[6] = root + "ddi";  // ég/hann bræ-ddi
				searchStrings[7] = root + "ddir"; // þú bræ-ddir
				searchStrings[8] = root + "ddum"; // við bræ-ddum
				searchStrings[9] = root + "dduð"; // þið bræ-dduð
				searchStrings[10] = root + "ddu"; // þeir bræ-ddu
				searchStrings[11] = root + "ðist"; //  bræ-ðist
				searchStrings[12] = root + "ðandi";   // bræ-ðandi
				break;
			case VerbActive5:
				searchStrings[1] = root + "i";   // ég þyng-i
				searchStrings[2] = root + "ir";  // þú/hann þyng-ir
				searchStrings[3] = root + "jum";  // við þyng-jum
				searchStrings[4] = root + "ið";  // þið þyng-ið
				searchStrings[5] = root + "ja";   // þeir þyng-ja
				searchStrings[6] = root + "di";  // ég/hann þyng-di
				searchStrings[7] = root + "dir"; // þú þyng-dir
				searchStrings[8] = root + "dum"; // við þyng-dum
				searchStrings[9] = root + "duð"; // þið þyng-duð
				searchStrings[10] = root + "du"; // þeir þyng-du
				searchStrings[11] = root + "dist"; //  þyng-dist
				searchStrings[12] = root + "jandi"; //  þyng-jandi
				break;
				// ágerast
			case VerbMiddle1:
                searchStrings[1] = root + "ist";   // ég/þú/hann/þið áger-ist
                searchStrings[2] = root + "umst";  // við áger-umst
				searchStrings[4] = root + "ast";  // þeir áger-ast
                searchStrings[5] = root + "ðist";   // ég/þú/hann áger-ðist
                searchStrings[6] = root + "ðumst";  // við áger-ðumst
				searchStrings[7] = root + "ðust";  // þið/þeir áger-ðust
                break;
            // drepast
			case VerbMiddle2:
                searchStrings[1] = root + "st";   // ég/þú/hann drep-st
                searchStrings[2] = root + "umst";  // við drep-umst
				searchStrings[3] = root + "ist";  // þið drep-ist
				searchStrings[4] = root + "ast";  // þeir drep-ast
                break;
        }
	}

	private String hljodVarp( String root, char transformedLetter, char rootLetter )
	{
		String newRoot = null;
		// hljóðvarp
		if( (root.length() >= 2 && root.charAt( 1 ) == transformedLetter) ||
		    (root.length() >= 3 && root.charAt( 2 ) == transformedLetter) )
			newRoot = root.replace( transformedLetter, rootLetter );

		return newRoot;
	}

	private void addProperNounTag( IceTokenTags tok, char gender, char number, char cas, char properNounType )
	{
		IceTag tag = new IceTag();
		tag.setTagStr( IceTag.tagProperNoun );

		tag.setGenderNumberCase( gender, number, cas );
		tag.setProperNameType( properNounType );
		tok.addTag( tag );
	}

	private void addNounTag( IceTokenTags tok, char gender, char number, char cas, boolean article )
	{
		IceTag tag = new IceTag();
		tag.setTagStr( IceTag.tagNoun );

		tag.setGenderNumberCase( gender, number, cas );

		if( article )
			tag.addArticle();

		tok.addTag( tag );
	}

	private void addAdjectiveTag( IceTokenTags tok, char gender, char number, char cas, char decl, char degree )
	{
		IceTag tag = new IceTag();
		tag.setTagStr( IceTag.tagAdj );

		tag.setGenderNumberCase( gender, number, cas );
		tag.setDeclension( decl );
		tag.setDegree( degree );

		tok.addTag( tag );
	}

	private void addPastParticipleTag( IceTokenTags tok, char gender, char number, char cas )
	{
		IceTag tag = new IceTag( IceTag.tagVerbPastPart );
		tag.setGenderNumberCase( gender, number, cas );
		tok.addTag( tag );
	}

	private boolean generateMissingVerb( IceTokenTags tok )
			//throws IOException
	{
		boolean found = false;
		if( tok.isOnlyWordClass( IceTag.WordClass.wcVerbPastPart ) )
		{
			// pínd kvk. et => pínd hvk. flt.
			if( tok.isGenderPerson( IceTag.cFeminine ) && tok.isNumber( IceTag.cSingular ) )
			{
				addPastParticipleTag( tok, IceTag.cNeuter, IceTag.cPlural, IceTag.cNominative );
				addPastParticipleTag( tok, IceTag.cNeuter, IceTag.cPlural, IceTag.cAccusative );
			}
			else if( tok.isGenderPerson( IceTag.cNeuter ) && tok.isNumber( IceTag.cPlural ) )
			{
				addPastParticipleTag( tok, IceTag.cFeminine, IceTag.cSingular, IceTag.cNominative );
				addPastParticipleTag( tok, IceTag.cNeuter, IceTag.cPlural, IceTag.cNominative );
				addPastParticipleTag( tok, IceTag.cNeuter, IceTag.cPlural, IceTag.cAccusative );
			}
			// There might be a missing adjective
			if( !tok.isAdjective() )
			{
				dummyToken.clearTags();
				dummyToken.lexeme = tok.lexeme;
				found = morphoAnalysisSuffix( dummyToken, false );
				if( found && dummyToken.isAdjective() )
				{
					dummyToken.removeAllBut( IceTag.WordClass.wcAdj );
					tok.addAllTags( dummyToken.allTagStrings() );
				}
			}
		}
		else if( tok.isVerbInfinitive() )
		{
			if( !tok.isVerbMiddleForm() )
			{
				if( !tok.tagExists( IceTag.tagVerbThirdPlural ) )
					tok.addTag( IceTag.tagVerbThirdPlural );
				found = true;
			}
			else
			{
				if( !tok.tagExists( IceTag.tagVerbThirdPluralMiddle ) )
					tok.addTag( IceTag.tagVerbThirdPluralMiddle );
				found = true;
			}

		}
		else if( tok.isVerbSupine() && !tok.isVerbPastPart() )
			addPastParticipleTag( tok, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative );

		// Is a singular tag missing or infinitive form?   neita=sfg3fn
		else if( tok.lexeme.endsWith( "a" ) && tok.isVerbActive() )
		{
			if( !tok.tagExists( IceTag.tagVerbInfActive ) )
			{
				tok.addTag( IceTag.tagVerbInfActive );
				found = true;
			}
			if( tok.tagExists( IceTag.tagVerbThirdPlural ) && !tok.tagExists( IceTag.tagVerbFirstSingular ) )
			{
				tok.addTag( IceTag.tagVerbFirstSingular );
				found = true;
			}
			else if( tok.tagExists( IceTag.tagVerbFirstSingular ) && !tok.tagExists( IceTag.tagVerbThirdPlural ) )
			{
				tok.addTag( IceTag.tagVerbThirdPlural );
				found = true;
			}
		}
		// borð-aði
		else if( tok.lexeme.endsWith( "aði" ) && tok.isVerbActive() )
		{
			if( tok.tagExists( IceTag.tagVerbFirstSingularPast ) && !tok.tagExists( IceTag.tagVerbThirdSingularPast ) )
			{
				tok.addTag( IceTag.tagVerbThirdSingularPast );
				found = true;
			}
			else
			if( tok.tagExists( IceTag.tagVerbThirdSingularPast ) && !tok.tagExists( IceTag.tagVerbFirstSingularPast ) )
			{
				tok.addTag( IceTag.tagVerbFirstSingularPast );
				found = true;
			}
		}
		// reyn-ir
		else if( tok.lexeme.endsWith( "ir" ) && tok.isVerbActive() )
		{
			if( tok.tagExists( IceTag.tagVerbThirdSingular ) && !tok.tagExists( IceTag.tagVerbSecondSingular ) )
			{
				tok.addTag( IceTag.tagVerbSecondSingular );
				found = true;
			}
			else if( tok.tagExists( IceTag.tagVerbSecondSingular ) && !tok.tagExists( IceTag.tagVerbThirdSingular ) )
			{
				tok.addTag( IceTag.tagVerbThirdSingular );
				found = true;
			}
		}
        // ég mæli
        else if( tok.lexeme.endsWith( "i" ) && tok.isVerbSubjunctive() )
        {
            if( tok.tagExists( IceTag.tagVerbThirdSingularSubjunctive ) ||
                tok.tagExists( IceTag.tagVerbFirstSingularSubjunctive ) ||
                tok.tagExists( IceTag.tagVerbThirdPluralSubjunctive ))
            {
				tok.addTag( IceTag.tagVerbThirdSingularSubjunctive );
                tok.addTag( IceTag.tagVerbFirstSingularSubjunctive );
                tok.addTag( IceTag.tagVerbThirdPluralSubjunctive );
                found = true;
			}
        }

        if( !found && !tok.isVerbAuxiliary() && !tok.isVerbSpecialAuxiliary() )
		{
			// Don't generate for verbs that are only subjunctive
			if( ((!tok.isVerbSubjunctive() || tok.isVerbActive()) &&
			     tok.lexeme.endsWith( "i" ) || tok.lexeme.endsWith( "ir" )) ||
			                                                                tok.isVerbPastPart() ||
			                                                                (tok.isVerbMiddleForm() && tok.lexeme.endsWith( "st" )) )
			// tok.lexeme.endsWith("st"))
			{
				dummyToken.clearTags();
				dummyToken.lexeme = tok.lexeme;
				found = endingAnalysis( dummyToken, IceTag.WordClass.wcUndef, tok.isUnknown() );
				if( found )
				{
					if( logger != null )
						logger.log( "Missing verb tags: " + tok.lexeme + " " + tok.allTagStrings() );
					dummyToken.removeAllButVerbs( true );
					if( dummyToken.numTags() > 0 )
					{
						tok.addAllTags( dummyToken.allTagStrings() );
						if( logger != null )
							logger.log( "Missing verb tags, after addition: " + tok.lexeme + " " + tok.allTagStrings() );
					}
				}
			}

		}
		return found;
	}

	private boolean checkAdjectiveClasses( IceTokenTags tok )
	{
		boolean found = false;
		char decl, degree;

		if( tok.lexeme.endsWith( "um" ) )  // góðum
		{
			found = true;
			addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cSingular, IceTag.cDative, IceTag.cStrong, IceTag.cPositive );
			addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cPlural, IceTag.cDative, IceTag.cStrong, IceTag.cPositive );
			addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cPlural, IceTag.cDative, IceTag.cStrong, IceTag.cPositive );
			addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cPlural, IceTag.cDative, IceTag.cStrong, IceTag.cPositive );
		}
        // blautra
        else if (tok.isAdjectiveStrong() && tok.isAdjectivePositive() && tok.isOnlyCase(IceTag.cGenitive) && tok.isOnlyNumber(IceTag.cPlural))
        {
            // If found is set to true then tags from ending analysis are not added.
            // In this case it might be harmful not to do ending analysis, because in some cases lhenvm should be added
            // Example: "raunverulegra"
            //found = true;
            addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cPlural, IceTag.cGenitive, IceTag.cStrong, IceTag.cPositive );
            addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cPlural, IceTag.cGenitive, IceTag.cStrong, IceTag.cPositive );
            addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cPlural, IceTag.cGenitive, IceTag.cStrong, IceTag.cPositive );
        }
        else if( tok.isAdjectiveComparative() )
		{
            if (tok.lexeme.endsWith( "a" )) // fallegra barn
            {
                found = true;
			    addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative, IceTag.cWeak, IceTag.cComparative );
			    addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cSingular, IceTag.cAccusative, IceTag.cWeak, IceTag.cComparative );
			    addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cSingular, IceTag.cDative, IceTag.cWeak, IceTag.cComparative );
			    addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cSingular, IceTag.cGenitive, IceTag.cWeak, IceTag.cComparative );
            }
            else if (tok.lexeme.endsWith( "i" )) // fallegri maður
            {
                found = true;
                addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cSingular, IceTag.cNominative, IceTag.cWeak, IceTag.cComparative );
                addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cSingular, IceTag.cAccusative, IceTag.cWeak, IceTag.cComparative );
                addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cSingular, IceTag.cDative, IceTag.cWeak, IceTag.cComparative );
                addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cSingular, IceTag.cGenitive, IceTag.cWeak, IceTag.cComparative );

                addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cSingular, IceTag.cNominative, IceTag.cWeak, IceTag.cComparative );
                addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cSingular, IceTag.cAccusative, IceTag.cWeak, IceTag.cComparative );
                addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cSingular, IceTag.cDative, IceTag.cWeak, IceTag.cComparative );
                addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cSingular, IceTag.cGenitive, IceTag.cWeak, IceTag.cComparative );

                addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cPlural, IceTag.cNominative, IceTag.cWeak, IceTag.cComparative );
                addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cPlural, IceTag.cAccusative, IceTag.cWeak, IceTag.cComparative );
                addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cPlural, IceTag.cDative, IceTag.cWeak, IceTag.cComparative );
		        addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cPlural, IceTag.cGenitive, IceTag.cWeak, IceTag.cComparative );
                addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cPlural, IceTag.cNominative, IceTag.cWeak, IceTag.cComparative );
                addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cPlural, IceTag.cAccusative, IceTag.cWeak, IceTag.cComparative );
                addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cPlural, IceTag.cDative, IceTag.cWeak, IceTag.cComparative );
		        addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cPlural, IceTag.cGenitive, IceTag.cWeak, IceTag.cComparative );
                addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cPlural, IceTag.cNominative, IceTag.cWeak, IceTag.cComparative );
                addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cPlural, IceTag.cAccusative, IceTag.cWeak, IceTag.cComparative );
                addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cPlural, IceTag.cDative, IceTag.cWeak, IceTag.cComparative );
		        addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cPlural, IceTag.cGenitive, IceTag.cWeak, IceTag.cComparative );
            }
        }
		else if( tok.isAdjectiveIndeclineable() ||
		         // only applies to fallegri maður, kona, menn, konur, börn
		         // but not to fallegra barn!
		         (tok.isAdjectiveComparative() && tok.lexeme.endsWith( "i" )) )
		{
			found = true;
			if( tok.isAdjectiveComparative() )
			{
				decl = IceTag.cWeak;
				degree = IceTag.cComparative;
			}
			else
			{
				decl = IceTag.cIndeclineable;
				degree = IceTag.cPositive;
			}

			addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cSingular, IceTag.cNominative, decl, degree );
			addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cSingular, IceTag.cAccusative, decl, degree );
			addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cSingular, IceTag.cDative, decl, degree );
			addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cSingular, IceTag.cGenitive, decl, degree );

			addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cSingular, IceTag.cNominative, decl, degree );
			addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cSingular, IceTag.cAccusative, decl, degree );
			addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cSingular, IceTag.cDative, decl, degree );
			addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cSingular, IceTag.cGenitive, decl, degree );

			if( tok.isAdjectiveIndeclineable() )
			{
				addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative, decl, degree );
				addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cSingular, IceTag.cAccusative, decl, degree );
				addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cSingular, IceTag.cDative, decl, degree );
				addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cSingular, IceTag.cGenitive, decl, degree );
			}

			addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cPlural, IceTag.cNominative, decl, degree );
			addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cPlural, IceTag.cAccusative, decl, degree );
			addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cPlural, IceTag.cDative, decl, degree );
			addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cPlural, IceTag.cGenitive, decl, degree );

			addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cPlural, IceTag.cNominative, decl, degree );
			addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cPlural, IceTag.cAccusative, decl, degree );
			addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cPlural, IceTag.cDative, decl, degree );
			addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cPlural, IceTag.cGenitive, decl, degree );

			addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cPlural, IceTag.cNominative, decl, degree );
			addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cPlural, IceTag.cAccusative, decl, degree );
			addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cPlural, IceTag.cDative, decl, degree );
			addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cPlural, IceTag.cGenitive, decl, degree );
		}
		else if( tok.isAdjectiveWeak() && tok.isAdjectivePositive() && tok.lexeme.endsWith( "u" ) ) // blautu
		{
			found = true;
			// lheþsf_lveovf_lveþvf_lveevf_lkfnvf_lkfovf_lkfþvf_lkfevf_lvfnvf_lvfovf_lvfþvf_lvfevf_lhfnvf_lhfovf_lhfþvf_lhfevf
			addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cSingular, IceTag.cDative, IceTag.cStrong, IceTag.cPositive );

			addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cSingular, IceTag.cAccusative, IceTag.cWeak, IceTag.cPositive );
			addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cSingular, IceTag.cDative, IceTag.cWeak, IceTag.cPositive );
			addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cSingular, IceTag.cGenitive, IceTag.cWeak, IceTag.cPositive );

			addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cPlural, IceTag.cNominative, IceTag.cWeak, IceTag.cPositive );
			addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cPlural, IceTag.cAccusative, IceTag.cWeak, IceTag.cPositive );
			addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cPlural, IceTag.cDative, IceTag.cWeak, IceTag.cPositive );
			addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cPlural, IceTag.cGenitive, IceTag.cWeak, IceTag.cPositive );
			addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cPlural, IceTag.cNominative, IceTag.cWeak, IceTag.cPositive );
			addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cPlural, IceTag.cAccusative, IceTag.cWeak, IceTag.cPositive );
			addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cPlural, IceTag.cDative, IceTag.cWeak, IceTag.cPositive );
			addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cPlural, IceTag.cGenitive, IceTag.cWeak, IceTag.cPositive );
			addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cPlural, IceTag.cNominative, IceTag.cWeak, IceTag.cPositive );
			addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cPlural, IceTag.cAccusative, IceTag.cWeak, IceTag.cPositive );
			addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cPlural, IceTag.cDative, IceTag.cWeak, IceTag.cPositive );
			addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cPlural, IceTag.cGenitive, IceTag.cWeak, IceTag.cPositive );
		}
        else if( tok.isAdjectiveWeak() && tok.isAdjectivePositive() && tok.lexeme.endsWith( "a" ) ) // blauta
		{
			found = true;
            // lkfosf_lkeovf_lkeþvf_lkeevf_lveosf_lvenvf_lhenvf_lheovf_lheþvf_lheevf
			addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cPlural, IceTag.cAccusative, IceTag.cStrong, IceTag.cPositive );
            addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cSingular, IceTag.cAccusative, IceTag.cWeak, IceTag.cPositive );
            addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cSingular, IceTag.cDative, IceTag.cWeak, IceTag.cPositive );
            addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cSingular, IceTag.cGenitive, IceTag.cWeak, IceTag.cPositive );

            addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cSingular, IceTag.cAccusative, IceTag.cStrong, IceTag.cPositive );
			addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cSingular, IceTag.cNominative, IceTag.cWeak, IceTag.cPositive );

            addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative, IceTag.cWeak, IceTag.cPositive );
            addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cSingular, IceTag.cAccusative, IceTag.cWeak, IceTag.cPositive );
            addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cSingular, IceTag.cDative, IceTag.cWeak, IceTag.cPositive );
            addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cSingular, IceTag.cGenitive, IceTag.cWeak, IceTag.cPositive );
        }

        else if( tok.isAdjectiveWeak() && tok.isAdjectiveSuper() && tok.lexeme.endsWith( "u" ) ) // stærstu
		{
			found = true;
			addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cPlural, IceTag.cNominative, IceTag.cWeak, IceTag.cSuperlative );
            addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cPlural, IceTag.cAccusative, IceTag.cWeak, IceTag.cSuperlative );
            addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cPlural, IceTag.cDative, IceTag.cWeak, IceTag.cSuperlative );
		    addAdjectiveTag( tok, IceTag.cMasculine, IceTag.cPlural, IceTag.cGenitive, IceTag.cWeak, IceTag.cSuperlative );
            addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cPlural, IceTag.cNominative, IceTag.cWeak, IceTag.cSuperlative );
            addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cPlural, IceTag.cAccusative, IceTag.cWeak, IceTag.cSuperlative );
            addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cPlural, IceTag.cDative, IceTag.cWeak, IceTag.cSuperlative );
		    addAdjectiveTag( tok, IceTag.cFeminine, IceTag.cPlural, IceTag.cGenitive, IceTag.cWeak, IceTag.cSuperlative );
            addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cPlural, IceTag.cNominative, IceTag.cWeak, IceTag.cSuperlative );
            addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cPlural, IceTag.cAccusative, IceTag.cWeak, IceTag.cSuperlative );
            addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cPlural, IceTag.cDative, IceTag.cWeak, IceTag.cSuperlative );
		    addAdjectiveTag( tok, IceTag.cNeuter, IceTag.cPlural, IceTag.cGenitive, IceTag.cWeak, IceTag.cSuperlative );
        }
        // almenn lvensf_lhfnsf_lhfosf
        else if (tok.tagExists( IceTag.tagAdjectiveFeminineSingularStrongNominative) ||
                 tok.tagExists( IceTag.tagAdjectiveNeuterPluralStrongNominative) ||
                 tok.tagExists( IceTag.tagAdjectiveNeuterPluralStrongAccusative)
                 )
        {
                // If found is set to true then tags from ending analysis are not added.
                // In this case it might be harmful not to do ending analysis, because in some cases lkensf should be added
                // Example: "stór"

                //found = true;
                tok.addTag(IceTag.tagAdjectiveFeminineSingularStrongNominative);
                tok.addTag(IceTag.tagAdjectiveNeuterPluralStrongNominative);
                tok.addTag(IceTag.tagAdjectiveNeuterPluralStrongAccusative);
        }

        return found;
	}

	private boolean generateMissingAdj( IceTokenTags tok )
			//throws IOException
	{
		boolean found;
		if( logger != null )
			logger.log( "Missing adj tags: " + tok.lexeme + " " + tok.allTagStrings() );

		found = checkAdjectiveClasses( tok );
		if( !found )
		{
            found = endingAnalysis( tok, IceTag.WordClass.wcAdj, tok.isUnknown() );
			if( tok.lexeme.endsWith( "lega" ) )    // fallega
				tok.addTag( IceTag.tagAdverb );
		}
		if( found )
		{
			if( logger != null )
				logger.log( "Missing adj tags, after addition: " + tok.lexeme + " " + tok.allTagStrings() );
		}

		return found;
	}

	private boolean checkNounClasses( IceTokenTags tok )
			//throws IOException
	{
		boolean weakMasculine;
		boolean onlyNeuter;
		boolean feminineSpec = false;   // same in nominative, accusative, dative
		boolean feminineSpec2 = false;   // same in nominative singular, genitive plural (róla)
		boolean found = false;

		//boolean noArticle = !tok.hasArticle();
		onlyNeuter = tok.isOnlyGender( IceTag.cNeuter );
		// skóli-SKÓLA-SKÓLA-SKÓLA , skólar-SKÓLA-skólum-SKÓLA, make sure the plural is also generated
		if( !onlyNeuter )
		{
			weakMasculine = tok.lexeme.endsWith( "a" ) && tok.isOnlyGender( IceTag.cMasculine ) && !tok.isOnlyNumber( IceTag.cPlural ) && !tok.isCase( IceTag.cNominative );
			if( !weakMasculine )
			{
				char lastChar = tok.lexeme.charAt( tok.lexeme.length() - 1 );
				// vist-vist-vist-vistar but not rigning,systir, steypireyður
				feminineSpec = tok.isOnlyGender( IceTag.cFeminine ) && !tok.isCase( IceTag.cGenitive ) && !tok.hasArticle() && !isVowel( lastChar ) &&
				               !(lastChar == '-' || lastChar == '.') &&
				               !tok.lexeme.endsWith( "ing" ) && !tok.lexeme.endsWith( "ir" ) && !tok.lexeme.endsWith( "ur" ) && tok.isOnlyNumber( IceTag.cSingular );
				if( !feminineSpec )  // konur (nf), konur (þf)
					feminineSpec2 = tok.isOnlyGender( IceTag.cFeminine ) && tok.isOnlyNumber( IceTag.cPlural ) && !tok.hasArticle() &&
					                (tok.isCase( IceTag.cNominative ) || tok.isCase( IceTag.cAccusative ));

			}
		}

		// If only neuter and both singular and plural then we can generate all the tags
		// svín (et, nf), svin (et, þf), svín (ft, nf), svín (ft, þf)
		// veski(et, nf), veski (et, þf), veski (et, þgf), veski (ft, nf), veski (ft, þf)
        if (onlyNeuter) {
          if( !tok.hasArticle() && !tok.isCase( IceTag.cGenitive ) )
          {
			if( !tok.isCase( IceTag.cDative ) && !tok.lexeme.endsWith( "i" ) )
			{
				if( tok.isNumber( IceTag.cSingular ) )
				{
					tok.addAllTags( IceTag.tagNounNeuterSingular );
					// Don't add the plural tags if u-hljóðvarp is likely, bað-böð, land-lönd, far-för
					String pluralLexeme = hljodVarp( tok.lexeme, 'a', 'ö' );   // A little trick, the function is usually used to produce the root
					if( pluralLexeme == null )    // then u-hljodvar was not possible
						tok.addAllTags( IceTag.tagNounNeuterPlural );
				}
				else if( tok.isNumber( IceTag.cPlural ) )
				{
					tok.addAllTags( IceTag.tagNounNeuterPlural );
				}
				found = true;
			}
			else
			if( tok.lexeme.endsWith( "i" ) && (tok.isCase( IceTag.cNominative ) || tok.isCase( IceTag.cAccusative )) )
			{
				tok.addAllTags( IceTag.tagNounNeuter );
				found = true;
			}
          }
          // 08.05.2008 HL

          else if (tok.hasArticle() && (tok.isCase(IceTag.cNominative) || tok.isCase(IceTag.cAccusative))) {
                if( tok.isNumber( IceTag.cSingular ) ) {
                    tok.addAllTags( IceTag.tagNounNeuterSingularArticle );
                    found = true;
                }
                if( tok.isNumber( IceTag.cPlural ) ) {   // t.d. þökin
                    tok.addAllTags( IceTag.tagNounNeuterPluralArticle );
                    found = true;
                }
          }
          if( found )
		  {
				if( logger != null )
					logger.log( "Missing noun tags, NEUTER after addition: " + tok.lexeme + " " + tok.allTagStrings() );
				return true;
		  }
		}
		else if( feminineSpec )
		{
			tok.addAllTags( IceTag.tagNounFeminineFirstThree );
			return true;
		}
		else if( feminineSpec2 )  // konur, konur
		{
			addNounTag( tok, IceTag.cFeminine, IceTag.cPlural, IceTag.cNominative, false );
			addNounTag( tok, IceTag.cFeminine, IceTag.cPlural, IceTag.cAccusative, false );
			return true;
		}

		return false;
	}


	private boolean generateMissingNoun( IceTokenTags tok )
			//throws IOException
	{
		boolean weakMasculine = false;
		boolean masculineType2 = false;
		boolean onlyNeuter = false;

		boolean found = false;

		boolean noArticle = !tok.hasArticle();
		onlyNeuter = tok.isOnlyGender( IceTag.cNeuter );
		// skóli-SKÓLA-SKÓLA-SKÓLA , skólar-SKÓLA-skólum-SKÓLA, make sure the plural is also generated
		if( !onlyNeuter )
		{
			weakMasculine = tok.lexeme.endsWith( "a" ) && tok.isOnlyGender( IceTag.cMasculine ) && !tok.isOnlyNumber( IceTag.cPlural ) && !tok.isCase( IceTag.cNominative );
		}

		found = checkNounClasses( tok );

		if( !found )
		{
			if( !onlyNeuter )
				masculineType2 = tok.lexeme.endsWith( "i" ) && tok.isOnlyGender( IceTag.cMasculine ) && tok.isOnlyCase( IceTag.cNominative );

			// Only generate missing tags if the token has one gender and number and is not a masculine/neuter singular ending
			// with i/s (dative, genitive)
			if( !masculineType2 && (onlyNeuter || (tok.isOnlyOneGenderNumber() && tok.numCases() < 3)) ) // &&
			{
				found = false;
				// Fill in tag gaps for noun that have only one gender, number

				if( logger != null )
					logger.log( "Missing noun tags: " + tok.lexeme + " " + tok.allTagStrings() );
				int numBefore = tok.numTags();
				IceTag tag = (IceTag)tok.getFirstTag();
				char gender = tag.getPersonGenderLetter();
				char number = tag.getNumberLetter();

				if( tok.hasArticle() )
				{
					found = suffixAnalysisArticle( tok, false );
					if( !found )
						found = endingAnalysis( tok, IceTag.WordClass.wcNoun, tok.isUnknown() );
				}
				else
				{
					boolean found1 = suffixAnalysisCases( tok, false );
					boolean found2 = suffixAnalysisCasesPlural( tok );
					found = found1 || found2;

					if( !found )
						found = endingAnalysis( tok, IceTag.WordClass.wcNoun, tok.isUnknown() );
				}

				if( found )
				{
					tok.removeAllBut( IceTag.WordClass.wcNoun );
					tok.removeAllButGender( gender, true );   // remove all inappropriate genders
					if( !onlyNeuter && !weakMasculine )
						tok.removeAllButNumber( number );
					if( noArticle )
						tok.removeArticle();
					int numAfter = tok.numTags();
					if( numBefore != numAfter )
					{
						if( logger != null )
							logger.log( "Missing noun tags, after addition: " + tok.lexeme + " " + tok.allTagStrings() );
					}
				}
			}
		}
		return found;
	}

	private boolean generateMissingProperNoun( IceTokenTags tok )
			//throws IOException
	{
		boolean found = false;
		boolean weakMasculine = tok.lexeme.endsWith( "a" ) && tok.isOnlyGender( IceTag.cMasculine ) && tok.isOnlyNumber( IceTag.cSingular ) && !tok.isCase( IceTag.cNominative );
		if( weakMasculine )
		{
			found = true;
			char properNounType = ((IceTag)tok.getFirstTag()).getProperNounType();
			addProperNounTag( tok, IceTag.cMasculine, IceTag.cSingular, IceTag.cAccusative, properNounType );
			addProperNounTag( tok, IceTag.cMasculine, IceTag.cSingular, IceTag.cDative, properNounType );
			addProperNounTag( tok, IceTag.cMasculine, IceTag.cSingular, IceTag.cGenitive, properNounType );
		}
		else
		if( tok.lexeme.endsWith( "u" ) && tok.isGenderPerson( IceTag.cFeminine ) && tok.isNumber( IceTag.cSingular ) && !tok.isCase( IceTag.cNominative ) )  // Siggu
		{
			found = true;
			char properNounType = ((IceTag)tok.getFirstTag()).getProperNounType();
			addProperNounTag( tok, IceTag.cFeminine, IceTag.cSingular, IceTag.cAccusative, properNounType );
			addProperNounTag( tok, IceTag.cFeminine, IceTag.cSingular, IceTag.cDative, properNounType );
			addProperNounTag( tok, IceTag.cFeminine, IceTag.cSingular, IceTag.cGenitive, properNounType );
		}

		else if( isForeignLexeme( tok.lexeme ) )   // Then all cases apply
		{
			found = true;
			IceTag tag = (IceTag)tok.getFirstTag();
			char properNounType = tag.getProperNounType();
			char gender = tag.getPersonGenderLetter();
			char number = tag.getNumberLetter();
			addProperNounTag( tok, gender, number, IceTag.cNominative, properNounType );
			addProperNounTag( tok, gender, number, IceTag.cAccusative, properNounType );
			addProperNounTag( tok, gender, number, IceTag.cDative, properNounType );
			addProperNounTag( tok, gender, number, IceTag.cGenitive, properNounType );

		}
		else
		{
			IceTag tag = (IceTag)tok.getFirstTag();
			char gender = tag.getPersonGenderLetter();   // nxee-ö
			char number = tag.getNumberLetter();
			char properNounType = tag.getProperNounType();
			if( gender == IceTag.cGenderUnspec )
			{
				addProperNounTag( tok, gender, number, IceTag.cNominative, properNounType );
				addProperNounTag( tok, gender, number, IceTag.cAccusative, properNounType );
				addProperNounTag( tok, gender, number, IceTag.cDative, properNounType );
				addProperNounTag( tok, gender, number, IceTag.cGenitive, properNounType );
			}
		}

		if( found )
		{
			if( logger != null )
				logger.log( "Missing proper noun tags, after addition: " + tok.lexeme + " " + tok.allTagStrings() );
		}
		return found;
	}

	public void generateMissingTags( IceTokenTags tok )
			//throws IOException
	{
		boolean found;

		if( (tok.isOnlyWordClass( IceTag.WordClass.wcVerb ) ||
             tok.isOnlyWordClass( IceTag.WordClass.wcVerbPastPart ) ||
		     tok.isVerbInfinitive()) &&
		     !tok.isVerbCaseMarking() && !tok.isVerbAuxiliary() &&
		     !tok.isVerbBe() )
		{
			generateMissingVerb( tok );
		}

		else
		{
			if( tok.isAdjective() )
			{
				// Use a dummy token with all but adjectives removed
				IceTokenTags dumToken = new IceTokenTags();
				dumToken.lexeme = tok.lexeme;
				dumToken.setAllTags( tok.allTagStrings() );
				dumToken.removeAllBut( IceTag.WordClass.wcAdj );
				found = generateMissingAdj( dumToken );
				if( found )
					tok.addAllTags( dumToken.allTagStrings() );  // This tok might have some other tags than adjectives
			}
			if( tok.isNoun() )
			{
				// Use a dummy token with all but nouns removed
				IceTokenTags dumToken = new IceTokenTags();
				dumToken.lexeme = tok.lexeme;
				dumToken.setAllTags( tok.allTagStrings() );
				dumToken.removeAllBut( IceTag.WordClass.wcNoun );
				found = generateMissingNoun( dumToken );
				if( found )
					tok.addAllTags( dumToken.allTagStrings() );

			}
			if( tok.isOnlyWordClass( IceTag.WordClass.wcProperNoun ) )
				generateMissingProperNoun( tok );
		}
	}

    public String dictionaryLookup( String lexeme, boolean ignoreCase)
    {
		String tagStr = dictionaryBase.lookup( lexeme, ignoreCase );
        if (tagStr == null)
            tagStr = dictionary.lookup( lexeme, ignoreCase );
        return tagStr;
    }

    public void dictionaryTokenLookup( IceTokenTags tok, boolean ignoreCase )
	{
        String tagStr = dictionaryLookup( tok.lexeme, ignoreCase );
        // Add all possible tags; tags are separated by "_"
		if( tagStr != null )
			tok.addAllTags( tagStr );
	}

	private boolean obviousProperNounEndings( IceTokenTags currToken )
	{
		boolean found = false;
		String lex = currToken.lexeme;

		if( lex.endsWith( "son" ) )
		{
			addNounTag( currToken, IceTag.cMasculine, IceTag.cSingular, IceTag.cNominative, false );
			addNounTag( currToken, IceTag.cMasculine, IceTag.cSingular, IceTag.cAccusative, false );
			found = true;
		}
		else if( lex.endsWith( "synir" ) )
		{
			addNounTag( currToken, IceTag.cMasculine, IceTag.cPlural, IceTag.cNominative, false );
			found = true;
		}
		else if( lex.endsWith( "syni" ) )
		{
			addNounTag( currToken, IceTag.cMasculine, IceTag.cSingular, IceTag.cDative, false );
			found = true;
		}
		else if( lex.endsWith( "sonar" ) )
		{
			addNounTag( currToken, IceTag.cMasculine, IceTag.cSingular, IceTag.cGenitive, false );
			found = true;
		}
		else if( lex.endsWith( "sona" ) )
		{
			addNounTag( currToken, IceTag.cMasculine, IceTag.cPlural, IceTag.cAccusative, false );
			addNounTag( currToken, IceTag.cMasculine, IceTag.cPlural, IceTag.cGenitive, false );
			found = true;
		}

		else if( lex.endsWith( "dóttir" ) )
		{
			addNounTag( currToken, IceTag.cFeminine, IceTag.cSingular, IceTag.cNominative, false );
			found = true;
		}
		else if( lex.endsWith( "dóttur" ) )
		{
			addNounTag( currToken, IceTag.cFeminine, IceTag.cSingular, IceTag.cAccusative, false );
			addNounTag( currToken, IceTag.cFeminine, IceTag.cSingular, IceTag.cDative, false );
			addNounTag( currToken, IceTag.cFeminine, IceTag.cSingular, IceTag.cGenitive, false );
			found = true;
		}

		if( found )
		{
			currToken.setProperNameType( IceTag.cPersonName );
			currToken.setUnknownType( IceTokenTags.UnknownType.ending );
		}

		return found;
	}


	private boolean analyzeProperNounSuffix( IceTokenTags currToken, boolean certainProperNoun )
			//throws IOException
	{
		boolean found = false;

		// First do analysis based on suffix removal
		if( !found && certainProperNoun )
		{
			found = morphoAnalysisSuffix( currToken, true );

            if( found )
			{
				currToken.removeAllBut( IceTag.WordClass.wcProperNoun ); // Only interested in proper noun tags
				if( currToken.noTags() )
				{
					currToken.clearTags();
					found = false;
				}
				else
				{
					if( logger != null )
						logger.log( "Proper noun analysis suffix: morpho " + currToken.lexeme + " " + currToken.allTagStrings() );
				}
			}
		}

		return found;
	}



	public boolean analyzeProperNoun( IceTokenTags currToken, boolean certainProperNoun )
			//throws IOException
    /*
	  * Analyzes proper nouns.
	  * certainProperNoun=true if the token is known to be a proper noun
	  * certainProperNoun=false if the token is not known to be a proper noun, e.g. is a first word of a sentence
	  */
	{
		boolean found;

        found = obviousProperNounEndings( currToken );

        // Needs to be commented out if only running the ending analyser
        if( !found )
			found = analyzeProperNounSuffix( currToken, certainProperNoun );
        
        // Then try lookup from endings
		if( !found )
		{
			found = endingAnalysisProper( currToken );

			if( found )
			{
				if( logger != null )
					logger.log( "Proper noun analysis suffix: Case ending " + currToken.lexeme + " " + currToken.allTagStrings() );
			}
		}

		// Make sure that person names have only singular, masculine or feminine tags
		if( found ) //&& certainProperNoun)
		{
			ArrayList tags = currToken.getTags();
			for( int i = 0; i <= tags.size() - 1; i++ )
			{
				IceTag tag = (IceTag)tags.get( i );
				if( tag.getProperNounType() == IceTag.cPersonName &&
				    (tag.isGender( IceTag.cNeuter ) || tag.isNumber( IceTag.cPlural )) )
					tag.setValid( false );
			}
			currToken.removeInvalidTags();
		}

		// default case
		if( !found )
		{
			defaultProperNoun( currToken );
			found = true;
		}
		return found;
	}

	private void defaultProperNoun( IceTokenTags currToken )
	{
		IceTag tag;
		currToken.setUnknownType( IceTokenTags.UnknownType.guessed );
		if( currToken.numTags() > 0 )   // could be the case for an induced proper noun
			tag = (IceTag)currToken.getFirstTag();
		else
			tag = new IceTag( IceTag.tagProperNoun );

		char gender = IceTag.cMasculine;
		// Possibly a feminine?
		if( currToken.lexeme.endsWith( "a" ) || currToken.lexeme.endsWith( "u" ) )
			gender = IceTag.cFeminine;

		tag.setGenderNumberCase( gender, IceTag.cSingular, IceTag.cNominative );
		currToken.setTag( tag );
		addProperNounTag( currToken, gender, IceTag.cSingular, IceTag.cAccusative, IceTag.cPersonName );
		addProperNounTag( currToken, gender, IceTag.cSingular, IceTag.cDative, IceTag.cPersonName );
		addProperNounTag( currToken, gender, IceTag.cSingular, IceTag.cGenitive, IceTag.cPersonName );
	}

    private boolean searchVerb( String root, MorphoClass mClass, IceTokenTags currToken,
                                Mood mood, Voice voice,
                                char tenseLetter, char personLetter, char numberLetter, char personLetter2, char numberLetter2, boolean isUnknown )
	{
		String tagStr, searchStr = null;
		IceTag dumTag, dumTag2, dumTag3;
		boolean found = false;
		boolean done = false;

		setSearchStrings( root, mClass );         // set the search strings
		for( int i = 1; i <= 23 && !done; i++ )
		{
            searchStr = searchStrings[i];
			if( searchStr != null )
			{
                tagStr = dictionaryLookup( searchStr, true );
                if( tagStr != null )
				{
				   dummyToken.clearTags();
				   dummyToken.setAllTags( tagStr );
				   if( dummyToken.isVerbAny() )
				   {
                      found = true;
					  done = true;
                      if (isUnknown)
                      {
						if( personLetter != IceTag.cGenderUnspec )
						{
							if( mood == Mood.Imperative )
							{
								dumTag = new IceTag( IceTag.tagVerbImperative );
								dumTag.setTense( tenseLetter );
								dumTag.setPersonGender( personLetter );
								dumTag.setNumber( numberLetter );
								currToken.addTag( dumTag );
							}
							if( mood == Mood.Indicative || mood == Mood.IndicativeSubjunctive)
							{
								dumTag = new IceTag( IceTag.tagVerb );
								dumTag.setTense( tenseLetter );
								dumTag.setPersonGender( personLetter );
								dumTag.setNumber( numberLetter );
                                if (voice == Voice.Middle)
                                    dumTag.setVoice(IceTag.cMiddle);
                                currToken.addTag( dumTag );
								// Add another person?
								if( personLetter2 != IceTag.cGenderUnspec )
								{
									dumTag3 = new IceTag( dumTag.getTagStr() );
									dumTag3.setPersonGender( personLetter2 );
									dumTag3.setNumber( numberLetter2 );
									currToken.addTag( dumTag3 );
								}
							}
							if( mood == Mood.Subjunctive || mood == Mood.IndicativeSubjunctive)
							{
								dumTag2 = new IceTag( IceTag.tagVerbSubjunctive );
								dumTag2.setTense( tenseLetter );
								dumTag2.setPersonGender( personLetter );
								dumTag2.setNumber( numberLetter );
                                if (voice == Voice.Middle)
                                    dumTag2.setVoice(IceTag.cMiddle);
                                currToken.addTag( dumTag2 );
								if( personLetter2 != IceTag.cGenderUnspec )
								{
									dumTag3 = new IceTag( dumTag2.getTagStr() );
									dumTag3.setPersonGender( personLetter2 );
									dumTag3.setNumber( numberLetter2 );
									currToken.addTag( dumTag3 );
								}
							}

						}
                      }
					}
				}
			}
		}
		if( found && isUnknown)
		{
			currToken.setUnknownType( IceTokenTags.UnknownType.morpho );
			if( logger != null )
				logger.log( "Search verb: " + searchStr + " " + currToken.lexeme + " " + currToken.allTagStrings() );
		}
		return found;
	}

	// Try to find compound verbs.  Only strip of the first 2,3 or 4 character prefixes
	public boolean compoundVerbAnalysis( IceTokenTags currToken )
	{
		boolean found = false;
		String suffix = null, root = null;

		int len = currToken.lexeme.length();
		if( len > 6 )
		{
			for( int k = 2; k <= 4 && !found; k++ )
			{
				suffix = currToken.lexeme.substring( k, len );
                String tagStr = dictionaryLookup( suffix, true );
                if( tagStr != null )
				{
					dummyToken.clearTags();
					dummyToken.addAllTags( tagStr );
					dummyToken.removeAllButVerbs( true );
					if( dummyToken.numTags() > 0 && dummyToken.isOnlyVerbAny() )
					{
						currToken.addAllTags( dummyToken.allTagStrings() );
						found = true;
						currToken.setUnknownType( IceTokenTags.UnknownType.morpho );
						if( logger != null )
							logger.log( "Compound verb analysis: " + suffix + " " + currToken.lexeme + " " + currToken.allTagStrings() );
					}
				}
			}
		}
		return found;
	}

	public boolean verbAnalysis( IceTokenTags currToken, IceTokenTags prevToken, boolean isUnknown )
	{
		String lex, root;
		boolean found = false;

		lex = currToken.lexeme;
		int len = lex.length();

		if( lex.endsWith( "a" ) && !lex.endsWith( "aða" ) )
		{
			if( prevToken != null && prevToken.isInfinitive() && isUnknown )
			{
				currToken.addInfinitiveVerbForm();
				currToken.setUnknownType( IceTokenTags.UnknownType.ending );
				found = true;
			}
			else
			{
				// þeir/ég borða
				root = lex.substring( 0, len - 1 );
				found = searchVerb( root, MorphoClass.VerbActive1, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cPlural, IceTag.cFirstPerson, IceTag.cSingular, isUnknown );   // search for the past tense
				if( !found )  // if found in this case then the verb is only the infinitive
					found = searchVerb( root, MorphoClass.VerbActive2, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cGenderUnspec, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );   // search for the past tense
				if( !found )
					found = searchVerb( root, MorphoClass.VerbActive5, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cGenderUnspec, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );   // search for the past tense
				if( !found && lex.endsWith( "ja" ) )   // ræsk-ja, þyng-ja
				{
					root = lex.substring( 0, len - 2 );
					found = searchVerb( root, MorphoClass.VerbActive5, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cGenderUnspec, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );   // search for the past tense
				}

				// Add the infinitive tag
				if( found && isUnknown )
					currToken.addTagFront( IceTag.tagVerbInfActive );
			}
		}
		// present participle
		else if( lex.endsWith( "andi" ) )
		{
			// espandi, hlaupandi
			root = lex.substring( 0, len - 4 );   // espa, hlaupa
			found = searchVerb( root, MorphoClass.VerbActive1, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cGenderUnspec, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
			if( found && isUnknown)
			{  // present participle
				currToken.addAllTags( IceTag.tagAdjectivesIndeclineable );
				currToken.addTag( IceTag.tagVerbPresentPart );
			}
		}
		else if( lex.endsWith( "mdar" ) )
		{
			// gleymdar, skemmdar
			root = lex.substring( 0, len - 2 );   // gleym is root
			found = searchVerb( root, MorphoClass.VerbActive1, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cGenderUnspec, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
			if( found && isUnknown)
			{  // past participle
				addPastParticipleTag( currToken, IceTag.cFeminine, IceTag.cSingular, IceTag.cNominative );
			}
		}
		else if( lex.endsWith( "ngdar" ) )
		{
			// flengdar, hengdar
			root = lex.substring( 0, len - 3 );   // fleng is root
			found = searchVerb( root, MorphoClass.VerbActive5, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cGenderUnspec, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
			if( found && isUnknown)
			{  // past participle
				addPastParticipleTag( currToken, IceTag.cFeminine, IceTag.cPlural, IceTag.cNominative );
			}
		}

				else if (lex.endsWith("aður"))
				{
					// borð-aður
					root = lex.substring(0,len-4);
					found = searchVerb(root, MorphoClass.VerbActive1, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cGenderUnspec, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown);
					if (found && isUnknown) {  // past participle
						addPastParticipleTag(currToken, IceTag.cMasculine, IceTag.cSingular, IceTag.cNominative);
					}
				}

				else if (lex.endsWith("aðir"))
				{
					// borð-aðir
					root = lex.substring(0,len-4);
					found = searchVerb(root, MorphoClass.VerbActive1, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cGenderUnspec, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown);
					if (found && isUnknown) {  // past participle
						addPastParticipleTag(currToken, IceTag.cMasculine, IceTag.cPlural, IceTag.cNominative);
					}
				}
				else if (lex.endsWith("aðar"))
				{
					// borð-aðar
					root = lex.substring(0,len-4);
					found = searchVerb(root, MorphoClass.VerbActive1, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cGenderUnspec, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown);
					if (found && isUnknown) {   // past participle
						addPastParticipleTag(currToken, IceTag.cFeminine, IceTag.cPlural, IceTag.cNominative);
					}
				}
				else if (lex.endsWith("uð"))
				{
					// borð-uð
					root = lex.substring(0,len-2);
					found = searchVerb(root, MorphoClass.VerbActive1, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cGenderUnspec, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown);
					if (found && isUnknown) {  // past participle
						addPastParticipleTag(currToken, IceTag.cFeminine, IceTag.cSingular, IceTag.cNominative);
                        addPastParticipleTag(currToken, IceTag.cNeuter, IceTag.cPlural, IceTag.cNominative);
                    }
				} 
		else if( (lex.endsWith( "dirðu" ) || lex.endsWith( "ðirðu" )) && len > 8 )
		{    // reyn-dirðu, horf-ðirðu
			root = lex.substring( 0, len - 5 );
			found = searchVerb( root, MorphoClass.VerbActive1, currToken, Mood.Indicative, Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
		}
		else if( (lex.endsWith( "irðu" ) || lex.endsWith( "arðu" ) || lex.endsWith( "urðu" )) && len > 7 )
		{    // svar-arðu, met-urðu, heyr-irðu
			root = lex.substring( 0, len - 4 );
			found = searchVerb( root, MorphoClass.VerbActive1, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
		}
		else if( lex.endsWith( "ar" ) && !(lex.endsWith( "aðar" ) && len > 6) )
		{    // þú/hann borð-ar    the present
			root = lex.substring( 0, len - 2 );
			found = searchVerb( root, MorphoClass.VerbActive1, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular, isUnknown );
		}
		else if( lex.endsWith( "ddum" ) )
		{    // við meiddum    the past
			root = lex.substring( 0, len - 4 );
			found = searchVerb( root, MorphoClass.VerbActive4, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cFirstPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural, isUnknown );
		}
		else if( lex.endsWith( "ttum" ) )
		{    // við breyt-tum    the past
			root = lex.substring( 0, len - 3 );
			found = searchVerb( root, MorphoClass.VerbActive2, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cFirstPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural, isUnknown );
		}
        else if( lex.endsWith( "uðum" ) )
	    {    // borð-uðum    the past
				root = lex.substring( 0, len - 4 );
				found = searchVerb( root, MorphoClass.VerbActive1, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cFirstPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
		}

        else if( lex.endsWith( "dum" ) || lex.endsWith( "tum" ) || lex.endsWith( "ðum" ) )
		{    // við reyn-dum, fyll-tum    the past
			root = lex.substring( 0, len - 3 );
			found = searchVerb( root, MorphoClass.VerbActive2, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cFirstPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural, isUnknown );
			if( !found )
				found = searchVerb( root, MorphoClass.VerbActive5, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cFirstPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural, isUnknown );
			if( !found && lex.endsWith( "tum" ) )   // breyt-um
			{
				root = lex.substring( 0, len - 2 );
				found = searchVerb( root, MorphoClass.VerbActive2, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
				if( !found )
				{
					// hir-tum, hir-ða
					root = lex.substring( 0, len - 3 ) + "ð";
					found = searchVerb( root, MorphoClass.VerbActive2, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cFirstPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural, isUnknown );
				}
				if( !found ) // köstum, glötum
				{
					root = lex.substring( 0, len - 2 );
					// u-hljóðvarp, köstum-kasta
					root = hljodVarp( root, 'ö', 'a' );
					if( root != null )
					{
						found = searchVerb( root, MorphoClass.VerbActive1, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural, isUnknown );
					}
				}
			}
			if( !found && lex.endsWith( "ðum" ) )   // greið-um
			{
				root = lex.substring( 0, len - 2 );
				found = searchVerb( root, MorphoClass.VerbActive2, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
			}
		}
		else if( lex.endsWith( "dduð" ) )
		{    // þið meidduð    the past
			root = lex.substring( 0, len - 4 );
			found = searchVerb( root, MorphoClass.VerbActive4, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural, isUnknown );
		}
		else if( lex.endsWith( "ttuð" ) )
		{    // þið breyt-tuð    the past
			root = lex.substring( 0, len - 3 );
			found = searchVerb( root, MorphoClass.VerbActive2, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural, isUnknown );
		}
		else if( lex.endsWith( "duð" ) || lex.endsWith( "tuð" ) || lex.endsWith( "ðuð" ) )
		{    // þið reyn-duð, fyll-tuð    the past
			root = lex.substring( 0, len - 3 );
			found = searchVerb( root, MorphoClass.VerbActive2, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural, isUnknown );
			if( !found )
				found = searchVerb( root, MorphoClass.VerbActive5, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural, isUnknown );
		}
		else if( lex.endsWith( "ddu" ) )
		{    // þeir meiddu    the past
			root = lex.substring( 0, len - 3 );
			found = searchVerb( root, MorphoClass.VerbActive4, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural, isUnknown );
		}
		else if( lex.endsWith( "ttu" ) )
		{    // þeir breyt-tu    the past
			root = lex.substring( 0, len - 2 );
			found = searchVerb( root, MorphoClass.VerbActive2, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural, isUnknown );
		}
		else if( lex.endsWith( "uðu" ) )
		{    // borð-uðu    the past
			root = lex.substring( 0, len - 3 );
			found = searchVerb( root, MorphoClass.VerbActive1, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
		}
		else if( lex.endsWith( "aðu" ) )
		{    // borð-aðu    the imperative
			root = lex.substring( 0, len - 3 );
			found = searchVerb( root, MorphoClass.VerbActive1, currToken, Mood.Imperative, Voice.Active, IceTag.cPresent, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
		}
		else if( lex.endsWith( "stu" ) && isUnknown )
		{    // fórstu, straukstu
			root = lex.substring( 0, len - 1 ); // Trick, search for fórst, straukst
			dummyToken.clearTags();
			dummyToken.lexeme = root;
			dictionaryTokenLookup( dummyToken, false );
			if( !dummyToken.noTags() && dummyToken.isVerbAny() )
			{
				IceTag tag = new IceTag( IceTag.tagVerbSecondSingular );
				tag.setTense( IceTag.cPast );
				currToken.addTag( tag );
				found = true;
				currToken.setUnknownType( IceTokenTags.UnknownType.morpho );
			}
		}
		else if( lex.endsWith( "du" ) || lex.endsWith( "tu" ) || lex.endsWith( "ðu" ) )
		{    // þeir reyn-du, fyll-tu, lif-ðu    the past
			int count = 1;
			root = lex.substring( 0, len - 2 );
			// u-hljóðvarp, földu - fel
			String newRoot = hljodVarp( root, 'ö', 'e' );
			if( newRoot != null )
				count = 2;

			for( int i = 1; i <= count && !found; i++ )
			{
				if( i == 2 )
					root = newRoot;

				found = searchVerb( root, MorphoClass.VerbActive2, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural, isUnknown );
				if( !found )
					found = searchVerb( root, MorphoClass.VerbActive5, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural, isUnknown );
			}
		}
		else if( lex.endsWith( "aði" ) )
		{    // borð-aði    the past
			root = lex.substring( 0, len - 3 );
			found = searchVerb( root, MorphoClass.VerbActive1, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular, isUnknown );
		}
		else if( lex.endsWith( "aðir" ) )
		{    // borð-aðir    the past
			root = lex.substring( 0, len - 4 );
			found = searchVerb( root, MorphoClass.VerbActive1, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
		}
		else if( lex.endsWith( "uðuð" ) )
		{    // borð-uðuð    the past
			root = lex.substring( 0, len - 4 );
			found = searchVerb( root, MorphoClass.VerbActive1, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
		}

		else if( lex.endsWith( "um" ) )
		{    // við borð-um    the present
			int count = 1;
			root = lex.substring( 0, len - 2 );
			// u-hljóðvarp, köstum - kast
			String newRoot = hljodVarp( root, 'ö', 'a' );
			if( newRoot != null )
				count = 2;

			for( int i = 1; i <= count && !found; i++ )
			{
				if( i == 2 )
					root = newRoot;
				found = searchVerb( root, MorphoClass.VerbActive1, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cFirstPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural, isUnknown );
			}
		}
		else if( lex.endsWith( "ið" ) ) // borð-ið
		{
			root = lex.substring( 0, len - 2 );
			found = searchVerb( root, MorphoClass.VerbActive1, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural, isUnknown );
		}
		else if( lex.endsWith( "iði" ) ) // ætl-iði
		{
			root = lex.substring( 0, len - 3 );
			found = searchVerb( root, MorphoClass.VerbActive1, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural, isUnknown );
		}
		else if( lex.endsWith( "að" ) )
		{    // borðað    the past participle
			// borðað
			root = lex.substring( 0, len - 2 );
			found = searchVerb( root, MorphoClass.VerbActive1, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cGenderUnspec, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural, isUnknown );
			//found = searchVerbPastPart(lex.substring(0,len-1), currToken, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative);
			if( found && isUnknown)   // add supine and past participle
			{
				currToken.addTag( IceTag.tagVerbSupine );
				addPastParticipleTag( currToken, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative );
			}
		}
		else if( lex.endsWith( "rt" ) || lex.endsWith( "mt" ) || lex.endsWith( "nt" ) || lex.endsWith( "gt" ) ||
		         lex.endsWith( "kt" ) || lex.endsWith( "lt" ) || lex.endsWith( "pt" ) || lex.endsWith( "tt" ) /*|| lex.endsWith( "st" )*/ )
		{
			if( lex.endsWith( "tt" ) )
			{ //flett
				root = lex;
				found = searchVerb( root, MorphoClass.VerbActive2, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cGenderUnspec, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
				if( !found ) // snætt
				{
					root = lex.substring( 0, len - 2 ) + "ð";   // snæð is root
					found = searchVerb( root, MorphoClass.VerbActive2, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cGenderUnspec, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
				}
			}
			else if( lex.endsWith( "kt" ) || lex.endsWith( "gt" ) )
			{ //vanrækt, smeygt => vanrækja, smeygja
				// smeygt, vanrækt
				root = lex.substring( 0, len - 1 );   // smeyg is root
				found = searchVerb( root, MorphoClass.VerbActive5, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cGenderUnspec, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
			}
			else
			{
				// kúrt, kennt, glápt
				root = lex.substring( 0, len - 1 );   // kúr is root
				found = searchVerb( root, MorphoClass.VerbActive2, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cGenderUnspec, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
			}
			if( found && isUnknown)
			{  // add supine and past participle
				currToken.addTag( IceTag.tagVerbSupine );
				addPastParticipleTag( currToken, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative );
			}
		}
		else if( lex.endsWith( "nd" ) )
		{
			// kennd
			root = lex.substring( 0, len - 1 );   // kenn is root
			found = searchVerb( root, MorphoClass.VerbActive2, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cGenderUnspec, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
			if( found && isUnknown)
			{   // past participle
				addPastParticipleTag( currToken, IceTag.cFeminine, IceTag.cSingular, IceTag.cNominative );
			}
		}
		else if( lex.endsWith( "endir" ) )  // active
		{
			// þú/hann sendir
			root = lex.substring( 0, len - 2 );   // sen is root
			found = searchVerb( root, MorphoClass.VerbActive3, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular, isUnknown );
		}
		else if( lex.endsWith( "entir" ) )  // active
		{
			// þú/hann sentir
			root = lex.substring( 0, len - 2 );   // sen is root
			found = searchVerb( root, MorphoClass.VerbActive3, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular, isUnknown );
		}
		else if( lex.endsWith( "ttir" ) )
		{    // hætt-ir, sætt-ir    the present
			root = lex.substring( 0, len - 2 );
			found = searchVerb( root, MorphoClass.VerbActive2, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular, isUnknown );
			if( !found )
			{
				root = lex.substring( 0, len - 3 ); // breyt-tir
				found = searchVerb( root, MorphoClass.VerbActive2, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
			}
		}
		else if( lex.endsWith( "ðir" ) )   // herð-ir
		{
			root = lex.substring( 0, len - 2 );
			found = searchVerb( root, MorphoClass.VerbActive1, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular, isUnknown );
			if( !found ) // could be past tens, horf-ðir
			{
				root = lex.substring( 0, len - 3 );
				found = searchVerb( root, MorphoClass.VerbActive2, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
			}
		}
		else if( lex.endsWith( "dir" ) || lex.endsWith( "tir" ) ) // active, reyndir, fylltir
		{
			// þú reyndir
			root = lex.substring( 0, len - 3 );
			found = searchVerb( root, MorphoClass.VerbActive2, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
			if( !found )
				found = searchVerb( root, MorphoClass.VerbActive5, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
			if( !found && lex.endsWith( "tir" ) )
			{
				root = lex.substring( 0, len - 2 ); // þú/hann breyt-ir
				found = searchVerb( root, MorphoClass.VerbActive2, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular, isUnknown );
			}
		}
		else if( lex.endsWith( "endi" ) )   // active
		{
			// ég sendi
			root = lex.substring( 0, len - 2 );   // sen is root
			found = searchVerb( root, MorphoClass.VerbActive3, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cFirstPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
		}
		else if( lex.endsWith( "enti" ) )  // active
		{
			// ég senti
			root = lex.substring( 0, len - 2 );   // sen is root
			found = searchVerb( root, MorphoClass.VerbActive3, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cFirstPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
		}
		else if( lex.endsWith( "ddi" ) ) // active
		{
			// ég meiddi
			root = lex.substring( 0, len - 3 );
			found = searchVerb( root, MorphoClass.VerbActive4, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular, isUnknown );
		}
		else if( lex.endsWith( "tti" ) ) // active
		{
			// ég/hann breytti
			root = lex.substring( 0, len - 2 );
			found = searchVerb( root, MorphoClass.VerbActive2, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular, isUnknown );
		}
		else if( lex.endsWith( "di" ) || lex.endsWith( "ti" ) || lex.endsWith( "ði" ) ) // active
		{
			// ég reyndi
			root = lex.substring( 0, len - 2 );
			found = searchVerb( root, MorphoClass.VerbActive2, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular, isUnknown );
			if( !found )
				found = searchVerb( root, MorphoClass.VerbActive5, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular, isUnknown );
			if( !found && lex.endsWith( "ti" ) )
			{
				root = lex.substring( 0, len - 1 ); //breyt-i
				found = searchVerb( root, MorphoClass.VerbActive2, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPresent, IceTag.cFirstPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
			}
			if( !found && lex.endsWith( "ði" ) )
			{
				root = lex.substring( 0, len - 1 ); //greið-i
				found = searchVerb( root, MorphoClass.VerbActive2, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPresent, IceTag.cFirstPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
			}
			// ígrundi - ígrunda
			if( !found )
			{
				root = lex.substring( 0, len - 1 ); //ígrund-i
				found = searchVerb( root, MorphoClass.VerbActive1, currToken, Mood.Subjunctive, Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular, isUnknown );
			}
		}
		else if( lex.endsWith( "ir" ) )
		{    // reyn-ir, fyll-ir    the present
			root = lex.substring( 0, len - 2 );
			found = searchVerb( root, MorphoClass.VerbActive2, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular, isUnknown );
		}
		else if( lex.endsWith( "mdur" ) )
		{
			// gleymdur, skemmdur
			root = lex.substring( 0, len - 2 );   // gleym is root
			found = searchVerb( root, MorphoClass.VerbActive1, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cGenderUnspec, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
			if( found && isUnknown)
			{  // past participle
				addPastParticipleTag( currToken, IceTag.cMasculine, IceTag.cSingular, IceTag.cNominative );
			}
		}
		else if( lex.endsWith( "ngdur" ) )
		{
			// flengdur, hengdur
			root = lex.substring( 0, len - 3 );   // fleng is root
			found = searchVerb( root, MorphoClass.VerbActive5, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cGenderUnspec, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
			if( found && isUnknown)
			{   // past participle
				addPastParticipleTag( currToken, IceTag.cMasculine, IceTag.cSingular, IceTag.cNominative );
			}
		}
        else if( lex.endsWith( "ður" ) )
		{
			// tryggður
			root = lex.substring( 0, len - 3 );   // trygg is root
			found = searchVerb( root, MorphoClass.VerbActive4, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cGenderUnspec, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
			if( found && isUnknown)
			{   // past participle
				addPastParticipleTag( currToken, IceTag.cMasculine, IceTag.cSingular, IceTag.cNominative );
			}
		}
        else if( lex.endsWith( "ur" ) )
		{    // bindur    the present
			root = lex.substring( 0, len - 2 );
			found = searchVerb( root, MorphoClass.VerbActive1, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular, isUnknown );
		}
		else if( lex.endsWith( "gg" ) )
		{    // legg,hegg    the present
			root = lex;
			found = searchVerb( root, MorphoClass.VerbActive5, currToken, Mood.Indicative, Voice.Active, IceTag.cPresent, IceTag.cFirstPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
		}
		else if( lex.endsWith( "i" ) ) // active and subjunctive
		{
			// ég reyni, þótt ég reyni
			root = lex.substring( 0, len - 1 );
			found = searchVerb( root, MorphoClass.VerbActive2, currToken, Mood.IndicativeSubjunctive, Voice.Active, IceTag.cPresent, IceTag.cFirstPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
			// Add 3. person subjunctive
			if( found && isUnknown)   // þótt hann reyni
			{
				IceTag tag = new IceTag( IceTag.tagVerbSubjunctive );
				tag.setTense( IceTag.cPresent );
				tag.setPersonGender( IceTag.cThirdPerson );
				tag.setNumber( IceTag.cSingular );
				currToken.addTag( tag );
			}
			if( !found )
			{
				// stimpli - stimpla
				found = searchVerb( root, MorphoClass.VerbActive1, currToken, Mood.Subjunctive, Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular, isUnknown );
			}
		}
        else if ( lex.endsWith( "ast" ))
		{
			// ágerast
            root = lex.substring( 0, len - 3 );
			found = searchVerb( root, MorphoClass.VerbMiddle1, currToken, Mood.Indicative, Voice.Middle, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
            // Add infinitive tag
			if( found && isUnknown)   // þótt hann reyni
                currToken.addInfinitiveVerbForm();
        }
        else if ( lex.endsWith( "ðist" ))
		{
			// ágerðist
            root = lex.substring( 0, len - 4 );
			found = searchVerb( root, MorphoClass.VerbMiddle1, currToken, Mood.Indicative, Voice.Middle, IceTag.cPast, IceTag.cFirstPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular, isUnknown );
            // Add 3. person subjunctive
			if( found && isUnknown)   // þótt hann reyni
			{
				IceTag tag = new IceTag( IceTag.tagVerbMiddle );
				tag.setTense( IceTag.cPast );
				tag.setPersonGender( IceTag.cThirdPerson );
				tag.setNumber( IceTag.cSingular );
				currToken.addTag( tag );
			}
        }
        else if ( lex.endsWith( "ist" ))
		{
			// ágerist
            root = lex.substring( 0, len - 3 );
			found = searchVerb( root, MorphoClass.VerbMiddle1, currToken, Mood.Indicative, Voice.Middle, IceTag.cPresent, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, isUnknown );
        }
        //else if( !lex.endsWith( "ast" ) && !lex.endsWith( "ist" ) && lex.endsWith( "st" ) )
        else if(lex.endsWith( "st" ) )
        {
            root = lex.substring( 0, len - 2 );
			found = searchVerb( root, MorphoClass.VerbMiddle2, currToken, Mood.Indicative, Voice.Middle, IceTag.cPresent, IceTag.cFirstPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular, isUnknown );
            // Add 3. person subjunctive and supine
			if( found && isUnknown)  
			{
				IceTag tag = new IceTag( IceTag.tagVerbMiddle );
				tag.setTense( IceTag.cPresent );
				tag.setPersonGender( IceTag.cThirdPerson );
				tag.setNumber( IceTag.cSingular );
				currToken.addTag( tag );
                currToken.addTag( IceTag.tagVerbSupineMiddle );
            }
        }

        return (found);
	}

	public boolean isForeignLexeme( String lex )
	{
		boolean foreign = false;
		if( lex.matches( ".*[cwqå].*" ) )
			foreign = true;
		else
		if( (lex.matches( ".*ie.*" ) && !lex.matches( ".*ðie.*" )) || lex.matches( ".*eu.*" ) || lex.matches( ".*io.*" ) ||
		    lex.matches( ".*eau.*" ) || lex.matches( ".*ou.*" ) )
			foreign = true;
		return foreign;
	}

	public boolean foreignAnalysis( IceTokenTags currToken )
	{
		boolean found = isForeignLexeme( currToken.lexeme );

		if( found )
		{
			currToken.setTag( IceTag.tagForeign );
			currToken.setUnknownType( IceTokenTags.UnknownType.guessed );
		}
		return found;
	}

	/*
 * Ending analysis for proper nouns
 */
	public boolean endingAnalysisProper( IceTokenTags currToken )
	{
		String tagStr;
		boolean found = false, foundKey;

		String lex = currToken.lexeme;

		foundKey = endingsProperDict.lookupSuffix( lex, true, suffixLength );
		if( foundKey )
		{
			tagStr = endingsProperDict.getValueFound();
			String key = endingsProperDict.getKeyFound();
			found = true;
			currToken.addAllTags( tagStr );

            if( key.length() > 2 )            // Also find the tags for the suffix of one less character
			{
				String smallerKey = key.substring(0,key.length()-1); // remove last char
				String tagStr2 = endingsProperDict.lookup( smallerKey, false );
				if( tagStr2 != null )
					currToken.addAllTags( tagStr2 );
			}
            
            currToken.setUnknownType( IceTokenTags.UnknownType.ending );
			if( currToken.lexeme.length() > maxPersonNameLength )
				currToken.removeProperNounType( IceTag.cPersonName );
			if( logger != null )
				logger.log( "Ending Proper: " + currToken.lexeme + " " + currToken.allTagStrings() );
		}
		return found;
	}


	public boolean endingAnalysis( IceTokenTags currToken, IceTag.WordClass wClass, boolean unknown )
    	/* Ending analysis.
            Tries to assigns the appropriate tag to tokens with null tag based on the word ending
            unknown is true if the token is unknown, else this method is called during generation of missing tags
        */
	{
		String tagStr = null, tagStr2 = null;
		boolean found = false;
		boolean foundKey, foundKey2 = false, longerKeyFound = false;
		String key = null, key2 = null;

		String lex = currToken.lexeme;
		if( lex.endsWith( "-" ) )
			lex = lex.substring( 0, lex.length() - 1 );

        foundKey = endingsBase.lookupSuffix( lex, false, maxSuffixLength );
		if( foundKey )
		{
			tagStr = endingsBase.getValueFound();
			key = endingsBase.getKeyFound();
			currToken.addAllTags( tagStr );
		}
		// Adjective endings are accurate in the main endings lexicon

		if( !adjectiveEnding( lex ) )
		{
			foundKey2 = endingsDict.lookupSuffix( lex, false, suffixLength );
			if( foundKey2 )
			{
				tagStr2 = endingsDict.getValueFound();
				key2 = endingsDict.getKeyFound();
			}
		}
		// If the length of the key in the supplied ending dictionaryOtb is longer than the one in the main dictionaryOtb
		// then also use those tags
		if( unknown && foundKey && foundKey2 )
		{
			if( key2.length() >= key.length() )
			{
				currToken.addAllTags( tagStr2 );
				if( currToken.isNoun() )
					checkNounClasses( currToken );    // Fill in missing tags
				longerKeyFound = true;
			}
		}
		else if( foundKey2 && !foundKey )
		{
			currToken.addAllTags( tagStr2 );
			if( currToken.isNoun() )
				checkNounClasses( currToken );    // Fill in missing tags
		}

		if( foundKey || foundKey2 )
		{
			if( wClass != IceTag.WordClass.wcUndef )
				currToken.removeAllBut( wClass );

			if( currToken.numTags() > 0 )
			{
				found = true;
				if( currToken.isUnknownNone() )
					currToken.setUnknownType( IceTokenTags.UnknownType.ending );

				if( logger != null )
				{
					if( longerKeyFound || (!foundKey && foundKey2) )
						logger.log( "Ending lookup2: " + currToken.lexeme + " " + "key: " + key2 + " value: " + tagStr2 + " " + currToken.allTagStrings() );
					else
						logger.log( "Ending lookup: " + currToken.lexeme + " " + "key: " + key + " value: " + tagStr + " " + currToken.allTagStrings() );
				}
			}
		}

		return found;
	}


	private boolean verbEnding( String lex )
	{
		return ((lex.endsWith( "ið" ) && !lex.endsWith( "eið" )) || lex.endsWith( "aði" ) || lex.endsWith( "aðir" ) ||
		        (lex.endsWith( "aður" ) && !lex.endsWith( "maður" )) || lex.endsWith( "uðum" ) ||
		        (lex.endsWith( "andi" ) && !lex.endsWith( "sandi" ) && /*!lex.endsWith("landi") &&*/ !lex.endsWith( "bandi" )) ||
		        lex.endsWith( "ist" ));
	}


	private boolean adjectiveEnding( String lex )
	{
		return (lex.endsWith( "legur" ) || lex.endsWith( "legan" ) || lex.endsWith( "lega" ) ||
		        lex.endsWith( "legi" ) || lex.endsWith( "legs" ) || lex.endsWith( "legu" ) || lex.endsWith( "legum" ) ||
		        lex.endsWith( "leg" ) || lex.endsWith( "legri" ) || lex.endsWith( "legra" ) || lex.endsWith( "legrar" ) ||
		        lex.endsWith( "legir" ) || lex.endsWith( "legar" ) ||
		        lex.endsWith( "astur" ) || lex.endsWith( "ust" ) || lex.endsWith( "ast" ) ||
		        lex.endsWith( "asta" ) || lex.endsWith( "ustu" ) ||
		        lex.endsWith( "astir" ) || lex.endsWith( "astar" ) ||
		        (lex.endsWith( "andi" ) && !lex.endsWith( "sandi" ) && /*!lex.endsWith("landi") &&*/ !lex.endsWith( "bandi" )) ||
		        lex.endsWith( "vana" )
		);
	}

	private void setDeclension( IceTokenTags currToken, char decl )
	{
        // Sets the declension for adjectives
		ArrayList tags = currToken.getTags();
		for( int i = 0; i < tags.size(); i++ )
		{
			IceTag tag = (IceTag)tags.get( i );
			tag.setDeclension( decl );
			if( logger != null )
				logger.log( "Set declension: " + currToken.lexeme + " " + currToken.allTagStrings() );
		}
	}

	private boolean isVowel( char ch )
	{
		for( int j = 0; j <= vowels.length - 1; j++ )
			if( ch == vowels[j] )
				return true;

		return false;
	}

	private boolean legalPrefix( String prefix, String suffix )
	{
		boolean legal = true;
		char nextLastPrefixChar = '*';
		char nextSuffixChar = '*';

		// First check if the prefix is a listed prefix
		for( int i = 0; i <= prefixes.getWords().size() - 1; i++ )
		{
			String listedPrefix = (String)prefixes.getWords().get( i );
			if( prefix.equalsIgnoreCase( listedPrefix ) )
				return true;
		}

		if( prefix.startsWith( "úl" ) ||
		    prefix.endsWith( "ei" ) || // ei-stunum, gnei-stuðu
            prefix.endsWith("h") || // tónleikah-aldarar
            prefix.equals( "svi" ) || // svi-fléttum
		    prefix.equals( "ba" ) || // ba-klás
		    prefix.equals( "hr" ) || // hr-ár
		    prefix.endsWith( "au" ) || // hau-staði
		    //prefix.endsWith( "eð" ) || // víðfeð-mara
		    prefix.endsWith( "sn" ) || // sn-ætt
		    prefix.endsWith( "ing" ) || // nísting-skuldi
		    prefix.equals( "gr" ) || // gr-afanna
		    (prefix.endsWith( "in" ) && suffix.startsWith( "g" )) || // setnin-garnar
		    suffix.startsWith( "nr" ) ||
		    suffix.equals( "anna" ) ||
		    suffix.equals( "dóttur" ) ||  // brön-dóttur
		    suffix.equals( "skerfi" ) ||  // gjaldkeyri-skerfi
		    suffix.equals( "ótt" ) ||
		    suffix.endsWith( "." ) ||
		    suffix.matches( ".?ja" ) ||   // ræs-kja
		    (suffix.matches( ".?aður" ) && !suffix.equals( "maður" )) || suffix.matches( ".?uðum" ) || suffix.matches( ".?un" ) ||
		    suffix.matches( ".?aði" ) ||
		    suffix.matches( ".?inn" ) ||
		    suffix.matches( ".?ina" ) ||
		    suffix.matches( ".?inum" ) || suffix.matches( ".?ið" ) ||
		    suffix.matches( ".?innar" ) || suffix.matches( ".?sins" ) ||
		    suffix.matches( ".?unni" ) ||
		    suffix.matches( ".?arnir" ) || suffix.matches( ".?aðar" ) // tjóð-raðar
				)
			return false;

		// hrakningas-ama, innl-endur, lántak-enda, símaskr-ána, mjólkurdreyt-ill, fal-lið
		// sv-æðin, landam-ærin
		int prefixLength = prefix.length();
		char lastPrefixChar = prefix.charAt( prefixLength - 1 );
		if( prefixLength > 1 )
			nextLastPrefixChar = prefix.charAt( prefixLength - 2 );
		char firstSuffixChar = suffix.charAt( 0 );
		nextSuffixChar = suffix.charAt( 1 );

		if( lastPrefixChar == 'l' || lastPrefixChar == 'k' ||
		    lastPrefixChar == 't' || lastPrefixChar == 'ð' ||
		    lastPrefixChar == 'v' || lastPrefixChar == 'b' || lastPrefixChar == 'p' ||
		    lastPrefixChar == 'f' || lastPrefixChar == 'g' || lastPrefixChar == 'm' )
		{
			if( isVowel( firstSuffixChar ) )
				legal = false;
		}
		if( legal )
		{
			// sparnaðarhlutfal-lið, flek-kinn
			if(
					(lastPrefixChar == 'l' && firstSuffixChar == 'l' && isVowel( nextSuffixChar )) ||
					(lastPrefixChar == 'n' && ((firstSuffixChar == 'n' && isVowel( nextSuffixChar )) ||
					                           (firstSuffixChar == 's' && !isVowel( nextLastPrefixChar )))) || // vatn-skerfum
					(lastPrefixChar == 'k' && firstSuffixChar == 'k') ||
					(lastPrefixChar == 'g' && firstSuffixChar == 'g') ||
					(lastPrefixChar == 'r' && nextLastPrefixChar != 'a' && firstSuffixChar == 'n') ||  // jár-nið, í lagi er ástar-nætur
					(lastPrefixChar == 'ð' && firstSuffixChar == 's') ||  // sjóð-slánum
					(lastPrefixChar == 'n' && firstSuffixChar == 'j') ||  // Sn-jólfur
					(lastPrefixChar == 'p' && (firstSuffixChar == 'r' || firstSuffixChar == 'l')) ||  // vip-runni, ep-lum
					lastPrefixChar == 'b' ||
					lastPrefixChar == 'þ' ||
					lastPrefixChar == 'j' ||
					lastPrefixChar == 'o' ||  // sto-list
					(lastPrefixChar == 'ú' && nextLastPrefixChar != 'b') || // bú- is ok
					lastPrefixChar == 'æ' ||  // fæ-reyingur
					lastPrefixChar == 'ö' ||  // kö-stunum
					lastPrefixChar == 'e' ||    // hne-turnar
					firstSuffixChar == 'x'  // háva-xið
					)
				legal = false;
		}

		if( legal )
		{
			// A legal prefix has to include at least one vowel
			for( int i = 0; i <= prefix.length() - 1; i++ )
				if( isVowel( prefix.charAt( i ) ) )
					return true;
		}
		return legal;
	}


	public boolean addPrefix( IceTokenTags currToken )
	{
		String tagStr, lex, prefix;
		boolean found = false;
		lex = currToken.lexeme;
		IceTokenTags dumToken = new IceTokenTags();

		for( int i = 0; i <= prefixes.getWords().size() - 1 && !found; i++ )
		{
			prefix = (String)prefixes.getWords().get( i );
			String searchStr = prefix + lex;

            tagStr = dictionaryLookup( searchStr, true );
            if( tagStr != null )
			{
				found = true;
				dumToken.lexeme = lex;
				dumToken.setAllTags( tagStr );
			}

			if( found )
			{
				if( dumToken.isNoun() || dumToken.isAdjective() )
				{
					if( dumToken.isNoun() )                      // Lets assume a noun is more likely
						dumToken.removeAllBut( IceTag.WordClass.wcNoun );
					else if( dumToken.isAdjective() )
						dumToken.removeAllBut( IceTag.WordClass.wcAdj );

					if( dumToken.numTags() > 0 )
					{
						// The suffix found might have some missing tags.  Try add them ...
						if( dumToken.isOnlyWordClass( IceTag.WordClass.wcNoun ) )
							generateMissingNoun( dumToken );
						else if( dumToken.isOnlyWordClass( IceTag.WordClass.wcAdj ) )
							generateMissingAdj( dumToken );

						currToken.addAllTags( dumToken.allTagStrings() );
						currToken.setUnknownType( IceTokenTags.UnknownType.morpho );
						if( logger != null )
							logger.log( "Prefix analysis: " + prefix + " " + lex + " " + currToken.allTagStrings() );
						found = true;
					}
					else
						found = false;
				}
				else
					found = false;
			}
		}
		return found;
	}

	public boolean compoundAnalysisSuffix( IceTokenTags currToken, boolean isProperNoun )
	{
        // Checks if the currToken is a compound word by looking up the longest suffix
		String tagStr, prefix, suffix, lex;
		boolean found = false;
		boolean morpho;

		int start = 1;      // start deleting from this character
		lex = currToken.lexeme;
		if( lex.endsWith( "-" ) )
			lex = lex.substring( 0, lex.length() - 1 );
		int end = lex.length() - 2;        // last character to delete

		// Lets assume currToken.lexeme = "litadýrðar"

		if( !adjectiveEnding( lex ) )
		{
			int len = lex.length();
			IceTokenTags dumToken = new IceTokenTags();

			char firstChar = lex.charAt( 0 );
			if( !isVowel( firstChar ) )
				start++;

			for( int i = start; i <= end && !found; i++ )
			{
				found = false;
				morpho = false;
				prefix = lex.substring( 0, i );      // prefix of the string; assume = "lita"
				suffix = lex.substring( i, len );    // suffix of the string; assume "dýrðar"
				if( legalPrefix( prefix, suffix ) )
				{
					dumToken.lexeme = suffix;
                    tagStr = dictionaryLookup( suffix, false );
                    if( tagStr != null )                      // If "dýrðar" was found
					{
						dumToken.setAllTags( tagStr );
						if( dumToken.isNoun() || dumToken.isProperNoun() || dumToken.isAdjective() )
							found = true;
						else
						{
							found = false;
							dumToken.clearTags();
						}
					}
					if( !found && !isProperNoun )
					{ // try suffix analysis
						if( !adjectiveEnding( dumToken.lexeme ) && dumToken.lexeme.length() > 3 )
						{
							found = morphoAnalysisSuffix( dumToken, isProperNoun );
							if( found )
							{
								morpho = true;
								if( logger != null )
									logger.log( "Compound analysis suffix special: " + lex + " " + dumToken.lexeme + " " + dumToken.allTagStrings() );
							}
						}
					}
					// Lets assume Noun and adjectives are more likely.  Add verbs also.
					if( found )
					{
						if( dumToken.isNoun() )                          // Lets assume a noun is more likely
						{
							if( !isProperNoun )
								dumToken.removeAllBut( IceTag.WordClass.wcNoun );
						}
						else if( dumToken.isAdjective() )
							dumToken.removeAllBut( IceTag.WordClass.wcAdj );
						if( dumToken.isProperNoun() )                          // Lets assume a noun is more likely
							dumToken.removeAllBut( IceTag.WordClass.wcProperNoun );

						if( dumToken.numTags() > 0 )
						{
							// The suffix found might have some missing tags.  Try add them ...
							if( !morpho )     // generateMissing is done in morphoAnalysis
							{
								if( dumToken.isOnlyWordClass( IceTag.WordClass.wcNoun ) )
									generateMissingNoun( dumToken );
								else if( dumToken.isOnlyWordClass( IceTag.WordClass.wcAdj ) )
									generateMissingAdj( dumToken );
							}
							currToken.addAllTags( dumToken.allTagStrings() );
							currToken.setUnknownType( IceTokenTags.UnknownType.morpho );
							currToken.setCompound( true );
							if( logger != null )
								logger.log( "Compound analysis suffix: " + suffix + " " + currToken.lexeme + " " + currToken.allTagStrings() );
							break;
						}
						else
							found = false;
					}

					else
						found = false;
				}
			}
		}
		return found;
	}

	public boolean compoundAnalysisPrefix( IceTokenTags currToken )
    // Checks if the currToken is a compound noun by looking at a prefix of the word
	//  This is called if compoundAnalysisSuffix does not work
	{
		final int start = 3;      // start deleting from this character
		final int fromEnd = 4;        // last character from end of word to delete
		final int maxLengthWord = 8; // maximum length of word to process

		// Lets assume currToken.lexeme = "verslunarsviðs"
		// In this case "sviðs" was not found so when the prefix "verslun" is found to be a noun then
		// do endingAnalysis on the word "verslunarsviðs"
		// Make sure that the remaining word (after the prefix) is not "small"

		String tagStr = null, prefix = null, suffix = null, lex = null;
		boolean found = false;

		lex = currToken.lexeme;
		if( lex.length() >= maxLengthWord && !adjectiveEnding( lex )
		    && !verbEnding( lex ) )
		{
			int len = lex.length();
			IceTokenTags dumToken = new IceTokenTags();

			for( int i = start; i <= len - fromEnd; i++ )
			{
				found = false;
				prefix = lex.substring( 0, i );      // prefix of the string; assume = "verslun"
				dumToken.lexeme = suffix;
                tagStr = dictionaryLookup( prefix, true );
                if( tagStr != null )                      // If "verslun" was found
				{
					dumToken.setAllTags( tagStr );
					found = true;

					// Lets only work with nouns
					if( found && dumToken.isNoun() ) // && dumToken.isCase(IceTag.cGenitive))
					{
						found = endingAnalysis( currToken, IceTag.WordClass.wcNoun, true );
						if( found )
						{
							currToken.setCompound( true );
							if( logger != null )
								logger.log( "Compound analysis prefix: " + prefix + " " + currToken.lexeme + " " + currToken.allTagStrings() );
							break;
						}
					}
					else
						found = false;
				}
			}
		}
		return found;
	}


	private void addCase( IceTokenTags currToken, String tagStr, char caseChar, boolean article )
	{
		IceTag tag = new IceTag( tagStr );
		tag.setCase( caseChar );
		if( article )        // Needs to be done on the tag level because if the tag already exists it won't be added
			tag.addArticle();
		currToken.addTag( tag );
	}

	private void addCases( IceTokenTags currToken, String tagStr, boolean nom, boolean acc, boolean dat, boolean gen, boolean article )
	{
		if( nom )
			addCase( currToken, tagStr, IceTag.cNominative, article );
		if( acc )
			addCase( currToken, tagStr, IceTag.cAccusative, article );
		if( dat )
			addCase( currToken, tagStr, IceTag.cDative, article );
		if( gen )
			addCase( currToken, tagStr, IceTag.cGenitive, article );
	}


	/*
	  * Assumes the global variables str1-strX have already been set
	  */

	private boolean searchSuffixCases( String root, MorphoClass mClass, IceTokenTags currToken, IceTag.WordClass wordClass, boolean nom, boolean acc, boolean dat, boolean gen,
	                                   char genderLetter, char numberLetter, boolean article, char declLetter )
	{
		String tagStr = null, searchStr;
		int index = 0;
		char caseLetter = '*', numLetter = '*', declension = '*';
		boolean found = false;
		//IceTokenTags tmpTok = null;
		boolean done = false;
		int end = 0;

		setSearchStrings( root, mClass );         // set the search strings
		if( wordClass == IceTag.WordClass.wcAdj )
			end = searchStringSize;
		else if( mClass == MorphoClass.NounMasculine1 )
			end = 20;
		else if( mClass == MorphoClass.NounFeminine1 )
			end = 18;
		else
			end = 16;

		for( int i = 1; i <= end && !done; i++ )
		{
			index = i;
			if( wordClass == IceTag.WordClass.wcAdj )
			{
				if( i <= 12 || i >= 25 )
					numLetter = IceTag.cSingular;
				else
					numLetter = IceTag.cPlural;
				if( i <= 24 )
					declension = IceTag.cStrong;
				else
					declension = IceTag.cWeak;
			}
			else
			{
				if( i <= 8 )
					numLetter = IceTag.cSingular;
				else
					numLetter = IceTag.cPlural;
			}

            searchStr = searchStrings[i];
            switch( i )
			{
				case 1:
                case 5:
                case 9:
                case 13:
                case 21:
                case 25:
                case 29:
                case 33:
                    caseLetter = IceTag.cNominative;
					break;
				case 2:
                case 6:
                case 10:
                case 14:
                case 22:
                case 26:
                case 30:
                case 34:
                    caseLetter = IceTag.cAccusative;
					break;
				case 3:
                case 7:
                case 11:
                case 15:
                case 23:
                case 27:
                case 31:
                case 35:
                    caseLetter = IceTag.cDative;
					break;
				case 4:
                case 8:
                case 12:
                case 16:
                case 24:
                case 28:
                case 32:
                case 36:
                    caseLetter = IceTag.cGenitive;
					break;

				case 17:
					if( mClass == MorphoClass.NounFeminine1 )
						caseLetter = IceTag.cGenitive;
					else
						caseLetter = IceTag.cNominative;
					break;
				case 18:
					if( mClass == MorphoClass.NounFeminine1 )
						caseLetter = IceTag.cGenitive;
					else
						caseLetter = IceTag.cAccusative;
					break;
				case 19:
					if( mClass == MorphoClass.NounMasculine1 )
						caseLetter = IceTag.cNominative;
					else
						caseLetter = IceTag.cDative;
					break;
				case 20:
					if( mClass == MorphoClass.NounMasculine1 )
						caseLetter = IceTag.cAccusative;
					else
						caseLetter = IceTag.cGenitive;
					break;
			}
			if( searchStr != null )
			{
				if( wordClass == IceTag.WordClass.wcProperNoun )
                    tagStr = dictionaryLookup( searchStr, false ); // don´t ignore case
                else
                    tagStr = dictionaryLookup( searchStr, true );

                if( tagStr != null )
				{
					tagStr = checkTag( tagStr, wordClass, genderLetter, caseLetter, numLetter, declension );
				}
				if( tagStr != null )
					done = true;
			}
		}

		if( tagStr != null )
		{
			dummyToken.clearTags();
			dummyToken.setAllTags( tagStr );
			if( ((index >= 5) && (index <= 8)) || ((index >= 13) && (index <= 20)) )
				dummyToken.removeArticle();
			dummyToken.removeAllBut( wordClass );

			if( dummyToken.numTags() > 0 && (dummyToken.isGenderPerson( genderLetter ) || dummyToken.isOnlyWordClass( IceTag.WordClass.wcAdj )) )   // Only interested in the correct gender
			{
				if( dummyToken.isOnlyWordClass( IceTag.WordClass.wcNoun ) || dummyToken.isOnlyWordClass( IceTag.WordClass.wcProperNoun ) || dummyToken.isOnlyWordClass( IceTag.WordClass.wcAdj ) )
				{
					if( !dummyToken.isOnlyWordClass( IceTag.WordClass.wcAdj ) )
						dummyToken.removeAllButGender( genderLetter, true );

					if( dummyToken.numTags() > 0 )
					{
						found = true;
						IceTag dumTag = (IceTag)dummyToken.getFirstTag();
						dumTag.setPersonGender( genderLetter );
						dumTag.setNumber( numberLetter );
						addCases( currToken, dumTag.getTagStr(), nom, acc, dat, gen, article );

						// Force the correct declension if the adjective has not both strong and weak declension
						if( currToken.isOnlyWordClass( IceTag.WordClass.wcAdj ) && !(currToken.isAdjectiveStrong() && currToken.isAdjectiveWeak()) )
							currToken.setDeclension( declLetter );

						if( logger != null )
							logger.log( "Search suffix cases: " + currToken.lexeme + " " + currToken.allTagStrings() );
						return true;
					}
				}
			}
		}
		return found;
	}

	// Check if the root is a weak masculine noun, evident by the singular cases
	// skóli-skóla-skóla-skóla
	private boolean checkWeakMasculineNoun( String root )
	{
		String tagStr;
		for( int i = 1; i <= 2; i++ )
		{
			if( i == 1 )
			{
                tagStr = dictionaryLookup( root + "i", true );
                if( tagStr == null )
                    tagStr = dictionaryLookup( root + "inn", true );   // with article
			}
			else
			{
                tagStr = dictionaryLookup( root + "a", true );
                if( tagStr == null )
                    tagStr = dictionaryLookup( root + "ann", true );   // with article
            }

			if( tagStr != null )
			{
				dummyToken.clearTags();
				dummyToken.addAllTags( tagStr );
				// Check for singular, masculine, noun
				dummyToken.removeAllBut( IceTag.WordClass.wcNoun );
				dummyToken.removeAllButGender( IceTag.cMasculine, true );
				if( !dummyToken.noTags() )
				{

					if( i == 1 )
					{
						IceTag tag = (IceTag)dummyToken.getFirstTag();
						if( tag.isNumber( IceTag.cSingular ) && tag.isCase( IceTag.cNominative ) )
							return true;
					}
					else if( dummyToken.isNumber( IceTag.cSingular ) && !dummyToken.isCase( IceTag.cNominative ) )
						return true;
				}
			}
		}
		return false;
	}

	// Checks the existance  of the nominative "ur" masculine ending
	private boolean checkNormalMasculineNoun( String root )
	{
        String tagStr = dictionaryLookup( root + "ur", false );
        if( tagStr == null )
			return false;

		dummyToken.clearTags();
		dummyToken.addAllTags( tagStr );
		// Check for singular, masculine, noun
		dummyToken.removeAllBut( IceTag.WordClass.wcNoun );
		dummyToken.removeAllButGender( IceTag.cMasculine, true );
		if( !dummyToken.noTags() )
		{
			if( dummyToken.isNumber( IceTag.cSingular ) && dummyToken.isCase( IceTag.cNominative ) )
				return true;
		}
		return false;
	}

	/*
 * Checks for special masculine nouns that are same in singular nominative and accusative
 * e.g. bar, koss
 */
	private boolean checkSpecialMasculineNoun( String root )
	{
		String tagStr = null;
        tagStr = dictionaryLookup( root, true );
        if( tagStr != null )
		{
			dummyToken.clearTags();
			dummyToken.addAllTags( tagStr );
			// Check for singular, masculine, noun
			dummyToken.removeAllBut( IceTag.WordClass.wcNoun );
			dummyToken.removeAllButGender( IceTag.cMasculine, true );
			if( !dummyToken.noTags() )
			{
				if( dummyToken.isNumber( IceTag.cSingular ) && dummyToken.isCase( IceTag.cNominative ) && dummyToken.isCase( IceTag.cAccusative ) )
					return true;
			}
		}
		return false;
	}

	/*
 * Does lookup for the word without the article
 */
	private boolean suffixAnalysisArticle( IceTokenTags currToken, boolean isProperNoun )
	{
		String lex, root;
		IceTag.WordClass wordClass = IceTag.WordClass.wcNoun;

		boolean found = false;
		if( isProperNoun )
			wordClass = IceTag.WordClass.wcProperNoun;

		lex = currToken.lexeme;
		int len = lex.length();

		if( lex.endsWith( "urinn" ) && (wordClass == IceTag.WordClass.wcNoun) )    // hestur-inn
		{
			addNounTag( currToken, IceTag.cMasculine, IceTag.cSingular, IceTag.cNominative, true );
			found = true;
		}
		else if( lex.endsWith( "arinn" ) )    // kennar-inn, gítar-inn
		{
			root = lex.substring( 0, len - 3 );
			found = searchSuffixCases( root, MorphoClass.NounMasculine8, currToken, wordClass, true, true, false, false, IceTag.cMasculine, IceTag.cSingular, true, IceTag.cNoDeclension );
			if( !found )
				found = searchSuffixCases( root, MorphoClass.NounMasculine2, currToken, wordClass, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, true, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "inn" ) && len > 3 )
		{   // hest-inn
			// first check if weak masculine noun
			root = lex.substring( 0, len - 3 );
			found = checkWeakMasculineNoun( root );
			if( found )
				addNounTag( currToken, IceTag.cMasculine, IceTag.cSingular, IceTag.cNominative, true );
			else
			{
				found = checkSpecialMasculineNoun( root );
				if( found )
				{
					addNounTag( currToken, IceTag.cMasculine, IceTag.cSingular, IceTag.cNominative, true );
					addNounTag( currToken, IceTag.cMasculine, IceTag.cSingular, IceTag.cAccusative, true );
				}
			}
			if( !found )
				found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, true, IceTag.cNoDeclension );
			if( !found )
				found = searchSuffixCases( root, MorphoClass.NounMasculine2, currToken, wordClass, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, true, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "nn" ) && len > 2 )
		{      // skóla-nn
			root = lex.substring( 0, len - 2 );
			if( !checkNormalMasculineNoun( root ) )
				found = searchSuffixCases( root, MorphoClass.NounMasculine2, currToken, wordClass, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, true, IceTag.cNoDeclension );
		}
		else
		if( (lex.endsWith( "junum" ) || lex.endsWith( "unum" ) || lex.endsWith( "ónum" ) || lex.endsWith( "ánum" ) || lex.endsWith( "únum" )) && len > 5 )
		{
			if( lex.endsWith( "junum" ) )
				root = lex.substring( 0, len - 5 ); // dekk-junum
			else
				root = lex.substring( 0, len - 4 ); // hest-unum, skónum, fánum
			int count = 1;
			// u-hljóðvarp, kökunum - kaka
			String newRoot = hljodVarp( root, 'ö', 'a' );
			if( newRoot != null )
				count = 2;

			for( int i = 1; i <= count && !found; i++ )
			{
				if( i == 2 )
					root = newRoot;

				found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, false, false, true, false, IceTag.cMasculine, IceTag.cPlural, true, IceTag.cNoDeclension );
				if( !found )
					found = searchSuffixCases( root, MorphoClass.NounFeminine1, currToken, wordClass, false, false, true, false, IceTag.cFeminine, IceTag.cPlural, true, IceTag.cNoDeclension );
				if( !found )
					found = searchSuffixCases( root, MorphoClass.NounFeminine2, currToken, wordClass, false, false, true, false, IceTag.cFeminine, IceTag.cPlural, true, IceTag.cNoDeclension );
				if( !found )
					found = searchSuffixCases( root, MorphoClass.NounNeuter1, currToken, wordClass, false, false, true, false, IceTag.cNeuter, IceTag.cPlural, true, IceTag.cNoDeclension );
				if( !found )
					found = searchSuffixCases( root, MorphoClass.NounNeuter4, currToken, wordClass, false, false, true, false, IceTag.cNeuter, IceTag.cPlural, true, IceTag.cNoDeclension );
			}
		}
		else if( lex.endsWith( "inum" ) && len > 4 )
		{
			root = lex.substring( 0, len - 4 ); // hest-inum
			found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, false, false, true, false, IceTag.cMasculine, IceTag.cSingular, true, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "anum" ) && len > 4 )
		{
			root = lex.substring( 0, len - 4 ); // skól-anum
			if( !checkNormalMasculineNoun( root ) )
				found = searchSuffixCases( root, MorphoClass.NounMasculine2, currToken, wordClass, false, false, true, false, IceTag.cMasculine, IceTag.cSingular, true, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "num" ) && len > 3 )
		{
			root = lex.substring( 0, len - 3 ); // bílnum, handleggnum
			found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, false, false, true, false, IceTag.cMasculine, IceTag.cSingular, true, IceTag.cNoDeclension );
		}

		else if( lex.endsWith( "ins" ) && len > 5 )
		{                    // hests-ins, svíns-ins
			root = lex.substring( 0, len - 4 ); // hest-sins
			found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, true, IceTag.cNoDeclension );
			if( !found )
			{
				root = lex.substring( 0, len - 4 ); // svín-sins
				found = searchSuffixCases( root, MorphoClass.NounNeuter1, currToken, wordClass, false, false, false, true, IceTag.cNeuter, IceTag.cSingular, true, IceTag.cNoDeclension );
			}
			if( !found ) // gerpis-ins
			{
				root = lex.substring( 0, len - 5 );
				found = searchSuffixCases( root, MorphoClass.NounNeuter2, currToken, wordClass, false, false, false, true, IceTag.cNeuter, IceTag.cSingular, true, IceTag.cNoDeclension );
			}
		}
		else if( lex.endsWith( "ans" ) && len > 3 )
		{   // skól-ans
			root = lex.substring( 0, len - 3 );
			if( !checkNormalMasculineNoun( root ) )
				found = searchSuffixCases( root, MorphoClass.NounMasculine2, currToken, wordClass, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, true, IceTag.cNoDeclension );
		}
		else if( (lex.endsWith( "arnir" ) || lex.endsWith( "irnir" )) && len > 5 )
		{  // hestar-nir, gest-irnir
			root = lex.substring( 0, len - 5 );
			found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, true, false, false, false, IceTag.cMasculine, IceTag.cPlural, true, IceTag.cNoDeclension );
		}

		else if( lex.endsWith( "una" ) && len > 3 )
		{
			root = lex.substring( 0, len - 3 );  // svip-una
			found = searchSuffixCases( root, MorphoClass.NounFeminine1, currToken, wordClass, false, true, false, false, IceTag.cFeminine, IceTag.cSingular, true, IceTag.cNoDeclension );
		}

		else if( lex.endsWith( "ina" ) && len > 3 )
		{
			root = lex.substring( 0, len - 3 );  // von-ina
			found = searchSuffixCases( root, MorphoClass.NounFeminine2, currToken, wordClass, false, true, false, false, IceTag.cFeminine, IceTag.cSingular, true, IceTag.cNoDeclension );

			if( !found ) // kæti-na
			{
				root = lex.substring( 0, len - 2 );  // kæti-na
				found = searchSuffixCases( root, MorphoClass.NounFeminine4, currToken, wordClass, false, true, false, false, IceTag.cFeminine, IceTag.cSingular, true, IceTag.cNoDeclension );
			}
			if( !found ) // gesti-na
			{
				root = lex.substring( 0, len - 3 );  // gest-ina
				found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, false, true, false, false, IceTag.cMasculine, IceTag.cPlural, true, IceTag.cNoDeclension );
			}
		}

		else if( lex.endsWith( "nna" ) && len > 4 )
		{                     // hest-anna
			root = lex.substring( 0, len - 4 );
			found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, false, false, false, true, IceTag.cMasculine, IceTag.cPlural, true, IceTag.cNoDeclension );
			if( !found )   // súpa-nna
				found = searchSuffixCases( root, MorphoClass.NounFeminine1, currToken, wordClass, false, false, false, true, IceTag.cFeminine, IceTag.cPlural, true, IceTag.cNoDeclension );
			if( !found )  // svína-nna
				found = searchSuffixCases( root, MorphoClass.NounNeuter1, currToken, wordClass, false, false, false, true, IceTag.cNeuter, IceTag.cPlural, true, IceTag.cNoDeclension );
			if( !found )   // skól-anna
			{
				if( !checkNormalMasculineNoun( root ) )
					found = searchSuffixCases( root, MorphoClass.NounMasculine2, currToken, wordClass, false, false, false, true, IceTag.cMasculine, IceTag.cPlural, true, IceTag.cNoDeclension );
			}
			if( !found )   // vona-nna
				found = searchSuffixCases( root, MorphoClass.NounFeminine2, currToken, wordClass, false, false, false, true, IceTag.cFeminine, IceTag.cPlural, true, IceTag.cNoDeclension );
			if( !found )  // efna-nna
				found = searchSuffixCases( root, MorphoClass.NounNeuter2, currToken, wordClass, false, false, false, true, IceTag.cNeuter, IceTag.cPlural, true, IceTag.cNoDeclension );
			if( !found && lex.endsWith( "nanna" ) && len > 5 )
			{  // súp-nanna
				root = lex.substring( 0, len - 5 );
				found = searchSuffixCases( root, MorphoClass.NounFeminine1, currToken, wordClass, false, false, false, true, IceTag.cFeminine, IceTag.cPlural, true, IceTag.cNoDeclension );
			}
		}
		else if( (lex.endsWith( "na" ) && !lex.endsWith( "ona" )) && len > 3 )
		{                     // hesta-na, súpu-na
			root = lex.substring( 0, len - 3 );  // hesta-na
			found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, false, true, false, false, IceTag.cMasculine, IceTag.cPlural, true, IceTag.cNoDeclension );
			if( !found )   // súpu-na
				found = searchSuffixCases( root, MorphoClass.NounFeminine1, currToken, wordClass, false, true, false, false, IceTag.cFeminine, IceTag.cSingular, true, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "in" ) && len > 2 )
		{
			root = lex.substring( 0, len - 2 );  // von-in
			found = searchSuffixCases( root, MorphoClass.NounFeminine2, currToken, wordClass, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, true, IceTag.cNoDeclension );
			if( !found ) // meining-in
				found = searchSuffixCases( root, MorphoClass.NounFeminine3, currToken, wordClass, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, true, IceTag.cNoDeclension );
			if( !found ) // svín-in
				found = searchSuffixCases( root, MorphoClass.NounNeuter1, currToken, wordClass, true, true, false, false, IceTag.cNeuter, IceTag.cPlural, true, IceTag.cNoDeclension );
			if( !found )  // gerp-in
			{
				root = lex.substring( 0, len - 2 );  // gerp-in
				found = searchSuffixCases( root, MorphoClass.NounNeuter2, currToken, wordClass, true, true, false, false, IceTag.cNeuter, IceTag.cPlural, true, IceTag.cNoDeclension );
			}
			if( !found )    // kæti-n
			{
				root = lex.substring( 0, len - 1 );
				found = searchSuffixCases( root, MorphoClass.NounFeminine4, currToken, wordClass, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, true, IceTag.cNoDeclension );
			}
		}
		else if( lex.endsWith( "an" ) && len > 2 )
		{     // súpa-n
			root = lex.substring( 0, len - 2 );
			found = searchSuffixCases( root, MorphoClass.NounFeminine1, currToken, wordClass, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, true, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "nni" ) && len > 4 )
		{  // súp-unni
			root = lex.substring( 0, len - 4 );
			found = searchSuffixCases( root, MorphoClass.NounFeminine1, currToken, wordClass, false, false, true, false, IceTag.cFeminine, IceTag.cSingular, true, IceTag.cNoDeclension );
			if( !found )    // von-inni
			{
				root = lex.substring( 0, len - 4 );
				found = searchSuffixCases( root, MorphoClass.NounFeminine2, currToken, wordClass, false, false, true, false, IceTag.cFeminine, IceTag.cSingular, true, IceTag.cNoDeclension );
			}
            if( !found )    // kæti-nni
			{
				root = lex.substring( 0, len - 3 );
				found = searchSuffixCases( root, MorphoClass.NounFeminine4, currToken, wordClass, false, false, true, false, IceTag.cFeminine, IceTag.cSingular, true, IceTag.cNoDeclension );
			}
        }
		else if( lex.endsWith( "nnar" ) && len > 5 )
		{   // súpu-nnar
			root = lex.substring( 0, len - 5 );
			found = searchSuffixCases( root, MorphoClass.NounFeminine1, currToken, wordClass, false, false, false, true, IceTag.cFeminine, IceTag.cSingular, true, IceTag.cNoDeclension );
			if( !found )    // vonar-innar
			{
				if( len >= 7 )
				{
					root = lex.substring( 0, len - 7 );
					found = searchSuffixCases( root, MorphoClass.NounFeminine2, currToken, wordClass, false, false, false, true, IceTag.cFeminine, IceTag.cSingular, true, IceTag.cNoDeclension );
				}
			}
		}

        else if( lex.endsWith( "urnar" ) && len > 5 )
		{ // súpur-nar
			root = lex.substring( 0, len - 5 );
			found = searchSuffixCases( root, MorphoClass.NounFeminine1, currToken, wordClass, true, true, false, false, IceTag.cFeminine, IceTag.cPlural, true, IceTag.cNoDeclension );
		}

		else if( lex.endsWith( "irnar" ) || lex.endsWith( "arnar" ) )    // vonir-nar
		{
			root = lex.substring( 0, len - 5 );
			found = searchSuffixCases( root, MorphoClass.NounFeminine2, currToken, wordClass, true, true, false, false, IceTag.cFeminine, IceTag.cPlural, true, IceTag.cNoDeclension );

		}
		else if( lex.endsWith( "ið" ) && len > 2 )
		{    // svín-ið
			root = lex.substring( 0, len - 2 );
			found = searchSuffixCases( root, MorphoClass.NounNeuter1, currToken, wordClass, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, true, IceTag.cNoDeclension );
			// veski-ð
			if( !found )
				found = searchSuffixCases( root, MorphoClass.NounNeuter2, currToken, wordClass, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, true, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "inu" ) && len > 3 )
		{  // svín-inu
			root = lex.substring( 0, len - 3 );
			found = searchSuffixCases( root, MorphoClass.NounNeuter1, currToken, wordClass, false, false, true, false, IceTag.cNeuter, IceTag.cSingular, true, IceTag.cNoDeclension );
			if( !found )   // veski-nu, sælgæti-nu
			{
				root = lex.substring( 0, len - 3 );
				found = searchSuffixCases( root, MorphoClass.NounNeuter2, currToken, wordClass, false, false, true, false, IceTag.cNeuter, IceTag.cSingular, true, IceTag.cNoDeclension );
			}
		}

		if( found )
		{
			if( currToken.numTags() > 0 )
			{
				if( logger != null )
					logger.log( "SUFFIX article: " + currToken.lexeme + " " + currToken.allTagStrings() );
				//currToken.setMorpho(true);
				currToken.setUnknownType( IceTokenTags.UnknownType.morpho );
				return true;
			}
			else
				found = false;
		}

		return found;
	}


	private String checkTag( String tagStr, IceTag.WordClass wordClass, char genderLetter, char caseLetter, char numLetter, char declension )
	{
		dummyToken.clearTags();
		dummyToken.addAllTags( tagStr );
		dummyToken.removeAllBut( wordClass );
		if( wordClass == IceTag.WordClass.wcNoun || wordClass == IceTag.WordClass.wcProperNoun )
			dummyToken.removeAllButGender( genderLetter, true );

		if( !dummyToken.noTags() )
		{
			if( !dummyToken.isCase( caseLetter ) )     // Then incorrect case/number was found.
				return null;
			else
			if( (wordClass == IceTag.WordClass.wcNoun || wordClass == IceTag.WordClass.wcProperNoun) && !dummyToken.isNumber( numLetter ) )
			{
				//logger.log("Check tag: " + tagStr + " " + numLetter);
				return null;
			}
			else if( wordClass == IceTag.WordClass.wcAdj && !dummyToken.isDeclension( declension ) )
				return null;
		}
		else
			return null;

		return tagStr;
	}


	private boolean suffixAnalysisCases( IceTokenTags currToken, boolean isProperNoun )
	{
		String lex, root;
		boolean found = false;

		IceTag.WordClass wordClass = IceTag.WordClass.wcNoun;
		if( isProperNoun )
			wordClass = IceTag.WordClass.wcProperNoun;

		lex = currToken.lexeme;
		if( lex.endsWith( "-" ) )
			lex = lex.substring( 0, lex.length() - 1 );
		int len = lex.length();

		if( lex.endsWith( "inn" ) )        // krist-inn
		{
			root = lex.substring( 0, len - 3 );
			found = searchSuffixCases( root, MorphoClass.Adj2, currToken, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cStrong );
		}
		else if( lex.endsWith( "in" ) )        // krist-in
		{
			root = lex.substring( 0, len - 2 );
			found = searchSuffixCases( root, MorphoClass.Adj2, currToken, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cStrong );
		}
		else if( lex.endsWith( "ins" ) )        // krist-ins
		{
			root = lex.substring( 0, len - 3 );
			found = searchSuffixCases( root, MorphoClass.Adj2, currToken, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cStrong );
			if( found )
				found = searchSuffixCases( root, MorphoClass.Adj2, currToken, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cStrong );
		}
		else if( lex.endsWith( "ið" ) )        // krist-ið
		{
			root = lex.substring( 0, len - 2 );
			found = searchSuffixCases( root, MorphoClass.Adj2, currToken, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cStrong );
			// frið,smið,hlið
			if( !found )
			{
				root = lex;
				found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
				if( !found )
					found = searchSuffixCases( root, MorphoClass.NounNeuter1, currToken, wordClass, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cNoDeclension );
			}
		}
		else if( lex.endsWith( "na" ) )
		{ // krist-na
			root = lex.substring( 0, len - 2 );
			found = searchSuffixCases( root, MorphoClass.Adj2, currToken, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cStrong );
		}
		else if( lex.endsWith( "inni" ) )        // krist-inni
		{
			root = lex.substring( 0, len - 4 );
			found = searchSuffixCases( root, MorphoClass.Adj2, currToken, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cStrong );
		}
		else if( lex.endsWith( "innar" ) )        // krist-innar
		{
			root = lex.substring( 0, len - 5 );
			found = searchSuffixCases( root, MorphoClass.Adj2, currToken, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cStrong );
		}
		else if( lex.endsWith( "nu" ) )
		{
			// þéttrið-nu dative is þéttrið-ið nominative
			root = lex.substring( 0, len - 2 );
			found = searchSuffixCases( root, MorphoClass.Adj2, currToken, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cStrong );
			// krist-nu, weak
			if( !found )
			{
				root = lex.substring( 0, len - 2 );
				found = searchSuffixCases( root, MorphoClass.Adj2, currToken, IceTag.WordClass.wcAdj, false, true, true, true, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cWeak );
			}
		}

		else if( lex.endsWith( "aðan" ) )
		{
			// hor-aðan
			root = lex.substring( 0, len - 4 );
			found = searchSuffixCases( root, MorphoClass.Adj4, currToken, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cStrong );
		}
		else if( lex.endsWith( "an" ) )
		{
			// þreytt-an accusative is þreytt-ur nominative, þreytt-um dative, þreytt-s genitive
			root = lex.substring( 0, len - 2 );
			found = searchSuffixCases( root, MorphoClass.Adj1, currToken, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cStrong );
			if( !found )
				found = searchSuffixCases( root, MorphoClass.Adj5, currToken, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cStrong );
		}
		else if( lex.endsWith( "aður" ) )
		{
			if( len > 4 )
			{
				// hor-aður
				root = lex.substring( 0, len - 4 );
				found = searchSuffixCases( root, MorphoClass.Adj4, currToken, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cStrong );
			}
			if( !found ) // iðnað-ur
			{
				root = lex.substring( 0, len - 2 );
				found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
			}
		}
		else if( lex.endsWith( "að" ) )
		{
			// hor-að
			root = lex.substring( 0, len - 2 );
			found = searchSuffixCases( root, MorphoClass.Adj4, currToken, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cStrong );
		}

		else if( lex.endsWith( "ur" ) )
		{
			// hest-ur genitive is hest-ur nominative
			root = lex.substring( 0, len - 2 );
			found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
			if( !found )
			{
				// gald-ur
				root = lex.substring( 0, len - 2 );
				found = searchSuffixCases( root, MorphoClass.NounMasculine3, currToken, wordClass, true, true, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
			}

			if( !found )
			{
				// hreið-ur nominative
				root = lex.substring( 0, len - 2 );
				found = searchSuffixCases( root, MorphoClass.NounNeuter3, currToken, wordClass, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cNoDeclension );
			}
			// fallegur nominative
			if( !found )
			{
				root = lex.substring( 0, len - 2 );
				found = searchSuffixCases( root, MorphoClass.Adj1, currToken, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cStrong );
			}

		}
		else if( lex.endsWith( "egt" ) )
		{
			// fallegt nominative
			root = lex.substring( 0, len - 1 );
			found = searchSuffixCases( root, MorphoClass.Adj1, currToken, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cStrong );
		}
		else if( lex.endsWith( "eg" ) )
		{
			// falleg nominative
			root = lex.substring( 0, len - 1 );
			found = searchSuffixCases( root, MorphoClass.Adj1, currToken, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cStrong );
		}
		else if( lex.endsWith( "rar" ) )
		{
			// þreytt-rar genitive is þreytt nominative
			root = lex.substring( 0, len - 3 );
			found = searchSuffixCases( root, MorphoClass.Adj1, currToken, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cStrong );
			if( !found && len > 4 )
			{
				// hor-aðrar
				root = lex.substring( 0, len - 4 );
				found = searchSuffixCases( root, MorphoClass.Adj4, currToken, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cStrong );
			}
			if( !found )    // lif-rar
			{
				root = lex.substring( 0, len - 3 );
				found = searchSuffixCases( root, MorphoClass.NounFeminine6, currToken, wordClass, false, false, false, true, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cNoDeclension );
			}
		}
		else if( lex.endsWith( "gar" ) )
		// meining-ar genitive is meining nomintaive
		{
			root = lex.substring( 0, len - 2 );
			found = searchSuffixCases( root, MorphoClass.NounFeminine3, currToken, wordClass, false, false, false, true, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "ðar" ) )
		{
			// hernað-ar genitive is hernað-ur nominative
			root = lex.substring( 0, len - 2 );
			found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
			// hlið-ar genitive is hlið nominative
			if( !found )
			{
				root = lex.substring( 0, len - 2 );
				found = searchSuffixCases( root, MorphoClass.NounFeminine2, currToken, wordClass, false, false, false, true, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cNoDeclension );
			}
		}
		else if( lex.endsWith( "unar" ) )
		{
			// vorkunar
			root = lex.substring( 0, len - 4 );
			found = searchSuffixCases( root, MorphoClass.NounFeminine5, currToken, wordClass, false, false, false, true, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "kkjar" ) )
		{
			// bekk-jar
			root = lex.substring( 0, len - 3 );
			found = searchSuffixCases( root, MorphoClass.NounMasculine9, currToken, wordClass, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "ar" ) )
		{
			// von-ar genitive is von nominative
			root = lex.substring( 0, len - 2 );
			found = searchSuffixCases( root, MorphoClass.NounFeminine2, currToken, wordClass, false, false, false, true, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cNoDeclension );
			// fundar,blundar genitive is fundur nominative
			if( !found && len > 4 && lex.endsWith( "ndar" ) )
				found = searchSuffixCases( root, MorphoClass.NounMasculine5, currToken, wordClass, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
			if( !found )    // gítar
			{
				root = lex;
				found = searchSuffixCases( root, MorphoClass.NounMasculine8, currToken, wordClass, true, true, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
			}
		}
		else if( lex.endsWith( "num" ) )
		{
			// þéttriðn-um
			root = lex.substring( 0, len - 3 );
			found = searchSuffixCases( root, MorphoClass.Adj2, currToken, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cStrong );
		}
		else if( lex.endsWith( "uðum" ) )
		{
			// hor-uðum
			root = lex.substring( 0, len - 4 );
			found = searchSuffixCases( root, MorphoClass.Adj4, currToken, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cStrong );
		}
		else if( lex.endsWith( "um" ) )
		{
			// góð-um
			root = lex.substring( 0, len - 2 );
			found = searchSuffixCases( root, MorphoClass.Adj1, currToken, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cStrong );
			if( !found )
				found = searchSuffixCases( root, MorphoClass.Adj5, currToken, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cStrong );

		}
		else if( lex.endsWith( "un" ) )
		{
			// vorkun
			root = lex.substring( 0, len - 2 );
			found = searchSuffixCases( root, MorphoClass.NounFeminine5, currToken, wordClass, true, true, true, false, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "urs" ) )
		{
			// hreið-urs genitive
			root = lex.substring( 0, len - 3 );
			found = searchSuffixCases( root, MorphoClass.NounNeuter3, currToken, wordClass, false, false, false, true, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cNoDeclension );
			if( !found )
				found = searchSuffixCases( root, MorphoClass.NounMasculine3, currToken, wordClass, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "is" ) )
		{
			// merk-is genitive is merki nominative
			root = lex.substring( 0, len - 2 );
			found = searchSuffixCases( root, MorphoClass.NounNeuter2, currToken, wordClass, false, false, false, true, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cNoDeclension );
			if( !found )   // lífeyr-is
				found = searchSuffixCases( root, MorphoClass.NounMasculine7, currToken, wordClass, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "aus" ) )
		{
			// hreyfingarlaus nominative masculine
			root = lex;
			found = searchSuffixCases( root, MorphoClass.Adj1, currToken, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cStrong );
		}
		else if( lex.endsWith( "urs" ) )
		{
			// gald-urs
			root = lex.substring( 0, len - 2 );
			found = searchSuffixCases( root, MorphoClass.NounMasculine3, currToken, wordClass, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "s" ) && !lex.endsWith( "ss" ) )
		{
			// hest-s genitive is hest-ur nominative
			root = lex.substring( 0, len - 1 );
			found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
			if( !found && wordClass == IceTag.WordClass.wcProperNoun )
				found = searchSuffixCases( root, MorphoClass.NounMasculine8, currToken, wordClass, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
			// svín-s
			if( !found )
				found = searchSuffixCases( root, MorphoClass.NounNeuter1, currToken, wordClass, false, false, false, true, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cNoDeclension );
			if( !found )
			{
				// sérstaks
				root = lex.substring( 0, len - 1 );
				found = searchSuffixCases( root, MorphoClass.Adj1, currToken, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cStrong );
				if( found )
					addAdjectiveTag( currToken, IceTag.cNeuter, IceTag.cSingular, IceTag.cGenitive, IceTag.cStrong, IceTag.cPositive );
			}
			if( !found && len > 3 )
			{
				// hor-aðs
				root = lex.substring( 0, len - 3 );
				found = searchSuffixCases( root, MorphoClass.Adj4, currToken, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cStrong );

				if( !found )
				{
					root = lex.substring( 0, len - 3 );
					found = searchSuffixCases( root, MorphoClass.Adj4, currToken, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cStrong );
				}
			}
		}
		else if( lex.endsWith( "andi" ) )
		{ // atvinnurekand-i
			root = lex.substring( 0, len - 1 );
			found = searchSuffixCases( root, MorphoClass.NounMasculine10, currToken, wordClass, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "anda" ) )
		{ // atvinnurekand-a
			root = lex.substring( 0, len - 1 );
			found = searchSuffixCases( root, MorphoClass.NounMasculine10, currToken, wordClass, false, true, true, true, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "a" ) )
		{
			// skól-a accusative/dative/genitive is skól-i nominative
			root = lex.substring( 0, len - 1 );
			if( !checkNormalMasculineNoun( root ) )
				found = searchSuffixCases( root, MorphoClass.NounMasculine2, currToken, wordClass, false, true, true, true, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );

			if( !found )
			{
				// kon-a nominative
				root = lex.substring( 0, len - 1 );
				found = searchSuffixCases( root, MorphoClass.NounFeminine1, currToken, wordClass, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cNoDeclension );
			}
			if( !found && lex.endsWith( "aða" ) && len > 3 )
			{
				// hor-aða
				root = lex.substring( 0, len - 3 );
				found = searchSuffixCases( root, MorphoClass.Adj4, currToken, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cWeak );
				// Could also be strong masculine plural horaðir-horaða-

				if( found )
					addAdjectiveTag( currToken, IceTag.cMasculine, IceTag.cPlural, IceTag.cAccusative, IceTag.cStrong, IceTag.cPositive );
			}
			// þreytt-a accusative is þreytt nominative
			//if ((!found || wordClassLastFound == IceTag.wcNoun) && !lex.endsWith("asta") && !lex.endsWith("ra"))
			if( !found && !lex.endsWith( "asta" ) && !lex.endsWith( "ra" ) )
			{
				root = lex.substring( 0, len - 1 );
				found = searchSuffixCases( root, MorphoClass.Adj1, currToken, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cStrong );
				if( !found )
					found = searchSuffixCases( root, MorphoClass.Adj5, currToken, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cStrong );
				// Could also be veak þreytta konan
				// masculine  þreytti-þreytta-þreytta-þreytta
				// neuter  þreytta-þreytta-þreytta-þreytta
				if( found )
				{
					addAdjectiveTag( currToken, IceTag.cFeminine, IceTag.cSingular, IceTag.cNominative, IceTag.cWeak, IceTag.cPositive );
					addAdjectiveTag( currToken, IceTag.cMasculine, IceTag.cSingular, IceTag.cAccusative, IceTag.cWeak, IceTag.cPositive );
					addAdjectiveTag( currToken, IceTag.cMasculine, IceTag.cSingular, IceTag.cDative, IceTag.cWeak, IceTag.cPositive );
					addAdjectiveTag( currToken, IceTag.cMasculine, IceTag.cSingular, IceTag.cGenitive, IceTag.cWeak, IceTag.cPositive );
					addAdjectiveTag( currToken, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative, IceTag.cWeak, IceTag.cPositive );
					addAdjectiveTag( currToken, IceTag.cNeuter, IceTag.cSingular, IceTag.cAccusative, IceTag.cWeak, IceTag.cPositive );
					addAdjectiveTag( currToken, IceTag.cNeuter, IceTag.cSingular, IceTag.cDative, IceTag.cWeak, IceTag.cPositive );
					addAdjectiveTag( currToken, IceTag.cNeuter, IceTag.cSingular, IceTag.cGenitive, IceTag.cWeak, IceTag.cPositive );
				}
			}

		}
		else if( lex.endsWith( "uðu" ) )
		{
			// hor-uðu
			root = lex.substring( 0, len - 3 );
			found = searchSuffixCases( root, MorphoClass.Adj4, currToken, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cStrong );
		}
		else if( lex.endsWith( "uð" ) )
		{
			// jöfn-uð accusative is jöfnuð-ur nominative
			root = lex;
			found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
			if( !found )
			{
				// hor-uð
				root = lex.substring( 0, len - 2 );
				found = searchSuffixCases( root, MorphoClass.Adj4, currToken, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cStrong );
				if( found )
				{
					addAdjectiveTag( currToken, IceTag.cNeuter, IceTag.cPlural, IceTag.cNominative, IceTag.cStrong, IceTag.cPositive );
					addAdjectiveTag( currToken, IceTag.cNeuter, IceTag.cPlural, IceTag.cAccusative, IceTag.cStrong, IceTag.cPositive );
				}
			}
		}

		else if( lex.endsWith( "gð" ) || lex.endsWith( "rð" ) )
		{
			// ánægð og útkeyrð
			root = lex;
			found = searchSuffixCases( root, MorphoClass.Adj1, currToken, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cStrong );
			if( found ) // add neutuer plural
			{
				addAdjectiveTag( currToken, IceTag.cNeuter, IceTag.cPlural, IceTag.cNominative, IceTag.cStrong, IceTag.cPositive );
				addAdjectiveTag( currToken, IceTag.cNeuter, IceTag.cPlural, IceTag.cAccusative, IceTag.cStrong, IceTag.cPositive );
			}
			if( lex.endsWith( "rð" ) )    // barð, arð
			{
				found = searchSuffixCases( root, MorphoClass.NounNeuter1, currToken, wordClass, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cNoDeclension );
				if( !found )
					found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
			}
		}
		else if( lex.endsWith( "ing" ) )
		{
			// meining
			root = lex;
			found = searchSuffixCases( root, MorphoClass.NounFeminine3, currToken, wordClass, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "ingu" ) )
		{
			// meining-u accusative/dative is meining nominative
			root = lex.substring( 0, len - 1 );
			found = searchSuffixCases( root, MorphoClass.NounFeminine3, currToken, wordClass, false, true, true, false, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cNoDeclension );

		}
		else if( lex.endsWith( "u" ) )
		{
			// fallegu
			if( lex.endsWith( "gu" ) )
			{
				root = lex.substring( 0, len - 1 );
				found = searchSuffixCases( root, MorphoClass.Adj1, currToken, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cStrong );
			}

			if( !found )
			{
				// súp-u accusative/dative/genitive is súp-a nominative
				root = lex.substring( 0, len - 1 );
				found = searchSuffixCases( root, MorphoClass.NounFeminine1, currToken, wordClass, false, true, true, true, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cNoDeclension );
			}

			// góð-u accusative/dative/genitive is góð-a nominative
			// First search strong declension
			if( !found )
			{
				root = lex.substring( 0, len - 1 );
				found = searchSuffixCases( root, MorphoClass.Adj1, currToken, IceTag.WordClass.wcAdj, false, true, true, true, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cWeak );

				if( found )
				// Generate the weak plural tags and strong neuter
				{
					addAdjectiveTag( currToken, IceTag.cNeuter, IceTag.cSingular, IceTag.cDative, IceTag.cStrong, IceTag.cPositive );

					addAdjectiveTag( currToken, IceTag.cMasculine, IceTag.cPlural, IceTag.cNominative, IceTag.cWeak, IceTag.cPositive );
					addAdjectiveTag( currToken, IceTag.cMasculine, IceTag.cPlural, IceTag.cAccusative, IceTag.cWeak, IceTag.cPositive );
					addAdjectiveTag( currToken, IceTag.cMasculine, IceTag.cPlural, IceTag.cDative, IceTag.cWeak, IceTag.cPositive );
					addAdjectiveTag( currToken, IceTag.cMasculine, IceTag.cPlural, IceTag.cGenitive, IceTag.cWeak, IceTag.cPositive );

					addAdjectiveTag( currToken, IceTag.cFeminine, IceTag.cPlural, IceTag.cNominative, IceTag.cWeak, IceTag.cPositive );
					addAdjectiveTag( currToken, IceTag.cFeminine, IceTag.cPlural, IceTag.cAccusative, IceTag.cWeak, IceTag.cPositive );
					addAdjectiveTag( currToken, IceTag.cFeminine, IceTag.cPlural, IceTag.cDative, IceTag.cWeak, IceTag.cPositive );
					addAdjectiveTag( currToken, IceTag.cFeminine, IceTag.cPlural, IceTag.cGenitive, IceTag.cWeak, IceTag.cPositive );

					addAdjectiveTag( currToken, IceTag.cNeuter, IceTag.cPlural, IceTag.cNominative, IceTag.cWeak, IceTag.cPositive );
					addAdjectiveTag( currToken, IceTag.cNeuter, IceTag.cPlural, IceTag.cAccusative, IceTag.cWeak, IceTag.cPositive );
					addAdjectiveTag( currToken, IceTag.cNeuter, IceTag.cPlural, IceTag.cDative, IceTag.cWeak, IceTag.cPositive );
					addAdjectiveTag( currToken, IceTag.cNeuter, IceTag.cPlural, IceTag.cGenitive, IceTag.cWeak, IceTag.cPositive );
				}
			}
		}
		else if( lex.endsWith( "i" ) )
		{

			if( lex.endsWith( "ri" ) && !lex.endsWith( "ari" ) && !lex.endsWith( "óri" ) ) // rule out stjóri
			{
				// hreið-ri dative
				root = lex.substring( 0, len - 2 );
				found = searchSuffixCases( root, MorphoClass.NounNeuter3, currToken, wordClass, false, false, true, false, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cNoDeclension );

				if( !found )
				{
					// gald-ri
					root = lex.substring( 0, len - 2 );
					found = searchSuffixCases( root, MorphoClass.NounMasculine3, currToken, wordClass, false, false, true, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
				}
				if( !found )
				{
					// þreytt-ri dative is þreytt nominative
					root = lex.substring( 0, len - 2 );
					found = searchSuffixCases( root, MorphoClass.Adj1, currToken, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cStrong );
				}
				if( !found && lex.endsWith( "aðri" ) )
				{
					// hor-aðri
					if( lex.length() >= 4 )
					{
						root = lex.substring( 0, len - 4 );
						found = searchSuffixCases( root, MorphoClass.Adj4, currToken, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cStrong );
					}
				}
			}
			if( !found )
			{
				// Special case for neuter
				if( currToken.isOnlyWordClass( IceTag.WordClass.wcNoun ) && currToken.isOnlyGender( IceTag.cNeuter ) &&
				    currToken.isNumber( IceTag.cSingular ) && currToken.isNumber( IceTag.cPlural ) )
				{
					// et: gerpi, gerpi, gerpi, gerpis   ft: gerpi, gerpi, gerpum, gerpa
					found = true;
					addNounTag( currToken, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative, false );
					addNounTag( currToken, IceTag.cNeuter, IceTag.cSingular, IceTag.cAccusative, false );
					addNounTag( currToken, IceTag.cNeuter, IceTag.cSingular, IceTag.cDative, false );
					addNounTag( currToken, IceTag.cNeuter, IceTag.cPlural, IceTag.cNominative, false );
					addNounTag( currToken, IceTag.cNeuter, IceTag.cPlural, IceTag.cAccusative, false );
				}
				if( !found )
				{
					// feiti nominative is feiti other cases
					root = lex;
					found = searchSuffixCases( root, MorphoClass.NounFeminine4, currToken, wordClass, true, true, true, true, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cNoDeclension );
				}
				if( !found )
				{
					root = lex.substring( 0, len - 1 );
					// hest-i dative is hest-ur nominative
					boolean weak = checkWeakMasculineNoun( root );
					if( !weak )
						found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, false, false, true, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );

					if( !found )
					{
						// skól-i nominative is skól-a other cases
						if( !checkNormalMasculineNoun( root ) )
							found = searchSuffixCases( root, MorphoClass.NounMasculine2, currToken, wordClass, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
					}
				}
				if( !found )
				{
					// hell-i  is hell-ir nominative
					root = lex.substring( 0, len - 1 );
					found = searchSuffixCases( root, MorphoClass.NounMasculine7, currToken, wordClass, false, true, true, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
				}
				if( !found )
				{
					// svín-i dative is svín nominative
					root = lex.substring( 0, len - 1 );
					found = searchSuffixCases( root, MorphoClass.NounNeuter1, currToken, wordClass, false, false, true, false, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cNoDeclension );
				}
				if( !found )
				{
					// gerpi dative is gerpi nominative
					root = lex.substring( 0, len - 1 );
					found = searchSuffixCases( root, MorphoClass.NounNeuter2, currToken, wordClass, true, true, true, false, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cNoDeclension );
				}

				// sveitt-i
				// First search strong declension
				if( !found && !lex.endsWith( "ari" ) )
				{
					root = lex.substring( 0, len - 1 );
					found = searchSuffixCases( root, MorphoClass.Adj1, currToken, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cWeak );
				}
			}
		}
		else if( lex.endsWith( "ll" ) )
		{
			// heill
			root = lex.substring( 0, len - 1 );
			found = searchSuffixCases( root, MorphoClass.Adj5, currToken, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cStrong );

			if( !found )
			{
				// stóll
				root = lex.substring( 0, len - 1 );
				found = searchSuffixCases( root, MorphoClass.NounMasculine6, currToken, wordClass, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
			}
			// ball, fall
			if( !found )
			{
				root = lex;
				found = searchSuffixCases( root, MorphoClass.NounNeuter1, currToken, wordClass, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cNoDeclension );
			}
		}
		else if( lex.endsWith( "kn" ) )
		{
			// fíkn, líkn, bákn, tákn
			root = lex;
			found = searchSuffixCases( root, MorphoClass.NounFeminine2, currToken, wordClass, true, true, true, false, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cNoDeclension );
			if( !found )
				found = searchSuffixCases( root, MorphoClass.NounNeuter1, currToken, wordClass, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "ng" ) )
		{
			// gang, lyng
			root = lex;
			found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
			if( !found )
				found = searchSuffixCases( root, MorphoClass.NounNeuter1, currToken, wordClass, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "lf" ) )
		{
			// gólf, hólf, golf
			root = lex;
			found = searchSuffixCases( root, MorphoClass.NounNeuter1, currToken, wordClass, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "l" ) )
		{
			// stól, pól, kjól
			root = lex;
			found = searchSuffixCases( root, MorphoClass.NounMasculine6, currToken, wordClass, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
			// sól, sál
			if( !found )
			{
				root = lex;
				found = searchSuffixCases( root, MorphoClass.NounFeminine2, currToken, wordClass, true, true, true, false, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cNoDeclension );

			}
			// fól, gól
			if( !found )
			{
				root = lex;
				found = searchSuffixCases( root, MorphoClass.NounNeuter1, currToken, wordClass, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cNoDeclension );
			}
			// heil
			if( !found )
			{
				root = lex;
				found = searchSuffixCases( root, MorphoClass.Adj5, currToken, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cStrong );
			}
		}
		else if( lex.endsWith( "tt" ) )
		{
			// pott
			root = lex;
			found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
			// gátt
			if( !found )
			{
				root = lex;
				found = searchSuffixCases( root, MorphoClass.NounFeminine2, currToken, wordClass, true, true, true, false, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cNoDeclension );
			}
			// gott
			if( !found )
			{
				root = lex;
				found = searchSuffixCases( root, MorphoClass.NounNeuter1, currToken, wordClass, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cNoDeclension );
			}
			// þreytt
			if( !found )
			{
				root = lex;
				found = searchSuffixCases( root, MorphoClass.Adj1, currToken, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cStrong );
				if( found )
					addAdjectiveTag( currToken, IceTag.cFeminine, IceTag.cSingular, IceTag.cNominative, IceTag.cStrong, IceTag.cPositive );
			}
		}
		else if( lex.endsWith( "kt" ) || lex.endsWith( "rt" ) )
		{
			// slakt, frekt, svart, bert, hert
			root = lex.substring( 0, len - 1 );
			found = searchSuffixCases( root, MorphoClass.Adj1, currToken, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cStrong );
		}
		else if( lex.endsWith( "t" ) )
		{
			// hest
			root = lex;
			found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
			// kúnst
			if( !found )
				found = searchSuffixCases( root, MorphoClass.NounFeminine2, currToken, wordClass, true, true, true, false, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "bb" ) )
		{
			// stubb
			root = lex;
			found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
			// gubb
			if( !found )
			{
				root = lex;
				found = searchSuffixCases( root, MorphoClass.NounNeuter1, currToken, wordClass, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cNoDeclension );

			}
		}
		else if( lex.endsWith( "pp" ) )
		{
			// kropp
			root = lex;
			found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "kk" ) )
		{
			// flokk
            root = lex;
			found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
            // grikk, hrekk
            if (found && (lex.endsWith( "ekk" ) || lex.endsWith( "ikk" )))
            {
                // Then add the accusative tag
                String tagStr = currToken.getLastTagStr();
                IceTag newTag = new IceTag(tagStr);
                newTag.setCase(IceTag.cDative);
                currToken.addTag(newTag);
                //found = searchSuffixCases( root, MorphoClass.NounMasculine9, currToken, wordClass, false, true, true, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
            }
		}
		else if( lex.endsWith( "m" ) )
		{
			// keim, Ásgrím
			root = lex;
			found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "p" ) )
		{
			// tap, skáp
			root = lex;
			found = searchSuffixCases( root, MorphoClass.NounNeuter1, currToken, wordClass, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cNoDeclension );
			if( !found )
				found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
		}

		else if( lex.endsWith( "d" ) || lex.endsWith( "ð" ) || lex.endsWith( "k" ) )
		{
			// mynd, sand, umdeild, barð, arð, lak, sek
			root = lex;
			if( !lex.endsWith( "ð" ) )
				found = searchSuffixCases( root, MorphoClass.NounFeminine2, currToken, wordClass, true, true, true, false, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cNoDeclension );
			if( !found && !lex.endsWith( "æð" ) ) // samstæð, hliðstæð, conflict. with stæði
				found = searchSuffixCases( root, MorphoClass.NounNeuter1, currToken, wordClass, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cNoDeclension );
			if( !found )
				found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, wordClass, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );

			if( !found )
			{
				found = searchSuffixCases( root, MorphoClass.Adj1, currToken, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cStrong );
				if( found )
				{
					addAdjectiveTag( currToken, IceTag.cNeuter, IceTag.cPlural, IceTag.cNominative, IceTag.cStrong, IceTag.cPositive );
					addAdjectiveTag( currToken, IceTag.cNeuter, IceTag.cPlural, IceTag.cAccusative, IceTag.cStrong, IceTag.cPositive );
				}
			}
		}
		else if( lex.endsWith( "r" ) )
		{
			// bar, skúr, skór
			root = lex;
			found = searchSuffixCases( root, MorphoClass.NounMasculine4, currToken, wordClass, true, true, true, false, IceTag.cMasculine, IceTag.cSingular, false, IceTag.cNoDeclension );
			// hár, tár
			if( !found )
				found = searchSuffixCases( root, MorphoClass.NounNeuter1, currToken, wordClass, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, false, IceTag.cNoDeclension );
		}
		if( found )
		{
			if( logger != null )
				logger.log( "Suffix analysis case: " + currToken.lexeme + " " + currToken.allTagStrings() );
			currToken.setUnknownType( IceTokenTags.UnknownType.morpho );
		}
		//}
		return found;
	}


	private boolean suffixAnalysisCasesPlural( IceTokenTags currToken )
    //  Changes the word from plural to singular and does lookup
	{
		String lex, root;
		boolean found = false;

		lex = currToken.lexeme;
		if( lex.endsWith( "-" ) )
			lex = lex.substring( 0, lex.length() - 1 );
		int len = lex.length();

		if( lex.endsWith( "ar" ) )
		{
			// þreytt-ar is þreytt in singular nominative
			root = lex.substring( 0, len - 2 );
			found = searchSuffixCases( root, MorphoClass.Adj1, currToken, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cFeminine, IceTag.cPlural, false, IceTag.cStrong );
			if( !found ) // heil-ar
				found = searchSuffixCases( root, MorphoClass.Adj5, currToken, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cFeminine, IceTag.cPlural, false, IceTag.cStrong );
			if( !found )
			{
				// meining-ar is meining in singular mominative
				root = lex.substring( 0, len - 2 );
				found = searchSuffixCases( root, MorphoClass.NounFeminine3, currToken, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cFeminine, IceTag.cPlural, false, IceTag.cNoDeclension );
			}
			// hest-ar is hest-ur in singular nominative
			if( !found )
			{
				root = lex.substring( 0, len - 2 );
				found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cNoDeclension );
			}
			// skól-ar is skól-i in singular nominative
			if( !found )
			{
				root = lex.substring( 0, len - 2 );
				if( !checkNormalMasculineNoun( root ) )
					found = searchSuffixCases( root, MorphoClass.NounMasculine2, currToken, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cNoDeclension );
			}

			// maur-ar is maur in singular nominative
			if( !found )
			{
				root = lex.substring( 0, len - 2 );
				found = searchSuffixCases( root, MorphoClass.NounMasculine4, currToken, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cNoDeclension );
			}
			// krist-nar is krist-in in singular nominative
			if( !found )
			{
				root = lex.substring( 0, len - 3 );
				found = searchSuffixCases( root, MorphoClass.Adj2, currToken, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cFeminine, IceTag.cPlural, false, IceTag.cStrong );
			}
			if( !found && lex.endsWith( "rar" ) )
			{
				// gald-rar
				root = lex.substring( 0, len - 3 );
				found = searchSuffixCases( root, MorphoClass.NounMasculine3, currToken, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cNoDeclension );
			}

		}
		else if( lex.endsWith( "inna" ) )
		{
			// krist-nir is krist-inn in singular nominative
			root = lex.substring( 0, len - 4 );
			found = searchSuffixCases( root, MorphoClass.Adj2, currToken, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cStrong );
			if( found )
			{
				addAdjectiveTag( currToken, IceTag.cFeminine, IceTag.cPlural, IceTag.cGenitive, IceTag.cStrong, IceTag.cPositive );
				addAdjectiveTag( currToken, IceTag.cNeuter, IceTag.cPlural, IceTag.cGenitive, IceTag.cStrong, IceTag.cPositive );
			}
		}
		else if( lex.endsWith( "in" ) )
		{
			// krist-in is krist-ið in singular nominative
			root = lex.substring( 0, len - 2 );
			found = searchSuffixCases( root, MorphoClass.Adj2, currToken, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cNeuter, IceTag.cPlural, false, IceTag.cStrong );
		}

		else if( (lex.endsWith( "rra" ) || lex.endsWith( "ra" )) )
		{   //hárra , þreyttra
			// þreytt-ra is þreytt in singular nominative
			if( lex.endsWith( "rra" ) )
				root = lex.substring( 0, len - 3 );
			else
				root = lex.substring( 0, len - 2 );
			found = searchSuffixCases( root, MorphoClass.Adj1, currToken, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cFeminine, IceTag.cPlural, false, IceTag.cStrong );
			if( found )
			{
				addAdjectiveTag( currToken, IceTag.cMasculine, IceTag.cPlural, IceTag.cGenitive, IceTag.cStrong, IceTag.cPositive );
				addAdjectiveTag( currToken, IceTag.cNeuter, IceTag.cPlural, IceTag.cGenitive, IceTag.cStrong, IceTag.cPositive );
			}
			// Could also be masculine and neuter!
			if( !found )
			{
				found = searchSuffixCases( root, MorphoClass.Adj1, currToken, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cStrong );
				if( found )
				{
					addAdjectiveTag( currToken, IceTag.cFeminine, IceTag.cPlural, IceTag.cGenitive, IceTag.cStrong, IceTag.cPositive );
					addAdjectiveTag( currToken, IceTag.cNeuter, IceTag.cPlural, IceTag.cGenitive, IceTag.cStrong, IceTag.cPositive );
				}
			}
			if( !found )
			{
				found = searchSuffixCases( root, MorphoClass.Adj1, currToken, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cNeuter, IceTag.cPlural, false, IceTag.cStrong );
				if( found )
				{
					addAdjectiveTag( currToken, IceTag.cFeminine, IceTag.cPlural, IceTag.cGenitive, IceTag.cStrong, IceTag.cPositive );
					addAdjectiveTag( currToken, IceTag.cMasculine, IceTag.cPlural, IceTag.cGenitive, IceTag.cStrong, IceTag.cPositive );
				}
			}
			if( !found ) // gald-ra
			{
				root = lex.substring( 0, len - 2 );
				found = searchSuffixCases( root, MorphoClass.NounMasculine3, currToken, IceTag.WordClass.wcNoun, false, true, false, true, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cNoDeclension );
			}

		}
		else if( lex.endsWith( "ga" ) )
		{
			// hug-a accusative/genitive is hug-ur nomintaive
			root = lex.substring( 0, len - 1 );
			found = searchSuffixCases( root, MorphoClass.NounMasculine5, currToken, IceTag.WordClass.wcNoun, false, true, false, true, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cNoDeclension );
			if( !found )
			{
				root = lex.substring( 0, len - 1 );
				found = searchSuffixCases( root, MorphoClass.NounFeminine3, currToken, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cFeminine, IceTag.cPlural, false, IceTag.cNoDeclension );
			}
			// falleg-a menn accusative is fallegur singular nominative;
			if( !found )
			{
				root = lex.substring( 0, len - 1 );
				found = searchSuffixCases( root, MorphoClass.Adj1, currToken, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cStrong );
			}
		}

		else if( lex.endsWith( "ja" ) )
		{
			// verkj-a genitive is verk-ur nominative;
			root = lex.substring( 0, len - 2 );
			found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cNoDeclension );
			// merkj-a genitive is merki nominative;
			if( !found )
			{
				root = lex.substring( 0, len - 2 );
				found = searchSuffixCases( root, MorphoClass.NounNeuter2, currToken, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cNeuter, IceTag.cPlural, false, IceTag.cNoDeclension );
			}
		}
		else if( lex.endsWith( "a" ) )
		{

			if( lex.endsWith( "aða" ) && len > 4 )
			{ // horaða
				// hor-aða
				root = lex.substring( 0, len - 4 );
				found = searchSuffixCases( root, MorphoClass.Adj4, currToken, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cFeminine, IceTag.cSingular, false, IceTag.cStrong );
			}
			if( !found && lex.endsWith( "ða" ) )
			{
				// búð-a genitive is búð nominative;
				root = lex.substring( 0, len - 1 );
				found = searchSuffixCases( root, MorphoClass.NounFeminine2, currToken, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cFeminine, IceTag.cPlural, false, IceTag.cNoDeclension );
				// verkstæð-a genitive is verkstæð-i nomintaive
				if( !found )
				{
					root = lex.substring( 0, len - 1 );
					found = searchSuffixCases( root, MorphoClass.NounNeuter2, currToken, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cNeuter, IceTag.cPlural, false, IceTag.cNoDeclension );
				}
				// markað-a genitive is markað-ur nomintaive
				if( !found )
				{
					root = lex.substring( 0, len - 1 );
					found = searchSuffixCases( root, MorphoClass.NounMasculine5, currToken, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cNoDeclension );
				}
			}
			// hest-a accusative/genitive is hest-ur nominative;
			root = lex.substring( 0, len - 1 );
			if( !found )
				found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, IceTag.WordClass.wcNoun, false, true, false, true, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cNoDeclension );
			// skól-a
			if( !found )
			{
				if( !checkNormalMasculineNoun( root ) )
					found = searchSuffixCases( root, MorphoClass.NounMasculine2, currToken, IceTag.WordClass.wcNoun, false, true, false, true, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cNoDeclension );
			}
			// svín-a genitive is svín nominative;
			if( !found )
			{
				root = lex.substring( 0, len - 1 );
				found = searchSuffixCases( root, MorphoClass.NounNeuter1, currToken, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cNeuter, IceTag.cPlural, false, IceTag.cNoDeclension );
			}
			// kvikind-a genitive is kvikindi nominative;
			if( !found )
			{
				root = lex.substring( 0, len - 1 );
				found = searchSuffixCases( root, MorphoClass.NounNeuter2, currToken, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cNeuter, IceTag.cPlural, false, IceTag.cNoDeclension );
			}
			// súp-a genitive is súpa nominative;
			if( !found )
			{
				root = lex.substring( 0, len - 1 );
				found = searchSuffixCases( root, MorphoClass.NounFeminine1, currToken, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cFeminine, IceTag.cPlural, false, IceTag.cNoDeclension );
			}
			// von-a genitive is von nominative;
			if( !found )
			{
				root = lex.substring( 0, len - 1 );
				found = searchSuffixCases( root, MorphoClass.NounFeminine2, currToken, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cFeminine, IceTag.cPlural, false, IceTag.cNoDeclension );
			}
			// sveitt-a menn accusative is fallegur singular nominative;
			if( !found )
			{
				root = lex.substring( 0, len - 1 );
				found = searchSuffixCases( root, MorphoClass.Adj1, currToken, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cStrong );
			}
			if( !found && lex.endsWith( "na" ) && len > 4 )
			{
				// súp-na is súp-a in singular nominative
				root = lex.substring( 0, len - 2 );
				found = searchSuffixCases( root, MorphoClass.NounFeminine1, currToken, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cFeminine, IceTag.cPlural, false, IceTag.cNoDeclension );
				if( !found )
					// krist-na is krist-inn in singular nominative
					found = searchSuffixCases( root, MorphoClass.Adj2, currToken, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cStrong );
			}

		}
		else if( lex.endsWith( "gjum" ) )
		{  // engjum, hengjum
			// engj-um is engi
			root = lex.substring( 0, len - 3 );
			found = searchSuffixCases( root, MorphoClass.NounNeuter2, currToken, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cNeuter, IceTag.cPlural, false, IceTag.cNoDeclension );

		}
		else if( lex.endsWith( "kkjum" ) )
		{  // sekkjum, dekkjum
			root = lex.substring( 0, len - 3 );
			found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cNoDeclension );
			if( !found )
				found = searchSuffixCases( root, MorphoClass.NounNeuter1, currToken, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cNeuter, IceTag.cPlural, false, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "unum" ) )
		{
			// vitj-unum is vitj-un in singular nominative/accusative
			root = lex.substring( 0, len - 4 );
			found = searchSuffixCases( root, MorphoClass.NounFeminine5, currToken, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cFeminine, IceTag.cPlural, false, IceTag.cNoDeclension );
		}

		else if( lex.endsWith( "uðum" ) )    // hor-uðum
		{
			root = lex.substring( 0, len - 4 );
			int count = 1;
			// u-hljóðvarp
			String newRoot = hljodVarp( root, 'ö', 'a' );
			if( newRoot != null )
				count = 2;

			for( int i = 1; i <= count && !found; i++ )
			{
				if( i == 2 )
					root = newRoot;

				found = searchSuffixCases( root, MorphoClass.Adj4, currToken, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cStrong );
				// could also be feminine and neuter
				if( found )
				{
					addAdjectiveTag( currToken, IceTag.cFeminine, IceTag.cPlural, IceTag.cDative, IceTag.cStrong, IceTag.cPositive );
					addAdjectiveTag( currToken, IceTag.cNeuter, IceTag.cPlural, IceTag.cDative, IceTag.cStrong, IceTag.cPositive );
				}
			}
		}

		else if( lex.endsWith( "um" ) )
		{
			root = lex.substring( 0, len - 2 );
			int count = 1;
			// u-hljóðvarp
			String newRoot = hljodVarp( root, 'ö', 'a' );
			if( newRoot != null )
				count = 2;

			for( int i = 1; i <= count && !found; i++ )
			{
				if( i == 2 )
					root = newRoot;

				// góð-um is góð-ur in singular nominative
				found = searchSuffixCases( root, MorphoClass.Adj1, currToken, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cStrong );
				if( !found )
					found = searchSuffixCases( root, MorphoClass.Adj2, currToken, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cStrong );
				if( !found )
					found = searchSuffixCases( root, MorphoClass.Adj5, currToken, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cStrong );
				// could also be feminine and neuter
				if( found )
				{
					addAdjectiveTag( currToken, IceTag.cFeminine, IceTag.cPlural, IceTag.cDative, IceTag.cStrong, IceTag.cPositive );
					addAdjectiveTag( currToken, IceTag.cNeuter, IceTag.cPlural, IceTag.cDative, IceTag.cStrong, IceTag.cPositive );
				}
				// hest-um is hest-ur in singular nominative

				// súp-um is súp-u in singular dative
				if( !found )
					found = searchSuffixCases( root, MorphoClass.NounFeminine1, currToken, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cFeminine, IceTag.cPlural, false, IceTag.cNoDeclension );
				// von-um is von in singular dative,
				if( !found )
					found = searchSuffixCases( root, MorphoClass.NounFeminine2, currToken, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cFeminine, IceTag.cPlural, false, IceTag.cNoDeclension );
				// meining-um is meining in singular nominative,
				if( !found )
					found = searchSuffixCases( root, MorphoClass.NounFeminine3, currToken, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cFeminine, IceTag.cPlural, false, IceTag.cNoDeclension );
				if( !found )
					found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cNoDeclension );
				// skól-um is skól-a in singular dative
				if( !found )
				{
					if( !checkNormalMasculineNoun( root ) )
						found = searchSuffixCases( root, MorphoClass.NounMasculine2, currToken, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cNoDeclension );
				}
				// lán-um is lán
				if( !found )
					found = searchSuffixCases( root, MorphoClass.NounNeuter1, currToken, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cNeuter, IceTag.cPlural, false, IceTag.cNoDeclension );
				// gerp-um is gerpi
				if( !found )
					found = searchSuffixCases( root, MorphoClass.NounNeuter2, currToken, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cNeuter, IceTag.cPlural, false, IceTag.cNoDeclension );
				// staur-um is staur
				if( !found )
					found = searchSuffixCases( root, MorphoClass.NounMasculine4, currToken, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cNoDeclension );
			}
		}

		else if( lex.endsWith( "ndur" ) )
		{
			// áheyrend-ur is áheyrandi-i in singular nominative
			root = lex.substring( 0, len - 2 );
			found = searchSuffixCases( root, MorphoClass.NounMasculine2, currToken, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "ur" ) )
		{
			// súp-ur is súp-a in singular nominative
			root = lex.substring( 0, len - 2 );
			found = searchSuffixCases( root, MorphoClass.NounFeminine1, currToken, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cFeminine, IceTag.cPlural, false, IceTag.cNoDeclension );

		}

		else if( lex.endsWith( "anir" ) )
		{
			// vitj-anir is vitjun in singular nominative/accusative
			root = lex.substring( 0, len - 4 );
			found = searchSuffixCases( root, MorphoClass.NounFeminine5, currToken, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cFeminine, IceTag.cPlural, false, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "ir" ) )
		{
			// von-ir is von in singular nominative/accusative
			root = lex.substring( 0, len - 2 );
			found = searchSuffixCases( root, MorphoClass.NounFeminine2, currToken, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cFeminine, IceTag.cPlural, false, IceTag.cNoDeclension );
			// tug-ir is tug-ur in singular nominative
			if( !found )
			{
				root = lex.substring( 0, len - 2 );
				found = searchSuffixCases( root, MorphoClass.NounMasculine1, currToken, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cNoDeclension );
			}
			// þreytt-ir is þreytt-ur in singular nominative
			if( !found )
			{
				root = lex.substring( 0, len - 2 );
				found = searchSuffixCases( root, MorphoClass.Adj1, currToken, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cStrong );
			}
			// krist-nir is krist-inn in singular nominative
			if( !found )
			{
				root = lex.substring( 0, len - 3 );
				found = searchSuffixCases( root, MorphoClass.Adj2, currToken, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cStrong );
			}
			if( !found )
			{// heilir
				root = lex.substring( 0, len - 2 );
				found = searchSuffixCases( root, MorphoClass.Adj5, currToken, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cPlural, false, IceTag.cStrong );
			}

		}

		else if( lex.endsWith( "lf" ) )
		{
			// gólf, hólf, golf
			root = lex;
			found = searchSuffixCases( root, MorphoClass.NounNeuter1, currToken, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cNeuter, IceTag.cPlural, false, IceTag.cNoDeclension );
		}
		else if( lex.endsWith( "i" ) )
		{
			// special case for masculine plural words ending with "ir" in nominative
			// gestir-gesti
			root = lex + "r";
			//String tagStr = dictionary.lookup( root, true );
            String tagStr = dictionaryLookup( root, true );
            if( tagStr == null )
			{
				root = lex + "rnir";
				//tagStr = dictionary.lookup( root, true );
                tagStr = dictionaryLookup( root, true );
            }
			if( tagStr != null &&
			    (tagStr.equals( IceTag.tagNounMasculinePluralNominative ) || tagStr.equals( IceTag.tagNounMasculinePluralNominativeArticle )) )
			{
				addNounTag( currToken, IceTag.cMasculine, IceTag.cPlural, IceTag.cAccusative, false );
				found = true;
			}
		}

		//}
		if( found )
		{
			if( logger != null )
				logger.log( "Suffix analysis case plural: " + currToken.lexeme + " " + currToken.allTagStrings() );
			currToken.setUnknownType( IceTokenTags.UnknownType.morpho );
		}
		return found;
	}


	private void defaultTags( IceTokenTags currToken )
	{
		addNounTag( currToken, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative, false );
		addNounTag( currToken, IceTag.cNeuter, IceTag.cSingular, IceTag.cAccusative, false );
		addNounTag( currToken, IceTag.cNeuter, IceTag.cPlural, IceTag.cNominative, false );
		addNounTag( currToken, IceTag.cNeuter, IceTag.cPlural, IceTag.cAccusative, false );
		addNounTag( currToken, IceTag.cMasculine, IceTag.cSingular, IceTag.cAccusative, false );
		currToken.setUnknownType( IceTokenTags.UnknownType.guessed );
		if( logger != null )
			logger.log( "Ending analysis, default tags: " + currToken.lexeme + " " + currToken.allTagStrings() );
	}


	private void generateMissingTagsUnknown( IceTokenTags currToken )
	{
		if( currToken.isOnlyWordClass( IceTag.WordClass.wcAdj ) )
		{
			generateMissingAdj( currToken );
			// Check if we can rule out weak declension
			if( currToken.isAdjectiveWeak() && !isVowel( currToken.lexeme.charAt( currToken.lexeme.length() - 1 ) ) )
				setDeclension( currToken, IceTag.cStrong );
		}
		else if( currToken.isOnlyWordClass( IceTag.WordClass.wcNoun ) )
			generateMissingNoun( currToken );
	}

	public boolean morphoAnalysisSuffix( IceTokenTags currToken, boolean isProperNoun )
	{
		boolean found = suffixAnalysisArticle( currToken, isProperNoun );

		boolean found2 = suffixAnalysisCases( currToken, isProperNoun );
		boolean found3 = suffixAnalysisCasesPlural( currToken );

		if( (found || found2 || found3) ) // Possible adjective tags are numerous. Get them
            generateMissingTagsUnknown( currToken );

		return (found || found2 || found3);
	}

	

	public boolean numberAnalysis( IceTokenTags currToken )
	{
		boolean found = false;
		if( currToken.lexeme.matches( "[0-9\\-\\/\\.,]+%" ) )
		{
			currToken.setTag( IceTag.tagPercentage );
			found = true;
		}
		else if( currToken.lexeme.matches( "[0-9\\-\\/\\.,:]+" ) )
		{
			currToken.setTag( IceTag.tagOrdinal );
			found = true;
		}
		if( found )
			currToken.setUnknownType( IceTokenTags.UnknownType.guessed );
		return found;
	}

	public boolean morphoAnalysisToken( IceTokenTags currToken, IceTokenTags prevToken )
	{
		boolean found, verbFound = false;

		if( currToken.isPunctuation() )
			return false;

		// First check if the token has been marked as a proper noun
		if( currToken.isUnknownProperNoun() )
			found = analyzeProperNoun( currToken, true );
		else
		{
			found = numberAnalysis( currToken );

			// The next four statements should be commented out if only running the ending analyser

            if( !found )
				verbFound = verbAnalysis( currToken, prevToken, true );

			if( !found )
				found = morphoAnalysisSuffix( currToken, false );

			if( !found )
				found = addPrefix( currToken );

			if( !found )
				found = compoundAnalysisSuffix( currToken, false );

            if( !found && !verbFound)
				found = foreignAnalysis( currToken );

            if( !found && !verbFound )
			{
				found = endingAnalysis( currToken, IceTag.WordClass.wcUndef, true );
				if( !found )
				{
					defaultTags( currToken );
				}
			}
		}
		return (found || verbFound);
	}

  /*
 * Tries to assigns the appropriate tag to tokens with null tag based on the word suffix
 */
	public void morphoAnalysis()
	{
		IceTokenTags currToken, prevToken = null;

		for( int i = 0; i < tokens.size(); i++ )
		{
			currToken = (IceTokenTags)tokens.get( i );
			if( currToken.noTags() )
				morphoAnalysisToken( currToken, prevToken );
            
            prevToken = currToken;
		}
	}

}   // End class