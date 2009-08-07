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
package is.iclt.icenlp.core.tritagger;

import is.iclt.icenlp.core.tritagger.Ngrams;
import is.iclt.icenlp.core.tritagger.FreqLexicon;

import java.io.IOException;
import java.io.InputStream;

/**
 * Encapslulates the lexicons used by TriTagger.
 * @author Hrafn Loftsson
 */
public class TriTaggerLexicons {
     public static String modelPath = "ngrams/models/";
     public static String modelName = "otb";
     public static String ngramsEnding = ".ngram";
     public static String lambdaEnding = ".lambda";
     public static String lexEnding = ".lex";

     public Ngrams ngrams;
     public FreqLexicon freqLexicon;

     public TriTaggerLexicons(String modelPath, boolean createSuffixes) throws IOException {

        if( modelPath != null && !modelPath.equals( "" ) )
           this.modelPath = modelPath;

        ngrams = new Ngrams( modelPath+modelName+ngramsEnding, modelPath+modelName+lambdaEnding);
		freqLexicon = new FreqLexicon( modelPath+modelName+lexEnding, FreqLexicon.formatFrequency, createSuffixes );
     }

    // The nameInPath parameter is just used to be able to use two constructors
    public TriTaggerLexicons(String modelNameWithPath, boolean nameInPath, boolean createSuffixes) throws IOException {
        ngrams = new Ngrams( modelNameWithPath+ngramsEnding, modelNameWithPath+lambdaEnding);
		freqLexicon = new FreqLexicon( modelNameWithPath+lexEnding, FreqLexicon.formatFrequency, createSuffixes );
    }

     public TriTaggerLexicons(InputStream ngrams_is, InputStream lambda_is, InputStream frequency_is, boolean createSuffixes) throws IOException
     {
        ngrams = new Ngrams( ngrams_is, lambda_is );
		freqLexicon = new FreqLexicon( frequency_is, FreqLexicon.formatFrequency, createSuffixes );
     }

     public TriTaggerLexicons(TriTaggerResources triResources, boolean createSuffixes) throws IOException {
        this(triResources.isNgrams, triResources.isLambda, triResources.isFrequency, createSuffixes);
     }

}
