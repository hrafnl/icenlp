/*
 * Copyright (C) 2010 Ragnar Lárus Sigurðsson
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

package is.iclt.icenlp.core.iceparser;


import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.lang.String;


enum OutputFormatter_Type { FUNC, PHRASE, WORDS, WORD, TAG, ROOT, SENTENCE }

public class OutputFormatter
{
    public enum OutputType {plain, phrase_per_line, json, xml, tcf, tag}
	private OutputFormatter_Part root;

	private boolean mergeTags = false;
    private OutputType outType;

    private boolean firstLine=true;
	private StringReader r;
	private StringWriter w;
	private String tcfConstituents = "";
	private String tcfText = "";
	private String tokens = "";
	private String posTags = "";
    private String tcfErrors = "";
	private int tokenID = 0;
	private int constituentID = 0;
    private int errorID = 0;
    private Pattern pPhrase;
    private Pattern pFunc;

	public void setMergeTags(boolean newVal)
	{
		mergeTags = newVal;
	}

	public void parse(java.io.StringWriter _out) throws java.io.IOException
	{
		w = _out;

		root = read();

		if(mergeTags)
			root = mergeFuncPhrase(root, null);

		write(root);
	}

    public String parse(String str, OutputType outType, boolean mergeTags) throws java.io.IOException
    {
            this.r = new StringReader( str );
			this.w = new StringWriter( );

            this.outType = outType;


			setMergeTags(mergeTags);

			root = read();

			if(mergeTags)
			root = mergeFuncPhrase(root, null);

			write(root);
			// Return the result of the StringWriter;

			firstLine = false;
			return w.toString();
    }

    public String removePhraseClosing (String str, OutputType outputType) {
		String parsedString = str;

		if (outputType.equals(OutputFormatter.OutputType.plain)||outputType.equals(OutputFormatter.OutputType.phrase_per_line))
		{
			parsedString = parsedString.replaceAll("\\S+]","]");
			parsedString = parsedString.replaceAll("\\S+}","}");
		}

/*		// GöL
		// Adds an extra space between ". ." at the end of some sentence and the next sentence
		// - then checks the config to see which IceParserOutput the user wants
		// and sets the parsed string into an accessible variable
		if (outputType.equals(OutputFormatter.OutputType.plain))
		{
			//parsedString = str.replaceAll("([\\.\\?\\!])\\ [\\.\\?\\!]\\\n?", "$1 $1\n");
			parsedString = str.replaceAll("\\.\\ \\.", ". .\n");
			return parsedString;
		}
		*/

        return parsedString;
	}


	public OutputFormatter()
	{
        //patterns matching all possible function or phrase tags
//			Pattern pPhrase = Pattern.compile("FRWs?|AdvP|APs?|NP[s\\?]?|VP[bgips]?|PP|S?CP|InjP|MWE_(AdvP|AP|CP|PP)");
        //watch out for NP?Nca
        // VPb?Vn
        String errorCodes = "\\?([A-Z][a-z]+)+\\?";
        pPhrase = Pattern.compile("FRWs?|AdvP|APs?|NP[sd]?(" + errorCodes + ")?|VP[bgips]?(" + errorCodes + ")?|PP|S?CP|InjP|MWE_(AdvP|AP|CP|PP)");
        //Pattern pFunc = Pattern.compile("((\\*SUBJ|\\*I?OBJ(AP|NOM)?|\\*COMP)(<|>)?)|\\*QUAL|\\*TIMEX\\??");
        pFunc = Pattern.compile("(((\\*SUBJ|\\*I?OBJ(AP|NOM)?|\\*COMP)(<|>)?)|\\*QUAL|\\*TIMEX)(" + errorCodes + ")?");

        //System.out.println("pPhrase: " + pPhrase.pattern());
        //System.out.println("pFunc: " + pFunc.pattern());
    }

	// Puts a function tag to the next phrase tag inside of it.
	private OutputFormatter_Part mergeFuncPhrase(OutputFormatter_Part curr, OutputFormatter_Part parent)
	{
		if(curr.children.size()>0)
		{
			for(int i=0; i<curr.children.size(); i++)
			{
				mergeFuncPhrase(curr.children.get(i), curr);
			}
		}

		if(curr.OutputFormatter_Type == OutputFormatter_Type.FUNC)
		{
			int currIndex = parent.children.indexOf(curr);
			parent.children.remove(currIndex);

			String tag = curr.data.substring(1,curr.data.length());
			tag = tag.replace("*", "");
			boolean once = false;

			for(int i=0; i<curr.children.size(); i++)
			{
				OutputFormatter_Part child = curr.children.get(i);
				if(child.OutputFormatter_Type == OutputFormatter_Type.PHRASE && !once)
				{
					child.data+=("-"+tag);
					once=true;
				}

				parent.children.add(currIndex, child);
				currIndex++;
			}
		}

		if(parent == null)
			return curr;
		else 
			return parent;
	}
	private void print(String text)	
	{
		try
		{
			w.write(text);
		}
		catch(Exception e)
		{
			System.err.println(e);
		}
	}

    public String finish()
    {
        String str="";
        if(outType == OutputType.json)
            str = "\t}\n}"+"\n";
		else if(outType == OutputType.xml)
            str = "</ParsedText>"+"\n";
        return str;
    }

	private void write(OutputFormatter_Part root)
	{


		if(outType == OutputType.json)
		{
			writeJson(root);
		}
		else if(outType == OutputType.xml)
		{
			writeXml(root);
		}
		else if(outType == OutputType.tcf)
		{
			writeTcf(root);
		}
		else if(outType == OutputType.plain)
		{
			writePlaintext(root);
		}
		else if(outType == OutputType.phrase_per_line)
		{
			writePlainPerLine(root);
		}
	}

	private OutputFormatter_Part read() throws IOException 
	{
		String str;
		OutputFormatter_Part root = new OutputFormatter_Part(OutputFormatter_Type.ROOT);

		char[] arr = new char[8*1024];
		StringBuilder buf = new StringBuilder();
		int numChars;
		while((numChars = r.read(arr, 0, arr.length)) > 0)
		{
			buf.append(arr, 0, numChars);
		}
		str = buf.toString();

		String [] sentences = null;
		sentences = str.split("\n");

		for( String stre : sentences )
		{
			OutputFormatter_Part sentence = new OutputFormatter_Part("Sentence", OutputFormatter_Type.SENTENCE);
			sentence.children = treeMaker(stre, sentence.children);		
			root.children.add(sentence);
		}
		return root;
	}
	//creates a tree from the input text
	private ArrayList<OutputFormatter_Part> treeMaker(String str, ArrayList<OutputFormatter_Part> list)
	{
		//System.out.println("treeMarker str=("+str+")");
		int funcIndex, phraseIndex;
		str = str.trim();
		while(str.length() > 0)
		{
			str = str.trim();
			funcIndex = str.indexOf("{");
			phraseIndex = str.indexOf("[");


			Matcher mPhrase;
			Matcher mFunc;

			String funcTag, phraseTag;

			if(funcIndex == 0)
			{
				funcTag = str.substring(0, str.indexOf(" "));
				mFunc = pFunc.matcher(funcTag);
				if(!mFunc.find())
					funcIndex = -99;
			}
			if(phraseIndex == 0)
			{
				phraseTag = str.substring(0, str.indexOf(" "));
				mPhrase = pPhrase.matcher(phraseTag);
				if(!mPhrase.find())
					phraseIndex = -99;
			}
			if(funcIndex == 0)
			{
				String tag = str.substring(0, str.indexOf(" "));

                int tagEnd = 0;
                if (tag.contains("?"))
                {
                    tagEnd = tag.indexOf('?');
                }
                else
                {
                    tagEnd = tag.length();
                }
                // The closing tag does not contain the error codes!
				String closingTag = " " + tag.substring(1, tagEnd) + "}";
				//String closingTag = " " + tag.substring(1, tag.length()) + "}";

				if(str.indexOf(closingTag) != -1)
				{
					int endIndex = str.indexOf(closingTag) + closingTag.length() -1;

					String funcstr = str.substring(0, endIndex+1);
					funcstr = funcstr.trim();

					funcstr = funcstr.substring(funcstr.indexOf(" "), funcstr.lastIndexOf(closingTag));
			
					OutputFormatter_Part temp = new OutputFormatter_Part(tag, OutputFormatter_Type.FUNC);
					temp.children = treeMaker(funcstr, temp.children);

					list.add(temp);

					str = str.substring(endIndex+1, str.length());
				}
				else
				{
					list.add(words(str));
					str="";
				}
			}
			else if(phraseIndex == 0)
			{
				String tag = str.substring(0, str.indexOf(" "));


                int tagEnd = 0;

                if (tag.contains("?"))
                {
                    tagEnd = tag.indexOf('?');
                }
                else
                {
                    tagEnd = tag.length();
                }

				String closingTag = " " + tag.substring(1, tagEnd) + "]";
				//String closingTag = " " + tag.substring(1, tag.length()) + "]";

				if(str.indexOf(closingTag) != -1)
				{
					int endIndex = str.indexOf(closingTag) + closingTag.length() -1;

					String phrasestr = str.substring(0, endIndex+1);
					phrasestr = phrasestr.trim();

					phrasestr = phrasestr.substring(phrasestr.indexOf(" "), phrasestr.indexOf(closingTag));
					OutputFormatter_Part temp = new OutputFormatter_Part(tag, OutputFormatter_Type.PHRASE);
					temp.children = treeMaker(phrasestr, temp.children);

					list.add(temp);

					str = str.substring(endIndex+1, str.length());
				}
				else
				{
					list.add(words(str));
					str="";
				}
			}
			else if(funcIndex == -99 || phraseIndex == -99)
			{
				try
				{
					int countChars = str.indexOf(" ");
					String toAppend = str.substring(0, str.indexOf(" "));
		
					StringBuffer buff = new StringBuffer();
					buff.append(toAppend+" ");
			
					str = str.substring(countChars, str.length());
					str = str.trim();
	
					countChars = str.indexOf(" ");
					toAppend = str.substring(0, str.indexOf(" "));

					buff.append(toAppend+" ");
					list.add(words(buff.toString()));
					str = str.substring(countChars, str.length());
				}
				catch(Exception e)
				{
					list.add(words(str));
					str = "";
				}
			}
			else if(funcIndex > 0 || phraseIndex > 0)
			{
				int lesserIndex;

				if(funcIndex < 0)
					lesserIndex = phraseIndex;
				else if(phraseIndex < 0)
					lesserIndex = funcIndex;
				else if(funcIndex < phraseIndex)
					lesserIndex = funcIndex;
				else
					lesserIndex = phraseIndex;

				list.add(words(str.substring(0, lesserIndex-1)));

				str = str.substring(lesserIndex, str.length());

			}
			else
			{
				list.add(words(str));
				str="";
			}	
		}
	 	return list;
		
	} 
	private OutputFormatter_Part words(String str)
	{
		String [] temp = null;
      		temp = str.split(" ");

		OutputFormatter_Part retOutputFormatter_Part = new OutputFormatter_Part("WORDS" ,OutputFormatter_Type.WORDS);

		for(int i=0; i< (temp.length/2); i++)
		{
			OutputFormatter_Part tempOutputFormatter_Part = new OutputFormatter_Part(temp[i*2], OutputFormatter_Type.WORD);
			tempOutputFormatter_Part.children.add(new OutputFormatter_Part(temp[(i*2)+1], OutputFormatter_Type.TAG));

			retOutputFormatter_Part.children.add(tempOutputFormatter_Part);
		}
		
		return retOutputFormatter_Part;
	}


//					encoders				//




    //
    //Plaintext one phrase per line
    //
    private void writePlainPerLine(OutputFormatter_Part root)
    {
        plainPerLineTree(root.children, 0);
    }
    private void plainPerLineTree(ArrayList<OutputFormatter_Part> list, int depth)
    {
        for(OutputFormatter_Part child : list)
        {
            if(child.OutputFormatter_Type == OutputFormatter_Type.WORD || child.OutputFormatter_Type == OutputFormatter_Type.TAG || child.OutputFormatter_Type == OutputFormatter_Type.FUNC || child.OutputFormatter_Type == OutputFormatter_Type.PHRASE)
            {
                if(child.OutputFormatter_Type == OutputFormatter_Type.TAG && depth == 3)
                    print(child.data);
                else
                    print(child.data+" ");
            }

        plainPerLineTree(child.children, depth+1);

        if(child.OutputFormatter_Type == OutputFormatter_Type.FUNC || child.OutputFormatter_Type == OutputFormatter_Type.PHRASE)
        {
            String closetag = child.data.substring(1, child.data.length());
            if(child.OutputFormatter_Type == OutputFormatter_Type.FUNC)
                closetag+="}";
            else
                closetag+="]";
            if(depth > 1)
                print(closetag+" ");
            else
                print(closetag);
            }
            if(child.OutputFormatter_Type != OutputFormatter_Type.WORDS)
            {
                if(child.OutputFormatter_Type == OutputFormatter_Type.TAG && depth < 4)
                {
                    print("\n");
                }
                else if(depth < 2 && child.OutputFormatter_Type != OutputFormatter_Type.TAG)
                    print("\n");
            }
        }
    }


    //
    //Plaintext
    //
    private void writePlaintext(OutputFormatter_Part root)
    {
        plaintextTree(root.children);
    }
    private void plaintextTree(ArrayList<OutputFormatter_Part> list)
    {
        for(OutputFormatter_Part child : list)
        {
            if(child.OutputFormatter_Type == OutputFormatter_Type.WORD || child.OutputFormatter_Type == OutputFormatter_Type.TAG || child.OutputFormatter_Type == OutputFormatter_Type.FUNC || child.OutputFormatter_Type == OutputFormatter_Type.PHRASE)
            {
                print(child.data+" ");
            }
            plaintextTree(child.children);
            if(child.OutputFormatter_Type == OutputFormatter_Type.FUNC || child.OutputFormatter_Type == OutputFormatter_Type.PHRASE)
            {
                String closetag = child.data.substring(1, child.data.length());
                if(child.OutputFormatter_Type == OutputFormatter_Type.FUNC)
                closetag+="}";
                else
                closetag+="]";
                print(closetag+" ");
            }

            if(child.OutputFormatter_Type == OutputFormatter_Type.SENTENCE)
                print("\n");
        }
    }

//
//XML
//
	private void writeXml(OutputFormatter_Part root)
	{
		printXmltree(root.children, "\t");
	}
	
	private void printXmltree(ArrayList<OutputFormatter_Part> list, String indent)
	{
		for (OutputFormatter_Part var : list) 
		{	
			//replace xml reserved sybmols
			String data = var.data;
			data = data.replace("&", "&amp;");
			data = data.replace("<", "&lt;");
			data = data.replace(">", "&gt;");
			
			if(var.OutputFormatter_Type == OutputFormatter_Type.TAG)
			{
				print(indent+"<"+var.OutputFormatter_Type+">"+data+"</"+var.OutputFormatter_Type+">"+"\n");
			}
			else if(var.OutputFormatter_Type == OutputFormatter_Type.SENTENCE || var.OutputFormatter_Type == OutputFormatter_Type.WORDS)
			{
				print(indent+"<"+var.OutputFormatter_Type+">"+"\n");
				printXmltree(var.children, indent+"\t");
				print(indent+"</"+var.OutputFormatter_Type+">"+"\n");
			}
			else
			{
				print(indent+"<"+var.OutputFormatter_Type+"> "+""+data+""+"\n");
				printXmltree(var.children, indent+"\t");
				print(indent+"</"+var.OutputFormatter_Type+">"+"\n");
			}
		}
	}
	
//
//TCF
//	
	private void writeTcf(OutputFormatter_Part root)
	{
		
// start going through the input and store into variables
		printTCFtree(root.children, "    ","");

// start printing out the results

// basic info
		print("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
// ???
		print("<D-Spin xmlns=\"http://www.dspin.de/data\" version=\"0.4\">\n");
// meta data
		print(" <MetaData xmlns=\"http://www.dspin.de/data/metadata\">\n");
		print("  <source>RU, Reykjavik University</source>\n");
 		print(" </MetaData>\n");
// text corpus, divited into text, tokens(words) and tags.
		print(" <TextCorpus xmlns=\"http://www.dspin.de/data/textcorpus\" lang=\"is\">\n");
		print("  <text>"+tcfText.substring(0,tcfText.length()-1)+"</text>\n\n");

//<token ID="t1">Þetta</token>		
		print("  <tokens>\n");
		print(tokens);
		print("  </tokens>\n\n");

//   <tag tokenIDs="t1">NP</tag>
		print("  <POStags tagset=\"ifd\">\n");
		print(posTags);
		print("  </POStags>\n\n");
		
		print("  <parsing tagset=\"ifd\">\n");
		print("   <parse>\n");
		print(tcfConstituents);
		print("   </parse>\n");
		print("  </parsing>\n\n");
		
//	<dependency func="COMP" depIDs="t3" govIDs="t1"/>
/*	we do not have depparsing in the current state of iceNLP
		print("  <depparsing>\n");
		print("   <parse>\n");
		print(dependency);
		print("   </parse>\n");
		print("  </depparsing>\n\n");
*/
        if (!tcfErrors.equals(""))
        {
            print("  <errors>\n");
            print(tcfErrors);
            print("  </errors>\n\n");
        }

// finishing
		print(" </TextCorpus>\n");
		print("</D-Spin>");

		tcfText = "";
		tokens = "";
		posTags = "";
		tcfConstituents = "";
        tcfErrors = "";
		tokenID = 0;
		constituentID = 0;
        errorID = 0;
	}
	


// we are printing the items while climbing down the tree to the leaves,
// but a better version of this would be the other way around
// to avoid having to check if the next child includes a PHRASE on it's list if we are currently in a PHRASE
	private void printTCFtree(ArrayList<OutputFormatter_Part> list, String indent, String phrase)
	{
		for (OutputFormatter_Part var : list) 
		{	
			//replace xml reserved sybmols
			String data = var.data;
			data = data.replace("&", "&amp;");
			data = data.replace("<", "&lt;");
			data = data.replace(">", "&gt;");

			// we do not print TAG, WORDS
			// if we encounter FUNC we start making that list
			// if we see PHRASE we print out
			// if we see WORD we print out
			if(var.OutputFormatter_Type == OutputFormatter_Type.TAG)
			{
				posTags += "   <tag tokenIDs=\"t"+tokenID+"\">" + data + "</tag>\n";
				printTCFtree(var.children, indent+" ", phrase);
			}
			else if(var.OutputFormatter_Type == OutputFormatter_Type.WORDS)
			{
				printTCFtree(var.children, indent, phrase);
			}
			else if(var.OutputFormatter_Type == OutputFormatter_Type.SENTENCE)
			{
				constituentID++;

				tcfConstituents += (indent+"<constituent ID=\"c"+constituentID+"\" cat=\""+(var.OutputFormatter_Type)+"\">\n");
				printTCFtree(var.children, indent+" ", phrase);
				tcfConstituents += (indent+"</constituent>\n");
			}
			else if (var.OutputFormatter_Type == OutputFormatter_Type.FUNC)
			{
				constituentID++;

				// if we find something like : {*SUBJ>?Npn or [NP?Nn   or {*COMP&lt;?Cg?
				// we should write an error with p and n
				// any thing with a question mark will be grabbed as an error
				if (data.matches("[\\{\\[]\\S+\\?\\S+\\?"))
                {
					errorID++;
                    String errorType = extractError(data);
                    tcfErrors += "   <e ID=\"e"+errorID+"\" const=\"c"+constituentID+"\" type=\""+errorType+"\" />\n";
                }

				// remove the error code after and including the ? with the replaceAll
				tcfConstituents += (indent+"<constituent ID=\"c"+constituentID+"\" cat=\""+data.replaceAll("[\\[\\{]","")+"\">\n"); // *SUBJ&gt;
				printTCFtree(var.children, indent+" ", phrase);
				tcfConstituents += (indent+"</constituent>\n");

			}
			else if(var.OutputFormatter_Type == OutputFormatter_Type.WORD)
			{
				constituentID++;
				tokenID++;

				// print out the erros, if we find ? sign within a tag
                // the error detected here are words
                // that is : phrase="NP?" var.OutputFormatter_Type="WORD" data="möguleiki"
                // If we see an error we remove it before it is put into the constituent brackets
/*				if (phrase.matches("[\\[\\{]?[\\w]+[\\w\\W]+\\?.*"))
                {
                    errorID++;
                    String errorType = "underline";
                    errorType = extractError(phrase);
                    //  <e ID="e1" const="c5" type="highlight" />
                    tcfErrors += "   <e ID=\"e"+errorID+"\" const=\"c"+(constituentID+0)+"\" type=\""+errorType+"\"/>\n";
                }
*/


/*				if (aspell(data))
				{
                    errorID++;
                    tcfErrors += "   <e ID=\"e"+errorID+"\" const=\"c"+(constituentID+0)+"\" type=\"Xx\" />\n";
				}
*/
				tokens += "   <token ID=\"t"+tokenID+"\">"+data+"</token>\n";
				
				// getting the Text
				tcfText += (data+" ");
				
				// getting constituent
				//<constituent ID="c2" cat="NP" tokenIDs="t2"/>
				if (data.equals("."))
				{
					tcfConstituents += (indent+"<constituent ID=\"c"+constituentID+"\" cat=\".\" tokenIDs=\"t"+tokenID+"\"/>\n");
				}
				else if (data.equals(","))
				{
					tcfConstituents += (indent+"<constituent ID=\"c"+constituentID+"\" cat=\",\" tokenIDs=\"t"+tokenID+"\"/>\n");
				}
				else
				{
					// remove the error code after and including the ? with the replaceAll
					tcfConstituents += (indent+"<constituent ID=\"c"+constituentID+"\" cat=\""+phrase.replaceAll("\\[?(\\S\\S)(\\?\\S+\\?)?","$1")+"\" tokenIDs=\"t"+tokenID+"\"/>\n");//[PP?Pca?
				}


				// go to the tags
				printTCFtree(var.children, "", "");
			}
			else if(var.OutputFormatter_Type == OutputFormatter_Type.PHRASE)
			{
//				System.out.println("in data = "+data + " phrase="+phrase);
				// if the child contains a phrase we will print out "constituent" data
				// if the child does not contain a phrase we only move the child without printing the "constituent"
				if (isChildHasPHRASEorWORDS(var.children))
				{
					constituentID++;

					// print out the erros, if we find ? sign within a tag
					// the errors detected here is:
					//when a <constituent ID="c2" cat="NP" tokenIDs="t2"/> that is phrase="" or phrase="PP" var.OutputFormatter_Type="PHRASE" data="[NP?"
					// and avoid detecting :
					// gDB> (phrase=(NP?) var.OutputFormatter_Type=(PHRASE) data=([AP)
					if (data.matches("\\[?[\\w]+\\?.*"))
					{
						String errorType = extractError(data);

						int n = numberOfWORDinWORDS(var.children);

						if (n < 0)
						{
							for (int i = 0; i < n; i++) {
								errorID++;
								tcfErrors += "   <e ID=\"e"+errorID+"\" const=\"c"+(constituentID+i)+"\" type=\""+errorType+"\" />\n";
							}
						}
						else
						{
							errorID++;
							tcfErrors += "   <e ID=\"e"+errorID+"\" const=\"c"+(constituentID)+"\" type=\""+errorType+"\" />\n";
						}
					}

					// removing the [ in front of [NP and then the error coming after the question mark [NP?NcaNg?, which will let the phrase (NP) remain
					tcfConstituents += (indent+"<constituent ID=\"c"+constituentID+"\" cat=\""+data.replaceAll("\\[?\\{?(\\w\\w)\\??.*","$1")+"\">\n"); //[PP?Pca?
					printTCFtree(var.children, indent+" ", data);
					tcfConstituents += (indent+"</constituent>\n");
					}
				else
				{
					printTCFtree(var.children, indent, data.substring(1));
				}
			}
			else if (var.OutputFormatter_Type == OutputFormatter_Type.FUNC)
			{
				printTCFtree(var.children, indent, phrase);
			}
		}
	}
/*
	// input : string with words
	// output : true if input has incorrectly spelled word
	// output : false if input is a correctly spelled word
	private boolean aspell(String in)
	{

		String bashResults = "";
		try {
			String[] exec = { "/bin/sh","-c", "echo \""+in+"\" | aspell --dont-suggest -a -l is"};
			Process proc=Runtime.getRuntime().exec(exec);
			proc.waitFor();
			BufferedReader read = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = null;
			while(read.ready())
			{
				while ((line = read.readLine()) != null){
					bashResults += line+"\n";
				}
			}
			System.out.println(bashResults);
		}
		catch(IOException e)
		{
			System.out.println("gDB>>e=("+e.getMessage()+")");
		}
		catch (InterruptedException e)
		{
			System.out.println("gDB>>e="+e.getMessage()+")");
		}

		if (bashResults.contains("\n?") || bashResults.contains("\n#") || bashResults.contains("\n&") || bashResults.contains("\n-"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
 */
    // GöL : returns the errors... that is :takes everything from the ? to the end of the string.
    private String extractError(String in)
    {
        if (in.indexOf('?')+1 < in.length())
		{
			return in.substring(in.indexOf('?')+1,in.length()-1).replace("?",""); //remove the first and last ? in ?Pca?, then remove all ? marks which can be in ?Nn??Aca?
		}
			else
		{
            return "highlight";
		}
    }

// if we find a phrase in the OutputFormatter_Part list then we return true
// otherwise we do nothing
// usage: if we find a phrase and want to see if we should print it out (only if it contains a phrase in it's child's list)
// PHRASE → [PHRASE], WORD, WORD
	private boolean isChildHasPHRASEorWORDS(ArrayList<OutputFormatter_Part> list)
	{
		for (OutputFormatter_Part var : list)
		{
			if ((var.OutputFormatter_Type == OutputFormatter_Type.PHRASE)||(var.OutputFormatter_Type == OutputFormatter_Type.FUNC)||(var.OutputFormatter_Type == OutputFormatter_Type.WORDS)) {
				return true;
			}
		}
		return false;
	}

	private int numberOfWORDinWORDS(ArrayList<OutputFormatter_Part> list)
	{
		int n = 0;
		for (OutputFormatter_Part var : list)
		{
			if (var.OutputFormatter_Type == OutputFormatter_Type.WORDS) {
				n = numberOfWORDS(var.children);
			}
		}
		return n;
	}

	private int numberOfWORDS(ArrayList<OutputFormatter_Part> list)
	{
		int n = 0;
		for (OutputFormatter_Part var : list)
		{
			if (var.OutputFormatter_Type == OutputFormatter_Type.WORD) {
				n++;
			}
		}
		return n;
	}


//
//Json
//
	private void writeJson(OutputFormatter_Part root)
	{
        //    if (firstLine)
        print("{\n\t\"Parsed Text\":{\n");
		printJtree(root.children, "\t\t");
		print("\t}\n}\n");
	}

	private void printJtree(ArrayList<OutputFormatter_Part> list, String indent)
	{
		int count = 0;
		for (OutputFormatter_Part var : list) 
		{	
			if(var.OutputFormatter_Type == OutputFormatter_Type.WORDS)
			{
				print(indent+"\"WORDS\":[{"+"\n");
				int childrenCount = 0;
				for(OutputFormatter_Part word : var.children)
				{
					if(childrenCount < var.children.size()-1)
						print(indent+"\t\t\""+word.data + "\": \"" + word.children.get(0).data+"\","+"\n");
					else
						print(indent+"\t\t\""+word.data + "\": \"" + word.children.get(0).data+"\""+"\n");
				
					childrenCount++;
				}

				if(count < list.size()-1)
					print(indent+"\t\t"+"}],"+"\n");
				else
					print(indent+"\t\t"+"}]"+"\n");

			}
			else
			{
				print(indent+"\""+var.data+"\":{"+"\n");
				if(var.children.size() > 0)
					printJtree(var.children, indent+"\t");
				if(count < list.size()-1)
					print(indent+"},"+"\n");
				else
					print(indent+"}"+"\n");
			}
			count++;
		}
	}



} 
class OutputFormatter_Part
{
	public final OutputFormatter_Type OutputFormatter_Type;	
	public String data;
	public ArrayList<OutputFormatter_Part> children = new ArrayList<OutputFormatter_Part>();

	public OutputFormatter_Part(OutputFormatter_Type _OutputFormatter_Type)
	{	
		OutputFormatter_Type = _OutputFormatter_Type;
	}
	public OutputFormatter_Part( String _data, OutputFormatter_Type _OutputFormatter_Type)
	{
		OutputFormatter_Type = _OutputFormatter_Type;
		data = _data;
	}
}


