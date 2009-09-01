/*
 * Copyright (C) 2009 Anton Karl Ingason
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
 * Anton Karl Ingason, University of Iceland.
 * anton.karl.ingason@gmail.com
 */

package is.iclt.icenlp.runner;

import is.iclt.icenlp.core.formald.tags.TagFormat;
import is.iclt.icenlp.core.formald.tags.TaggedText;
import is.iclt.icenlp.core.lemmald.*;
import is.iclt.icenlp.core.utils.FileOperations;

import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;
import java.io.IOException;


/**
 * Runner class for Lemmald.
 * @author <a href="mailto:anton.karl.ingason@gmail.com">Anton Karl Ingason</a>
 */
public class RunLemmald {

    private static Options options = null; // Command line options    

    static {
        options = new Options();

      //  options.addOption("lemmatize",false,"lemmatize plain text");
      //  options.addOption("lemmatizeTagged",false,"lemmatize tagged text"); 
        options.addOption("pipeMode",false,"read input from stdin and write output to stdout");
        

        // train
        // options.addOption("t", false, "train lemmatizer" );

        // help
        options.addOption("h", false, "display this message");

        // input file
        options.addOption("i", true, "input file");
        options.getOption("i").setArgName("file");

        // output file
        options.addOption("o", true, "output file");
        options.getOption("o").setArgName("file");

    }
    
    public static void runConsole( CommandLine cmdLine ){
    	String input = null;
    	String output = null;
    	String outputFile = null;
    	
    	// Read input
    	if( cmdLine.hasOption("pipeMode") ){
    		try {
    			input = IOUtils.toString(System.in);
    		} catch ( IOException ex ){
    			System.out.println("Exception while reading from stdin!");
    			ex.printStackTrace();
    			System.exit(1);
    		}
    	}
    	else {
    		    		
    		if( ! cmdLine.hasOption("i")){
    			System.out.println("You must supply an input file!");
    			System.exit(1);
    		}   
    		else {
    			input = FileOperations.fileToString( cmdLine.getOptionValue("i"));
    		}
    		
    		if( ! cmdLine.hasOption("o") ){
    			System.out.println("You must supply an output file!");
    		}
    		else {
    			outputFile = cmdLine.getOptionValue("o");
    		}    		
    	}
    	
    	// Lemmatize    	
    	Lemmald lemmald = Lemmald.newInstance();
    	TaggedText result = lemmald.lemmatizeText( input );    	
    	output = result.toString(TagFormat.ICE2);
    	
    	// Return output
    	if( cmdLine.hasOption("pipeMode")){
    		System.out.print(output);
    	}
    	else {
    		FileOperations.stringToFile(outputFile, output);    		
    	}
    }

    public static void main(String[] args) {
    	     
    	 CommandLineParser parser = new GnuParser();
    	 try {
    	    // parse the command line arguments
    	    CommandLine cmdLine = parser.parse( options, args );
    	    if(cmdLine.hasOption("h") || args.length == 0 ){
    	    	HelpFormatter formatter = new HelpFormatter();
    	    	formatter.printHelp( "lemmatizer.sh", options );	
    	    }
    	    else {
    	    	runConsole( cmdLine );
    	    }
    	 }
    	 catch( ParseException exp ) {
    	    // oops, something went wrong
    	    System.err.println( "Could not parse command line arguments.  Reason: " + exp.getMessage() );
    	 }
    	
    }
}
