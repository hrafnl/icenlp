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

import java.util.ArrayList;

/**
 * A node in a Trie.
 * @author Hrafn Loftsson
 */
public class TrieNode {
    char label;
    boolean endOfWord;
    String value;
    //int value;
    ArrayList nextNodes;

public TrieNode()
{
   label = ' ';
   endOfWord = false;
   value = null;
   //value = -1;
   nextNodes = null;
}


public TrieNode(char aChar, boolean endWord)
{
   this();
   label = aChar;
   endOfWord = endWord;
}


public TrieNode(char aChar, boolean endWord, String aValue)
{
   label = aChar;
   endOfWord = endWord;

   if (endWord && aValue != null)
        value = aValue;
   else
        value = null;
   nextNodes = null;
}

public ArrayList getNextNodes()
{
    return nextNodes;
}

public char getLabel()
{
    return label;
}

public String getValue()
//public int getValue()
{
   return value;
}

//public void setValue(int val)
public void setValue(String val)
{
    value = val;
}

/*
* Looking for a subnode of the current node accessible with chr
*/
public TrieNode findSubNode(char chr)
{
    int len = 0;

    if (nextNodes != null)
        len = nextNodes.size();
    else
        return null;

    for (int i=0; i<len; i++)
    {
       TrieNode node = (TrieNode)nextNodes.get(i);
       if (node.getLabel() == chr)
            return node;
    }
    return null;
}

public void addSubNode(TrieNode node)
{
   if (nextNodes == null)
      nextNodes = new ArrayList();
   nextNodes.add(node);
}

public int numCharacters()
{
    int countSubNodes = 0;
    if (nextNodes != null)
    {
        for (int i=0; i<nextNodes.size(); i++)
        {
            TrieNode node = (TrieNode)nextNodes.get(i);
            countSubNodes = countSubNodes + node.numCharacters();
        }
    }
    return 1 + countSubNodes;
}

public int numStrings()
{
    int count = 0;
    if (nextNodes != null)
    {
        for (int i=0; i<nextNodes.size(); i++)
        {
            TrieNode node = (TrieNode)nextNodes.get(i);
            if (node.endOfWord)
                count++;
            count = count + node.numStrings();
        }
    }
    return count;
}

}                      
