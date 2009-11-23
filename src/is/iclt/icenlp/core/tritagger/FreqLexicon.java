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
package is.iclt.icenlp.core.tritagger;

import is.iclt.icenlp.core.utils.FileEncoding;

import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;
import java.util.Map;
import java.io.*;

/**
 * A lexicon with frequency information.
 * <br> Derives suffix information from the lexicon.
 * <br> Used by TriTagger
 * @author Hrafn Loftsson
 */
public class FreqLexicon {
   private HashMap myWords;
   private HashMap mySuffixesLower;    // We use different estimates for lower case vs.
   private HashMap mySuffixesUpper;    // upper case words
   private HashMap myTagsLower;     // tags of lower case rare words, mapped to frequencies
   private HashMap myTagsUpper;     // tags of lower case rare words, mapped to frequencies
   private static final int maxWordFreqForSuffixHandling=10;
   private static final int minWordFreqForSuffixHandling=1;
   private static final int minWordLengthForSuffixHandling=1;
   public final int suffixStart=1;  // For each word suffix construcion starts at letter word.length - suffixStart;
   public final int suffixLength=10;    // For each word suffix, construcion ends at letter word.length - suffixLength;
   public int corpusSize=0;             // Size of the corpus that was used to construct the lexicon
   private int corpusSizeLower=0;      // The corpus size behind lower case suffixes
   private int corpusSizeUpper=0;     // The corpus size behind upper case suffixes
   private boolean suffixesCreated;
   private double theta;                        // Used for smoothing suffix lexical probabilities
   private int myFormat;                // The format of the lexicon
   public static final int formatFrequency=0;   // Use to state that the lexicon has frequency figures
   public static final int formatNoFrequency=1; // Use to state that the lexicon has no frequency figures


    // Each key (word) is mapped to a Vector
    // The vector consists of is.iclt.icenlp.core.tritagger.FreqLexEntry objects
    public FreqLexicon(String fileName, int format, boolean createSuffixes)
    throws IOException
    {
        init1(format);
        //BufferedReader input = new BufferedReader(new FileReader(fileName));
        BufferedReader input = FileEncoding.getReader(fileName);
        loadFile(input, createSuffixes);
        init2();
        suffixesCreated = createSuffixes;

    }

    public FreqLexicon( InputStream in, int format, boolean createSuffixes ) throws IOException, NullPointerException
    {
           if( in == null )
               throw new NullPointerException( "InputStream was not initialized correctly (null)" );

           init1(format);
           //BufferedReader input = new BufferedReader(new InputStreamReader(in));
           BufferedReader input = FileEncoding.getReader(in);
           loadFile(input, createSuffixes);
           init2();
           suffixesCreated = createSuffixes;
    }

    private void init1(int format)
    {
        myWords = new HashMap();
        mySuffixesLower = new HashMap();
        mySuffixesUpper = new HashMap();
        myTagsLower = new HashMap();
        myTagsUpper = new HashMap();
        myFormat = format;

    }

    private void init2()
    {
        corpusSizeLower = sumTags(myTagsLower);
        corpusSizeUpper = sumTags(myTagsUpper);
        //theta = computeTheta();
    }

    public boolean suffixesCreated()
    {
        return suffixesCreated;
    }

    private void loadFile(BufferedReader input, boolean doCreateSuffixes)
    throws IOException
    {
       String strs[];
       String key="";
       FreqLexEntry entry;
       int freq=1, keyFreq, tagStart, tagIncrement;

       corpusSize=0;
       // A word is the key and a vector of lexicon entries (is.iclt.icenlp.core.tritagger.FreqLexEntry) are values.
       // The line looks like: w freq t1 freq (t2 freq) (t3 freq) ... (tn freq)
       String currLine = input.readLine();
       while (currLine != null)
       {
           if (currLine.length() != 0 && currLine.charAt(0) != '[')   // Not an empty line and not a comment
           {
              Vector entries = new Vector();
              strs = currLine.split("\\s");   // Split the line
              key = strs[0];     // the word

              if (myFormat == formatFrequency)
              {
                keyFreq = Integer.parseInt(strs[1]);   // frequency of the word
                tagStart = 2;
                tagIncrement = 2;
              }
              else // no frequency
              {
                  tagStart = 1;
                  tagIncrement = 1;
                  keyFreq = strs.length-1;  // The number of tags
              }

              for (int i=tagStart; i<strs.length; i=i+tagIncrement)
              {
                  String tag = strs[i];
                  if (myFormat == formatFrequency)
                    freq = Integer.parseInt(strs[i+1]);   // frequency of the tag

                  entry = new FreqLexEntry(tag, freq);  // Default frequency is 1
                  entries.add(entry);
              }
              if (!key.matches("^@CARD.*"))   // Skip special Cardinal info
              {
                corpusSize += keyFreq;
                myWords.put(key, entries);
                // Create suffixes distribution for words that meet certain criteria
                if (doCreateSuffixes && keyFreq >= minWordFreqForSuffixHandling && keyFreq <= maxWordFreqForSuffixHandling &&
                        key.length() >= minWordLengthForSuffixHandling)
                {
                    createSuffixes(key, entries);
                }
              }
           }
           currLine = input.readLine();
       }
       input.close();
    }


    public double getTheta()
    {
        return theta;
    }

    public Vector getTagsUpper()
    {
        return getTags(myTagsUpper);
    }

    public Vector getTagsLower()
    {
        return getTags(myTagsLower);
    }

    private Vector getTags(HashMap theTags)
    {
        Vector v = new Vector();
        Iterator it = theTags.entrySet().iterator();
        while (it.hasNext())    // Loop through all tags in the hash
        {
            Map.Entry entry = (Map.Entry) it.next();   // Get an entry
            String tagStr = (String)entry.getKey();  // The key is a tag string
            v.add(tagStr);
        }
        return v;
    }

    private HashMap getHashSuffixes(boolean isUpperCase)
    {
        if (isUpperCase)
           return mySuffixesUpper;
        else
           return mySuffixesLower;
    }

    private HashMap getHashTags(boolean isUpperCase)
    {
        if (isUpperCase)
           return myTagsUpper;
        else
           return myTagsLower;
    }

    // theta is the standard deviation of the maximum likelihood probabilities of the tags (Brants, 2000)

    private double computeTheta()
    {
        Vector vUpper = getTags(myTagsUpper);
        Vector vLower = getTags(myTagsLower);

        int size = corpusSizeLower + corpusSizeUpper;
        int numTagsUpper = vUpper.size();
        int numTagsLower = vLower.size();
        int numTags = numTagsUpper + numTagsLower;
        /*
        double total = 0.0;
        // Compute the average
        for (int i=0; i<numTagsUpper; i++)
        {
            String tag = (String)vUpper.elementAt(i);
            Integer intObj = (Integer)myTagsUpper.get(tag);
            int freq = intObj.intValue();
            total += (double)freq/numTags;          // total = total + P(tag_i)
        }
        for (int i=0; i<numTagsLower; i++)
        {
            String tag = (String)vLower.elementAt(i);
            Integer intObj = (Integer)myTagsLower.get(tag);
            int freq = intObj.intValue();
            total += (double)freq/numTags;
        }
        double averageTagProb = total / numTags;*/
        double averageTagProb = (double)1.0/numTags;

        // Now compute the standard deviation
        double sum=0.0;
        for (int i=0; i<numTagsUpper; i++)
        {
            String tag = (String)vUpper.elementAt(i);
            Integer intObj = (Integer)myTagsUpper.get(tag);
            int freq = intObj.intValue();
            double probTag = (double)freq/size;
            double x = probTag - averageTagProb;
            sum = sum + x*x;
        }
        for (int i=0; i<numTagsLower; i++)
        {
            String tag = (String)vLower.elementAt(i);
            Integer intObj = (Integer)myTagsLower.get(tag);
            int freq = intObj.intValue();
            double probTag = (double)freq/size;
            double x = probTag - averageTagProb;
            sum = sum + x*x;
        }
        // Brants (2000) talks about using standard deviation but his formula is actually the variance
        return Math.sqrt((double)sum/(numTags-1));
        //return (double)sum/(numTags-1);
    }

    // Returns the probability of the supplied tag with the regard to a suffix trie
    /*public double getProbTagSuffixTrie(String tag, boolean isUpperCase)
    {
        int size;
        HashMap theHash = getHashTags(isUpperCase);

        if (isUpperCase)
            size = corpusSizeUpper;
        else
            size = corpusSizeLower;

        Integer intObj = (Integer)theHash.get(tag);
        if (tag == null)
            return 0.0;
        else {
            int value = intObj.intValue();
            return (double)value/size;
        }

    } */

    public double getProbWord(String word)
    {
        return (double)getFrequency(word,myWords)/corpusSize;
    }

    // The probability of a suffix is estimated as the frequency of the suffix divided by the number of suffixes generated
    public double getProbSuffix(String suffix, boolean isUpperCase)
    {
        int size;
        HashMap theHash = getHashSuffixes(isUpperCase);

        if (isUpperCase)
            size = corpusSizeUpper;
        else
            size = corpusSizeLower;

        int suffixFreq = getFrequency(suffix, theHash);
        return (double)suffixFreq/size;
    }

    // Returns the longest suffix length applicable for the given word
    public int getLongestSuffixLength(String word, boolean isUpperCase)
    {
          HashMap theHash = getHashSuffixes(isUpperCase);
          int len = word.length();
          int suffixLen = Math.min(suffixLength, len);   // The length of the suffix used
          int suffixIndex = len-suffixLen;

          for (int i=suffixIndex; i<len; i++)
          {
              Object value;
              String suffix = word.substring(i);   // suffix = word(i..len)
              value = theHash.get(suffix);
              if (value != null)                        // Then this suffix exists in the hash
                  break;
              else {
                  suffixLen--;
              }
          }
          return suffixLen;
    }

    // Runs through the a tag hash and computes the total frequency, which is equal to the size of
    // the rare word corpus encountered
    private int sumTags(HashMap theHash)
    {
        Iterator it = theHash.entrySet().iterator();
        int totalFreq = 0;
        while (it.hasNext())    // Loop through all entries in the hash
        {
            Map.Entry entry = (Map.Entry) it.next();   // Get an entry
            Integer intObj = (Integer)entry.getValue();  // The value is an integer object
            int freq = intObj.intValue();
            totalFreq = totalFreq + freq;
        }
        return totalFreq;
    }

    // Runs through the whole hash and computes the total frequency, which is equal to the size of
    // the corpus used to construct the hash
    /*
    private int getTotalFrequency(HashMap theHash)
    {
        Iterator it = theHash.entrySet().iterator();
        int totalFreq = 0;
        while (it.hasNext())    // Loop through all entries in the hash
        {
            Map.Entry entry = (Map.Entry) it.next();   // Get an entry
            String word = (String)entry.getKey();      // The key is a word/suffix
            int freq = getFrequency(word, theHash);
            totalFreq = totalFreq + freq;
        }
        return totalFreq;
    } */

    private int getFrequency(String word, HashMap theHash)
    {
        Vector entries = (Vector)theHash.get(word);
        if (entries == null)
            return 0;

        int frequency=0;
        for (int i=0; i<entries.size(); i++)
        {
            FreqLexEntry entry = (FreqLexEntry)entries.elementAt(i);
            frequency = frequency + entry.getFrequency();
        }
        return frequency;
    }

    public int getFrequencyWord(String word)
    {
        return getFrequency(word, myWords);
    }

    public int getFrequencySuffix(String suffix, boolean isUpperCase)
    {
        HashMap theHash = getHashSuffixes(isUpperCase);
        return getFrequency(suffix, theHash);
    }

    private int getFrequencyWordTag(HashMap theHash, String word, String tag)
    {
        Vector entries = (Vector)theHash.get(word);
        if (entries == null)
            return 0;

        int frequency=0;
        for (int i=0; i<entries.size(); i++)
        {
            FreqLexEntry entry = (FreqLexEntry)entries.elementAt(i);
            if (entry.getTag().equals(tag))
            {
                frequency = entry.getFrequency();
                break;
            }
        }
        return frequency;
    }

    // Returns the frequency of the supplied word having the supplied tag
    public int getFrequencyWordTag(String word, String tag)
    {
        return getFrequencyWordTag(myWords, word, tag);
    }

    // Returns the frequency of the supplied suffix having the supplied tag
    public int getFrequencySuffixTag(String suffix, String tag, boolean isUpperCase)
    {
        HashMap theHash = getHashSuffixes(isUpperCase);
        return getFrequencyWordTag(theHash, suffix, tag);
    }

    private String lookup(HashMap theHash, String word)
    {
        Vector entries = (Vector)theHash.get(word);
        String tagStr=null;
        if (entries != null)
        {
            for (int i=0; i<entries.size(); i++)
            {
                FreqLexEntry entry = (FreqLexEntry)entries.elementAt(i);
                if (i==0)
                    tagStr = entry.getTag();
                else
                    tagStr = tagStr + "_" + entry.getTag();
            }
        }
        return tagStr;
    }

    public String lookupWord(String word, boolean ignoreCase) {
        String lookupWord;

        if (ignoreCase)
          lookupWord = word.toLowerCase();
        else
          lookupWord = word;

        return lookup(myWords, lookupWord);
    }

    public String lookupSuffix(String word, boolean isUpperCase) {
        HashMap theHash = getHashSuffixes(isUpperCase);
        return lookup(theHash, word);
    }

    public int getNumEntries()
    {
        return myWords.size();
    }

    // Searches for the tag in the LexEntries of the vector v
    private FreqLexEntry searchTag(String tag, Vector vector)
    {
        for (int i=0; i<vector.size(); i++)
        {
            FreqLexEntry myEntry = (FreqLexEntry)vector.elementAt(i);
            if (myEntry.getTag().equals(tag))
                return myEntry;
        }
        return null;
    }

    private void addSuffixEntry(String suffix, Vector vectorToAdd, HashMap theHashMap)
    {
        FreqLexEntry entryToAdd;
        Vector myVector = (Vector)theHashMap.get(suffix);
        if (myVector == null)  // If nothing is there yet
        {
            myVector = new Vector();
            for (int i=0; i<vectorToAdd.size(); i++)
            {
                entryToAdd = (FreqLexEntry)vectorToAdd.elementAt(i);
                FreqLexEntry newEntry = new FreqLexEntry(entryToAdd.getTag(), entryToAdd.getFrequency());
                myVector.add(newEntry);
            }
            theHashMap.put(suffix, myVector);     // The suffix is the key, the value is a vector of LexEntries
        }
        else {  // The vector is already there - its values no need to be updated
            for (int i=0; i<vectorToAdd.size(); i++)  // Loop through the vector which holds entries to be added
            {
                entryToAdd = (FreqLexEntry)vectorToAdd.elementAt(i);
                String tag = entryToAdd.getTag();
                FreqLexEntry myEntry = searchTag(tag, myVector);    // Get my entry which corresponds to this tag
                if (myEntry == null)     // Then the tag was not found in the vector, add a new entry
                {
                    myEntry = new FreqLexEntry(entryToAdd.getTag(), entryToAdd.getFrequency());
                    myVector.add(myEntry);
                }
                else {                     // The tag was found in the vector; increment the tag frequency
                 myEntry.setFrequency(myEntry.getFrequency() + entryToAdd.getFrequency());
                }
            }
        }
    }

    // Builds a hash for tags of rare words used in suffix handling
    // A tag is a key, a frequency is a value
    private void storeSuffixTags(Vector vector, HashMap suffixTags/*, int howOften*/)
    {
        // The vector stores all the tags and frequencies to add to the hash
        for (int i=0; i<vector.size(); i++)
        {
            FreqLexEntry entry = (FreqLexEntry)vector.elementAt(i); // The entry object
            String tag = entry.getTag();                    // The tag string
            int freq = entry.getFrequency();                // The frequency

            Integer intObj = (Integer)suffixTags.get(tag);  // Get the integer object
            if (intObj == null)                             // If not found
            {
                intObj = new Integer(freq);
                suffixTags.put(tag, intObj);
            }
            else                                            // else increment the frequency
            {
                int theFreq = intObj.intValue();
                theFreq += freq;
                intObj = new Integer(theFreq);
                suffixTags.put(tag, intObj);
            }
        }
    }

    private void createSuffixes(String word, Vector vector)
    {
        // We used the last suffixStart to suffixEnd characters of the word as suffixes
        HashMap theHashMapSuffixes, theHashMapTags;
        String suffix;
        int length = word.length();
        char firstChar = word.charAt(0);
        boolean upperCase = Character.isUpperCase(firstChar);

        theHashMapTags = getHashTags(upperCase);
        // Store the tags, each frequency of a tag in the vector is multiplied by howOften
        //int howOften = Math.min(suffixLength-suffixStart+1, length-1);
        //storeSuffixTags(vector, theHashMapTags, howOften);
        storeSuffixTags(vector, theHashMapTags);

        theHashMapSuffixes = getHashSuffixes(upperCase);
        // Create a suffix trie using tag information in vector
        for (int i=suffixStart; i<=suffixLength && length>=i; i++)   // Use the whole word for suffix
        {
            suffix = word.substring(length-i);
            addSuffixEntry(suffix, vector, theHashMapSuffixes);
        }
    }
}
