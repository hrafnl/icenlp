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

import java.io.*;

/**
 * Encapsulates a Trie data structure.
 * @author Hrafn Loftsson
 */
public class Trie {
    private TrieNode rootNode;      // The root node is a dummy node
    private int nextIndex=0;        // Marks the next index of the character in a string to be
                                    // inserted after the prefix has been found
    private String keyFound=null;
	private String valueFound=null;
    public static final char dummyLabel=' ';

public Trie()
{
    rootNode = new TrieNode(dummyLabel, false, null); // the root node
}

// Loads the contents of a file name into the trie
public Trie(String filename, boolean reverseKeys) throws IOException
{
    this();
    BufferedReader in = FileEncoding.getReader(filename);
    load(in, reverseKeys);
    // Close the file.
    in.close();
}

public Trie( InputStream in, boolean reverseKeys) throws IOException, NullPointerException
{
    this();
    if( in == null )
       throw new NullPointerException( "InputStream was not initialized correctly (null)" );
    BufferedReader br = FileEncoding.getReader(in);
    load(br, reverseKeys);
}

public String getKeyFound()
{
		return keyFound;
}

public String getValueFound()
{
		return valueFound;
}
/*
private int searchValues(String value)
{
    int index=-1;
    for (int i=0; i<myValues.size(); i++) {
        String myValue = (String)myValues.get(i);
        if (myValue.equals(value)) {
            index = i;
            break;
        }
    }
    return index;
}
*/
/*
private void setValue(TrieNode currNode, String strValue)
{
    int idx = myValues.indexOf(strValue);
    if (idx >= 0)
        currNode.setValue(idx);
    else
    {
        myValues.add(strValue);
        currNode.value = myValues.size()-1;
    }
}
*/

private String reverseKey(String key)
{
    StringBuffer strBuf = new StringBuffer(key);
    strBuf = strBuf.reverse();
    return strBuf.toString();
}

/* Can handle both lexicons (only keys) and dictionaries (keys and values
*  Separator for keys and values is "="
*  It is assumed that lines starting with "#" are comments
* */
private void load(BufferedReader br, boolean reverseKeys) throws IOException
{
    String line;
    // Read a line at a time until the end of the file is reached.
    if (br == null)
      throw new NullPointerException( "BufferedReader in load() is null" );
    int count=0;
    while( (line = br.readLine()) != null )
    {
       if (line.length() == 0 || line.charAt(0) == '#') // Then empy or comment
            continue;
       if (line.contains("=") && line.length() > 1)  // Then a dictionary, key and a value
       {
          int idxSeparator = line.indexOf('=');
          String key = line.substring(0,idxSeparator);
          String value = line.substring(idxSeparator+1);
          if (reverseKeys) {
              key = reverseKey(key);
          }
          insert(key, value);
       }
       else {                   // Then a lexicon, only a key
           insert(line);
       }
       count++;
    }
}

// Inserts a string into the trie
public TrieNode insert(String str)
{
   return insertKeyValue(str, null);
}

// Inserts a string and a value into the trie
public TrieNode insert(String str, String value)
{
   return insertKeyValue(str, value);
}


public TrieNode insertKeyValue(String str, String value)
{
    TrieNode newNode;
    boolean endOfWord = false;
    int len = str.length();
    TrieNode currentNode = findPrefix(str);
    // Add part of the word which is not in Trie
    // Make sure we don't add a duplicate
    if (currentNode.equals(rootNode) || (currentNode != null && nextIndex != len))
    {
	    int strPos = nextIndex; // The next index for the char to be added
        // For each remaining character, add a new node
        while (strPos < len)
	    {
            char chr = str.charAt(strPos);
            // End of word?
            if (strPos == len-1)
                endOfWord=true;

            newNode = new TrieNode(chr, endOfWord, value);
            //newNode = new TrieNode(chr, endOfWord);
            //if (endOfWord)
            //    setValue(newNode, value);

            currentNode.addSubNode(newNode);  // Add the node to the current node
            currentNode = newNode;
            strPos++;
	    }
    }
    // This applies if a path was found for a longer word
    else if (currentNode != null && nextIndex == len && !currentNode.endOfWord) {
        //setValue(currentNode, value);
        currentNode.value = value;
        currentNode.endOfWord = true;
    }
    return currentNode;
}

/*
 * Returns the node in the trie corresponding to the given string
*/
public TrieNode find(String str)
{
    int len = str.length();
    //TrieNode node = findPrefix(str, len);
    TrieNode node = findPrefix(str);
    if (node.equals(rootNode) || nextIndex < len)  // Then not found
        return null;
    else return node;
}

private TrieNode findPrefix(String str)
{
    // Look for the prefix of the word. The prefix is already stored in the trie
    boolean found=true;
    int strPos=0;

    TrieNode currentNode=rootNode, subNode;
    int len = str.length();
    while (found && strPos<len)
    {
        char chr = str.charAt(strPos);
        subNode = currentNode.findSubNode(chr);  // Can we find the char in a sub node?
	    if (subNode != null)
        {
            found = true;
            //keyFound.append(chr);
            strPos++;
            currentNode = subNode;
        }
        else
            found=false;
    }
    nextIndex = strPos;  // Set the string pos next index
    return currentNode;
}

private TrieNode findLongestPrefix(String str, int maxLen)
{
    // Find the longest prefix of string str stored in the trie.
    // The prefix needs to be a key
    // The length of the prefix has to be <= maxLen
    boolean found=true;
    int strPos=0;

    TrieNode currentNode=rootNode, subNode, longestFound=null;
    //keyFound.setLength(0);
    int len = str.length();
    while (found && strPos<len && strPos < maxLen)
    {
        char chr = str.charAt(strPos);
        subNode = currentNode.findSubNode(chr);  // Can we find the char in a sub node?
	    if (subNode != null)
        {
            found = true;
            //keyFound.append(chr);
            strPos++;
            if (subNode.endOfWord) {
                longestFound = subNode;
                keyFound = str.substring(0,strPos);
            }
            currentNode = subNode;
        }
        else
            found=false;
    }
    return longestFound;
}

/*
 * Returns the value (a string) associated with the given key
 */
public String lookup(String key, boolean ignoreCase)
{
    String lookupWord;
    if( ignoreCase )
	    lookupWord = key.toLowerCase();
	else
		lookupWord = key;

    TrieNode node = find(lookupWord);

    if (node == null)
        return null;
    else {
        return node.getValue();
    }
}

/*
 * Returns the value (a string) associated with the longest suffix for the given word
 * This function assumes that the Trie has been built with reversed keys!
 */
public boolean lookupSuffix(String word, boolean ignoreCase, int maxSuffixLen)
{
    String lookupWord;
    if( ignoreCase )
	    lookupWord = word.toLowerCase();
	else
		lookupWord = word;

    lookupWord = reverseKey(lookupWord);
    TrieNode node = findLongestPrefix(lookupWord, maxSuffixLen);

    if (node == null || node.getValue() == null)
        return false;
    else {
        valueFound = node.getValue();
        return true;
    }
}


public void print()
{
   printMe(rootNode, "");
}

private void printMe(TrieNode node, String prefix)
{
    String nodeStr;

    if (node.label != Trie.dummyLabel)
        nodeStr = prefix + node.label;
    else
        nodeStr = prefix;

    if (node.endOfWord) {
        if (node.value != null)
            System.out.println(nodeStr+" => " + node.value.toString());
        else
            System.out.println(nodeStr);
        //int idx = node.getValue();
        //String theValue = (String)myValues.get(idx);
        //System.out.println(nodeStr+" => " + theValue);
    }
    if (node.nextNodes != null)
    {
        for (int i=0; i<node.nextNodes.size(); i++)
        {
            TrieNode nextNode = (TrieNode)node.nextNodes.get(i);
            printMe(nextNode, nodeStr);
        }
    }
}


public int numCharacters()
{
    // Subtract 1 because the root is a dummy node
    return rootNode.numCharacters()-1;
}

public int numStrings()
{
    return rootNode.numStrings();
}


}