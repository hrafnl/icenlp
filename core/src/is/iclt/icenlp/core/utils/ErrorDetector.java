package is.iclt.icenlp.core.utils;

import is.iclt.icenlp.core.lemmald.Lemmald;
import is.iclt.icenlp.core.lemmald.LemmaResult;

import java.io.FileNotFoundException;
import java.io.IOException;


 /**
 * Identifies and marks errors found in the flex.
 * @author Guðmundur Örn Leifsson
 */
public class ErrorDetector {

	static WordHashMap verbs;

	static boolean SqlEnabled = false;
	static String SqlUser;
	static String SqlPass;
	static String SqlUrl;

	public static void setSQL(boolean enableSQL, String url, String user, String pass) {
		SqlEnabled = enableSQL;
		SqlUser = user;
		SqlPass = pass;
		SqlUrl = url;
	}

	// for Phrase_NP
	static String encO =  IceParserUtils.encodeOpen;
	static String encC =  IceParserUtils.encodeClose;

	// for Phrase_NP
	private static int GetModifierNLG(String letter)
	{
		if(letter.equals("n") || letter.equals("l") || letter.equals("g") )
			return 0;
		if(letter.equals("f") || letter.equals("t"))
			return 1;

		return -1;
	}

	// for Phrase_NP
	// returns an error string like : ?g+ca
	private static String CheckGenNumCase(String str)
	{
		//remove all tags that do not contain case/number/gender
		str = str.replaceAll("\\s*\\^\\w{0,3}\\$\\s*","");
		// and returns nothing if there are less than two tags (continues to detect an error if it contains two or more ^ signs)
		if (!str.matches(".*\\^.*\\^.*"))
		{
			return "";
		}


		StringBuilder error = new StringBuilder();
		String [] tags = str.split(" ");

		String tagI = tags[0].substring(encO.length(), tags[0].length()-encC.length());

		int mod1;
		mod1 = GetModifierNLG(tagI.substring(0,1));

		int forLoopStop = tags.length;
		int mod2;
		String gen1,num1,case1, gen2,num2,case2;

		for(int i=0; i<forLoopStop; i++)
		{
			String tagX = tags[i].substring(encO.length(), tags[i].length()-encC.length());

			mod2 = GetModifierNLG(tagX.substring(0,1));

			//System.out.println("Mod1 : "+mod1+"\nMod2 : "+mod2);

			if(mod1 == -1 || mod2 == -1) continue;

			//ef t þá verður 2 sæti að vera f

			if(tagI.length() < 4+mod1 || tagX.length() < 4+mod2)
			{
				continue;
			}

			gen1 = tagI.substring(1+mod1, 2+mod1);
			num1 = tagI.substring(2+mod1, 3+mod1);
			case1 = tagI.substring(3+mod1, 4+mod1);

			gen2 = tagX.substring(1+mod2, 2+mod2);
			num2 = tagX.substring(2+mod2, 3+mod2);
			case2 = tagX.substring(3+mod2, 4+mod2);

//			System.out.println("gnc1 : "+gen1+" - "+num1+" - "+case1);
//			System.out.println("gnc2 : "+gen2+" - "+num2+" - "+case2);

			if( !gen1.equals(gen2) || !num1.equals(num2) || !case1.equals(case2))
			{
				// if there has been something written to error we need to add "+" between error tags
				if (!gen1.equals(gen2)&&(!gen1.equals("x"))&&(!gen2.equals("x")))
				{
					error.append("Ng");
				}

            	if (!num1.equals(num2)) {
					error.append("Nn");
				}

               	if (!case1.equals(case2)) {
					error.append("Nca");
				}
			}
		}

		if (0 < error.length())
		{
			error.insert(0,'?');
			error.append('?');
		}

		return error.toString();
	}


	// from Phrase_NP.flex {NounPhrase}
	public static String ErrorCheck(String originalStr, String phraseType)
	{
//		System.out.println("gDB>> ErrorCheck originalStr=("+originalStr+") phraseType=("+phraseType+")");

		String tokenlessStr = RemoveTokens(originalStr);
		String errors = CheckGenNumCase(tokenlessStr);//create the error string which is going to be something like g+n+ca

		//HL: return " [" + PhraseType + errors + " " + originalStr + " " + PhraseType + errors + "] ";
        return " [" + phraseType + errors + " " + originalStr + " " + phraseType + "] ";
	}




	// for Phrase_NP and Func_SUBJ
	private static String RemoveTokens(String str)
	{
		str = IceParserUtils.RemoveFromSymbolToWhitespace("[", str);
		str = IceParserUtils.RemoveFromSymbolToWhitespace("{", str);
		str = new StringBuffer(str).reverse().toString();
		str = IceParserUtils.RemoveFromSymbolToWhitespace("]", str);
		str = IceParserUtils.RemoveFromSymbolToWhitespace("}", str);
		str = new StringBuffer(str).reverse().toString();
		str = IceParserUtils.RemoveSpacesAndWords(str);
		return str;
	}





	private static char findGender(String[] input)
	{
		// we prefer to find gender in nouns
		// go through all the inputs and return the gender of the noun if there is any noun found
		for (String a : input)
		{
			// when reaching the rest (null values) then stop the loop
			if (a == null)
			{
				break;
			}

			if (a.charAt(0) == 'n')
			{
				return a.charAt(1);
			}
		}

		// checks the first letter in the tag and pulls out the appropriate letter to indicate the gender
		for (String a : input)
		{
			// when reaching the rest (null values) then stop the loop
			if (a == null)
			{
				break;
			}

			if ((a.charAt(0) == 'l') || (a.charAt(0) == 'g'))  // nkeng , lkensf , ghen
			{
				return a.charAt(1);
			}
			else if ((a.charAt(0) == 'f')||((a.charAt(0) == 't')&&(a.length() == 5))) //feken , tfvfn (watch out for short tags)
			{
				return a.charAt(2);
			}
			else if ((a.charAt(0) == 's')&&(a.length()>3)) // sþgven
			{
				return a.charAt(3);
			}
		}
		return 'x'; // unspecified gender
	}


	private static boolean isGenderMismatch(char c1, char c2)
	{
		// c1!=c2, if the gender matches return false
		// !((c1 == '1')||...||(c2 == 'x')) f the gender is a (1. or 2.) person or unidentified, return false
		// otherwise return true
		return c1!=c2 && !((c1 == '1')||(c1 == '2')||(c1 == 'x')||(c2 == '1')||(c2 == '2')||(c2 == 'x'));
	}


	private static char findNumber(String[] input)
	{
		// we prefer to find number in nouns
		// go through all the inputs and return the number of the noun if there is any noun found
		for (String a : input)
		{
			// when reaching the rest (null values) then stop the loop
			if (a == null)
			{
				break;
			}

			if (a.charAt(0) == 'n')
			{
				return a.charAt(2);
			}
		}

		// checks the first letter in the tag and pulls out the appropriate letter to indicate the gender
		for (String a : input)
		{
			// when reaching the rest (null values) then stop the loop
			if (a == null)
			{
				break;
			}
			if ((a.charAt(0) == 'l') || (a.charAt(0) == 'g'))  // nkeng , lkensf , ghen
			{
				return a.charAt(2);
			}
			else if ((a.charAt(0) == 'f')||((a.charAt(0) == 't')&&(a.length() == 5))) //feken , tfvfn (watch out for short tags)
			{
				return a.charAt(3);
			}
			else if ((a.charAt(0) == 's')&&(a.length()>3)) // sþgven  sfg3eþ
			{
				return a.charAt(4);
			}
		}
		return 'x'; // unspecified gender
	}

	// returns true if there is a mismatch between the first tag that has a number and the incoming number
	private static boolean isNumberMismatch(char n1, char n2)
	{
		// n1!=n2, return true if there is a mismatch
		// !((n1 == 'x')||(n2 == 'x')), and neither one of the numbers are unknown
		return n1!=n2 && !((n1 == 'x')||(n2 == 'x'));
	}

	// returns the person if there is any person found, or x if none is found
	private static char findPerson(String[] input)
	{
		// we prefer to find number in nouns
		// go through all the inputs and return the number of the noun if there is any noun found
		for (String a : input)
		{
			// when reaching the rest (null values) then stop the loop
			if (a == null)
			{
				break;
			}

			if ((a.charAt(0) == 'n')&&((a.charAt(1) == 'k')||(a.charAt(1) == 'v')|(a.charAt(1) == 'h')))
			{
				return '3';
			}
		}

		// checks the first letter in the tag and pulls out the appropriate letter to indicate the gender
		for (String a : input)
		{
			// when reaching the rest (null values) then stop the loop
			if (a == null)
			{
				break;
			}

			if ((a.charAt(0) == 'n')||(a.charAt(0) == 'l')||(a.charAt(0) == 'g'))  // nkeng , lkensf , ghen
			{
				if ((a.charAt(1) == 'k')||(a.charAt(1) == 'v')|(a.charAt(1) == 'h'))
				{
					return '3';
				}
			}
			else if ((a.charAt(0) == 't')&&(a.length() == 5)) // tfvfn (watch out for short tags)
			{

				if ((a.charAt(2) == 'k')||(a.charAt(2) == 'v')|(a.charAt(2) == 'h'))
				{
					return '3';
				}
			}
			else if (a.charAt(0) == 'f') //feken, fp1en
			{
				if ((a.charAt(2) == 'k')||(a.charAt(1) == 'v')|(a.charAt(1) == 'h'))
				{
					return '3';
				}
				else
				{
					return a.charAt(2);
				}
			}
			else if ((a.charAt(0) == 's')&&(a.length()>3)) // sþgven  sfg3eþ sfg1en
			{
				return a.charAt(3);
			}
		}
		return 'x'; // unspecified gender
	}

		// returns true if there is a mismatch between the first tag that has a person and the incoming person
	private static boolean isPersonMismatch(String[] input, char person)
	{
//		System.out.println("isPersonMishmatch input=("+input+") person=("+person+")");
		// if the person of the target is unknown we cannot tell if there is a mismatch or not
		if (person == 'x')
		{
			return false;
		}

		for (String a : input)
		{
			// when reaching the rest (null values) then stop the loop
			if (a == null)
			{
				break;
			}

			if ((a.charAt(0) == 'n') || (a.charAt(0) == 'l') || (a.charAt(0) == 'g'))  // nkeng , lkensf , ghen
			{
				if (person != '3')// n l and g only have third person so if we land here and person is not 3rd person, we return an error
				{
					return true;
				}
			}
			else if ((a.charAt(0) == 'f')||((a.charAt(0) == 't')&&(a.length() == 5))) //feken , tfvfn (watch out for short tags)
			{
				// if we see f with k v or h and persona is not 3, we return true (an error), if it is 3 we return false
				if ((a.charAt(2) == 'k')||(a.charAt(2) == 'v')||(a.charAt(2) == 'h'))
				{
					if (person != '3')
					{
						return true;
					}
				}
				else // if we land here it means the a.charAt returns 1,2 or 3 and we can match it OR if it returns k v or h with persona = 3
				{
					if (a.charAt(2) != person)
					{
						return true;
					}
				}
			}
			else if ((a.charAt(0) == 's')&&(a.length()>3)) // sþgven sfg1en but watch out for ssg
			{
				// if we see f with k v or h and persona is not 3, we return an error
				if (((a.charAt(3) == 'k')||(a.charAt(2) == 'v')||(a.charAt(2) == 'h'))&&(person != '3'))
				{
					return true;
				}
				else // if we land here it means the a.charAt returns 1,2 or 3 and we can match it
				{
					if (a.charAt(3) != person)
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	// returns the findCase if there is any findCase found, or x if none is found
	private static char findCase(String[] input)
	{
		// checks the first letter in the tag and pulls out the appropriate letter to indicate the gender
		for (String a : input)
		{
			// when reaching the rest (null values) then stop the loop
			if (a == null)
			{
				break;
			}

			if ((a.charAt(0) == 'n')||(a.charAt(0) == 'l')||(a.charAt(0) == 'g'))  // nkeng , lkensf , ghen
			{
				return a.charAt(3);
			}
			else if ((a.charAt(0) == 't')&&(a.length() == 5)||(a.charAt(0) == 'f')) // tfvfn (watch out for short tags)//feken, fp1en
			{
					return a.charAt(4);
			}
			else if ((a.charAt(0) == 's')&&(a.length()>3)) // sþgven  sfg3eþ sfg1en
			{
				return a.charAt(5);
			}
		}
		return 'x'; // unspecified case
	}


	// gets in a string like : ^lkensf$ ^nkeng$ ^sfg3en$ ^aa$ ^lkensf$
	// we can also handle any other strings now and just filter out strings that do not start with a ^
	// then it is turned into a string array where each item is [lkensf][nkeng][sfg3en][aa][lkensf]
	private static String[] toTagList(String input)
	{

		String[] output;
		int x = 0;

		String [] tags = input.split(" ");
		int forLoopStop = tags.length;

		output = new String[tags.length];

		// each string is on the form ^nkeng$ which we want to keep
		// we take the ^ and $ off and only keep nouns, verbs...
		for (int i = 0; i < forLoopStop; i++)
		{
			if ((tags[i].length() > 0)&&(tags[i].charAt(0) == '^'))  // make sure we only add tags (strings starting with ^) to output
			{
				output[x] = tags[i].substring(1,tags[i].length()-1);
				x++;
			}
		}

		return output;
	}

	// finds and crops out the phrase requested.
	// phraseType can be the beginning of a phrase
	private static String findPhrase(String s1, String phraseType)
	{
		int s1PosOfPrase = s1.indexOf(phraseType);

		if (phraseType.equals(""))
		{
			return s1;
		}
		if (s1PosOfPrase != -1)
		{
			int maxLength = s1.length(); // to make sure we don't check outside of the incoming string
			int startPos = s1PosOfPrase+phraseType.length(); // the start of the phrase we are looking for
			int endPos = s1.lastIndexOf(phraseType); // the end location of the phrase we are looking for

			while (true)
			{
				if ((s1.charAt(startPos) == ' ')&&(startPos <= maxLength))
				{
					break;
				}
				startPos++;
			}

			return s1.substring(startPos,endPos);
		}
		else
		{
			return "";
		}
	}

	private static String removePhrase(String s1, String phraseType)
	{
		if (phraseType.equals(""))
		{
			return s1;
		}

		String out = s1.substring(0,s1.indexOf(phraseType)-1); // -1 to take out the initial [

		out = out + s1.substring(s1.lastIndexOf(phraseType)+phraseType.length()+1); // +phraseType.length() to take out the remainder of the phraseType and the +1 to get rid of the ]
		if (out.length() > 0)
		{
			return out;
		}
		else
		{
			return s1;
		}
	}

	// takes in a string array,
	// searches for every noun and checks the gender
	// if it finds a mismatch between the genders of the nouns it returns true
	private static boolean multipleGendersInNouns (String[] s)
	{
		char lastGender=findGender(s);
		for (String a: s)
		{
			if (a.charAt(0) == 'n')
			{
				if (a.charAt(1) == lastGender)
				{
					lastGender = a.charAt(1);
				}
				else
					return true;
			}
		}
		return false;
	}

	// checks if the input contains QUAL, if it does, removes it and returns the rest
	private static String removeQual(String in)
	{
		if (in.contains("*QUAL"))
		{
			return in.replaceAll("\\{\\*QUAL.*\\*QUAL\\}","");
		}
		else
			return in;
	}

	// from Func_COMP.flex {SubjVerbAdvPCompl}, {SubjVerbCompl}, {SubjCompl} and {VerbSubjCompl}
	// from Func_OBJ2.flex {SubjVerbComp}, {SubjVerbAdvPComp}
	public static String CompAgreementCheck(String s1, String s2, String open, String close, int order)
	{
//		System.out.println("CompAgreementCheck s1=("+s1+") s2=("+s2+")");
		StringBuilder out0 = new StringBuilder();
		// check if the compliment contains a noun, if so then we dont look for an error
		// Vigdís var forseti
		if ((order == 1) && (s2.contains(" ^n")))
		{
			return out0.append(s1).append(open).append(s2).append(close).toString();
		}
		else if ((order != 1) && (s1.contains(" ^n")))
		{
			return out0.append(open).append(s1).append(close).append(s2).toString();
		}

		StringBuilder error = new StringBuilder();

		String phrase = "";
		if (s1.contains("VPb") || s2.contains("VPb")) // if the incoming sentences do not have VPb
		{
			phrase = "VPb";
		}

		// get s1 without the VPb
		String s1tmp = removePhrase(s1,phrase);

		// if there is a *QUAL we need to strip it off as it can accidentally be picked instead of the main noun
		s1tmp = removeQual(s1tmp);
		String s2tmp = removeQual(s2);

		// s1:({*SUBJ> [NPn Börnin ^nhfng$  NP] *SUBJ>}  [VPb  voru ^sfg3fþ$   VPb])
		// pull out the VPb tag and compare it to the subject
//		String[] VPbTags = toTagList(RemoveTokens(findPhrase(s1,phrase)));
		 /* detect mismatch between VPb and the subject and object */




		String[] s1Tags = toTagList(RemoveTokens(s1tmp));
		String[] s2Tags = toTagList(RemoveTokens(s2tmp));


		// gender = h ; number = f
		//if we find NPs it means we have something like "maðurinn og konan eru góð" or "maðurinn og drengurinn eru góðir"
		// we check if we have NPs, and then we check if the gender matches, if it doesn't we change the gender to neutral
		if (s1.contains("[NPs"))
		{
			if (multipleGendersInNouns(s1Tags))
			{
				// if there are multiple genders we want the word to be neutral and plural
				if (isGenderMismatch(findGender(s2Tags),'h'))
				{
					error.append("Cg");
				}
				if (isNumberMismatch(findNumber(s2Tags),'f'))
				{
					error.append("Cn");
				}
			} else
			{
				// if there is only a single gender we keep the gender of it but want the word to be plural
				if (isGenderMismatch(findGender(s2Tags),findGender(s1Tags)))
				{
					error.append("Cg");
				}
				if (isNumberMismatch(findNumber(s2Tags),'f'))
				{
					error.append("Cn");
				}
			}

		} else if (s2.contains("[NPs"))
		{
			if (multipleGendersInNouns(s2Tags))
			{
				// if there are multiple genders we want the word to be neutral and plural
				if (isGenderMismatch(findGender(s1Tags),'h'))
				{
					error.append("Cg");
				}
				if (isNumberMismatch(findNumber(s1Tags),'f'))
				{
					error.append("Cn");
				}
			} else
			{
				// if there is only a single gender we keep the gender of it but want the word to be plural
				if (isGenderMismatch(findGender(s1Tags),findGender(s2Tags)))
				{
					error.append("Cg");
				}
				if (isNumberMismatch(findNumber(s1Tags),'f'))
				{
					error.append("Cn");
				}
			}
		} else
		{
			if (isGenderMismatch(findGender(s2Tags),findGender(s1Tags)))
			{
				error.append("Cg");
			}
			if (isNumberMismatch(findNumber(s2Tags),findNumber(s1Tags)))
			{
				error.append("Cn");
			}
		}



		/*
		if (error.length() > 0)
		{
			error.insert(0,'?');
            error.append('?');
		}
		*/

    // order 1 = s1 {comp s2 comp}
//		System.out.println("gDB>> open=("+open+")");
		open = addError(open, error.toString());
		//open = open.substring(0,open.length()-1)+error+" ";
		// HL: close = close.substring(0,close.indexOf('}'))+error+"} ";

		/*
		StringBuffer out = new StringBuffer();
		if (order == 1)
		{
			out.append(s1).append(open).append(s2).append(close);
		}
	// order 2 = {comp s1 comp} s2
		else
		{
			out.append(open).append(s1).append(close).append(s2);
		}
		return out.toString();
		*/

        return makeOutput(s1,s2,open,close,order != 1);
	}

	// receives a phrase and marks the first phrase or function with the error
	// checks if there already has been marked an error and makes sure the number of ? symbols fits
	private static String addError(String phrase, String error)
	{
		// if we do not receive an error we do not mark the phrase
		if (error.length()<1)
		{
			return phrase;
		}
		else
		{
			// extracts the first phrase or function and replaces with the mark <-.->
			// stores the rest of the phrase
			String temp = phrase.replaceFirst("\\s*([\\{\\[]\\S+)\\s?.*","$1");
			String foundation = phrase.replaceFirst("(\\s*)[\\{\\[]\\S+(\\s?.*)","$1<-.->$2");
			if (temp.contains("?"))
			{   // {*COMP<?Cg? → {*COMP<?Cg?Xx?
				return foundation.replace("<-.->",temp+error+"?");
			}
			else
			{   //{*COMP<      → {*COMP<?Xx?
				return foundation.replace("<-.->",temp+"?"+error+"?");
			}
		}
	}


	// looks at s1 and s2 and turns an error if their numbers doesn't match
	// if order is 1, then the s2 will get open and close around it, otherwise it will be s1
	// from Func_SUBJ.flex {VerbSubject}, {SubjectVerb}
	public static String agreementSubjectVerbCheckNumberAndPerson(String s1, String s2, String open, String close, int order)
	{
//		System.out.println("agreementSubjectVerbCheckNumberAndPerson s1=("+s1+") s2=("+s2+")");
		String[] s1Tags = toTagList(RemoveTokens(s1));
		String[] s2Tags = toTagList(RemoveTokens(s2));
		StringBuilder error = new StringBuilder();

//		String VPTags[];
//		VPTags = new String[1];
		String VPTemp = "";

		 /* detect mismatch between VPb and the subject and object */

		if (s1.contains("[VP")) {
			VPTemp = findPhrase(s1,"VP");
		}
		// if we find VP tag in s1 we check if the gender matches s2
		// if we don't find it in s1 we check s2 and check for gender match there
		if (!VPTemp.equals(""))
		{
			// if we have multiple cases of NP then it is marked with NPs. If we find that we know the verb is
			// most likely plural. "Hann og hún eru góð"
			if (s2.contains("[NPs"))
			{
 				if (isNumberMismatch('f',findNumber(toTagList(VPTemp))))
				{
					error.append("Vn");
				}
			}
			else if (isNumberMismatch(findNumber(s1Tags),findNumber(toTagList(VPTemp))))
			{
				error.append("Vn");
			}

			if (isPersonMismatch(s1Tags,findPerson(toTagList(VPTemp))))
			{
				error.append("Vp");
			}
			s1 = addError(s1, "VP", error.toString());
		}
		else
		{
//			VPTemp = findPhrase(s2,"VP");

			// if we have multiple cases of NP then it is marked with NPs. If we find that we know the verb is
			// most likely plural. "Hann og hún eru góð"
			if (s1.contains("[NPs"))
			{
				if (isNumberMismatch('f',findNumber(s2Tags))) {
					error.append("Vn");
				}
			}
			else if (isNumberMismatch(findNumber(s1Tags),findNumber(s2Tags)))
			{
				error.append("Vn");
			}
			if (isPersonMismatch(s1Tags,findPerson(s2Tags)))
			{
				error.append("Vp");
			}
			s2 = addError(s2, "VP", error.toString());
		}

		/*
		StringBuffer out = new StringBuffer();

		if (order == 1)
		{
			out.append(s1).append(open).append(s2).append(close);
		}
		else
		{
			//System.out.println("gDB>> open="+open+" s1="+s1+" close="+close+" s2="+s2);
			out.append(open).append(s1).append(close).append(s2);
		}
			return out.toString();
		*/


        return makeOutput(s1,s2,open,close,order != 1);
	}

	// adds error to the identified phrase in s1 and returns the results
	private static String addError (String s1, String phrase, String error)
	{
		int s1Pos = s1.indexOf("["+phrase);
		if (s1.contains("{"))
		{
			s1Pos = s1.indexOf("{"+phrase);
		}

//		System.out.println("gDB>> s1=("+s1+") phrase=("+phrase+") error=("+error+")");
		// make sure the phrase can be found by the function and that there is an error to mark
		if ((s1Pos == -1)||(error.length() < 1))
		{
			return s1;
		}

		// mark the first NP
		int phraseStartExtend = 0;
		int phraseEndExtend = 0;
		int startOfPhrase = s1Pos+phrase.length()+1;
		int endOfPhrase = s1.indexOf(phrase,startOfPhrase)-phrase.length()+1;
		int endOfS1 = s1.length();

		// increase the length of startOfPhrase if the error/phrase is not exact and some letters are missing
		// counts the number of letters are after the start of phrase [NPn until it finds whitespace
		while(true)
		{
			if ((s1.charAt(startOfPhrase+phraseStartExtend) != ' ')&&(startOfPhrase < endOfPhrase))
			{
				phraseStartExtend++;
			}
			else
			{
				break;
			}
		}
		startOfPhrase += phraseStartExtend;

		// increase the length of startOfPhrase if the error/phrase is not exact and some letters are missing
		// counts the number of letters are after the start of phrase NPn] until it finds the close bracket ] sign
		while(true)
		{
			if ((s1.charAt(endOfPhrase+phraseEndExtend) != ']')&&(startOfPhrase+phraseEndExtend < endOfS1))
			{
				phraseEndExtend++;
			}
			else
			{
				break;
			}
		}
		endOfPhrase += phraseEndExtend;

		StringBuilder out = new StringBuilder();
		out.append(s1.substring(0,startOfPhrase)) // start the string with everything in front of the phrase
				.append('?').append(error).append('?') // insert the error after the first NP
				.append(s1.substring(startOfPhrase,endOfPhrase)) //everything inside NP
				//HL: .append('?').append(error) // insert the error after the second NP
				.append(s1.substring(endOfPhrase,endOfS1)); // append the rest

		return out.toString();
	}

	private static String stripOffWhitespaces (String in)
	{
		int start = 0;
		int end = 0;

		// find where the word begins, from left
		int max = in.length();
		for (int i = 0;i < max;i++)
		{
			if (in.charAt(i) != ' ')
			{
				start = i;
				break;
			}
		}

		// find where the word begins, from right
		for (int i = max-1; 1 < i;i--)
		{
			if (in.charAt(i) != ' ')
			{
				break;
			}
				end = i;
		}

		return in.substring(start,end);
	}

	// from Func_OBJ.flex {SubjVerbObj},
	public static String agreementCheckSubjVerbObj(String s1, String s2, String open, String close, int order)
	{
//		System.out.println("agreementCheckSubjVerbObj s1=("+s1+") s2=("+s2+")");
		Boolean error = false;
		String s1in = s1.substring(s1.indexOf("[VP")+3,s1.indexOf("VP]")); // gets the verb and tag from s1
		String word = s1in.substring(0,s1in.indexOf('^')); // gets the verb s1
		word = stripOffWhitespaces(word);
//		System.out.println("word=("+word+")");
		String tag = s1in.substring(s1in.indexOf('^')+1,s1in.indexOf('$')); // gets the tag from s1

//		System.out.println("tag=("+tag+")");
		char s2case = findCase(toTagList(s2)); // get the tag of the nounphrase in s2

//		System.out.println("s2case=("+s2case+")");
		String lemma = lemmado(word,tag);

//		System.out.println("lemma=("+lemma+")");
		String possibleTags = "";
		try
		{
			// initialize verbs if it hasn't been
			if (null == verbs)
			{
				verbs = new WordHashMap("/lists/verbs.txt");
			}
			 possibleTags = verbs.wordLookup(lemma);
//			System.out.println("possibletags="+possibleTags);
		}
		catch (FileNotFoundException e) {System.out.println("Error [FileNotFound] "+e);}
		catch (IOException i) {System.out.println("Error [IO] "+i);}

//		System.out.println("possibleTags=("+possibleTags+") verb case=("+Character.toString(s2case)+")");

//		if the case of the object is the same as any possible tag for the verb, then do nothing, if there is a mismatch the nounphrase will be error marked
// 		or if we cannot find the word in the sqlWordLoopup then we do nothing
		if (possibleTags.contains(Character.toString(s2case))||(possibleTags.equals("")))
		{
			// do nothing
		}
		else
		{
			// case not found in database, mark the nounphrase with an error
			s2 = addError(s2,"NP","Aca");
//			System.out.println("gDB>> test s2=("+addError(s2,"NP","Aca")+")");
			error = true;
		}

		// there is something missing to do if there is any different order
		// then we need to compare s2in and possibleTags
		// currently is only in one place in Func_OBJ.flex
		if ((!error)&&(order == 3))
		{
			open = "";
			close = "";
		}

		  /*
		StringBuffer out = new StringBuffer();
		out.append(s1).append(open).append(s2).append(close);
		return out.toString();
		*/
        return makeOutput(s1,s2,open,close,false);
	}

	private static String lemmado(String wordform, String tag)
	{
		Lemmald myLemmald = Lemmald.getInstance();

            if (myLemmald != null)
			{
                 LemmaResult lemmaResult = myLemmald.lemmatize(wordform,tag);// String wordform, String tag
 		 		return lemmaResult.getLemma();
            }

		 return "";
	}


	// when we try to
	public static String objVerbSubj (String s1, String s2, String open, String close)
	{
//		System.out.println("gDB>> objVerbSubj s1=("+s1+") s2=("+s2+") open=("+open+") close=("+close+")");
		String noun[] = toTagList(s1);
		String verb[] = toTagList(findPhrase(s2,"VP"));

		if (isNumberMismatch(findNumber(noun),findNumber(verb)))
		{
			s2 = addError(s2,"VP","Vn");
//		System.out.println("s2=("+s2+")");
		}

	/*
		StringBuffer out = new StringBuffer();
		out.append(open).append(s1).append(close).append(s2);
		return out.toString();
	*/

        return makeOutput(s1,s2,open,close,true);
	}



	// from Phrase_PP.flex
	public static String PPErrorCheck(String originalStr, String open, String close)
	{
		String tokenlessStr = RemoveTokens(originalStr);
		String errors = PPCaseCompare(tokenlessStr);//create the error string which is going to be something like g+n+ca

		// fit the error into the open and closing of the phrase markings
		if (0 < errors.length())
		{
			// remove white spaces at the end of the variable and add the error code (and the signs that were removed)
			open = open.replaceAll("(.*\\S)\\s+","$1")+errors+" ";
		}

//        return open + " " + originalStr + close;
        return makeOutput(originalStr,"",open + " ",close,true);
	}

	// extract the case of the first adjective tag (^aþ$)
	// extract the case of the last tag.
	// create an error code if there is a mismatch and case was detected for both words (x is undetected)
	private static String PPCaseCompare(String tagString)
	{
		String errorCode= "";

		char adjectiveCase = tagString.replaceAll(".*\\^a([noþe])\\$.*","$1").charAt(0);
		String lastTag = tagString.replaceAll(".*\\^(\\S+)\\$\\s+$","$1");  //^aþ$ ^aa$ ^nkeþ$ )
		String[] tmpTag={lastTag};
		char lastCase = findCase(tmpTag);

		// if we find a mismatch then we write the error code
		if ((adjectiveCase != lastCase)&&(lastCase != 'x')&&(adjectiveCase != 'x'))
		{
			errorCode = "?Pca?";
		}

		return errorCode;
	}


	// input:
	// s1 - first part of sentence
	// s2 - second part of sentence
	// open - how the new phrase opens
	// close - how the new phrase closes
	// isFirstMarked - if true, then the new phrase will open and close around s1, otherwise s2
	// returns a string in this order, s1 s2, where open and close goes around either s1 or s2 according to isFirstMarked
	private static String makeOutput(String s1, String s2, String open, String close, boolean isFirstMarked/*which of s1/s2 is marked*/)
	{
		if (isFirstMarked)
		{
			return open+s1+close+s2;
		}
		else
		{
			return s1+open+s2+close;
		}
	}
}

