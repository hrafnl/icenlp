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

import is.iclt.icenlp.core.tokenizer.TokenTags;
import is.iclt.icenlp.core.tokenizer.Segmentizer;

/**
 * Generates the output for TriTagger.
 * @author Hrafn Loftsson
 */
public class TriTaggerOutput {
    protected int outputFormat = Segmentizer.tokenPerLine;
    private static final String unknownStr="<UNKNOWN>";

    public TriTaggerOutput(int outFormat)
    {
        outputFormat = outFormat;
    }


    public String buildOutput( TokenTags tok, int index, int numTokens ) {
         String str;

        if (outputFormat == Segmentizer.tokenPerLine)
            str = tok.lexeme + " " + tok.getFirstTagStr();
        else {
            str = tok.lexeme + " " + tok.getFirstTagStr();
            if (index < numTokens-1)
                str = str + " ";
        }

       if (tok.isUnknown())
       {
          if (outputFormat == Segmentizer.tokenPerLine)
            str = str + " " + unknownStr;
       }
       return str;
     }
}
