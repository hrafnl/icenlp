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

import is.iclt.icenlp.facade.IceTaggerFacade;
import is.iclt.icenlp.facade.TriTaggerFacade;
import is.iclt.icenlp.facade.IceParserFacade;
import is.iclt.icenlp.core.icetagger.IceTaggerLexicons;
import is.iclt.icenlp.core.icetagger.IceTagger;
import is.iclt.icenlp.core.tritagger.TriTaggerLexicons;
import is.iclt.icenlp.core.tokenizer.Sentences;
import is.iclt.icenlp.core.utils.Lexicon;
import is.iclt.icenlp.core.utils.FileEncoding;

import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.regex.Pattern;

/**
 * An application with GUI to tag and parse Icelandic text.
 * @author Hrafn Loftsson
 */
public class TagAndParse implements ActionListener {
    private static JFrame frame;
    //private static JButton quitButton;
    private JTextArea textInput;
    private JTextArea textTagged;
    private JTextArea textParsed;
    private JCheckBox checkIncludeFunc, checkPhrasePerLine, checkTokenPerLine;
    private JCheckBox checkPotentialErrors, checkFeatureAgreement;
    // checkTriTagger=true if use TriTagger with IceTagger
    // checkOnlYTriTagger=true if only use TriTagger for tagging (not IceTagger)
    private JCheckBox checkTriTagger, checkOnlyTriTagger;
    private IceTaggerFacade tagger;
    private TriTaggerFacade tritagger;
    private IceParserFacade parser;
    private String modelPath="../../ngrams/models/";


   public TagAndParse() throws IOException
   {
        System.out.println("Loading dictionaries ...");

        // When running IceTagger without help from the HMM model then supply the parameter IceTagger.HmmModelType.none
        tagger = new IceTaggerFacade(IceTagger.HmmModelType.startend);
        tritagger = new TriTaggerFacade();
        parser = new IceParserFacade();

   }

    // Only used for debugging
    public TagAndParse(boolean debug) throws IOException
    {
        System.out.println("Loading dictionaries ...");
        Lexicon tokLexicon = new Lexicon("../../dict/tokenizer/lexicon.txt");

        IceTaggerLexicons iceLexicons = new IceTaggerLexicons("../../dict/icetagger/");

        tagger = new IceTaggerFacade(iceLexicons, tokLexicon);

        // Create an instance of TriTagger
        System.out.println("Loading TriTagger ...");
        TriTaggerLexicons triLexicons = new TriTaggerLexicons(modelPath, false);
        tagger.createTriTagger(triLexicons);

        parser = new IceParserFacade();

   }

    private void createAndShowGUI(String fileName) throws IOException
    {
            //Make sure we have nice window decorations.
            JFrame.setDefaultLookAndFeelDecorated(true);

            //Create and set up the window.
            frame = new JFrame("Tagging (IceTagger/TriTagger) and parsing (IceParser) of Icelandic text");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setBounds(30,30,200,300);

            createComponents(frame.getContentPane());
            //frame.getContentPane().add(contents, BorderLayout.CENTER);
            if (fileName != null)
                 loadFile(fileName);

            //Display the window.
            frame.pack();
            frame.setVisible(true);
    }

    // Inserts new line between every word/tag pair
    private String setNewlines(String taggedStr)
    {
       StringBuffer buf = new StringBuffer();
       Pattern p = Pattern.compile(" ");
       String[] strs;
       strs = p.split(taggedStr);
       for (int i=0; i<=strs.length-1; i++)
       {
          buf.append(strs[i]);
          if (i%2 == 1 && i!=strs.length-1)
            buf.append("\n");
          else
            buf.append(" ");
       }
       return buf.toString();
    }

    public void actionPerformed(ActionEvent e) {
       String taggedStr=null;
       if (e.getActionCommand().equals("Analyse"))
       {
           try {
                Sentences sents = tagText(textInput.getText());
                taggedStr = sents.toString();
           }
           catch (IOException ex) {System.err.println("IOException: " + ex.getMessage()); }

           if (taggedStr != null) {
                if (checkTokenPerLine.isSelected())
                    textTagged.setText(setNewlines(taggedStr));
                else
                    textTagged.setText(taggedStr);
                textTagged.setCaretPosition(0);

               // Redding í bili.  Finna þarf út úr því hvernig action er sett upp fyrir checkBox
                if (checkPotentialErrors.isSelected())
                    checkFeatureAgreement.setSelected(true);

                try {
                    String parsedStr = parser.parse(taggedStr, checkIncludeFunc.isSelected(), checkPhrasePerLine.isSelected(), checkFeatureAgreement.isSelected(), checkPotentialErrors.isSelected());
                    textParsed.setText(parsedStr);
                    textParsed.setCaretPosition(0);
                }
                catch (IOException ex) {System.err.println("IOException: " + ex.getMessage()); }
           }
       }
       /*else if (e.getActionCommand().equals("Errors"))
       {
           if (checkPotentialErrors.isSelected())
               checkFeatureAgreement.setSelected(true);
       } */
       
       else if (e.getActionCommand().equals("Stop"))
         System.exit(0);
   }

    public void createComponents(Container pane) {
                JButton tagButton = new JButton("Analyse text");
                tagButton.setMnemonic(KeyEvent.VK_I);
                tagButton.addActionListener(this);
                tagButton.setPreferredSize(new Dimension(20,20));
                tagButton.setActionCommand("Analyse");

                int rows=10;
                int cols=100;

                Dimension textDimension = new Dimension(300,300);
                Font myFont = new Font("Courier", Font.PLAIN, 16);
                textInput = new JTextArea(
                            "",rows,cols
                    );
                textInput.setLineWrap(true);
                textInput.setWrapStyleWord(true);
                textInput.setFont(myFont);
                textInput.setPreferredSize(textDimension);

                JScrollPane scrollingArea = new JScrollPane(textInput);
                //scrollingArea.setBorder(BorderFactory.createEmptyBorder(30,30,100,100));

                textTagged = new JTextArea("",rows,cols);
                textTagged.setLineWrap(true);
                textTagged.setWrapStyleWord(true);
                textTagged.setFont(myFont);
                textTagged.setEditable(false);
                JScrollPane scrollingArea2 = new JScrollPane(textTagged);
                //textTagged.setPreferredSize(textDimension);

                textParsed = new JTextArea("",rows,cols);
                textParsed.setLineWrap(true);
                textParsed.setWrapStyleWord(true);
                textParsed.setFont(myFont);
                textParsed.setEditable(false);
                JScrollPane scrollingArea3 = new JScrollPane(textParsed);
                //textParsed.setPreferredSize(textDimension);

                checkTriTagger = new JCheckBox("Use an HMM tagger with IceTagger");
                checkTriTagger.setSelected(true); // turn  the check box on or off
                checkOnlyTriTagger = new JCheckBox("Only use an HMM tagger");
                checkOnlyTriTagger.setSelected(false); // turn  the check box on or off
                checkIncludeFunc = new JCheckBox("Syntactic functions");
                checkIncludeFunc.setSelected(true); // turn  the check box on or off
                checkPhrasePerLine = new JCheckBox("Phrases per line");
                checkPhrasePerLine.setSelected(true); // turn  the check box on or off
                checkTokenPerLine = new JCheckBox("Word/tag per line");
                checkTokenPerLine.setSelected(false); // turn  the check box on or off
                checkPotentialErrors = new JCheckBox("Mark grammatical errors");
                checkPotentialErrors.setSelected(false); // turn  the check box on or off
                //checkPotentialErrors.addActionListener();
                checkPotentialErrors.setActionCommand("Errors");
                checkFeatureAgreement = new JCheckBox("Rely on feature agreement");
                checkFeatureAgreement.setSelected(false); // turn  the check box on or off


                JLabel labAnalyse = new JLabel("Text to analyse:");
                JLabel labTagging = new JLabel("Tagged text:");
                JLabel labParsing = new JLabel("Parsed text:");
                //JLabel labDummy = new JLabel("");


                pane.setLayout(new BoxLayout(frame.getContentPane(),BoxLayout.Y_AXIS));

                JPanel checkBoxPanel = new JPanel(new FlowLayout());
                checkBoxPanel.add(checkTriTagger);
                checkBoxPanel.add(checkOnlyTriTagger);
                checkBoxPanel.add(checkTokenPerLine);

                JPanel checkBoxPanel2 = new JPanel(new FlowLayout());
                checkBoxPanel2.add(checkIncludeFunc);
                checkBoxPanel2.add(checkPhrasePerLine);

                JPanel checkBoxPanel3 = new JPanel(new FlowLayout());
                checkBoxPanel3.add(checkFeatureAgreement);
                checkBoxPanel3.add(checkPotentialErrors);

                pane.add(checkBoxPanel, BorderLayout.CENTER);
                pane.add(checkBoxPanel2, BorderLayout.CENTER);
                pane.add(checkBoxPanel3, BorderLayout.CENTER);
                pane.add(tagButton, BorderLayout.CENTER);
                //pane.add(labDummy, BorderLayout.CENTER);
                pane.add(labAnalyse, BorderLayout.CENTER);
                pane.add(scrollingArea,BorderLayout.CENTER);
                pane.add(labTagging, BorderLayout.WEST);
                pane.add(scrollingArea2,BorderLayout.CENTER);
                pane.add(labParsing, BorderLayout.WEST);
                pane.add(scrollingArea3,BorderLayout.CENTER);
            }




   public Sentences tagText(String text) throws IOException
   {
        if (checkOnlyTriTagger.isSelected())
            return tritagger.tag(text);
        else {
            tagger.useTriTagger(checkTriTagger.isSelected());   // use TriTagger with IceTagger?
            return tagger.tag(text);
        }
   }

   private void loadFile(String fileName) throws IOException{
    String str;

    textInput.setText("");
    BufferedReader bf = FileEncoding.getReader(fileName);

    while((str = bf.readLine()) != null) {
            textInput.append(str);
    }
    bf.close();
   }

   // Read in command line args
   public static void main(String[] args)
   throws IOException {

       //TagAndParse app = new TagAndParse();
       TagAndParse app = new TagAndParse(true);

       if (args.length == 1) {
          app.createAndShowGUI(args[0]);
       }
       else {
          app.createAndShowGUI(null);
        }

   }
}
