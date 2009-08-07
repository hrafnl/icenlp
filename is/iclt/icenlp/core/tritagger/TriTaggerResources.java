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

import java.io.InputStream;

/**
 * Reads the resource files (lexicons) used by TriTagger from the .jar file.
 * @author Hrafn Loftsson
 */
public class TriTaggerResources {
     final String modelPath = "/ngrams/models/";
     final String modelName = "otb";
     final String ngramsEnding = ".ngram";
     final String lambdaEnding = ".lambda";
     final String lexEnding = ".lex";

    public InputStream isNgrams,isLambda,isFrequency;

   public TriTaggerResources() {
            isNgrams = getClass().getResourceAsStream( modelPath+modelName+ngramsEnding );
            isLambda = getClass().getResourceAsStream( modelPath+modelName+lambdaEnding );
            isFrequency = getClass().getResourceAsStream( modelPath+modelName+lexEnding );
   }
}
