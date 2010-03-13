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
package is.iclt.icenlp.core.icetagger;

import is.iclt.icenlp.core.utils.Lexicon;
import is.iclt.icenlp.core.utils.Idioms;
import is.iclt.icenlp.core.icemorphy.IceMorphyLexicons;

import java.io.InputStream;
import java.io.IOException;

/**
 * Encapslulates the lexicons used by IceTagger.
 * @author Hrafn Loftsson
 */
public class IceTaggerLexicons {
    // Default paths and file names
    public String dictPathTagger = "dict/icetagger/";
    public static String idiomsDictionary = "idioms.dict";
    public static String verbPrepDictionary = "otb.verbPrep.dict";
    public static String verbObjDictionary = "otb.verbObj.dict";
    public static String verbAdverbDictionary = "otb.verbAdverb.dict";
    // IceMorphy variables
    public IceMorphyLexicons morphyLexicons;
    public Lexicon verbPrep, verbObj, verbAdverb;
    public Idioms idioms;

    public IceTaggerLexicons(String taggerDictPath) throws IOException
    {
           if( taggerDictPath != null && !taggerDictPath.equals( "" ) )
               this.dictPathTagger = taggerDictPath;

           morphyLexicons = new IceMorphyLexicons(this.dictPathTagger);

           verbPrep = new Lexicon(this.dictPathTagger + verbPrepDictionary);
           verbObj =  new Lexicon(this.dictPathTagger + verbObjDictionary);
           verbAdverb =  new Lexicon(this.dictPathTagger + verbAdverbDictionary);
           idioms = new Idioms(this.dictPathTagger + idiomsDictionary);
    }

    
     public IceTaggerLexicons(String dictBaseFileWithPath,
                              String dictFileWithPath,
                              String endingsBaseFileWithPath,
                              String endingsWithFilePath,
                              String endingsProperFileWithPath,
                              String verbPrepWithFilePath,
                              String verbObjectWithFilePath,
                              String verbAdverbWithFilePath,
                              String idiomsFileWithPath,
                              String prefixesFileWithPath,
                              String frequencyFileWithPath
                              ) throws IOException
    {
           morphyLexicons = new IceMorphyLexicons(
                              dictFileWithPath,
                              dictBaseFileWithPath,
                              endingsBaseFileWithPath,
                              endingsWithFilePath,
                              endingsProperFileWithPath,
                              prefixesFileWithPath,
                              frequencyFileWithPath);
           verbPrep = new Lexicon(verbPrepWithFilePath);
           verbObj =  new Lexicon(verbObjectWithFilePath);
           verbAdverb =  new Lexicon(verbAdverbWithFilePath);
           idioms = new Idioms(idiomsFileWithPath);
    }

    public IceTaggerLexicons(   InputStream dictionaryBase_in,
                                InputStream dictionary_in,
                                InputStream endingsBase_in,
                                InputStream endings_in,
                                InputStream endingsProp_in,
                                InputStream verbPrep_in,
                                InputStream verbObj_in,
                                InputStream verbAdverb_in,
                                InputStream idomsDict_in,
                                InputStream prefixDict_in,
                                InputStream tagFrec_in
                                )
                throws IOException, NullPointerException  {

            morphyLexicons = new IceMorphyLexicons(
                                    dictionary_in,
                                    dictionaryBase_in,
                                    endingsBase_in,
                                    endings_in,
                                    endingsProp_in,
                                    prefixDict_in,
                                    tagFrec_in
                                );
            verbPrep = new Lexicon( verbPrep_in );
            verbObj = new Lexicon( verbObj_in );
            verbAdverb = new Lexicon( verbAdverb_in );
            idioms = new Idioms( idomsDict_in );
       }

    public IceTaggerLexicons(IceTaggerResources iceResources) throws IOException {
        this(iceResources.isDictionaryBase,
             iceResources.isDictionary,
             iceResources.isEndingsBase,
             iceResources.isEndings,
             iceResources.isEndingsProper,
             iceResources.isVerbPrep,
             iceResources.isVerbObj,
             iceResources.isVerbAdverb,
             iceResources.isIdioms,
             iceResources.isPrefixes,
             iceResources.isTagFrequency
                );
    }
}
