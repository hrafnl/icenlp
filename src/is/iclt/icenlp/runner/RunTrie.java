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
package is.iclt.icenlp.runner;

import is.iclt.icenlp.core.utils.Trie;

import java.io.IOException;


/**
 * Test class for Trie
 * @author Hrafn Loftsson
 */
public class RunTrie {
   private static Trie myTrie;

   private static void testTrieDict()
   {
       myTrie = new Trie();
       myTrie.insert("a","1");
       myTrie.insert("ba","2");
       myTrie.insert("ba","3");
       myTrie.insert("bc","4");
       myTrie.insert("bcd","5");
       myTrie.insert("abc","6");
       myTrie.insert("abc","7");
       myTrie.insert("cd","8");
       myTrie.insert("cdef","9");
       myTrie.insert("abba","10");

       String value = myTrie.lookup("bcd", false);
       if (value != null)
       {
           System.out.println("Value for bcd: " + value);
       }
       value = myTrie.lookup("abcd", false);
       if (value == null)
       {
           System.out.println("Value not found for abcd:");
       }

   }

   private static void testTrie()
   {
       myTrie = new Trie();
       myTrie.insert("a");
       myTrie.insert("ba");
       myTrie.insert("ba");
       myTrie.insert("bc");
       myTrie.insert("bcd");
       myTrie.insert("abc");
       myTrie.insert("abc");
       myTrie.insert("cd");
       myTrie.insert("cdef");
       myTrie.insert("abba");
   }

   private static void testTrieFile(String fileName) throws IOException
   {
       myTrie = new Trie(fileName, true);
       //myTrie = new Trie(fileName, false);
       boolean found = myTrie.lookupSuffix("yfirhafinn", false, 10);
       if (found) {
           System.out.println("Found key: " + myTrie.getKeyFound());
           System.out.println("Found value: " + myTrie.getValueFound());
       }
       found = myTrie.lookupSuffix("stórifellir", false, 6);
       if (found) {
           System.out.println("Found key: " + myTrie.getKeyFound());
           System.out.println("Found value: " + myTrie.getValueFound());
       }
       found = myTrie.lookupSuffix("stórifellir", false, 4);
       if (!found) System.out.println("Not found: ");



   }

   private static void printStats()
   {
       int numChars = myTrie.numCharacters();
       System.out.println("# of characters: " + Integer.toString(numChars));
       int numStrings = myTrie.numStrings();
       System.out.println("# of strings: " + Integer.toString(numStrings));
   }

   public static void main(String[] args) throws IOException
   {
       //testTrie();
       //testTrieDict();
       testTrieFile(args[0]);
       
       myTrie.print();
       printStats();
   }
}
