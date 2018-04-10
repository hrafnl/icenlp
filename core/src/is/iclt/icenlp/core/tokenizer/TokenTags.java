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
package is.iclt.icenlp.core.tokenizer;

import is.iclt.icenlp.core.utils.IceTag;
import is.iclt.icenlp.core.utils.Tag;
import is.iclt.icenlp.core.tokenizer.Token;

import java.util.regex.Pattern;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Tokens with PoS tags.
 * @author Hrafn Loftsson
 */
public class TokenTags extends Token
{
    protected ArrayList tags;        // All possible tags for the word
    protected boolean unknown;      // unknown word?
    public String goldTag;          // The correct (gold) tag in the given context

    public TokenTags()
    {
        super();
        tags = new ArrayList(6);
        unknown = false;
        goldTag = null;
    }

    public TokenTags(String str, TokenCode tc)
    {
        super(str, tc);
        tags = new ArrayList(6);
        unknown = false;
    }

    public TokenTags(String str, TokenCode tc, String goldTag)
    {
        this(str, tc);
        this.goldTag = goldTag;
    }
    
    // The following methods are really abstract methods
    public void setCardinalKey(String key) {return;}
    public void setSuffixLength(int len) {return;};
    public void setProbSuffix(double prob) {return;};
    public void setMorpho(boolean flag) {return;};
    public int getSuffixLength() {return 0;};
    public double getProbSuffix() {return 0.0;};
    public boolean isCardinal() {return false;};
    public String getCardinalKey() {return null;};
    public boolean isMorpho() {return false;};

    public void setUnknown(boolean flag)
    {
        unknown = flag;
    }

    public boolean isUnknown()
    {
        return (unknown==true);
    }

    public void clearTags()
    {
        tags.clear();
    }

/*
    Returns the first tag
*/
    public Tag getFirstTag()
    {
        return (Tag)tags.get(0);
    }

    public Tag getLastTag()
    {
        return (Tag)tags.get(tags.size()-1);
    }

    public ArrayList getTags()
    {
        return tags;
    }

    public String getFirstTagStr()
    {
        if (tags.isEmpty())
            return "";
        else
            return getFirstTag().getTagStr();
    }

    public String getLastTagStr()
    {
        if (tags.isEmpty())
            return "";
        else
            return getLastTag().getTagStr();
    }

    public int numTags()
    {
        // Tags might have been marked as invalid
        int num=0;
        for (int i=0; i<tags.size(); i++)
        {
            Tag tag = (Tag)tags.get(i);
            if (tag.isValid())
                num++;
        }
        return num;
    }

/*
*  Clears all tags and sets a new one
*/  public void setTag(Tag t)
    {
        clearTags();
        addTag(t);
    }

    public void setTag(String t)
    {
        clearTags();
        addTag(t);
    }

    public boolean tagExists(String t)
    {
        for (int i=0; i<=tags.size()-1;i++)
        {
            Tag tag = (Tag)tags.get(i);
            if (tag.getTagStr().equals(t))
                return true;
        }
        return false;
    }
    public void addTag(Tag t)
    {
        if (!tagExists(t.getTagStr()))
            tags.add(t);
    }

    public void addTag(String t)
    {
        if (!tagExists(t))
            tags.add(new Tag(t));
    }
    
    public void addTagWithLemma(String t, String lemma)
    {
    	if (!tagExists(t))
    	{
    		IceTag ta = new IceTag(t);
    		ta.setLemma(lemma);
    		
    		tags.add(ta);
    	}
    }

    public void addTagFront(String t)
    {
        if (!tagExists(t))
            tags.add(0, new Tag(t));
    }

    protected String[] splitTags(String tagStr)
    {
       String regex;
       if (tagStr.equals("_"))  // Special case when the tag is actually the '_' character
         regex = " ";
       else
         regex = "_";
       Pattern p = Pattern.compile(regex);
       // Get all the tags as strings
       return (p.split(tagStr));
    }

    protected String[] splitTagsWithSeparator(String tagStr, String sep)
    {
       String regex;
       if (tagStr.equals(sep))  // Special case when the tag is actually the '_' character
         regex = " ";
       else
         regex = sep;
       Pattern p = Pattern.compile(regex);
       // Get all the tags as strings
       return (p.split(tagStr));
    }

    public void setAllTags(ArrayList tagVector)
    {
        tags = tagVector;
    }

    public void setAllTags(String t)
    {
        clearTags();
        addAllTags(t);
    }


    public void addAllTags(String t)
    {
        String tagStr;
        // Add all possible tags; tags are separated by "_"
        tagStr = t;

        //String[] tags = splitTags(t);
        String[] tags = splitTags(tagStr);
           for (int i=0; i<tags.length; i++)
              addTag(tags[i]);
    }
    
    public void addAllTagsWithLemma(String t, String lemma)
    {
        String tagStr;
        // Add all possible tags; tags are separated by "_"
        tagStr = t;

        String[] tags = splitTags(tagStr);
        
        for (int i=0; i<tags.length; i++)
        {
        	addTagWithLemma(t, lemma);
        }
    }

    public void addAllTagsWithSeparator(String t, String sep)
    {
        // Add all possible tags; tags are separated by sep
        String tagStr=t;

        String[] tags = splitTagsWithSeparator(tagStr, sep);
           for (int i=0; i<tags.length; i++)
              addTag(tags[i]);
    }

    public void addAllTagsReverse(String t)
    {
        // Add all possible tags; tags are separated by "_"
        // Adds tags in reverse, mainly used to check the effect of processing
        // the tags in reverse order of frequency
        String[] tags = splitTags(t);
           for (int i=tags.length-1; i>=0; i--)
              addTag(tags[i]);
    }


    public void addAllTagsFront(String t)
    {
        // Add all possible tags; tags are separated by "_"
        String[] tags = splitTags(t);
           for (int i=tags.length-1; i>=0; i--)
              addTagFront(tags[i]);
    }

    public boolean noTags()
    {
        return tags.isEmpty();
    }


    public void removeInvalidTags()
    {
       int count = tags.size();
       Iterator iterator = tags.iterator();
       while (iterator.hasNext()) {
          Tag tag = (Tag)iterator.next();
          if (!tag.isValid() && count > 1)
          {
            iterator.remove();
            count--;
          }
       }
    }

    public void removeAllButFirstTag()
    {
       int count=1;
       Iterator iterator = tags.iterator();
       while (iterator.hasNext()) {
          Tag tag = (Tag)iterator.next();
          if (count > 1)
            iterator.remove();
          count++;
       }
    }


    public void removeAllBut(String tagStr)
    {
       Iterator iterator = tags.iterator();
       while (iterator.hasNext()) {
          Tag tag = (Tag)iterator.next();
          if (!(tag.getTagStr().equals(tagStr)))
            iterator.remove();
       }
    }

    public String allTagStrings()
    {
        return allTagStringsWithSeparator("_");
    }

    public String allTagStringsWithSeparator(String sep)
    {
        String tagStr = "";
        int i=0;
        Iterator iterator = tags.iterator();
        
        while (iterator.hasNext())
        {
            Tag element = (Tag)iterator.next();
            
            if (i==0)
            {
                tagStr = element.toString();
            }
            else
            {
                tagStr = tagStr + sep + element.toString();
            }
            
            i++;
        }
        
        return tagStr;
    }

    public String allLexicalUnits()
    {
        String tagStr = null;
        int i=0;
        Iterator iterator = tags.iterator();
        
        tagStr = lexeme + ":";
        
        while (iterator.hasNext())
        {
            Tag element = (Tag)iterator.next();
            
            if (i==0)
            {
                tagStr = tagStr + element.getLemma() + "(" + element.toString() + ")";
            }
            else
            {
                tagStr = tagStr + "_" + element.getLemma() + "(" + element.toString() + ")";
            }
            
            i++;
        }
        
        return tagStr;
    }

    public String toString()
    {
        String output = null;

        String tagStr = allTagStrings();

        output = lexeme + " <" + tokenCode.toString() + ">" +
            " <" + tagStr + ">"; // + " <" + wClass + ">";
        if (isUnknown())
            output = output + " <UNKNOWN>";

        return output;
    }

}
