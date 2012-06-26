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

import is.iclt.icenlp.core.iceparser.OutputFormatter;

public class RunIceParserBase
{
	protected String inputFile=null, outputFile=null, outputPath=null;
	protected boolean includeFunc = false;
	//protected boolean phrasePerLine = false;
    protected boolean agreement=false;
	protected boolean markGrammarError=false;
    protected boolean standardInputOutput=false;
	protected boolean mergeLabels=false;

	private int outputSetCount=0;
    protected OutputFormatter.OutputType outputType= OutputFormatter.OutputType.plain;

	protected void getParam(String[] args)
	{
		for( int i = 0; i < args.length; i++ )
		{
			if( args[i].equals( "-help" )  || args[i].equals("--help") || args[i].equals("-h") ) 
			{
				printHeader();
				showParametersExit();
		    }
		    if( args[i].equals( "-i" ) )
				inputFile = args[i + 1];
			if( args[i].equals( "-o" ) )
				outputFile = args[i + 1];
			if(args[i].equals( "-p" ) )
				outputPath = args[i + 1];
			if( args[i].equals( "-f" ) )
				includeFunc = true;
			if( args[i].equals( "-a" ) )
				agreement = true;	
			if( args[i].equals( "-e" ) )
			{
				agreement = true;	
				markGrammarError = true;		
			}
			if( args[i].equals( "-m" ) )
			{
				mergeLabels=true;
			}
			if( args[i].equals( "-json" ) )
			{
				outputType= OutputFormatter.OutputType.json;
				outputSetCount++;
				canChooseOne();
			}
			if( args[i].equals( "-xml" ) )
			{
				outputType= OutputFormatter.OutputType.xml;;
				outputSetCount++;
				canChooseOne();
			}
			if( args[i].equals( "-l" ) )
			{
				outputType= OutputFormatter.OutputType.phrase_per_line;;
				//phrasePerLine=true;
				outputSetCount++;
				canChooseOne();
			}
		}
	}
	protected void canChooseOne()
	{
		if(outputSetCount > 1)
		{
			System.out.println( "\nPlease only select one output type." );
			System.out.println( "The output Parameters are the following:" );
			System.out.println( "\t-l	One phrase per line" );
			System.out.println( "\t-json	Output in json format " );
			System.out.println( "\t-xml	Output in xml format " );
			System.out.println( "\tLeave blank for plain text output\n" );
			System.exit(0);
		}
	}
	protected void showParametersExit()
	{
		System.out.println( "Arguments: ");
		System.out.println( "-help (shows this info)" );
		System.out.println( "-i <input file>" );
	   	System.out.println( "-o <output file>" );
		System.out.println( "-p <output Path>" );
		System.out.println( "-f      annotate functions");
		System.out.println( "-l      one phrase/function per line in the output");
		System.out.println( "        else the output is one sentence per line");
		System.out.println( "-a 	rely on feature agreement"	 );
		System.out.println( "-e 	Mark possible grammatical errors"	 );
		System.out.println( "-m 	Merge function labels with phrase labels"	 );
		System.out.println( "-json	Output in json format"	 );
		System.out.println( "-xml	Output in xml format"	 );
		System.exit(0);
	}

	protected void printHeader()
	{
		System.out.println("***************************************************");
		System.out.println("*  IceParser - An incremental finite-state parser *");
		System.out.println("*  Version 1.1                                    *");
		System.out.println("*  Copyright (C) 2006-2012, Hrafn Loftsson        *");
		System.out.println("***************************************************");
	}
}



