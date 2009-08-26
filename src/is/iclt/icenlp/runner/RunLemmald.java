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

import is.iclt.icenlp.core.lemmald.LemmaResult;
import is.iclt.icenlp.core.lemmald.Lemmald;
import is.iclt.icenlp.core.lemmald.LemmaldSettings;
import is.iclt.icenlp.core.lemmald.LemmaldUtils;
import is.iclt.icenlp.core.lemmald.Trainer;
import is.iclt.icenlp.core.lemmald.tools.FileOperations;
import org.apache.commons.cli.*;
import is.iclt.icenlp.core.lemmald.icenlp.Sentence;
import is.iclt.icenlp.core.lemmald.icenlp.Word;
import is.iclt.icenlp.facade.IceNLP;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.io.IOUtils;

/**
 * INCOMPLETE Runner for Lemmald.
 * @author <a href="mailto:anton.karl.ingason@gmail.com">Anton Karl Ingason</a>
 */
public class RunLemmald {

    private static Options options = null; // Command line options

    static {
        options = new Options();

        options.addOption("lemmatize",false,"lemmatize plain text");
        options.addOption("lemmatizeTagged",false,"lemmatize tagged text");
        options.addOption("lemmatizeFolder",false,"lemmatize files in folder");

        // train
        options.addOption("t", false, "train lemmatizer" );

        // help
        options.addOption("h", false, "display this message");

        // input file
        options.addOption("i", true, "input file");
        options.getOption("i").setArgName("file");

        // output file
        options.addOption("o", true, "output file");
        options.getOption("o").setArgName("file");

        // rule database file
        options.addOption("r", true, "rule database");
        options.getOption("r").setArgName("file");

        // postfix rules file
        options.addOption("p", true, "postfix rules file");
        options.getOption("p").setArgName("file");
    }

    public static void runConsole( String[] args ){

        System.out.println( IceNLP.getInstance().tagLines("Við erum rauðir kaktusar"+System.getProperty("line.separator")+" Hvað á það að þýða?") );
               
        final CommandLine cmd = parseArguments(options, args);

        // Print help message
        if (cmd.hasOption("h")) {
            printHelp();
            return;
        }

        // Check if we should run in silent mode
        if( cmd.hasOption("s") ){
            LemmaldSettings.setValue("systemOut", false );
        }

        if( cmd.hasOption("lemmatize")){


        }
        else if( cmd.hasOption("lemmatizeFolder") ){

        }
        else if( cmd.hasOption("lemmatizeTagged") ){
            // if no input or output file, expect stdin mode
            if( (!cmd.hasOption("i")) && (!cmd.hasOption("o")) ){
                LemmaldSettings.setValue("systemOut", false ); // silent mode on
                Lemmald lemmald = Lemmald.getInstance();
            }
            else // else require input and output files
            {

            }
        }
        else if( cmd.hasOption("train")){

        }
        else {
            printHelp();
        }
    }

    public static void runConsoleOld( String[] args ){
        if( args.length == 0 ){
            Lemmald lemmatizer = new Lemmald();
          //  lemmatizer.runLemmatizerTaggedStdin();
        }
        else if( args.length == 3 && args[0].equals("-train")){
            // train lemmatizer
            System.out.println("Training ...");

            String inputFile = args[1];
            String outputFile = args[2];

            Trainer trainer = new Trainer();
            trainer.trainLemmatizer( inputFile, outputFile );
            LemmaldUtils.print( "Done" );
        }
        else if( args.length == 4 && args[0].equals("-lemmatize")){

            String settingsFile = args[1];
            String inputFile = args[2];
            String outputFile = args[3];

            Lemmald main = new Lemmald();
       //     main.runLemmatizer(inputFile,outputFile);

            LemmaldUtils.print( "Done" );
        }
        else if( args.length == 4 && args[0].equals("-lemmatizeTagged")){
            String settingsFile = args[1];
            String inputFile = args[2];
            String outputFile = args[3];

            Lemmald main = new Lemmald();
          //  main.runLemmatizerTagged(inputFile,outputFile);

            LemmaldUtils.print( "Done" );
        }
        else if( args.length == 3 && args[0].equals("-lemmatizeFolder")){
            String settingsFile = args[1];
            String inputFolder = args[2];

           // Lemmald.getInstance().lemmatizeFolder(inputFolder);

            LemmaldUtils.print( "Done" );
        }
        else {
            System.out.println("Problem with arguments.");
        }
    }

    private static CommandLine parseArguments(Options options, String[] args) {
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException ex) {
            System.out.println("Problem with arguments!");
            printHelp();
        }
        return cmd;
    }

    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar Corpald.jar is.iclt.formald.ParseConverter", options);
    }


    public void runLemmatizer( String inputFile, String outputFile ){

        // Load input file to lemmatize
        LemmaldUtils.print("Loading input file");
        String texti = FileOperations.getContents( inputFile );
      //  texti = texti.replace("&nbsp", "");

        // Load IceNLP
        // System.out.println("Loading IceNLP");
        IceNLP iceNLP = IceNLP.getInstance();

        // Tag input
        LemmaldUtils.print("Pos-tagging input");
        ArrayList<Sentence> sentences = null;// iceNLP.tagText( texti );

        // Take time
        long start = System.currentTimeMillis();
        int wordcount = 0;
        LemmaldUtils.print("Lemmatizing ...");

        // Prepare output SB
        StringBuilder output = new StringBuilder();

        // Prepare variables
        Sentence sentence;
        Word word;
        String wordForm;
        LemmaResult lemmaResult;
        String line;

        for( int i=0; i<sentences.size(); i++ ){
            sentence = sentences.get(i);

            for( int j=0; j<sentence.size(); j++ ){
                word = sentence.get(j);
                wordForm = word.getWord().toLowerCase();
                lemmaResult = Lemmald.getInstance().lemmatize( wordForm, word.getTag() );

                line = wordForm+" "+word.getWord() +" "+lemmaResult.getLemma()+" "+ word.getTag().trim();

                if( line.length() != 0 ){
                    output.append( line + System.getProperty("line.separator") );
                }

                // Write count and time every 1000th word
                wordcount++;
                if( wordcount % 1000 == 0 ){
                    long curTime = System.currentTimeMillis() - start;
                    LemmaldUtils.print( "Done lemmatizing " + wordcount + " words (time:" + curTime + " ms)" );
                }
            }
            output.append( System.getProperty("line.separator") );
        }

        // Calculate total time
        long time = System.currentTimeMillis() - start;
        LemmaldUtils.print("Done lemmatizing "+wordcount+" words in "+time + " ms");

        // Write output to file
        FileOperations.writeContents( output.toString(), outputFile );
    }

    public void lemmatizeFolder( String folder ){

        File dir = new File( folder );
        File[] files = dir.listFiles();
        for( int i=0; i<files.length; i++ ){
            String path = files[i].getPath();
            if( path.endsWith(".txt")){
                System.out.println( "Lemmatizing: " + path );
                String outputPath = files[i].getParent() + "/lemma_" + files[i].getName();
                File outFile = new File(outputPath);
                if( !outFile.exists() && !files[i].getName().startsWith("lemma_") ){
                    runLemmatizer( path, outputPath );
                }
            }
        }
    }

    public void runLemmatizerTagged( String taggedInputFile, String outputFile ){

        LemmaldUtils.print("Loading tagged input");
        String inputData = FileOperations.getContents( taggedInputFile );
        StringBuilder outputData = new StringBuilder();
/*
        StringTokenizer lineTok = new StringTokenizer( inputData, System.getProperty("line.separator"), true );
        String currentLine;
        String currentWordForm;
        String currentTag;
        LemmaResult lemma;
        String[] lineTokens;

        while( lineTok.hasMoreTokens() ){
            currentLine = lineTok.nextToken();

//            outputData.append("L"+currentLine.length()+":");
            if( currentLine.equals( "\n" ) ){
                outputData.append( System.getProperty("line.separator") );
            }
            else {
                lineTokens = currentLine.split(" ");
                if( lineTokens.length == 2 ){
                     currentWordForm = lineTokens[0];
                     currentTag = lineTokens[1];
                     lemma = lemmatize( currentWordForm, currentTag );
                     outputData.append( currentLine + " " + lemma.getLemma() );
                }
            }
        }
 * */
        FileOperations.writeContents(outputData.toString(),outputFile);
    }

    public void runLemmatizerTaggedStdin(){
        Lemmald lemmald = Lemmald.getInstance();

        StringBuilder output = new StringBuilder();
        try {
            String rawInput = IOUtils.toString(System.in);
            String[] lines = rawInput.split( System.getProperty("line.separator") );
            for( String line:lines ){
                String[] lineTokens = line.trim().split(" ");
                if( lineTokens.length == 2 ){
                    output.append( line.trim() +  " " + lemmald.lemmatize( lineTokens[0], lineTokens[1] ).getLemma() );
                }
                output.append( System.getProperty("line.separator") );
            }

            System.out.print( output.toString() );
        } catch (IOException ex) {
            System.out.println("Could not read from Stdin!");
            ex.printStackTrace();
        }
    }

    public void runLemmatizerStdin(){

    }

    public static void main(String[] args) {    	
    	  System.out.println("INCOMPLETE Lemmald Runner, DO NOT USE!");
          runConsole( args );
    }
}
