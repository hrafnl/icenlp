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

import is.iclt.icenlp.core.tokenizer.IceTokenTags;
import is.iclt.icenlp.core.lemmald.LemmaResult;

import java.io.IOException;

/**
 * Generates the output for IceTagger in Apertium format.
 * @author Hrafn Loftsson
 */
public class IceTaggerOutputApertium extends IceTaggerOutput{

    public IceTaggerOutputApertium(int outFormat, String wordTagSeparator, boolean useFullOutput, boolean useFullDisambiguation, String tagMapFile, boolean showLemma) throws IOException
    {
        super(outFormat, wordTagSeparator, useFullOutput, useFullDisambiguation, tagMapFile, showLemma);
    }

    public String buildOutput( IceTokenTags tok, int index, int numTokens )
    {
        String str, tag, mappedTag;
        String lemma="";

        tag = tok.getFirstTagStr();

        if (myTagMap != null)
            mappedTag = getMappedTag(tag);
        else
            mappedTag = tag;

        // Add the lemma?
        if (myLemmald != null) {
            LemmaResult lemmaResult = myLemmald.lemmatize(tok.lexeme,tag);
            lemma = lemmaResult.getLemma();
            //lemma = myLemmald.getLemma(tok.lexeme, tag);
        }

        //str = "^" + tok.lexeme + "/" + lemma + mappedTag + "$ ";
        // Apertium þarf ekki orðið sjálft, eingöngu lemmuna
        str = "^" + lemma + mappedTag + "$ ";
        return str;
    }
}
