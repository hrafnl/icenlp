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
package is.iclt.icenlp.core.tokenizer;

import java.io.InputStream;

/**
 * Reads the resource file (a lexicon) used by Tokenizer from the .jar file.
 * @author Hrafn Loftsson
 */
public class TokenizerResources {
   final String dictPath = "/dict/tokenizer/";
   final String lexFileName = "lexicon.txt";
   public InputStream isLexicon;

   public TokenizerResources() {
       isLexicon = getClass().getResourceAsStream( dictPath + lexFileName );

   }
}
