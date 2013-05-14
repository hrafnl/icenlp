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
package is.iclt.icenlp.core.icemorphy;

import is.iclt.icenlp.core.utils.Lexicon;
import is.iclt.icenlp.core.utils.WordList;
import is.iclt.icenlp.core.utils.Trie;
import is.iclt.icenlp.core.icetagger.IceFrequency;

import java.io.IOException;
import java.io.InputStream;

 /**
 * Encapslulates the lexicons used by IceMorphy.
 * @author Hrafn Loftsson
 */
public class IceMorphyLexicons {
// Default paths and file names
    public String dictPathTagger = "dict/icetagger/";
    //public static String dictionary = "otbBinSmall.dict";
    public static String dictionary = "otb.dict";
    //public static String dictionary = "otbBin.dict";
     public static String dictionaryBase = "baseDict.dict";
    // IceMorphy variables
    public static String endingsDictionary = "otb.endings.dict";
    public static String endingsProperDictionary = "otb.endingsProper.dict";
    public static String endingsBaseDictionary = "baseEndings.dict";
    public static String prefixesDictionary = "prefixes.dict";
    public static String tagFrequencyFile = "otbTags.freq.dict";

    public Lexicon dict;
    public Lexicon baseDict;
    public Trie endingsBase;
    public Trie endings;
    public Trie endingsProper;
    public WordList prefixes;
    public IceFrequency tagFrequency;

    public IceMorphyLexicons(String taggerDictPath) throws IOException {
           if( taggerDictPath != null && !taggerDictPath.equals( "" ) )
               this.dictPathTagger = taggerDictPath;

           dict = new Lexicon( this.dictPathTagger + dictionary);
           baseDict = new Lexicon( this.dictPathTagger + dictionaryBase);
           endingsBase = new Trie(this.dictPathTagger + endingsBaseDictionary, true);
           endings = new Trie(this.dictPathTagger + endingsDictionary, true);
           endingsProper = new Trie(this.dictPathTagger + endingsProperDictionary, true);
           prefixes = new WordList(this.dictPathTagger + prefixesDictionary);
           tagFrequency = new IceFrequency(this.dictPathTagger + tagFrequencyFile);
    }

     public IceMorphyLexicons(String dictBaseWithFilePath,
                              String dictWithFilePath,
                              String endingsBaseFileWithPath,
                              String endingsWithFilePath,
                              String endingsProperFileWithPath,
                              String prefixesFileWithPath,
                              String frequencyFileWithPath
                              ) throws IOException
    {
           if (dictBaseWithFilePath != null)
                baseDict = new Lexicon(dictBaseWithFilePath);
           if (dictWithFilePath != null)
                dict = new Lexicon(dictWithFilePath);
           if (endingsBaseFileWithPath != null)
                endingsBase = new Trie(endingsBaseFileWithPath, true);
           if (endingsWithFilePath != null)
                endings = new Trie(endingsWithFilePath, true);
           if (endingsProperFileWithPath != null)
                endingsProper = new Trie(endingsProperFileWithPath, true);
           if (prefixesFileWithPath != null)
                prefixes = new WordList(prefixesFileWithPath);
           if (frequencyFileWithPath != null)
                tagFrequency = new IceFrequency(frequencyFileWithPath);
    }

    public IceMorphyLexicons(   InputStream dictionaryBase_in,
                                InputStream dictionary_in,
                                InputStream endingsBase_in,
                                InputStream endings_in,
                                InputStream endingsProp_in,
                                InputStream prefixDict_in,
                                InputStream tagFrec_in
                                )
                throws IOException, NullPointerException  {

            baseDict = new Lexicon( dictionaryBase_in );
            dict = new Lexicon( dictionary_in );
            endingsBase = new Trie( endingsBase_in, true);
            endings = new Trie( endings_in, true);
            endingsProper = new Trie( endingsProp_in, true);
            prefixes = new WordList( prefixDict_in );
            if (tagFrec_in != null)
                tagFrequency = new IceFrequency( tagFrec_in );
       }


     public IceMorphyLexicons(IceMorphyResources morphyResources) throws IOException {
         this(   morphyResources.isDictionaryBase,
                 morphyResources.isDictionary,
                 morphyResources.isEndingsBase,
                 morphyResources.isEndings,
                 morphyResources.isEndingsProper,
                 morphyResources.isPrefixes,
                 morphyResources.isTagFrequency
         );
     }
}
