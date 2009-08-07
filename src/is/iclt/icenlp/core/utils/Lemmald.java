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
package is.iclt.icenlp.core.utils;

import is.knowledge.lemmatizer.LemmaResult;
import is.knowledge.lemmatizer.Lemmatizer;

/**
 * A wrapper around a lemmatizer.
 * @author Hrafn Loftsson
 */
public class Lemmald {
    private Lemmatizer myLemmatizer;

    public Lemmald(String file)
    {
        myLemmatizer = new Lemmatizer(file);
    }

    private String fixLemma(String lemma)
    {
        int index = lemma.indexOf('/'); // éta/eta
        if (index == -1)
            index = lemma.indexOf('$'); // sökkva$^1$
        if (index != -1)
            lemma = lemma.substring(0,index);
        return lemma;
    }


    public String getLemma(String lexeme, String tag)
    {
        LemmaResult lemma = myLemmatizer.lemmatize(lexeme,tag);
        String theLemma = lemma.getLemma();
        theLemma = fixLemma(theLemma);
        return theLemma;
    }
}
