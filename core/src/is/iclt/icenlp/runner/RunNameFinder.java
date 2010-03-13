/*
 * Copyright (C) 2009 Aðalsteinn Tryggvason
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

package is.iclt.icenlp.runner;

import is.iclt.icenlp.core.iceNER.NameSearcher;

/**
 * Runs Named Entity Recognition.
 * @author Aðalsteinn Tryggvason
 */
public class RunNameFinder {

    /**
     * @param args the command line arguments
     */


    public static void main(String args[])
	{
        try
        {
            int nParamCount = args.length;
            boolean bFormOverFunction = true;
            boolean bGready = false;

            NameSearcher searcer = new NameSearcher(bFormOverFunction);
             /**/
            //Greedy with gazett list
            if(nParamCount==8)
            {
                searcer.loadSearcher(args[0], Integer.parseInt(args[1]),args[2], Integer.parseInt(args[3]), args[4], args[6], Integer.parseInt(args[5]));
                bGready = true;
            }
            //With gazett list
            else if(nParamCount==7)
            {
                searcer.loadSearcher(args[0], Integer.parseInt(args[1]),args[2], Integer.parseInt(args[3]), args[4], args[6], Integer.parseInt(args[5]));
            }
            //Greedy
            else if(nParamCount==6)
            {
                searcer.loadSearcher(args[0], Integer.parseInt(args[1]),args[2], Integer.parseInt(args[3]), args[4]);
                bGready = true;
            }
            //Default
            else
            {
                searcer.loadSearcher(args[0], Integer.parseInt(args[1]),args[2], Integer.parseInt(args[3]), args[4]);
            }
            /**/
            //searcer.loadSearcher("C:\\Loka\\Tagged.out",5639,"C:\\Loka\\scan.out", 650, "C:\\Loka\\doc.out", "C:\\Loka\\location.txt",527);
            //searcer.loadSearcher("C:\\Loka\\Tagged.out",1915,"C:\\Loka\\scan.out", 206, "C:\\Loka\\doc.out" );
            //searcer.loadSearcher("C:\\Loka\\Tagged.out",106,"C:\\Loka\\scan.out",650, "C:\\Loka\\doc.out" );

            searcer.indexNamedEntity();
            //searcer.listAllIndexedNames();
            if(bFormOverFunction)
            {
                bFormOverFunction = true;
                searcer.findPersonFromRole();
                searcer.markFromHash(bFormOverFunction);
                searcer.lookForDifferentCases();
                searcer.findFromRole();
            }
            else
            {
                searcer.findPersonFromRole();
                searcer.markFromHash(bFormOverFunction);
                searcer.lookForDifferentCases();
                searcer.findFromRole();
            }
            searcer.findPersonByAge();
            searcer.markFromHash(true);
            searcer.findFromInitilas();
            searcer.lookForMissedCompanies();
            searcer.looForPairs();
    //      searcer.lookForUnmatchedFromRole();
    //      searcer.lookForNounInName();
            searcer.findPersonFromLocation();
            searcer.lookForListings();
            //searcer.findCompanyByLocation();
            searcer.markFromHash(true);
            searcer.fixPersonNames();
            searcer.fixCompanyLocation();
            //searcer.lookForUnmatchedFromHash();
            searcer.completePartlyFoundNames();
            //searcer.findFromCapitalLetters();
            searcer.lookForAdjectiveInName();
            searcer.cleanup();
            if(bGready)
            {
                searcer.cleanupPersons();
                searcer.findFromAction();
                searcer.findLocationsFromAdverb();
                searcer.markFromHash(true);
            }
            //searcer.markMiscEntities();
            searcer.printMarkedText();
        }
        catch (Exception ex)
        {
            System.out.println(ex);
            System.out.println("Error!");
            System.out.println("The names were not marked");
        }
    }
}
