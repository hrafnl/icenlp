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

/**
 * Encapsulates a PoS tag.
 * @author Hrafn Loftsson
 */
public class Tag {
   protected StringBuffer tagStr;
   protected boolean valid;

   public Tag()
   {
       tagStr = new StringBuffer();
       valid = true;
   }
   public Tag(String str)
   {
       tagStr = new StringBuffer();
       tagStr.append(str);
       interpretTag();
       valid = true;
   }

   public void setValid(boolean v)
   {
       valid = v;
   }

   public boolean isValid()
   {
      return (valid == true);
   }

   public String getTagStr()
   {
       return tagStr.toString();
   }

   public Character getFirstLetter()
   {
       return tagStr.charAt(0);
   }

   public void setTagStr(String str)
   {
       clearTagStr();
       tagStr.append(str);
       interpretTag();
   }

   public void clearTagStr()
   {
      tagStr.delete(0,tagStr.length());
   }

   /*
 * Dummy method
 */
  protected void interpretTag()
  {
  }

    public String toString()
    {
        return tagStr.toString();
    }


}