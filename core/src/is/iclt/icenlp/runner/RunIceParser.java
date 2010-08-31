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

import is.iclt.icenlp.facade.IceParserFacade;
import is.iclt.icenlp.core.utils.FileEncoding;
//import is.iclt.icenlp.core.utils.FileEncoding;
import is.iclt.icenlp.core.iceparser.*;

import java.io.*;

/**
 * Runs IceParser.
 * @author Hrafn Loftsson
 */
public class RunIceParser extends RunIceParserBase
{
    private void parse() throws IOException
    {
        BufferedReader br;
        BufferedWriter bw;
        String str;

        IceParserFacade ipf = new IceParserFacade();

        if (inputFile == null) {
            standardInputOutput=true;
            br = FileEncoding.getReader(System.in);
            bw = FileEncoding.getWriter(System.out);
        }
        else 
		{
			if(inputFile == null || outputFile == null)
			{
				showParametersExit();
			}
           printHeader(); 
           br = FileEncoding.getReader(inputFile);
           bw = FileEncoding.getWriter(outputFile);
           System.out.println( "Input file: " + inputFile );
           System.out.println( "Output file: " + outputFile );
        }


        //System.out.println( "Default file encoding: " + FileEncoding.getEncoding());
		StringBuffer buff = new StringBuffer();
        int count=0;
        while((str = br.readLine()) != null) 
		{
			count++;
			if (!standardInputOutput && count%500==0)
				System.out.print("Lines: " + count + "\r");
			//bw.write(ipf.parse(str, includeFunc, phrasePerLine, agreement, markGrammarError));
			//bw.write("\n");

			//if tags are merged then phrase per line is done in the outputFormatter
			if(phrasePerLine && !mergeTags)			
				buff.append(ipf.parse(str, includeFunc, phrasePerLine, agreement, markGrammarError));
			else
				buff.append(ipf.parse(str, includeFunc, false, agreement, markGrammarError));
			buff.append("\n");
		}

		//
		if( !( (outputType==0 || outputType==1) && !mergeTags) ) 
		{
			OutputFormatter of = new OutputFormatter();
			switch (outputType) 
			{
			  case 0:
					of.setPlain(true);
					break;
			  case 1: 
					of.setPlainPerLine(true);				
					break;
			  case 2:
					of.setJson(true);
					break;
			  case 3:
					of.setXml(true);
					break;
			  default:
					of.setPlain(true);
					break;
			}
			if(mergeTags)
				of.setMergeTags(true);

			StringReader sr = new StringReader( buff.toString() );
			StringWriter sw = new StringWriter( );		

			of.yyclose();
			of.yyreset(sr);
			of.parse(sw);

			bw.write(sw.toString());

			sr.close();
			sw.close();
		}
		else
		{
			bw.write(buff.toString());
		}
       
        //if (!standardInputOutput && count%500==0)
          //  System.out.println("Lines: " + count);

		
		bw.flush();
        bw.close();
    }

    public static void main(String[] args) throws IOException 
	{

        RunIceParser runner = new RunIceParser();

        runner.getParam(args);

        long start = System.currentTimeMillis();

        runner.parse();

        long end = System.currentTimeMillis();
        if (!runner.standardInputOutput)
            System.out.println("Time in msec " + (end-start));
    }
}
