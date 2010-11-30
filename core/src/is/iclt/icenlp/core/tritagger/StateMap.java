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

import is.iclt.icenlp.core.utils.Tag;
import is.iclt.icenlp.core.tokenizer.TokenTags;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * A hash of states.
 * <br> Used by TriTagger.
 * @author Hrafn Loftsson
 */
public class StateMap {
    HashMap myStateIDs;       // Maps tags (or pair of tags) to integer identifiers
    //private final int maxNumberStates = 65536;
    private final int maxNumberStates = 1310727;
    String[] myStates;         // Maps integer identifiers to tags

    public StateMap()
    {
        myStateIDs = new HashMap();
        myStates = new String[maxNumberStates];
    }

    // Returns the integer identifier (running counter) for the supplied state
    public int getStateId(String tag)
    {
        Integer value = (Integer)myStateIDs.get(tag);

        if (value == null)
            return -1;
        else
            return value.intValue();
    }

    public int getStateId(String tag1, String tag2)
    {
        return getStateId(tag1 + " " + tag2);
    }

    public String getState(int num)
    {
        return myStates[num];
    }


    private String[] split(String str)
    {
       String strs[];
       strs = str.split(" ");   // Split the two tags
       if (strs.length != 2)
       {
           System.out.println("Expected two tags in string " + str);
           System.exit(1);
       }
       return strs;
    }

    public String getSecondTag(int num)
    {
        String strs[];
        String stateStr = getState(num);
        strs = split(stateStr);
        return strs[1];
    }

    public String getFirstTag(int num)
    {
        String strs[];
        String stateStr = getState(num);
        strs = split(stateStr);
        return strs[0];
    }

    private void quit(int states)
    {
        System.out.println("Found " + Integer.toString(states) + " states but maximum is: " + Integer.toString(maxNumberStates));
        System.exit(0);
    }

    // Maps the tags found in the sentence to unique integers and returns the number of unique states found
    public int mapTagsBigrams(ArrayList tokens)
    {
        myStateIDs.clear();
        int counter=0;
        for (int i=0; i<tokens.size(); i++)
        {
            TokenTags tok = (TokenTags)tokens.get(i);
            ArrayList tags = tok.getTags();
            for (int j=0; j<tags.size(); j++)
            {
               Tag tag = (Tag)tags.get(j);
               String tagStr = tag.getTagStr();
               if (!myStateIDs.containsKey(tagStr))
               {
                    myStateIDs.put(tagStr, new Integer(counter));
                    if (counter == maxNumberStates)
                            quit(counter);
                        else
                            myStates[counter] = tagStr;
                    counter++;
               }
            }
        }
        return counter;
    }

    // Maps the tags found in the sentence to unique integers and returns the number of unique states found
    public int mapTagsTrigrams(ArrayList tokens)
    {
        myStateIDs.clear();
        int counter=0;
        for (int i=1; i<tokens.size(); i++)
        {
            TokenTags tok = (TokenTags)tokens.get(i);
            TokenTags prevTok = (TokenTags)tokens.get(i-1);
            ArrayList tags = tok.getTags();
            ArrayList prevTags = prevTok.getTags();
            for (int j=0; j<tags.size(); j++)
            {
               Tag tag = (Tag)tags.get(j);
               String currTagStr = tag.getTagStr();
               for (int k=0; k<prevTags.size(); k++)
               {
                   Tag prevTag = (Tag)prevTags.get(k);
                   String prevTagStr = prevTag.getTagStr();
                   String key = prevTagStr + " " + currTagStr;
                    if (!myStateIDs.containsKey(key))
                    {
                        myStateIDs.put(key, new Integer(counter));
                        if (counter == maxNumberStates)
                            quit(counter+1);
                        else
                            myStates[counter] = key;
                        counter++;
                    }
               }
            }
        }
        return counter;
    }
}
