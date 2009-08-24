/* LanguageTool, a natural language style checker
 * Copyright (C) 2009 Daniel Naber (http://www.danielnaber.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */

package is.iclt.icenlp.core.tokenizer;

import is.iclt.icenlp.core.formald.FormaldUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import net.sourceforge.segment.TextIterator;
import net.sourceforge.segment.srx.SrxDocument;
import net.sourceforge.segment.srx.SrxParser;
import net.sourceforge.segment.srx.SrxTextIterator;
import net.sourceforge.segment.srx.io.Srx2Parser;
// import de.danielnaber.languagetool.tools.Tools;

/**
 * Class to tokenize sentences using an SRX file.
 *
 * @author Marcin Miłkowski 
 * @author Anton Karl Ingason // minor modifications to adapt this to IceNLP
 */
public class SrxSegmentizer {

  private volatile static SrxSegmentizer uniqueInstance;

  private BufferedReader srxReader;
  private final SrxDocument document;
  private final String language;
  private String parCode;

  static final String RULES = "/dict/formald/segment.srx";

  public SrxSegmentizer(final String language) {
    this.language = language;
    try {
      srxReader = new BufferedReader(new InputStreamReader( Object.class.getClass().getResourceAsStream( RULES ) ));
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    final SrxParser srxParser = new Srx2Parser();
    document = srxParser.parse(srxReader);
    setSingleLineBreaksMarksParagraph(false);
  }

  public final List<String> tokenize(final String text) {
    final List<String> segments = new ArrayList<String>();
    final TextIterator textIterator = new SrxTextIterator(document, language + parCode, text);
    while (textIterator.hasNext()) {
      segments.add(textIterator.next());
    }
    return segments;
  }

  public final String sentencePerLine( final String text ){
    final List<String> sentences = tokenize( text );
    StringBuilder sb = new StringBuilder();
    for( String sentence : sentences ){
        sb.append( sentence.trim() + System.getProperty("line.separator"));
    }
    return sb.toString();
  }

  public final boolean singleLineBreaksMarksPara() {
    return "_one".equals(parCode);
  }

  /**
   * @param lineBreakParagraphs
   *          if <code>true</code>, single lines breaks are assumed to end a
   *          paragraph, with <code>false</code>, only two ore more consecutive
   *          line breaks end a paragraph
   */
  public final void setSingleLineBreaksMarksParagraph(
      final boolean lineBreakParagraphs) {
    if (lineBreakParagraphs) {
      parCode = "_one";
    } else {
      parCode = "_two";
    }
  }

  @Override
  protected final void finalize() throws Throwable {
    if (srxReader != null) {
      srxReader.close();
    }
    super.finalize();
  }

  public static SrxSegmentizer newInstance(){
    return new SrxSegmentizer("is");
  }

  public static SrxSegmentizer getInstance(){
        if( uniqueInstance == null ){
            synchronized( SrxSegmentizer.class ){
                if( uniqueInstance == null ){
                    uniqueInstance = new SrxSegmentizer("is");
                }
            }
        }
        return uniqueInstance;
  }

  public static void terminate(){
        uniqueInstance = null;
  }
  
  public void runStdin(){
	  
      StringBuilder output = new StringBuilder();
      try {
          String input = IOUtils.toString(System.in);
          
          List<String> sentences = tokenize(input);
          for( String sentence : sentences ){
               output.append( sentence );
               output.append( System.getProperty("line.separator") );
          }	            
          System.out.print( output.toString().trim() + System.getProperty("line.separator") );
      } catch (IOException ex) {
          System.out.println("Could not read from Stdin!");
          ex.printStackTrace();
      }
      	  
  }
  
  public void runTextFiles( String inputFile, String outputFile ){
	  String input = FormaldUtils.fileToString(inputFile);
	  
	  StringBuilder output = new StringBuilder();
	  List<String> sentences = tokenize(input);
      for( String sentence : sentences ){
           output.append( sentence );
           output.append( System.getProperty("line.separator") );
      }	            
      
	  FormaldUtils.stringToFile(outputFile, output.toString().trim() );
  }
  
  public static void main( String[] args ){
  	  // String test = "'\"Dæmi um texta: Nú er 16. des og 2. jan og 12. jan og 17. des og 1. febrúar og 8. desember.\"' og 7.7.2009 og fleira. Það pr. 1.p. og. það er þt. m.p.m.p. A.K.K.K. Svaka dót. Og U.S.A. skv. ja. Eitt. (Og annað). Jaja (!) vei. Það [í alvöru!] virkar. Það er þ. á m. þ.á.m. þf. sþ. að t.a.m. st. er. Fór Rvk. í Siglufj. og Rvík. þar. Það gott p.r. og PR. mama ófn. með óákv.gr. o.þ.u.l. er o.fl. með o.s.frv. verulega hér. Mín p.s. leturbr. o.m.fl. því að lh.þt. er með 4 ltr. af efni. Það var kgúrsk. um klst. frá Khöfn. í kl. 5 í gær. Frá Ólafsfj. í gær. Í Árneshr. og Hrunamannahr. erum við. Það er hr. ég hérna. Við viljum u-hljv. og i-hljóðv. í hvelli í 1.hl. og 2. hl. alltaf. Ég er fyrrv. dr. Anton gaur. Á bls. 5 er b.t. þín. Sögn í bh. er hér. Er ás. Með ákv.gr. og ákv. gr. hér. Með aukaf. og ao. og aths. þar. Lítill alm. alg. andh. ath. efst. Bara afs. Jens. Hér er a.fn. og. Sögn í 1.p. og 3.p. og líka og ab.fn. en ekki afffn er sko 1. persóna í lagi. Er o.þ.u.l. ab.fn. hér og a.n.l. góður? Sí og æ. Það er a. með þ. sem ö. og við förum í. Það og m.a. svo að þeir eru hér. Og þ.l. og a.m.k. gott. Nú er 1. ágúst og 11. ágúst í dag. Þú ert t.d. í því. Ég er Nr. 1 í þessu NR. 1 dæmi. Flott mál. ";
	  
       SrxSegmentizer sentenceTokenizer = SrxSegmentizer.newInstance();
              
       if( args.length == 0 ){      
    	   sentenceTokenizer.runStdin();
       }
       else if ( args.length == 2 ){
    	   sentenceTokenizer.runTextFiles( args[0], args[1] );
       }
       else {
    	   System.out.println("Problem with arguments");
    	   return;
       }       
  }

}