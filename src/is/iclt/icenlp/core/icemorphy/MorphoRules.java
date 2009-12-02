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

import is.iclt.icenlp.core.utils.IceTag;

import java.util.ArrayList;

/**
 * A class containing morphological rules used by IceMorphy for guessing
 * the tag profile for unknown words.
 *
 * <p>IceMorphy is described in the paper:
 * <ul>
 * <li>Hrafn Loftsson. Tagging Icelandic text: A linguistic rule-based approach. Nordic Journal of Linguistics (2008), 31(1), 47-72.</li>
 *
 * @author Hrafn Loftsson
 */


public class MorphoRules {
    public enum MorphoClass
    { NounMasculine1, NounMasculine2, NounMasculine3, NounMasculine4, NounMasculine5,
      NounMasculine6, NounMasculine7, NounMasculine8, NounMasculine9, NounMasculine10,
      NounFeminine1,  NounFeminine2, NounFeminine3,  NounFeminine4,  NounFeminine5, NounFeminine6,
      NounNeuter1,  NounNeuter2, NounNeuter3,  NounNeuter4,
      Adj1, Adj2, Adj3, Adj4, Adj5,
      VerbActive1, VerbActive2, VerbActive3, VerbActive4, VerbActive5, VerbActive6,
      VerbMiddle1, VerbMiddle2, VerbMiddle3, None
    }
    
    public ArrayList<MorphoRuleNounAdjective> listNounArticle;
    public ArrayList<MorphoRuleNounAdjective> listNounAdjectiveSingular;
    public ArrayList<MorphoRuleNounAdjective> listNounAdjectivePlural;
    public ArrayList<MorphoRuleVerbFinite> listVerbFinite;
    public ArrayList<MorphoRuleVerb> listVerb;
    public ArrayList<MorphoRuleVerbPastParticiple> listVerbPastParticiple;
    
    public String[] searchStrings;
    public final int searchStringLastIndex=35;

    // MORPHOLOGICAL ENDINGS associated with certain morphological classes
    // Masculine nouns
    // Example: suffixeNounMasculine1 lists inflectional endings in singular and plural for a
    //          root beloning to the class NounMasculine1, e.g.
    //              hest-ur, hest-, hest-i, hest-s, hest-ar, hest-a, hest-um, hest-a
    //          articlesMasculine are endings added to the root+suffix, e.g.
    //              hest-ur-inn, hest-inn, hest-i-num, hest-s-ins, hest-ar-nir, hest-a-na, hest-u(m)-num, hest-a-nna
    //          If an article contains the pattern ".*/[0-9]+", then the number part signifies how many characters 
    //          need to be deleted from the root+suffix before adding the article
    private static String[] suffixesNounMasculine1 = {"ur", "", "i", "s", "ar", "a", "um", "a"}; // hest-
    private static String[] articlesMasculine = {"inn", "inn", "num", "ins", "nir", "na", "num/1", "nna"};

    private static String[] suffixesNounMasculine2 = {"i", "a", "a", "a"}; // skól-
    private static String[] articlesMasculine2 = {"nn", "nn", "num", "ns"};

    private static String[] suffixesNounMasculine3 = {"ur", "ur", "ri", "urs", "rar", "ra", "rum", "ra"}; // gald-
    private static String[] suffixesNounMasculine4 = {"", "", "", "s", "ar", "a", "um", "a"}; // maur-
    private static String[] suffixesNounMasculine5 = {"ur", "", "i", "ar", "ir", "i", "um", "a"}; // fund-
    private static String[] suffixesNounMasculine6 = {"l", "", "i", "s", "ar", "a", "um", "a"}; // stól-

    private static String[] suffixesNounMasculine7 = {"ir", "i", "i", "s", "ar", "a", "um", "a"}; // hell-
    private static String[] articlesMasculine7 = {"inn", "nn", "num", "ins", "nir", "na", "num/1", "nna"};

    private static String[] suffixesNounMasculine8 = {"", "", "", "s", "ar", "a", "um", "a"}; // gítar-
    private static String[] suffixesNounMasculine9 = {"ur", "", "", "jar", "ir", "i", "jum", "ja"}; // bekk-

    private static String[] suffixesNounMasculine10 = {"i", "a", "a", "a", "ur", "ur", "um", "a"}; // atvinnurekand-
    private static String[] articlesMasculine10 = {"nn", "nn", "num", "ns", "nir", "na", "num/1", "nna"};

    // Feminine nouns
    private static String[] suffixesNounFeminine1 = {"a", "u", "u", "u", "ur", "ur", "um", "a"}; // súp-
    private static String[] articlesFeminine1 = {"n", "na", "nni", "nnar", "nar", "nar", "num/1", "nna"};

    private static String[] suffixesNounFeminine2 = {"", "", "", "ar", "ir", "ir", "um", "a"}; // von-
    private static String[] articlesFeminine2 = {"in", "ina", "inni", "innar", "nar", "nar", "num/1", "nna"};

    private static String[] suffixesNounFeminine3 = {"", "u", "u", "ar", "ar", "ar", "um", "a"}; // meining-
    private static String[] articlesFeminine3 = {"in", "na", "nni", "innar", "nar", "nar", "num/1", "nna"};

    private static String[] suffixesNounFeminine4 = {"", "", "", ""}; // kæti-
    private static String[] articlesFeminine4 = {"n", "na", "nni", "nnar"};

    private static String[] suffixesNounFeminine5 = {"un", "un", "un", "unar", "anir", "anir", "unum", "ana"}; // vitj-
    private static String[] articlesFeminine5 = {"in", "ina", "inni", "innar", "nar", "nar", "num/1", "nna"};

    private static String[] suffixesNounFeminine6 = {"ur", "ur", "ur", "rar"}; // lif-
    private static String[] articlesFeminine6 = {"rin/2", "rina/2", "rinni/2", "innar"};

    // Neuter nouns
    private static String[] suffixesNounNeuter1 = {"", "", "i", "s", "", "", "um", "a"}; // svín-
    private static String[] articlesNeuter1 = {"ið", "ið", "nu", "ins", "in", "in", "num/1", "nna"};

    private static String[] suffixesNounNeuter2 = {"i", "i", "i", "is", "i", "i", "um", "a"}; // efn-
    private static String[] articlesNeuter2 = {"ð", "ð", "nu", "ins", "n", "n", "num/1", "nna"};

    private static String[] suffixesNounNeuter3 = {"ur", "ur", "ri", "urs", "ur", "ur", "rum", "ra"}; // fóð-
    private static String[] articlesNeuter3 = {"rið/2", "rið/2", "nu", "ins", "rin/2", "rin/2", "num/1", "nna"};

    private static String[] suffixesNounNeuter4 = {"a", "a", "a", "a", "u", "u", "um", "a"}; // hjart-
    private static String[] articlesNeuter4 = {"ð", "ð", "nu", "ns", "n", "n", "num/1", "nna"};

    // Adjectives
    // This lists all inflectional endings in masculine, feminine, neutuer, singular and plural.
    // Example:
    //   suffixesAdj1:       þreytt-ur, þreytt-an, þreytt-um, þreytt-s, þreytt-, þreytt-a, þreytt-ri, þreytt-rar, þreytt-, þreytt-, þreytt-u, þreytt-s
    //   suffixesAdj1Plural: þreytt-ir, þreytt-a, þreytt-um, þreytt-ra, þreytt-ar, þreytt-ar, þreytt-um, þreytt-ra, þreytt-, þreytt-, þreytt-u, þreytt-ra
    private static String[] suffixesAdj1 = {"ur", "an", "um", "s", "", "a", "ri", "rar", "", "", "u", "s"}; // þreytt-
    private static String[] suffixesAdj1Plural = {"ir", "a", "um", "ra", "ar", "ar", "um", "ra", "", "", "um", "ra"}; // þreytt-

    private static String[] suffixesAdjWeak = {"i", "a", "a", "a", "a", "u", "u", "u", "a", "a", "a", "a"};

    private static String[] suffixesAdj2 = {"inn", "inn", "num", "ins", "in", "na", "inni", "innar", "ið", "ið", "nu", "ins"}; // krist-inn
    private static String[] suffixesAdj2Plural = {"nir", "na", "num", "inna", "nar", "nar", "num", "inna", "in", "in", "num", "inna"}; // þreytt-

    private static String[] suffixesAdj4 = {"aður", "aðan", "uðum", "aðs", "uð", "aða", "aðri", "aðrar", "að", "að", "uðu", "aðs"}; // horað-
    private static String[] suffixesAdj4Plural = {"aðir", "aða", "uðum", "aðra", "aðar", "aðar", "uðum", "aðra", "uð", "uð", "uðum", "aðra"}; //

    private static String[] suffixesAdj5 = {"l", "an", "um", "s", "", "a", "ri", "rar", "t", "t", "u", "s"}; // heil-

    // Verbs
    // This list all possibles suffixes
    // Example for suffixesVerbActive1:
    //  borð-a, borð-ar, borð-um, borð-ið, borð-aði, borð-aðir, borð-uðum, borð-uðu, borð-að, borð-ist, borð-aðist, borð-andi
    private static String[] suffixesVerbActive1 = {"a", "ar", "um", "ið", "aði", "aðir", "uðum", "uðuð", "uðu", "að", "ist", "aðist", "andi"}; // borð-a
    private static String[] suffixesVerbActive2 = {"i", "ir", "um", "ið", "a", "di", "ti", "ði", "dir", "tir",
                       "ðir", "dum", "tum", "ðum", "duð", "ðuð", "du", "tu", "ðu", "ist", "t", "andi"}; // reyn-a, fyll-a, lif-a
    private static String[] suffixesVerbActive3 = {"di", "dir", "dum", "dið", "da", "ti", "tir", "tum", "tuð", "tu", "dist", "andi"}; // bend-a
    private static String[] suffixesVerbActive4 = {"ði", "ðir", "ðum", "ðið", "ða", "ddi", "ddir", "ddum", "dduð", "ddu", "ðist", "ðandi"}; // bræ-ða
    private static String[] suffixesVerbActive5 = {"i", "ir", "jum", "ið", "ja", "di", "dir", "dum", "duð", "du", "dist", "jandi"}; // þyng-ja
    private static String[] suffixesVerbActive6 = {"ði", "ðir", "ðum", "ðið", "ða", "ti", "tir", "tum", "tuð", "tu", "tist", "tandi"}; // virð-a

    private static String[] suffixesVerbMiddle1 = {"ist", "umst", "ast", "ðist", "ðumst", "ðust"}; // áger-ast
    private static String[] suffixesVerbMiddle2 = {"st", "umst", "ist", "ast"}; // drep-ast
    //private static String[] suffixesVerbMiddle3 = {"ast", "umst", "ast", "aðist", "uðumst", "uðust"}; // burð-ast



    /**
     * Constructor
     */
    public MorphoRules() {
         searchStrings = new String[searchStringLastIndex+1];
                 
         createNounArticleRules();
         createNounAdjectiveSingularRules();
         createNounAdjectivePluralRules();
         createVerbFiniteRules();
         createVerbRules();
         createVerbPastParticipleRules();
    }

    /**
	 * Returns the declension (strong or weak) corresponding to the i-th adjective search string
	 */
    public char getDeclension(int index) {

           char declension;
           if( index <= 23 )
			    declension = IceTag.cStrong;
		   else
				declension = IceTag.cWeak;

            return declension;
    }

    /**
	 * Returns the number (singular or plural) corresponding to the i-th search string
	 */
    public char getNumber(IceTag.WordClass wClass, int index) {

        char numLetter;
        if( wClass == IceTag.WordClass.wcAdj )
		{
				if( index <= 11 || index >= 24 )
					numLetter = IceTag.cSingular;
				else
					numLetter = IceTag.cPlural;
		}
		else
		{
				if( index <= 7 )
					numLetter = IceTag.cSingular;
				else
					numLetter = IceTag.cPlural;
		}
        return numLetter;
    }

    /**
	 * Returns the case (nominative, accusative, dative, genitive) corresponding to the i-th search string
	 */
    public char getCase(MorphoClass mClass, int index) {

        char caseLetter='*';
        switch( index )
		{
				case 0: case 4: case 8: case 12: case 20: case 24: case 28: case 32:
                    caseLetter = IceTag.cNominative;
					break;
				case 1: case 5: case 9: case 13: case 21: case 25: case 29: case 33:
                    caseLetter = IceTag.cAccusative;
					break;
				case 2: case 6: case 10: case 14: case 22: case 26: case 30: case 34:
                    caseLetter = IceTag.cDative;
					break;
				case 3: case 7: case 11: case 15: case 23: case 27: case 31: case 35:
                    caseLetter = IceTag.cGenitive;
					break;

				case 16:
					if( mClass == MorphoClass.NounFeminine1 )
						caseLetter = IceTag.cGenitive;
					else
						caseLetter = IceTag.cNominative;
					break;
				case 17:
					if( mClass == MorphoClass.NounFeminine1 )
						caseLetter = IceTag.cGenitive;
					else
						caseLetter = IceTag.cAccusative;
					break;
				case 18:
					if( mClass == MorphoClass.NounMasculine1 )
						caseLetter = IceTag.cNominative;
					else
						caseLetter = IceTag.cDative;
					break;
				case 19:
					if( mClass == MorphoClass.NounMasculine1 )
						caseLetter = IceTag.cAccusative;
					else
						caseLetter = IceTag.cGenitive;
					break;
		}
        return caseLetter;
    }

    /**
	 * Returns true if the i-th search string has an article
	 */
    public boolean withArticle(int index) {

        return ((index >= 4) && (index <= 7)) || ((index >= 12) && (index <= 19));
    }


    /**
	 * Generates the search string given root+suffix and article
	 */

    private String getSearchStringArticle(String rootAndSuffix, String article) {

       String result=rootAndSuffix+article;

       // Might need to remove letters from the rootAndSuffix before adding the article
       // E.g. the article might look like "num/1", denoting that 1 character needs to be
       // removed from the wordform before adding the article "num".
       if (article.matches(".*/.*")) {
           String[] components = article.split("/");
           if (components.length == 2) {
             article = components[0];
             String numberStr = components[1];
             int number = Integer.parseInt(numberStr);
             int rootSuffixLen = rootAndSuffix.length();
             if (number <= rootSuffixLen)
                result = rootAndSuffix.substring(0,rootSuffixLen-number) + article;
           }
       }
       return result;
    }


    /**
	 * Generates all search strings for the given root belonging to the given morphological noun class.
     * Uses the supplies suffixes and articles.
	 */
    private void generateSearchStringsNoun(MorphoClass type, String root, String[] suffixes, String[] articles)
    {
        for (int i=0; i<=3; i++) { // singular: root + suffix
           searchStrings[i] = root + suffixes[i];
        }
        for (int i=4; i<=7; i++)  // singular: root + suffix + article
           //searchStrings[i] = searchStrings[i-4] + articles[i-4];
           searchStrings[i] = getSearchStringArticle(searchStrings[i-4], articles[i-4]);

        if (suffixes.length > 4) { // Then plural as well
            if (type == MorphoClass.NounMasculine10) // atvinnurekand-
                root = root.substring( 0, root.length() - 3 ) + "end"; // atvinnurekend

            for (int i=8; i<=11; i++)  // plural: root + suffix
                searchStrings[i] = root + suffixes[i-4];

            for (int i=12; i<=15; i++)  // plural: root + suffix + article
                searchStrings[i] = getSearchStringArticle(searchStrings[i-4], articles[i-8]);

            // Special cases
            if (type == MorphoClass.NounMasculine1) {
                searchStrings[16] = root + "ir";    // gestir
                searchStrings[17] = root + "i";
                searchStrings[18] = searchStrings[16] + "nir";
                searchStrings[19] = searchStrings[17] + "na";
            }
            else if (type == MorphoClass.NounFeminine1) {
                searchStrings[16] = root + "na";    // til súpna, special genitive, plural
                searchStrings[17] = searchStrings[16] + "nna";    // til súpna, special genitive, plural
            }

        }
    }

    /**
	 * Generates all search strings for the given root belonging to the given morphological adjective class.
     * Uses the supplies suffixes for singular and plural as well as weak declension.
	 */
    private void generateSearchStringsAdj(MorphoClass type, String root, String[] suffixes, String[] suffixesPlural, String[] suffixesWeak)
    {
        for (int i=0; i<=11; i++) { // singular: root + suffix

           if (i==0 && type == MorphoClass.Adj1) {
                if( root.endsWith( "á" ) )    // hár, grár, blár,
					searchStrings[i] = root + "r";
                else
                    searchStrings[i] = root + suffixes[i];
           }
           else
               searchStrings[i] = root + suffixes[i];
        }

        for (int i=12; i<=23; i++)  // plural: root + suffix
           searchStrings[i] = root + suffixesPlural[i-12];

        if (suffixesWeak != null) {
            for (int i=24; i<=searchStringLastIndex; i++)  // weak declension root + suffix
                searchStrings[i] = root + suffixesWeak[i-24];
        }
    }

    /**
	 * Generates all search strings for the given verb root.
     * Uses the supplies suffixes.
	 */
    private void generateSearchStringsVerb(String root, String[] suffixes)
    {
        for (int i=0; i<suffixes.length; i++)  // root + suffix
            searchStrings[i] = root + suffixes[i];
    }

    /**
	 * The main method called by IceMorphy to initialise the search strings.
     * Each search string is potentially a wordform related to the given root belonging to the given morphological class.
	 */
    public void setSearchStrings( String root, MorphoClass type )
	{
        for (int i=0; i<=searchStringLastIndex; i++)
                searchStrings[i]=null;

        switch( type )
		{
            /********* NOUNS *********/
		     case NounMasculine1:
                generateSearchStringsNoun(type, root, suffixesNounMasculine1, articlesMasculine);
				break;
			case NounMasculine2:
                generateSearchStringsNoun(type, root, suffixesNounMasculine2, articlesMasculine2);
				break;
			case NounMasculine3:
                generateSearchStringsNoun(type, root, suffixesNounMasculine3, articlesMasculine);
                break;
			case NounMasculine4:
                generateSearchStringsNoun(type, root, suffixesNounMasculine4, articlesMasculine);
                break;
        	case NounMasculine5:
                generateSearchStringsNoun(type, root, suffixesNounMasculine5, articlesMasculine);
                break;
			case NounMasculine6:
                generateSearchStringsNoun(type, root, suffixesNounMasculine6, articlesMasculine);
                break;
			case NounMasculine7:
                generateSearchStringsNoun(type, root, suffixesNounMasculine7, articlesMasculine7);
                break;
            case NounMasculine8:
                generateSearchStringsNoun(type, root, suffixesNounMasculine8, articlesMasculine);
                break;
    		case NounMasculine9:
                generateSearchStringsNoun(type, root, suffixesNounMasculine9, articlesMasculine);
                break;
		    case NounMasculine10:
                generateSearchStringsNoun(type, root, suffixesNounMasculine10, articlesMasculine10);
                break;

			case NounFeminine1:
                generateSearchStringsNoun(type, root, suffixesNounFeminine1, articlesFeminine1);
                break;
			case NounFeminine2:
                generateSearchStringsNoun(type, root, suffixesNounFeminine2, articlesFeminine2);
                break;
			case NounFeminine3:
                generateSearchStringsNoun(type, root, suffixesNounFeminine3, articlesFeminine3);
                break;
    		case NounFeminine4:
                generateSearchStringsNoun(type, root, suffixesNounFeminine4, articlesFeminine4);
                break;
			case NounFeminine5:
                generateSearchStringsNoun(type, root, suffixesNounFeminine5, articlesFeminine5);
                break;
            case NounFeminine6:
                generateSearchStringsNoun(type, root, suffixesNounFeminine6, articlesFeminine6);
                break;

			case NounNeuter1:
                generateSearchStringsNoun(type, root, suffixesNounNeuter1, articlesNeuter1);
                break;
			case NounNeuter2:
                generateSearchStringsNoun(type, root, suffixesNounNeuter2, articlesNeuter2);
                break;
			case NounNeuter3:
                generateSearchStringsNoun(type, root, suffixesNounNeuter3, articlesNeuter3);
                break;
			case NounNeuter4:
                generateSearchStringsNoun(type, root, suffixesNounNeuter4, articlesNeuter4);
                break;
            
            /********* ADJECTIVES *********/

			case Adj1:
                generateSearchStringsAdj(type, root, suffixesAdj1, suffixesAdj1Plural, suffixesAdjWeak);
                break;
			case Adj2:
                generateSearchStringsAdj(type, root, suffixesAdj2, suffixesAdj2Plural, null);
                break;
			case Adj4:
                generateSearchStringsAdj(type, root, suffixesAdj4, suffixesAdj4Plural, null);
                break;
			case Adj5:
                generateSearchStringsAdj(type, root, suffixesAdj5, suffixesAdj1Plural, null);
                break;

            /********* VERBS *********/


			case VerbActive1:
                generateSearchStringsVerb(root, suffixesVerbActive1);
                break;
			case VerbActive2:
                generateSearchStringsVerb(root, suffixesVerbActive2);
                break;
			case VerbActive3:
                generateSearchStringsVerb(root, suffixesVerbActive3);
                break;
			case VerbActive4:
                generateSearchStringsVerb(root, suffixesVerbActive4);
                break;
			case VerbActive5:
                generateSearchStringsVerb(root, suffixesVerbActive5);
                break;
            case VerbActive6:
                generateSearchStringsVerb(root, suffixesVerbActive6);
                break;
			case VerbMiddle1:
                generateSearchStringsVerb(root, suffixesVerbMiddle1);
                break;
			case VerbMiddle2:
                generateSearchStringsVerb(root, suffixesVerbMiddle2);
            //case VerbMiddle3:
            //    generateSearchStringsVerb(root, suffixesVerbMiddle3);
            break;
        }
	}

    public void createNounArticleRules() {
    // Lets explain how IceMorpy uses these rules by using the first rule here as an example:
    // MorphoRuleNounAdjective("urinn", 5, MorphoClass.NounMasculine1, true, false, false, false, IceTag.cMasculine, IceTag.cSingular));
    //
    // If an unknown lexeme matches the regular expression "urinn", then IceMorphy guesses the stem
    // by removing this 5 character suffix from the word.
    // IceMorphy then generates all possible word forms based on the given morphological class (NounMasculine1).
    // If a word form is found in the dictionary, then it is assumed that the unknown lexeme belongs to the same class.
    // The tag profile is then created by using the additional information in the rule, i.e. info about the cases, gender, and number.
    // For our given example, nominative, masucline, singular is given and the final tag created will thus be "nkeng"
    //    
            
            listNounArticle = new ArrayList<MorphoRuleNounAdjective>();
            // hest-urinn
            listNounArticle.add(new MorphoRuleNounAdjective("urinn", 5, MorphoClass.NounMasculine1, true, false, false, false, IceTag.cMasculine, IceTag.cSingular));
            // gítar-inn
            listNounArticle.add(new MorphoRuleNounAdjective("arinn", 3, MorphoClass.NounMasculine8,  true, true, false, false, IceTag.cMasculine, IceTag.cSingular));
            // kennar-inn
            listNounArticle.add(new MorphoRuleNounAdjective("arinn", 3, MorphoClass.NounMasculine2,  true, false, false, false, IceTag.cMasculine, IceTag.cSingular));

            // hest-arnir
            listNounArticle.add(new MorphoRuleNounAdjective("arnir", 5, MorphoClass.NounMasculine1,  true, false, false, false, IceTag.cMasculine, IceTag.cPlural ));
            // gest-irnir
            listNounArticle.add(new MorphoRuleNounAdjective("irnir", 5, MorphoClass.NounMasculine1,  true, false, false, false, IceTag.cMasculine, IceTag.cPlural ));

            // hest-inn
            listNounArticle.add(new MorphoRuleNounAdjective("inn", 3, MorphoClass.NounMasculine1,  false, true, false, false, IceTag.cMasculine, IceTag.cSingular ));
			// skól-inn
            listNounArticle.add(new MorphoRuleNounAdjective("inn", 3, MorphoClass.NounMasculine2,  true, false, false, false, IceTag.cMasculine, IceTag.cSingular ));
            // skól-ann
            listNounArticle.add(new MorphoRuleNounAdjective("ann", 3 , MorphoClass.NounMasculine2,  false, true, false, false, IceTag.cMasculine, IceTag.cSingular ));

            // bekk-junum
            listNounArticle.add(new MorphoRuleNounAdjective("junum", 5, MorphoClass.NounMasculine9,  false, false, true, false, IceTag.cMasculine, IceTag.cPlural ));
            // dekk-junum
            listNounArticle.add(new MorphoRuleNounAdjective("junum", 5, MorphoClass.NounFeminine1,  false, false, true, false, IceTag.cFeminine, IceTag.cPlural));
			// hest-unum
            listNounArticle.add(new MorphoRuleNounAdjective("unum", 4, MorphoClass.NounMasculine1,  false, false, true, false, IceTag.cMasculine, IceTag.cPlural ));
            // von-unum
            listNounArticle.add(new MorphoRuleNounAdjective("unum", 4, MorphoClass.NounFeminine2,  false, false, true, false, IceTag.cFeminine, IceTag.cPlural));
            // játning-unum
            listNounArticle.add(new MorphoRuleNounAdjective("unum", 4, MorphoClass.NounFeminine3,  false, false, true, false, IceTag.cFeminine, IceTag.cPlural));
            // svín-unum
            listNounArticle.add(new MorphoRuleNounAdjective("unum", 4, MorphoClass.NounNeuter1,  false, false, true, false, IceTag.cNeuter, IceTag.cPlural));
            // nýr-unum
            listNounArticle.add(new MorphoRuleNounAdjective("unum", 4, MorphoClass.NounNeuter4,  false, false, true, false, IceTag.cNeuter, IceTag.cPlural));

            // hest-inum
            listNounArticle.add(new MorphoRuleNounAdjective("inum", 4, MorphoClass.NounMasculine1,  false, false, true, false, IceTag.cMasculine, IceTag.cSingular));
            // skól-anum
            listNounArticle.add(new MorphoRuleNounAdjective("anum", 4, MorphoClass.NounMasculine2,  false, false, true, false, IceTag.cMasculine, IceTag.cSingular));
            // bíl-num, handlegg-num
            listNounArticle.add(new MorphoRuleNounAdjective("num", 3, MorphoClass.NounMasculine1,  false, false, true, false, IceTag.cMasculine, IceTag.cSingular));

            // hest-sins
            listNounArticle.add(new MorphoRuleNounAdjective("sins", 4, MorphoClass.NounMasculine1,  false, false, false, true, IceTag.cMasculine, IceTag.cSingular));
            // svín-sins
            listNounArticle.add(new MorphoRuleNounAdjective("sins", 4, MorphoClass.NounNeuter1,  false, false, false, true, IceTag.cNeuter, IceTag.cSingular));
            // gerpi-sins
            listNounArticle.add(new MorphoRuleNounAdjective("sins", 5, MorphoClass.NounNeuter2,  false, false, false, true, IceTag.cNeuter, IceTag.cSingular));

            // skól-ans
            listNounArticle.add(new MorphoRuleNounAdjective("ans", 3, MorphoClass.NounMasculine2,  false, false, false, true, IceTag.cMasculine, IceTag.cSingular));

            // svip-una
            listNounArticle.add(new MorphoRuleNounAdjective("una", 3, MorphoClass.NounFeminine1,  false, true, false, false, IceTag.cFeminine, IceTag.cSingular));

		    // von-ina
            listNounArticle.add(new MorphoRuleNounAdjective("ina", 3, MorphoClass.NounFeminine2,  false, true, false, false, IceTag.cFeminine, IceTag.cSingular));
            // kæti-na
            listNounArticle.add(new MorphoRuleNounAdjective("ina", 2, MorphoClass.NounFeminine4,  false, true, false, false, IceTag.cFeminine, IceTag.cSingular ));
			// gest-ina
            listNounArticle.add(new MorphoRuleNounAdjective("ina", 3, MorphoClass.NounMasculine1,  false, true, false, false, IceTag.cMasculine, IceTag.cPlural ));

            // súp-nanna
            listNounArticle.add(new MorphoRuleNounAdjective("nanna", 5, MorphoClass.NounFeminine1,  false, false, false, true, IceTag.cFeminine, IceTag.cPlural ));

            // hest-anna
            listNounArticle.add(new MorphoRuleNounAdjective("anna", 4, MorphoClass.NounMasculine1,  false, false, false, true, IceTag.cMasculine, IceTag.cPlural ));
            // súp-anna
            listNounArticle.add(new MorphoRuleNounAdjective("anna", 4, MorphoClass.NounFeminine1,  false, false, false, true, IceTag.cFeminine, IceTag.cPlural ));
            // svín-anna
            listNounArticle.add(new MorphoRuleNounAdjective("anna", 4, MorphoClass.NounNeuter1,  false, false, false, true, IceTag.cNeuter, IceTag.cPlural ));
            // skól-anna
            listNounArticle.add(new MorphoRuleNounAdjective("anna", 4, MorphoClass.NounMasculine2,  false, false, false, true, IceTag.cMasculine, IceTag.cPlural ));
            // von-anna
            listNounArticle.add(new MorphoRuleNounAdjective("anna", 4, MorphoClass.NounFeminine2,  false, false, false, true, IceTag.cFeminine, IceTag.cPlural ));
            // efn-anna
            listNounArticle.add(new MorphoRuleNounAdjective("anna", 4, MorphoClass.NounNeuter2,  false, false, false, true, IceTag.cNeuter, IceTag.cPlural ));

            // von-arinnar
            listNounArticle.add(new MorphoRuleNounAdjective("innar", 7, MorphoClass.NounFeminine2,  false, false, false, true, IceTag.cFeminine, IceTag.cSingular ));
            // fræði-nnar
            listNounArticle.add(new MorphoRuleNounAdjective("innar", 4, MorphoClass.NounFeminine4,  false, false, false, true, IceTag.cFeminine, IceTag.cSingular ));

            // súp-unnar
            listNounArticle.add(new MorphoRuleNounAdjective("unnar", 5, MorphoClass.NounFeminine1,  false, false, false, true, IceTag.cFeminine, IceTag.cSingular ));

            // súp-urnar
            listNounArticle.add(new MorphoRuleNounAdjective("urnar", 5, MorphoClass.NounFeminine1,  true, true, false, false, IceTag.cFeminine, IceTag.cPlural ));
            // von-irnar
            listNounArticle.add(new MorphoRuleNounAdjective("irnar", 5, MorphoClass.NounFeminine2,  true, true, false, false, IceTag.cFeminine, IceTag.cPlural ));
            listNounArticle.add(new MorphoRuleNounAdjective("arnar", 5, MorphoClass.NounFeminine2,  true, true, false, false, IceTag.cFeminine, IceTag.cPlural ));

            // súp-unni
            listNounArticle.add(new MorphoRuleNounAdjective("unni", 4, MorphoClass.NounFeminine1,  false, false, true, false, IceTag.cFeminine, IceTag.cSingular ));
            // von-inni
            listNounArticle.add(new MorphoRuleNounAdjective("inni", 4, MorphoClass.NounFeminine2,  false, false, true, false, IceTag.cFeminine, IceTag.cSingular ));
            // kæti-nni
            listNounArticle.add(new MorphoRuleNounAdjective("inni", 3, MorphoClass.NounFeminine4,  false, false, true, false, IceTag.cFeminine, IceTag.cSingular ));

            // hest-ana
            listNounArticle.add(new MorphoRuleNounAdjective("ana", 3, MorphoClass.NounMasculine1,  false, true, false, false, IceTag.cMasculine, IceTag.cPlural ));

            // svín-inu
            listNounArticle.add(new MorphoRuleNounAdjective("inu", 3, MorphoClass.NounNeuter1,  false, false, true, false, IceTag.cNeuter, IceTag.cSingular ));
            // vesk-inu
            listNounArticle.add(new MorphoRuleNounAdjective("inu", 3, MorphoClass.NounNeuter2,  false, false, true, false, IceTag.cNeuter, IceTag.cSingular ));

            // von-in
            listNounArticle.add(new MorphoRuleNounAdjective("in", 2, MorphoClass.NounFeminine2,  true, false, false, false, IceTag.cFeminine, IceTag.cSingular ));
            // meining-in
            listNounArticle.add(new MorphoRuleNounAdjective("in", 2, MorphoClass.NounFeminine3,  true, false, false, false, IceTag.cFeminine, IceTag.cSingular ));
            // svín-in
            listNounArticle.add(new MorphoRuleNounAdjective("in", 2, MorphoClass.NounNeuter1,  true, true, false, false, IceTag.cNeuter, IceTag.cPlural ));
            // vesk-in
            listNounArticle.add(new MorphoRuleNounAdjective("in", 2, MorphoClass.NounNeuter2,  true, true, false, false, IceTag.cNeuter, IceTag.cPlural ));
            // kæti-n
            listNounArticle.add(new MorphoRuleNounAdjective("in", 1, MorphoClass.NounFeminine4,  true, false, false, false, IceTag.cFeminine, IceTag.cSingular ));

            // súp-an
            listNounArticle.add(new MorphoRuleNounAdjective("an", 2, MorphoClass.NounFeminine1,  true, false, false, false, IceTag.cFeminine, IceTag.cSingular ));

            // svín-ið
            listNounArticle.add(new MorphoRuleNounAdjective("ið", 2, MorphoClass.NounNeuter1,  true, true, false, false, IceTag.cNeuter, IceTag.cSingular ));
            // vesk-ið
            listNounArticle.add(new MorphoRuleNounAdjective("ið", 2, MorphoClass.NounNeuter2,  true, true, false, false, IceTag.cNeuter, IceTag.cSingular ));
    }
    
    public void createNounAdjectiveSingularRules() {
            
            listNounAdjectiveSingular = new ArrayList<MorphoRuleNounAdjective>();
            // krist-inn
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("inn", 3, MorphoClass.Adj2, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cMasculine, IceTag.cSingular,  IceTag.cStrong ));
            // krist-in
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("in", 2, MorphoClass.Adj2, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong ));
            // krist-ins
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ins", 3, MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));
            // krist-ið
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ið", 2, MorphoClass.Adj2, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cStrong ));
            // frið, smið,
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ið", 0, MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // hlið
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ið", 0, MorphoClass.NounNeuter1,  IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // krist-na
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("na", 2, MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cStrong ));
            // krist-inni
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("inni", 4, MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong ));
            // krist-innar
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("innar", 5, MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong ));

            // krist-num
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("num", 3, MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));

            // krist-nu
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("nu", 2, MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cStrong ));
            // krist-nu weak
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("nu", 2, MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, true, true, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cWeak ));

            // hor-aðan
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("aðan", 4, MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));
            // þreytt-an
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("an", 2, MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));
            // heil-an
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("an", 2, MorphoClass.Adj5, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));

            // hor-aður
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("aður", 4, MorphoClass.Adj4, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));
            // iðnað-ur
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("aður", 2, MorphoClass.NounMasculine5, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));

            // hor-uðum
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("uðum", 4, MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));

            // hor-aðrar
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("aðrar", 5, MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong ));

            // hor-aðri
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("aðri", 4, MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong ));

            // hor-aðs
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("aðs", 3, MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));

            // hor-aða weak and strong
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("aða", 3, MorphoClass.Adj4, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cWeak, true ));
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("aða", 3, MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, true, true, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cWeak, true ));
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("aða", 3, MorphoClass.Adj4, IceTag.WordClass.wcAdj, true, true, true, true, IceTag.cNeuter, IceTag.cSingular, IceTag.cWeak, true ));
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("aða", 3, MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong, false ));

            // hor-uðu weak feminine and strong neuter
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("uðu", 3, MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, true, true, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cWeak, true ));
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("uðu", 3, MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cStrong, false ));


            // hor-að
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("að", 2, MorphoClass.Adj4, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cStrong ));


            // hreyfingarlaus-t
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("aust", 1, MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cStrong ));

            // hreyfingarlaus
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("aus", 0, MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));

            // atvinnurekand-i
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("andi", 1, MorphoClass.NounMasculine10, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // atvinnurekand-a
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("anda", 1, MorphoClass.NounMasculine10, IceTag.WordClass.wcAdj, false, true, true, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));

            // hest-ur
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ur", 2, MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // gald-ur
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ur", 2, MorphoClass.NounMasculine3, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // hreið-ur
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ur", 2, MorphoClass.NounNeuter3, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // falleg-ur
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ur", 2, MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));

            // falleg-um
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("um", 2, MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));
            // heil-um
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("um", 2, MorphoClass.Adj5, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));

            // falleg-a weak and strong
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ega", 1, MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cWeak, true ));
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ega", 1, MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, true, true, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cWeak, true ));
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ega", 1, MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, true, true, true, IceTag.cNeuter, IceTag.cSingular, IceTag.cWeak, true ));
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ega", 1, MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong, false ));

            // falleg-t
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("egt", 1, MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cStrong ));
            // falleg-u
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("egu", 1, MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cStrong, true ));
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("egu", 1, MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, true, true, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cWeak, false ));

            // falleg
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("eg", 0, MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong ));

            // þreytt-rar
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("rar", 3, MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong ));
            // lif-rar
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("rar", 3, MorphoClass.NounFeminine6, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));

            // meining-ar
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("gar", 2, MorphoClass.NounFeminine3, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));

            // meining-u
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ingu", 1, MorphoClass.NounFeminine3, IceTag.WordClass.wcNoun, false, true, true, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));

            // meining
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ing", 0, MorphoClass.NounFeminine3, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));

            // hernað-ar
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ðar", 2, MorphoClass.NounMasculine5, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // hlið-ar
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ðar", 2, MorphoClass.NounFeminine2, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));

            // vitj-unar
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("unar", 4, MorphoClass.NounFeminine5, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));

            // kennar-i
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ari", 1, MorphoClass.NounMasculine2, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // kennar-a
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ara", 1, MorphoClass.NounMasculine2, IceTag.WordClass.wcNoun, false, true, true, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));

            // bekk-jar
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("kkjar", 3, MorphoClass.NounMasculine9, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));

            // fund-ar
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ndar", 2, MorphoClass.NounMasculine5, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));

            // von-ar
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ar", 2, MorphoClass.NounFeminine2, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));
            // gítar
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ar", 0, MorphoClass.NounMasculine8, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));

            // vitj-un
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("un", 2, MorphoClass.NounFeminine5, IceTag.WordClass.wcNoun, true, true, true, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));

            // hreið-urs
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("urs", 3, MorphoClass.NounNeuter3, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // gald-urs
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("urs", 3, MorphoClass.NounMasculine3, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));

            // jöfn-uð
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("uð", 0, MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // hor-uð
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("uð", 2, MorphoClass.Adj4, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong ));


            // merk-is
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("is", 2, MorphoClass.NounNeuter2, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // lífeyr-is
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("is", 2, MorphoClass.NounMasculine7, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));

            // hest-s
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("s", 1, MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // gítar-s
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("s", 1, MorphoClass.NounMasculine8, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // svín-s
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("s", 1, MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // sérstak-s
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("s", 1, MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));


            // skól-a
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("a", 1, MorphoClass.NounMasculine2, IceTag.WordClass.wcNoun, false, true, true, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // kon-a
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("a", 1, MorphoClass.NounFeminine1, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));
            // þreytt-a
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("a", 1, MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong));
            //listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("a", 1, MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, true, true, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cWeak, true ));
            //listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("a", 1, MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, true, true, true, IceTag.cNeuter, IceTag.cSingular, IceTag.cWeak, false ));

            // heil-a
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("a", 1, MorphoClass.Adj5, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong));
            //listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("a", 1, MorphoClass.Adj5, IceTag.WordClass.wcAdj, false, true, true, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cWeak, true ));
            //listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("a", 1, MorphoClass.Adj5, IceTag.WordClass.wcAdj, false, true, true, true, IceTag.cNeuter, IceTag.cSingular, IceTag.cWeak, false ));

            // súp-u
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("u", 1, MorphoClass.NounFeminine1, IceTag.WordClass.wcNoun, false, true, true, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));
            // góð-u weak and strong
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("u", 1, MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, true, true, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cWeak));
            //listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("u", 1, MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cStrong, false ));
      
            // hreið-ri
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ri", 2, MorphoClass.NounNeuter3, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // gald-ri
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ri", 2, MorphoClass.NounMasculine3, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // þreytt-ri
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ri", 2, MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong ));

            // hest-i
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("i", 1, MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // skól-i
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("i", 1, MorphoClass.NounMasculine2, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // svín-i
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("i", 1, MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // vesk-i
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("i", 1, MorphoClass.NounNeuter2, IceTag.WordClass.wcNoun, true, true, true, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // fund-i
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("i", 1, MorphoClass.NounMasculine5, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // hell-i
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("i", 1, MorphoClass.NounMasculine7, IceTag.WordClass.wcNoun, false, true, true, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // feiti
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("i", 0, MorphoClass.NounFeminine4, IceTag.WordClass.wcNoun, true, true, true, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));
            // þreytt-i weak
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("i", 1, MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cWeak ));

            // heil-l
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ll", 1, MorphoClass.Adj5, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));
            // stól-l
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ll", 1, MorphoClass.NounMasculine6, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // fall
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ll", 0, MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));

            // fíkn,
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("kn", 0, MorphoClass.NounFeminine2, IceTag.WordClass.wcNoun, true, true, true, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));
            // tákn
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("kn", 0, MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));

            // gang
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ng", 0, MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // lyng
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ng", 0, MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun,  true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));

            // gólf, hólf, golf
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("lf", 0, MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));

            // stól, kjól
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("l", 0, MorphoClass.NounMasculine6, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // fól, gól
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("l", 0, MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // heil
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("l", 0, MorphoClass.Adj5, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong ));

            // pott
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("tt", 0, MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // gátt
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("tt", 0, MorphoClass.NounFeminine2, IceTag.WordClass.wcNoun, true, true, true, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));
            // gott
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("tt", 0, MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // þreytt
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("tt", 0, MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cStrong ));

            // slakt, frekt,
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("kt", 1, MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cStrong ));
            // svart, bert,
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("rt", 1, MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cStrong ));

            // hest
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("t", 0, MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // kúnst
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("t", 0, MorphoClass.NounFeminine2, IceTag.WordClass.wcNoun, true, true, true, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));

            // stubb
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("bb", 0, MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // gubb
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("bb", 0, MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));

            // kopp
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("pp", 0, MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));

            // grikk
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ikk", 0, MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, true, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // hrekk
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ekk", 0, MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, true, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // flokk
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("kk", 0, MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));

            // keim, Ásgrím
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("m", 0, MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension));

            // tap
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("p", 0, MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // skáp
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("p", 0, MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));

            // odd
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("d", 0, MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));

            // mynd
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("d", 0, MorphoClass.NounFeminine2, IceTag.WordClass.wcNoun, true, true, true, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));
            // umdeild
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("d", 0, MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong ));

            // bak, lak
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("k", 0, MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // bók, blók
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("k", 0, MorphoClass.NounFeminine2, IceTag.WordClass.wcNoun, true, true, true, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));
            // hrauk
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("k", 0, MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));


            // barð
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ð", 0, MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // arð
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ð", 0, MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // hliðstæð
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ð", 0, MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong ));

            // bar, skúr, skór
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("r", 0, MorphoClass.NounMasculine4, IceTag.WordClass.wcAdj, true, true, true, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // hár, tár
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("r", 0, MorphoClass.NounNeuter1, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));

    }

    public void createNounAdjectivePluralRules() {

            listNounAdjectivePlural = new ArrayList<MorphoRuleNounAdjective>();
            // krist-inna
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("inna", 4, MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("inna", 4, MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cFeminine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("inna", 4, MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cNeuter, IceTag.cPlural, IceTag.cStrong, false ));

            // krist-nar
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("nar", 3, MorphoClass.Adj2, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cStrong ));
            // krist-in
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("in", 2, MorphoClass.Adj2, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cNeuter, IceTag.cPlural, IceTag.cStrong ));

            // hor-uðum
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("uðum", 4, MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("uðum", 4, MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("uðum", 4, MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cNeuter, IceTag.cPlural, IceTag.cStrong, false ));

            // hor-aða
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("aða", 3, MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong ));

            // eng-jum, heng-jum
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("gjum", 3, MorphoClass.NounNeuter2, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cNeuter, IceTag.cPlural, IceTag.cNoDeclension ));
            // bekk-jum
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("kkjum", 3, MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));
            // dekk-jum
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("kkjum", 3, MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cNeuter, IceTag.cPlural, IceTag.cNoDeclension ));

            // heil-um
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("lum", 3, MorphoClass.Adj5, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("lum", 3, MorphoClass.Adj5, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("lum", 3, MorphoClass.Adj5, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cNeuter, IceTag.cPlural, IceTag.cStrong, false ));

            // vitj-unum
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("unum", 4, MorphoClass.NounFeminine5, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));
        
            // kristn-um
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("num", 3, MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("num", 3, MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("num", 3, MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cNeuter, IceTag.cPlural, IceTag.cStrong, false ));

            // falleg-um
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("um", 2, MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("um", 2, MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("um", 2, MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cNeuter, IceTag.cPlural, IceTag.cStrong, false ));

            // súp-um
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("um", 2, MorphoClass.NounFeminine1, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));
            // von-um
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("um", 2, MorphoClass.NounFeminine2, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));
            // meining-um
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("um", 2, MorphoClass.NounFeminine3, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));
            // hest-um
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("um", 2, MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));
            // skól-um
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("um", 2, MorphoClass.NounMasculine2, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));
            // lán-um
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("um", 2, MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cNeuter, IceTag.cPlural, IceTag.cNoDeclension ));
            // gerp-um
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("um", 2, MorphoClass.NounNeuter2, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cNeuter, IceTag.cPlural, IceTag.cNoDeclension ));
            // staur-um
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("um", 2, MorphoClass.NounMasculine4, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));

            // há-rra
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("rra", 3, MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cFeminine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("rra", 3, MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("rra", 3, MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cNeuter, IceTag.cPlural, IceTag.cStrong, false ));

            // þreytt-ra
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ra", 2, MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cFeminine, IceTag.cPlural, IceTag.cStrong, true));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ra", 2, MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ra", 2, MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cNeuter, IceTag.cPlural, IceTag.cStrong, false ));

            // gald-ra
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ra", 2, MorphoClass.NounMasculine3, IceTag.WordClass.wcNoun, false, true, false, true, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));

            // gald-rar
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("rar", 3, MorphoClass.NounMasculine3, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));

            // þreytt-ar
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ar", 2, MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cStrong ));
            // heil-ar
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ar", 2, MorphoClass.Adj5, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cStrong ));
            // meining-ar
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ar", 2, MorphoClass.NounFeminine3, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));
            //  hest-ar
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ar", 2, MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));
            // skól-ar
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ar", 2, MorphoClass.NounMasculine2, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));
            // maur-ar
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ar", 2, MorphoClass.NounMasculine4, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));

            // hug-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ga", 1, MorphoClass.NounMasculine5, IceTag.WordClass.wcNoun, false, true, false, true, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));
            // meining-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ga", 1, MorphoClass.NounFeminine3, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));
            // falleg-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ga", 1, MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong ));

            // verk-ja
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ja", 2, MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));
            // merk-ja
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ja", 2, MorphoClass.NounNeuter2, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cNeuter, IceTag.cPlural, IceTag.cNoDeclension ));

            // búð-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ða", 1, MorphoClass.NounFeminine2, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));
            // minnismið-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ða", 1, MorphoClass.NounNeuter2, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cNeuter, IceTag.cPlural, IceTag.cNoDeclension ));
            // markað-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ða", 1, MorphoClass.NounMasculine5, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));

            // súp-na
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("[^a]na", 2, MorphoClass.NounFeminine1, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));
            // krist-na
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("na", 2, MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong ));

            // hest-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("a", 1, MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, true, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));
            // skól-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("a", 1, MorphoClass.NounMasculine2, IceTag.WordClass.wcNoun, false, true, false, true, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));
            // svín-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("a", 1, MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cNeuter, IceTag.cPlural, IceTag.cNoDeclension ));
            // kvikind-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("a", 1, MorphoClass.NounNeuter2, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cNeuter, IceTag.cPlural, IceTag.cNoDeclension ));
            // súp-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("a", 1, MorphoClass.NounFeminine1, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));
            // von-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("a", 1, MorphoClass.NounFeminine2, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));
            // þreytt-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("a", 1, MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong ));

            // áheyrend-ur
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ndur", 2, MorphoClass.NounMasculine2, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));

            // súp-ur
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ur", 2, MorphoClass.NounFeminine1, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));

            // vitj-anir
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("anir", 4, MorphoClass.NounFeminine5, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));

            // krist-nir
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("nir", 3, MorphoClass.Adj2, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong ));

            // von-ir
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ir", 2, MorphoClass.NounFeminine2, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));
            // tug-ir
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ir", 2, MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));
            // þreytt-ir
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ir", 2, MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong ));
            // heil-ir
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ir", 2, MorphoClass.Adj5, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong ));

            // gólf
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("lf", 0, MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cNeuter, IceTag.cPlural, IceTag.cNoDeclension ));

    }

    public void createVerbFiniteRules() {

            listVerbFinite = new ArrayList<MorphoRuleVerbFinite>();

            // þeir/ég borða
            listVerbFinite.add(new MorphoRuleVerbFinite("a", 1, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cPlural, IceTag.cFirstPerson, IceTag.cSingular ));

            // borð-ar
            listVerbFinite.add(new MorphoRuleVerbFinite("[^a]ðar", 2, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular));
            // skrif-ar
            listVerbFinite.add(new MorphoRuleVerbFinite("ar", 2, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular));

            // mei-ddum
            listVerbFinite.add(new MorphoRuleVerbFinite("ddum", 4, MorphoClass.VerbActive4, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cFirstPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));
            // grei-dduð
            listVerbFinite.add(new MorphoRuleVerbFinite("dduð", 4, MorphoClass.VerbActive4, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));

            // breyt-tum
            listVerbFinite.add(new MorphoRuleVerbFinite("ttum", 3, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cFirstPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural ));
            // breyt-tuð
            listVerbFinite.add(new MorphoRuleVerbFinite("ttuð", 3, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));

            // borð-uðum
            listVerbFinite.add(new MorphoRuleVerbFinite("uðum", 4, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cFirstPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cSingular ));

            // borð-uðuð
            listVerbFinite.add(new MorphoRuleVerbFinite("uðuð", 4, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cSingular ));

            // borð-uðu
            listVerbFinite.add(new MorphoRuleVerbFinite("uðu", 3, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cSingular ));

            // borð-aðu
            listVerbFinite.add(new MorphoRuleVerbFinite("aðu", 3, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Imperative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular));

            // borð-aðir
            listVerbFinite.add(new MorphoRuleVerbFinite("aðir", 4, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular));

            // borð-aði
            listVerbFinite.add(new MorphoRuleVerbFinite("aði", 3, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular));

            // reyn-dum
            listVerbFinite.add(new MorphoRuleVerbFinite("dum", 3, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cFirstPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));
            // þyng-dum
            listVerbFinite.add(new MorphoRuleVerbFinite("dum", 3, MorphoClass.VerbActive5, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cFirstPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));

            // reyn-duð
            listVerbFinite.add(new MorphoRuleVerbFinite("duð", 3, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));
            // þyng-duð
            listVerbFinite.add(new MorphoRuleVerbFinite("duð", 3, MorphoClass.VerbActive5, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));

            // fyll-tuð
            listVerbFinite.add(new MorphoRuleVerbFinite("tuð", 3, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));
            listVerbFinite.add(new MorphoRuleVerbFinite("tuð", 3, MorphoClass.VerbActive5, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));

            // breyt-um
            listVerbFinite.add(new MorphoRuleVerbFinite("tum", 2, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cFirstPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural ));
            // hir-tum
            listVerbFinite.add(new MorphoRuleVerbFinite("tum", 3, MorphoClass.VerbActive6, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cFirstPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural ));

            // borð-um
            listVerbFinite.add(new MorphoRuleVerbFinite("ðum", 3, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular ));

            // greið-um
            listVerbFinite.add(new MorphoRuleVerbFinite("ðum", 3, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular ));

            // hætt-ir
             listVerbFinite.add(new MorphoRuleVerbFinite("ttir", 2, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular));
            // breyt-tir
             listVerbFinite.add(new MorphoRuleVerbFinite("ttir", 3, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular ));

            // svar-arðu
            listVerbFinite.add(new MorphoRuleVerbFinite("arðu", 4, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular));
            // heyr-irðu
            listVerbFinite.add(new MorphoRuleVerbFinite("irðu", 4, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular));
            // met-urðu
            listVerbFinite.add(new MorphoRuleVerbFinite("urðu", 4, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular));

            // sen-dir
            listVerbFinite.add(new MorphoRuleVerbFinite("endir", 3, MorphoClass.VerbActive3, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular));

            // sen-tir
            listVerbFinite.add(new MorphoRuleVerbFinite("entir", 3, MorphoClass.VerbActive3, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular));

            // sen-di
            listVerbFinite.add(new MorphoRuleVerbFinite("endi", 2, MorphoClass.VerbActive3, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cFirstPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular));

            // sen-ti
            listVerbFinite.add(new MorphoRuleVerbFinite("enti", 2, MorphoClass.VerbActive3, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cFirstPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular));


            // herð-ir
            listVerbFinite.add(new MorphoRuleVerbFinite("ðir", 2, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular ));
            // horf-ðir
            listVerbFinite.add(new MorphoRuleVerbFinite("ðir", 3, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular));

            // áger-ðist
            listVerbFinite.add(new MorphoRuleVerbFinite("ðist", 4, MorphoClass.VerbMiddle1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Middle, IceTag.cPast, IceTag.cFirstPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular));
            // áger-ist
            listVerbFinite.add(new MorphoRuleVerbFinite("ist", 3, MorphoClass.VerbMiddle1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Middle, IceTag.cPresent, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular));
            // áger-ast
            listVerbFinite.add(new MorphoRuleVerbFinite("ast", 3, MorphoClass.VerbMiddle1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Middle, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cSingular));
            // -st
            listVerbFinite.add(new MorphoRuleVerbFinite("st", 2, MorphoClass.VerbMiddle2, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Middle, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular));

            // reyn-dir
            listVerbFinite.add(new MorphoRuleVerbFinite("dir", 3, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular ));
            // þyng-dir
            listVerbFinite.add(new MorphoRuleVerbFinite("dir", 3, MorphoClass.VerbActive5, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular));

            // breyt-tir
            listVerbFinite.add(new MorphoRuleVerbFinite("tir", 3, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular));

            // grei-ddu
            listVerbFinite.add(new MorphoRuleVerbFinite("ddu", 3, MorphoClass.VerbActive4, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));

            // breyt-tu
            listVerbFinite.add(new MorphoRuleVerbFinite("ttu", 2, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));

            // grei-ddi
            listVerbFinite.add(new MorphoRuleVerbFinite("ddi", 3, MorphoClass.VerbActive4, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular));

            // breyt-ti
            listVerbFinite.add(new MorphoRuleVerbFinite("tti", 2, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular));

            // reyn-du
            listVerbFinite.add(new MorphoRuleVerbFinite("du", 2, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));
            // fyll-tu
            listVerbFinite.add(new MorphoRuleVerbFinite("tu", 2, MorphoClass.VerbActive5, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));
            // lif-ðu
            listVerbFinite.add(new MorphoRuleVerbFinite("ðu", 2, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));

            // skrif-um
            listVerbFinite.add(new MorphoRuleVerbFinite("um", 2, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cFirstPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));

            // ætl-iði
            listVerbFinite.add(new MorphoRuleVerbFinite("iði", 3, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));
            // skrif-ið
            listVerbFinite.add(new MorphoRuleVerbFinite("ið", 2, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));
            // reyn-ið
            listVerbFinite.add(new MorphoRuleVerbFinite("ið", 2, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));

            // reyn-di
            listVerbFinite.add(new MorphoRuleVerbFinite("di", 2, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular));
            // þyng-di
            listVerbFinite.add(new MorphoRuleVerbFinite("di", 2, MorphoClass.VerbActive5, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular));
            // ígrund-i
            listVerbFinite.add(new MorphoRuleVerbFinite("di", 1, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Subjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular ));

            // byrs-ti
            listVerbFinite.add(new MorphoRuleVerbFinite("ti", 2, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular));
            // brey-ti
            listVerbFinite.add(new MorphoRuleVerbFinite("ti", 1, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cFirstPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular));
            // -ti
            listVerbFinite.add(new MorphoRuleVerbFinite("ti", 2, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular));

            // grei-ði
            listVerbFinite.add(new MorphoRuleVerbFinite("ði", 1, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cFirstPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular ));

            // reyn-ir
            listVerbFinite.add(new MorphoRuleVerbFinite("ir", 2, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular));
            // þyng-ir
            listVerbFinite.add(new MorphoRuleVerbFinite("ir", 2, MorphoClass.VerbActive5, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular));

            // legg, hegg
            listVerbFinite.add(new MorphoRuleVerbFinite("gg", 0, MorphoClass.VerbActive5, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cFirstPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular));

            // reyn-i
            listVerbFinite.add(new MorphoRuleVerbFinite("i", 1, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cFirstPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, true));
            listVerbFinite.add(new MorphoRuleVerbFinite("i", 1, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.Subjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular, false));
            // stimpl-i
            listVerbFinite.add(new MorphoRuleVerbFinite("i", 1, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Subjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular));

    }


    public void createVerbPastParticipleRules() {
            listVerbPastParticiple = new ArrayList<MorphoRuleVerbPastParticiple>();
            // gleym-dur
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("mdur", 3, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cMasculine, IceTag.cSingular, IceTag.cNominative, false));
            // gleym-dar
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("mdar", 3, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cFeminine, IceTag.cPlural, IceTag.cNominative, false));

            // fleng-dur
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("ngdur", 3, MorphoClass.VerbActive5, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cMasculine, IceTag.cSingular, IceTag.cNominative, false));
            // fleng-dar
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("ngdar", 3, MorphoClass.VerbActive5, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cFeminine, IceTag.cPlural, IceTag.cNominative, false ));

            // trygg-ður
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("ður", 3, MorphoClass.VerbActive5, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cMasculine, IceTag.cSingular, IceTag.cNominative, false));

            // borð-aður
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("aður", 4, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cMasculine, IceTag.cSingular, IceTag.cNominative, false));
            // borð-aðir
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("aðir", 4, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cMasculine, IceTag.cPlural, IceTag.cNominative, false));
            // borð-aðar
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("aðar", 4, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cFeminine, IceTag.cPlural, IceTag.cNominative, false));
            // borð-uð
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("uð", 2, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cFeminine, IceTag.cSingular, IceTag.cNominative, false, true));
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("uð", 2, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cNeuter, IceTag.cPlural, IceTag.cNominative, false, false));
            // borð-að
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("að", 2, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative, true ));

            // flett
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("tt", 0, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative, true));
            // kúr-t
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("rt", 1, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative, true));
            // skemm-t
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("mt", 1, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative, true));
            // kenn-t
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("nt", 1, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative, true));
            // gláp-t
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("pt", 1, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative, true));
            // vanræk-t
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("kt", 1, MorphoClass.VerbActive5, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative, true));
            // smeyg-t
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("gt", 1, MorphoClass.VerbActive5, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative, true));
            // kenn-d
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("nd", 1, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cFeminine, IceTag.cSingular, IceTag.cNominative, false ));

    }

    public void createVerbRules() {

        listVerb = new ArrayList<MorphoRuleVerb>();

        // að þyngja - the infinitive
        listVerb.add(new MorphoRuleVerb("ja", 2, MorphoClass.VerbActive5, MorphoRuleVerb.Mood.Infinitive, MorphoRuleVerb.Voice.Active, IceTag.cPresent ));

        // að borða - the infinitive
        listVerb.add(new MorphoRuleVerb("a", 1, MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Infinitive, MorphoRuleVerb.Voice.Active, IceTag.cPresent));

        // að reyna - the infinitive
        listVerb.add(new MorphoRuleVerb("a", 1, MorphoClass.VerbActive2, MorphoRuleVerb.Mood.Infinitive, MorphoRuleVerb.Voice.Active, IceTag.cPresent));

        // burð-ast
        //listVerb.add(new MorphoRuleVerb("ðast", 3, MorphoClass.VerbMiddle3, MorphoRuleVerb.Mood.Infinitive, MorphoRuleVerb.Voice.Middle, IceTag.cPresent));

        // að ágerast
        listVerb.add(new MorphoRuleVerb("ast", 3, MorphoClass.VerbMiddle1, MorphoRuleVerb.Mood.Infinitive, MorphoRuleVerb.Voice.Middle, IceTag.cPresent));

    }
}


