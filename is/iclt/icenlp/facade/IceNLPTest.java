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

/**
 * A class for testing the IceNLP class.
 * @author Anton Karl Ingason
 */
public class IceNLPTest {


public static void main(String args[]) {
    String input = "Ég er rauður kaktus\n og ég ætla að syngja þetta lag.";
    String output;
    IceNLP theInstance = IceNLP.getInstance();

    // Tilreiðing
    output = theInstance.tokenize(input);
    System.out.println(output);

    // Orðhlutafræðileg greining
    output = theInstance.analyze(input);
    System.out.println(output);

    //Mörkun:
    output = theInstance.tag(input);
    System.out.println(output);

    //Mörkun og þáttun:
    output = theInstance.tagAndParse(input);
    System.out.println(output);


}

}