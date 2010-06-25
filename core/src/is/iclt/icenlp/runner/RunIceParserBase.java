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

public class RunIceParserBase
{	
	protected String inputFile=null, outputFile=null, outputPath=null;
	protected boolean includeFunc = false;
	protected boolean phrasePerLine = false;
    protected boolean agreement=false;
    protected boolean standardInputOutput=false;

	protected void getParam(String[] args)
	{
		for( int i = 0; i < args.length; i++ )
		{
			if( args[i].equals( "-help" ) ) 
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
			if( args[i].equals( "-l" ) )
				phrasePerLine = true;
			if( args[i].equals( "-a" ) )
				agreement = true;			
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
		System.exit(0);
	}

	protected void printHeader()
	{
		System.out.println("***************************************************");
		System.out.println("*  IceParser - An incremental finite-state parser *");
		System.out.println("*  Version 1.1                                    *");
		System.out.println("*  Copyright (C) 2006-2010, Hrafn Loftsson        *");
		System.out.println("***************************************************");
	}
}



