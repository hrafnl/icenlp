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
    public Lexicon verbPrep, verbObj, verbAdverb;
    public Idioms idioms;

    public IceTaggerLexicons(String taggerDictPath) throws IOException
    {
           if( taggerDictPath != null && !taggerDictPath.equals( "" ) )
               this.dictPathTagger = taggerDictPath;

           verbPrep = new Lexicon(this.dictPathTagger + verbPrepDictionary);
           verbObj =  new Lexicon(this.dictPathTagger + verbObjDictionary);
           verbAdverb =  new Lexicon(this.dictPathTagger + verbAdverbDictionary);
           idioms = new Idioms(this.dictPathTagger + idiomsDictionary);
    }

    public IceTaggerLexicons(String verbPrepWithFilePath,
                             String verbObjectWithFilePath,
                             String verbAdverbWithFilePath,
                             String idiomsFileWithPath
    ) throws IOException
    {
           verbPrep = new Lexicon(verbPrepWithFilePath);
           verbObj =  new Lexicon(verbObjectWithFilePath);
           verbAdverb =  new Lexicon(verbAdverbWithFilePath);
           idioms = new Idioms(idiomsFileWithPath);
    }

    public IceTaggerLexicons(
                                InputStream verbPrep_in,
                                InputStream verbObj_in,
                                InputStream verbAdverb_in,
                                InputStream idomsDict_in
                                )
                throws IOException, NullPointerException  {

            verbPrep = new Lexicon( verbPrep_in );
            verbObj = new Lexicon( verbObj_in );
            verbAdverb = new Lexicon( verbAdverb_in );
            idioms = new Idioms( idomsDict_in );
       }

    public IceTaggerLexicons(IceTaggerResources iceResources) throws IOException {
        this(   iceResources.isVerbPrep,
                iceResources.isVerbObj,
                iceResources.isVerbAdverb,
                iceResources.isIdioms
        );
    }
}
