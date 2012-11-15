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

import is.iclt.icenlp.core.icemorphy.IceMorphyLexicons;
import is.iclt.icenlp.core.icemorphy.IceMorphyResources;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Reads the resource files (lexicons) used by IceTagger from the .jar file.
 * @author Hrafn Loftsson
 */
public class IceTaggerResources {
   final String dictPathTagger = "/dict/icetagger/";
   public InputStream isIdioms, isVerbPrep,isVerbObj, isVerbAdverb;

    public IceTaggerResources() {
            isIdioms = getClass().getResourceAsStream( dictPathTagger + IceTaggerLexicons.idiomsDictionary );
            isVerbPrep = getClass().getResourceAsStream( dictPathTagger + IceTaggerLexicons.verbPrepDictionary );
            isVerbObj = getClass().getResourceAsStream( dictPathTagger + IceTaggerLexicons.verbObjDictionary );
            isVerbAdverb = getClass().getResourceAsStream( dictPathTagger + IceTaggerLexicons.verbAdverbDictionary );
            //System.out.println(getNumLines(isDictionary));
   }

   private int getNumLines(InputStream is) {

       int count = 0;
       try {
         BufferedReader bf = new BufferedReader(new InputStreamReader(is));

         while (bf.readLine() != null)
            count++;
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return count;
    }



}
