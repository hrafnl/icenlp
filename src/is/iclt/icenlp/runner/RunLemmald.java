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
import is.iclt.icenlp.facade.IceNLP;

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
        options.addOption("lemmatizeTagged",false,"lemmatize tagged text (one word and tag per line in input)"); 

        // train
        // options.addOption("t", false, "train lemmatizer" );

        // help
        options.addOption("h", false, "display this message");

        // input file
        options.addOption("i", true, "input file, reads from stdin if none is supplied");
        options.getOption("i").setArgName("file");

        // output file
        options.addOption("o", true, "output file, writes to stdout if none is supplied");
        options.getOption("o").setArgName("file");

    }
    
    public static void runConsole( CommandLine cmdLine ){

    	String input = null;
    	String output = null;
    	String outputFile = null;
    	
    	// Read input, assume reading from stdin if no input file is specified
    	if( cmdLine.hasOption("i") ){
    		input = FileOperations.fileToString( cmdLine.getOptionValue("i"));
    	}
    	else {
    		try {
    			input = IOUtils.toString(System.in);
    		} catch ( IOException ex ){
    			System.out.println("Exception while reading from stdin!");
    		}
    	}
    	    	    	
    	// Create a TaggedText object to lemmatize
    	TaggedText taggedText = null;    	
    	if( cmdLine.hasOption("lemmatizeTagged")){
    		// Lemmatize tagged  	
    		taggedText = TaggedText.newInstance(input, TagFormat.ICE2);    	        		
    	}
    	else {  // tag and lemmatize plain text
    		taggedText = IceNLP.newInstance().tagText( input );    	
    	}
    	// Lemmatize
    	Lemmald lemmald = Lemmald.newInstance();    	
    	lemmald.lemmatizeTagged( taggedText );    	
    	output = taggedText.toString(TagFormat.ICE2);

    	// Return output
    	if( cmdLine.hasOption("o")){
    		outputFile = cmdLine.getOptionValue("o");
    		FileOperations.stringToFile(outputFile, output);    		
    	}
    	else {
    		System.out.print(output);    		
    	}
    	
    }

    public static void main(String[] args) {
    	     
    	 CommandLineParser parser = new GnuParser();
    	 try {
    	    // parse the command line arguments
    	    CommandLine cmdLine = parser.parse( options, args );
    	    if(cmdLine.hasOption("h") ){
    	    	HelpFormatter formatter = new HelpFormatter();
    	    	formatter.printHelp( "lemmatize.sh", options );	
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
