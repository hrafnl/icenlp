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

import is.iclt.icenlp.core.utils.IceTag;

/**
 * Created by IntelliJ IDEA.
 * User: hrafn
 * Date: 22.11.2009
 * Time: 17:20:46
 * To change this template use File | Settings | File Templates.
 */
public class MorphoRuleVerb extends MorphoRule {
    public enum Mood {Indicative, Subjunctive, Imperative, IndicativeSubjunctive, Infinitive};
    public enum Voice {Active, Middle};

    Mood mood;
    Voice voice;
    char tense;

    public MorphoRuleVerb() {

    }

    public MorphoRuleVerb(String ending, int subtractForLookup, IceMorphy.MorphoClass morphoClass, Mood mood, Voice voice, char tense) {
        super(ending, subtractForLookup, morphoClass);
        this.mood = mood;
        this.voice = voice;
        this.tense = tense;
    }
}
