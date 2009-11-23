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
 * Time: 16:55:27
 * To change this template use File | Settings | File Templates.
 */
public class MorphoRuleNounAdjective extends MorphoRule {
    IceTag.WordClass wordClass;
    boolean nominative;
    boolean accusative;
    boolean dative;
    boolean genitive;
    char gender;
    char number;
    char declension;


public MorphoRuleNounAdjective(String ending, int subtractForLookup, IceMorphy.MorphoClass morphoClass,
                        boolean nominative, boolean accusative, boolean dative, boolean genitive,
                        char gender, char number) {

        super(ending, subtractForLookup, morphoClass);
        this.nominative = nominative;
        this.accusative = accusative;
        this.dative = dative;
        this.genitive = genitive;
        this.gender = gender;
        this.number = number;
    }

    public MorphoRuleNounAdjective(String ending, int subtractForLookup, IceMorphy.MorphoClass morphoClass, IceTag.WordClass wordClass,
                        boolean nominative, boolean accusative, boolean dative, boolean genitive,
                        char gender, char number, char declension) {

        this(ending, subtractForLookup, morphoClass, nominative, accusative, dative, genitive, gender, number);
        this.wordClass = wordClass;
        this.declension = declension;
    }

    public MorphoRuleNounAdjective(String ending, int subtractForLookup, IceMorphy.MorphoClass morphoClass, IceTag.WordClass wordClass,
                        boolean nominative, boolean accusative, boolean dative, boolean genitive,
                        char gender, char number, char declension, boolean searchAgain) {

        this(ending, subtractForLookup, morphoClass, nominative, accusative, dative, genitive, gender, number);
        this.wordClass = wordClass;
        this.declension = declension;
        this.searchAgainWhenFound = searchAgain;
    }

}
