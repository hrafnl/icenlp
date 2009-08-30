/*
 * Copyright (C) 2009 Anton Karl Ingason
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
 * Anton Karl Ingason.
 * anton.karl.ingason@gmail.com
 */
package is.iclt.icenlp.facade;

import is.iclt.icenlp.core.formald.tags.TagFormat;

/**
 * A class for testing the IceNLP class.
 * @author Anton Karl Ingason
 */
public class IceNLPTest {


public static void main(String args[]) {
    String input = "Ég er a.m.k. rauður kaktus"+System.getProperty("line.separator")+" og ég ætla að syngja þetta lag.";
    String output;
    IceNLP theInstance = IceNLP.getInstance();

    // Tilreiðing
    output = theInstance.tokenize(input);
    System.out.println(output);

    // Orðhlutafræðileg greining
    output = theInstance.analyze(input);
    System.out.println(output);

    //Mörkun:
    output = theInstance.tagLines(input).toString(TagFormat.ICE2);
    System.out.println("TAG START");
    System.out.print(output);
    System.out.println("TAG END");

    //Mörkun:
    output = theInstance.tagAndLemmatizeText("Ég er nr. 1 í a.m.k. þessu leikriti. Allir eiga sinn tíma. Hún kom kl. 5 í dag.").toString(TagFormat.ICE2);
    
    System.out.println("TAGT START");
    System.out.print(output);
    System.out.println("TAGT END");
    
    
    //Mörkun og þáttun:
    System.out.println("Inputlines:" + input);
    
    output = theInstance.tagAndParseLines(input);
    System.out.println("ParseOutput:\n"+output);

    

}

}