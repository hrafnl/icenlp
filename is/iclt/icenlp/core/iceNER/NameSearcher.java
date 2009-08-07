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

package is.iclt.icenlp.core.iceNER;
import java.io.*;
import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * Main class for named entity recognition.
 * @author Aðalsteinn Tryggvason
 */
public class NameSearcher {
    boolean m_bFormOverFunction;
    String m_sOutFile;
    String[][] m_sTextArray;
    String[] m_sLineArray;
    String m_sLine = "";
    Hashtable m_sNameHash = new Hashtable();
    Hashtable m_sRoleHash = new Hashtable();
    ArrayList m_IndexList = new ArrayList();
    ArrayList m_NameList = new ArrayList();
    ArrayList m_CertainList = new ArrayList();
    ArrayList m_ScannesNamesList = new ArrayList();
    ArrayList m_listFirstName = new ArrayList();
    int m_nLineCount;

    public NameSearcher(boolean bFormFunction)
    {
        m_bFormOverFunction = bFormFunction;

    }
    
    public NameSearcher(String sTaggedFile, int TaggedlineCount, String sScanFile, int scanLineCount, String sOutFile )
    {
        m_sOutFile = sOutFile;
        m_sTextArray = new String[TaggedlineCount][3];
        m_nLineCount = TaggedlineCount;
        loadTaggedFile(sTaggedFile,TaggedlineCount );
        loadScannerFile(sScanFile, scanLineCount);
    }
    public NameSearcher(String sTaggedFile, int TaggedlineCount, String sScanFile, int scanLineCount, String sOutFile, String sGazettFile, int gazettLineCount)
    {
        m_sOutFile = sOutFile;
        m_sTextArray = new String[TaggedlineCount][3];
        m_nLineCount = TaggedlineCount;
        loadTaggedFile(sTaggedFile,TaggedlineCount );
        loadScannerFile(sGazettFile, gazettLineCount);
        loadScannerFile(sScanFile, scanLineCount);
    }
    public void loadSearcher(String sTaggedFile, int TaggedlineCount, String sScanFile, int scanLineCount, String sOutFile )
    {
        m_sOutFile = sOutFile;
        m_sTextArray = new String[TaggedlineCount][3];
        m_nLineCount = TaggedlineCount;
        loadTaggedFile(sTaggedFile,TaggedlineCount );
        loadScannerFile(sScanFile, scanLineCount);
    }
    public void loadSearcher(String sTaggedFile, int TaggedlineCount, String sScanFile, int scanLineCount, String sOutFile, String sGazettFile, int gazettLineCount)
    {
        m_sOutFile = sOutFile;
        m_sTextArray = new String[TaggedlineCount][3];
        m_nLineCount = TaggedlineCount;
        loadTaggedFile(sTaggedFile,TaggedlineCount );
        loadScannerFile(sGazettFile, gazettLineCount);
        loadScannerFile(sScanFile, scanLineCount);  
    }
    public boolean loadTaggedFile(String sfile, int lineCount)
    {
        try
		{
            File file = new File(sfile);
            BufferedReader reader = null;
            try
            {
                //reader = new BufferedReader(new FileReader(file));
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));

                for(int i=0; i<m_sTextArray.length; i++)
                {
                    m_sLine = reader.readLine();
                    if(!m_sLine.equals(""))
                    {
                        m_sLineArray = m_sLine.split(" ");
                        m_sTextArray[i][0] = m_sLineArray[0];
                        m_sTextArray[i][1] = m_sLineArray[1];
                        m_sTextArray[i][2] = "";
                    }
                    else
                    {
                        m_sTextArray[i][0] = "";
                        m_sTextArray[i][1] = "";
                        m_sTextArray[i][2] = "";
                    }
                }
            }
            catch(Exception e)
            {
                return false;
            }
            reader.close();
            return true;
        }
        catch (Exception e)
        {
            System.err.println("File input error");
            return false;
		}
    }
    public boolean  loadScannerFile(String sfile, int lineCount)
    {
        try
		{
            File file = new File(sfile);
            BufferedReader reader = null;
            try
            {
                //reader = new BufferedReader(new FileReader(file));
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                for(int i=0; i<lineCount; i++)
                {
                    m_sLine = reader.readLine();
                    if(!m_sLine.equals(""))
                    {
                        m_sLineArray = m_sLine.split(" SEP ");
                        if(m_sLineArray[1].startsWith("ROLE")||m_sLineArray[1].startsWith("RELATION"))
                        {
                            loadRoleHash(m_sLineArray[0],m_sLineArray[1]);
                        }
                        else
                        {
                            loadNameHash(m_sLineArray[0],m_sLineArray[1]);
                        }                
                    }
                }
            }
            catch(Exception e)
            {
                return false;
            }
            reader.close();
            return true;
        }
        catch (Exception e)
        {
            System.err.println("File input error");
            return false;
		}
    }
   
    //Merki sérnofn og erlend orð sem byrja á stórumstaf
    public void indexNamedEntity()
    {
        //Finn fyrst öll nöfn sem eru merkt sem sérnöfn, erlend orð og orð
        //sem byrja á stórum staf í miðri setningu
        for(int i=0; i<m_nLineCount; i++)
        {
            if(isProperNoun(m_sTextArray[i][1])||isPartOfCompanyName(m_sTextArray[i][0])||
                    (isForeign(m_sTextArray[i][1])&&startsWithUpper(m_sTextArray[i][0]))||
                    (startsWithUpper(m_sTextArray[i][0])&&!isStartOfSentance(i)))
            //if(isProperNoun(m_sTextArray[i][1])&& startsWithUpper(m_sTextArray[i][0]))
            {
                //Geimi index á nafnið
                m_IndexList.add(i);
                //Geimi nafnið,
                m_NameList.add(m_sTextArray[i][0]);
            }
        }
        //Skoða orð í upphafi setningar önnur en sérnöfn, ef þau hafa fundist
        //áður sem nafn, þá indexa sem nafn
        for(int i=0; i<m_nLineCount; i++)
        {
            if(isStartOfSentance(i)&&(isNoun(m_sTextArray[i][1])||isVerb(m_sTextArray[i][1])||isAdjective(m_sTextArray[i][1])))
            {
                if(m_NameList.contains(m_sTextArray[i][0])||tryDifferntCasesBolean(m_sTextArray[i][0], m_sTextArray[i][1]))
                {
                    //Geimi index á nafnið
                    m_IndexList.add(i);
                    //Geimi nafnið,
                    m_NameList.add(m_sTextArray[i][0]);
                }
            }
        }
    }
    public void loadNameHash(String sName, String sTag)
    {
        String[] sTempArray;
        String sTempName = "";
        if(!m_sNameHash.containsKey(sName.trim()))
        {
            sTempArray = sName.split(" ");
            for(int n=0; n<sTempArray.length; n++)
            {
                sTempName = sTempName + " " + sTempArray[n];
                if(!m_sNameHash.containsKey(sTempName.trim()))
                {
                    m_sNameHash.put(sTempName.trim(), sTag);
                    if(startsWithUpper(sTempArray[n]))
                    {
                       if(!m_sNameHash.containsKey(sTempArray[n]))
                       {
                            m_sNameHash.put(sTempArray[n], sTag);
                       }
                    }
                }
            }
            sTempName="";
            for(int n=sTempArray.length-1; n>0; n--)
            {
                sTempName =  sTempArray[n]+ " " + sTempName;
                if(!m_sNameHash.containsKey(sTempName))
                {
                    m_sNameHash.put(sTempName.trim(), sTag);
                }
            }
        }         
    }
    public void overwriteNameHash(String sName, String sTag)
    {
        String[] sTempArray;
        String sTempName = "";
        sTempArray = sName.split(" ");
        for(int n=0; n<sTempArray.length; n++)
        {
            sTempName = sTempName + " " + sTempArray[n];
            m_sNameHash.put(sTempName.trim(), sTag);
            if(startsWithUpper(sTempArray[n]))
            {
                m_sNameHash.put(sTempArray[n], sTag);
            }
        }
        sTempName="";
        for(int n=sTempArray.length-1; n>0; n--)
        {
            sTempName =  sTempArray[n]+ " " + sTempName;
            m_sNameHash.put(sTempName.trim(), sTag);
        }
    }
    public void loadRoleHash(String sRole, String sClass)
    {
        if(!m_sRoleHash.containsKey(sRole.trim()))
        {
            m_sRoleHash.put(sRole.trim(), sClass);
        }
    }
    public String getNameTag(String sName)
    {
        return m_sNameHash.get(sName.trim()).toString();
    }

    public String getRoleTag(String sName)
    {
        return m_sRoleHash.get(sName.trim()).toString();
    }

    public boolean nameHashcontains(String sName)
    {
        if(m_sNameHash.containsKey(sName.trim()))
        {
            return true;
        }
        return false;
    }

    public boolean roleHashContains(String sName)
    {
        if(m_sRoleHash.containsKey(sName.trim()))
        {
            return true;
        }
        return false;
    }
    //Geimi öll nöfn sem þegar hafa fundist, orð fyrir orð
    public void listAllIndexedNames()
    {
       String sName;
       int nStart;
       int nStop;
       Iterator itr;
       itr = m_IndexList.iterator();
       while (itr.hasNext())
       {
           nStart = Integer.parseInt(itr.next().toString());
           nStop = findFullName(nStart);//nStop = findFullName(nStart+1);
           //nStart = findStartOfName(nStart);
           sName =  getFullName(nStart, nStop);
           m_NameList.add(sName);
       }
       itr = m_NameList.iterator();
       while (itr.hasNext())
       {
           System.out.println(itr.next().toString());
       }
    }

   public void markFromHash(boolean  bLocation)
   {
       String sName;
       String sNext;
       String sTag;
       int nStart;
       int nStop;
       Iterator itr;
       itr = m_IndexList.iterator();
       while (itr.hasNext())
       {
           nStart = Integer.parseInt(itr.next().toString());
           //Ef Þegar búið að merkja nafn þá henda index
           if(!m_sTextArray[nStart][2].equals(""))
           {
                //////itr.remove();
           }
           else
           {
               nStop = findFullName(nStart);//nStop = findFullName(nStart+1);
               nStart = findStartOfName(nStart);
               sName =  getFullName(nStart, nStop);
               if(nameHashcontains(sName))
               {
                   //Company nöfn innihalda hugsanlega meira en sérnöfn
                   /*
                   sNext = " "+m_sTextArray[nStop+1][0];
                   while(!sNext.equals(" ")&&nameHashcontains(sName+sNext))
                   {
                       nStop++;
                       sName =  sName + sNext;
                       sNext = " " + m_sTextArray[nStop+1][0];
                   }*/
                   sTag = getNameTag(sName);
                   //Ef location er true þá má nota öll gildi
                   if(bLocation)
                   {
                        updateTextArray(nStart, nStop,sTag);
                        //Geimi nafnið í Array list
                        //Geimi nafnið í Array list
                        m_CertainList.add(nStart);
                        m_CertainList.add(nStop);
                        //////itr.remove();
                   }
                   else
                   {
                       if(!sTag.equals("LOCATION"))
                       {
                           updateTextArray(nStart, nStop,sTag);
                           //Geimi nafnið í Array list
                           m_CertainList.add(nStart);
                           m_CertainList.add(nStop);
                           //////itr.remove();
                       }
                   }
               }
               //Ef skanner hefur greint hluta nafsins, þá merkja allt
               else if(nameHashcontains(m_sTextArray[nStart][0]))
               {
                   sTag = getNameTag(m_sTextArray[nStart][0]);
                   updateTextArray(nStart, nStop,sTag);
               }
           }
       }
   }

   public void findFromInitilas()
   {
       String sName;
       //int index;The
       int nStart;
       int nStop;
       String sTag = "";
       String sInitial;
       boolean bNotFound = true;
       Iterator itr;
       itr = m_IndexList.iterator();
       Iterator innerItr;
       
       //while (itr.hasNext())
       for(int index=0; index<m_sTextArray.length;index++)
       {
           //index = Integer.parseInt(itr.next().toString());
           if(isAllUpper(m_sTextArray[index][0]) && m_sTextArray[index][2].equals(""))
           {
               //Bera saman við fundin nöfn, er verið að vísa í eithvað
               //sem hefur verið nefnt áður
               innerItr = m_CertainList.iterator();
               while (innerItr.hasNext()&& bNotFound)
               {
                   nStart = Integer.parseInt(innerItr.next().toString());
                   nStop = Integer.parseInt(innerItr.next().toString());
                   //Ef ekki sama lengd þá óþarfi að halda áfram
                   if(m_sTextArray[index][0].length() == (nStop - nStart+1))
                   {
                       sInitial = "";
                       for(int i = nStart; i <= nStop; i++)
                       {
                           if(m_sTextArray[i][0].length() > 1)
                           {
                               sInitial = sInitial.concat(m_sTextArray[i][0].substring(0, 1));
                           }    
                       }
                       if(m_sTextArray[index][0].equals(sInitial))
                       {
                           sTag = m_sTextArray[nStart][2];
                           updateTextArray(index, index, sTag);
                           bNotFound = false;
                       }
                   }
               }
               //Ef tilvísun fannst ekki, merkja sem fyrirtæki
               if(bNotFound && m_sTextArray[index][0].length()<4)
               {
                   sTag = "COMPANY";
                   updateTextArray(index, index, sTag);
               }
               
               //////itr.remove();
           }

       }
   }
   /*Ef nafn fyritækis er ekki markað sem sérnafn af IceTagger
    * þá reyna að finna það útfrá endingu. 
    */
   public void lookForMissedCompanies()
   {
       for(int i=0; i<m_sTextArray.length;i++)
       {
           if(m_sTextArray[i][2].equals(""))
           {
              //Ef stakt hf, ehf o.sv.frv. Ef finnst þá athuga hvort næstu orð á undan byrji á stórum staf
               //Ath skoða í name hash. NameScanner fann að öllum líkindum rétta nafnið
               ////////////////////////////////////////////////////////////////////77
               if(isMissedCompanyEnding(m_sTextArray[i][0]))
               {
                   //fletta max þrjú orð til baka
                   for(int n=i-1;n>i-3;n--)
                   {
                       if(startsWithUpper(m_sTextArray[n][0]))
                       {
                           updateTextArray(n, i, "COMPANY");
                           break;
                       }
                   }
               }
           }
       }
   }
   public void lookForUnmatchedFromHash()
   {
       int nStop;
       String sName;
       for(int i=0; i<m_sTextArray.length;i++)
       {
           if(startsWithUpper(m_sTextArray[i][0])&&isNoun(m_sTextArray[i][1])&& m_sTextArray[i][2].equals(""))
           {
               nStop = findFullName(i+1);
               sName = getFullName(i, nStop);
               if(nameHashcontains(m_sTextArray[i][0]))
               {
                   String sTag = getNameTag(m_sTextArray[i][0]);
                   updateTextArray(i, nStop, sTag);
               }
           }
       }      
   }
   public void lookForListings()
   {
       int nStart;
       int nStop;
       int nIndex;
       boolean bIsList = false;
       String sTag= "";
       Iterator itr;
       itr = m_IndexList.iterator();
       while (itr.hasNext())
       {
           nIndex = Integer.parseInt(itr.next().toString());
           nStart = nIndex;
           nStop = findFullName(nStart);
           if(!m_sTextArray[nStop][2].equals(""))
           {
               sTag = m_sTextArray[nStop][2];
           }
           while(nStop < m_nLineCount-2 &&
                   m_IndexList.contains(nStop+2) &&
                   isInsicnificant(m_sTextArray[nStop+1][1])||
                   isConjunction(m_sTextArray[nStop+1][1])&&
                   bIsList)
           {
               nStart = nStop+2;
               if(m_IndexList.contains(nStart))
               {
                    nStop = findFullName(nStart);
                    if(!m_sTextArray[nStop][2].equals(""))
                    {
                        sTag = m_sTextArray[nStop][2];
                    }
                    bIsList = true;
               }
               else
               {
                   bIsList = false;
               }
           }
           //Ef endar á punkti þá upptalning
           if(bIsList && isEndOfList(m_sTextArray[nStop+1][1]))
           {
               for(int i=nIndex; i<=nStop; i++)
               {
                   if(!(!m_sTextArray[i][2].isEmpty()|| isInsicnificant(m_sTextArray[i][1])||isConjunction(m_sTextArray[i][1])))
                   {
                       m_sTextArray[i][2] = sTag;
                   }
               }
           }
           bIsList = false;
       }
   }
 
   public void looForPairs()
   {
       int nStart;
       int nStop;
       //boolean bIsPair = false;
       String sTag= "";
       Iterator itr;
       itr = m_IndexList.iterator();
       while (itr.hasNext())
       {
           nStart = Integer.parseInt(itr.next().toString());
           //Ef Þegar búið að merkja nafn þá henda index
           nStop = findFullName(nStart);
           sTag = m_sTextArray[nStart][2];
           //Ef á eftir nafni kemur samtenging og nýtt nafn, þá par
           if(m_sTextArray[nStop+1][0].equals("og")&&m_IndexList.contains(nStop+2))
           {
               int nStartSecond = nStop+2;
               int nStopSecond = findFullName(nStartSecond);
               String sTagSecond = m_sTextArray[nStartSecond][2];
               //Ef bæði tóm þá er ekkert hægt að gera
               if(!sTag.equals(sTagSecond))
               {
                   if(sTag.equals(""))
                   {
                       updateTextArray(nStart, nStop, sTagSecond);
                   }
                   if(sTagSecond.equals(""))
                   {
                       updateTextArray(nStartSecond, nStopSecond, sTag);
                   }
               }
           }
       }
   }

   public void findLocationsFromAdverb()
   {
       int nStop;
       String sTag= "LOCATION";
       String sName;

       for(int i=0; i<m_sTextArray.length;i++)
       {
           if(0<i && i < m_sTextArray.length&&startsWithUpper(m_sTextArray[i][0])&&isAdverb(m_sTextArray[i-1][1]) &&
                   m_sTextArray[i][2].equals("")&&( m_sTextArray[i-1][0].equals("í")|| m_sTextArray[i-1][0].equals("á")||
                    m_sTextArray[i-1][0].equals("Í")|| m_sTextArray[i-1][0].equals("Á"))&&!startsWithUpper(m_sTextArray[i+1][0]))
                   //|| m_sTextArray[i-1][0].equals("frá")|| m_sTextArray[i-1][0].equals("til")))
           {
                //nStop = findFullName(i);
                //sName = getFullName(i, nStop);
                updateTextArray(i, i, sTag);
                //this.loadNameHash(sName, sTag);
                //i = nStop;
           }
       }
   }
   //Xxx Xxxxxx Person
   public void cleanupPersons()
   {
       int nStart;
       String sTag= "PERSON";
       String sName;
       Iterator itr;
       itr = m_IndexList.iterator();
       while (itr.hasNext())
       {
           nStart = Integer.parseInt(itr.next().toString());

           if(m_sTextArray[nStart][2].equals(""))
           {
               if(nStart < m_sTextArray.length-2&&startsWithUpper(m_sTextArray[nStart+1][0])&&
                       m_sTextArray[nStart+1][2].equals("")&&!startsWithUpper(m_sTextArray[nStart+2][0]))
               {
                   updateTextArray(nStart, nStart+1, sTag);
                   sName = getFullName(nStart, nStart+1);
                   this.loadNameHash(sName, sTag);
               }
           }
       }
   }
   //Luka Zajc frá Slóveníu
   public void findPersonFromLocation()
   {
       int nStart;
       int nStop;
       String sTag= "PERSON";
       Iterator itr;
       itr = m_IndexList.iterator();
       while (itr.hasNext())
       {
           nStart = Integer.parseInt(itr.next().toString());
           //Ef Þegar búið að merkja nafn þá henda index
           if(m_sTextArray[nStart][2].equals(""))
           {
               nStop = findFullName(nStart);
               if(nStop<m_sTextArray.length-2&&m_sTextArray[nStop+1][0].equals("frá")&&
                       m_sTextArray[nStop+2][2].equals("LOCATION"))
               {
                   updateTextArray(nStart, nStop, sTag);
               }
           }
       }
   }
   //Litla Hraun

   public void lookForAdjectiveInName()
   {
       int nStart;
       String sTag= "";
       Iterator itr;
       itr = m_IndexList.iterator();
       while (itr.hasNext())
       {
           nStart = Integer.parseInt(itr.next().toString());

           if(isAdjective(m_sTextArray[nStart][1])&&!m_sTextArray[nStart+1][2].equals(""))
           {
               sTag = m_sTextArray[nStart+1][2];
               updateTextArray(nStart, nStart, sTag);
           }
       }
   }
/*
   {

       for(int i=1; i<m_sTextArray.length;i++)
       {
           if(startsWithUpper(m_sTextArray[i][0])&&isAdjective(m_sTextArray[i][1])&&
                   !m_sTextArray[i][2].equals("")&& m_sTextArray[i+1][2].equals("")&&
                   (isNoun(m_sTextArray[i+1][1])||isAdjective(m_sTextArray[i+1][1])))
           {
               m_sTextArray[i+1][2] = m_sTextArray[i][2];
               //hashSecondLook(i);
           }
       }
       /*for(int i=1; i<m_sTextArray.length;i++)
       {
           if((startsWithUpper(m_sTextArray[i][0])&&isAdjective(m_sTextArray[i][1])&& m_sTextArray[i][2].equals("")&&isNoun(m_sTextArray[i+1][1])&& !m_sTextArray[i-1][0].equals(""))
               ||(startsWithUpper(m_sTextArray[i][0])&&isNoun(m_sTextArray[i][1])&& m_sTextArray[i][2].equals("")&&isAdjective(m_sTextArray[i+1][1])&& !m_sTextArray[i-1][0].equals("")))
           {
               hashSecondLook(i);
           }
       }
   }*/
   public void lookForNounInName()
   {
       for(int i=1; i<m_sTextArray.length;i++)
       {
           if(startsWithUpper(m_sTextArray[i][0])&&isNoun(m_sTextArray[i][1])&&
                   m_sTextArray[i][2].equals("")&&isNoun(m_sTextArray[i+1][1])&&
                   m_sTextArray[i+1][2].equals("")&& !m_sTextArray[i-1][0].equals(""))
           {
               hashSecondLook(i);
           }
       }
   }
   //Háskólinn í Reykjavík
   public void findCompanyByLocation()
   {
       int nStart;
       int nStop;
       String sTag = "COMPANY";
       for(int i=0; i<m_sTextArray.length;i++)
       {
           if(m_sTextArray[i][2].equals("COMPANY")&&startsWithUpper(m_sTextArray[i+2][0])&&
                   isAdverb(m_sTextArray[i+1][1]))
           {
               nStart = i+2;
               nStop = findFullName(nStart);//nStop = findFullName(nStart+1);
               updateTextArray(nStart-1, nStop, sTag);
           }
       }
   }
   //Ef samstæð orð byrja á stórum staf þá líklega nafn.
   public void findFromCapitalLetters()
   {
       String sTag = "";
       String sName = "";
       int nStart = 0;
       int nStop = 0;
       for(int i=0; i<m_sTextArray.length;i++)
       {
           nStart = i;
           if(startsWithUpper(m_sTextArray[i][0])&&!isStartOfSentance(i)&&
                   startsWithUpper(m_sTextArray[i+1][0])&&m_sTextArray[i][2].equals(""))
           {
               while(startsWithUpper(m_sTextArray[i][0])||isForeign(m_sTextArray[i][1]))
               {
                   i++;
               }
               nStop=i-1;
               sName = getFullName(nStart, nStop);

               sTag = isRolePartOfName(nStart,nStop);
               if(!sTag.equals(""))
               {
                   updateTextArray(nStart, nStop, sTag);
                   this.loadNameHash(sName, sTag);
               }
               else
               {
                   sTag = isRoleFollowing(nStop+1);
                   if(!sTag.equals(""))
                   {
                       updateTextArray(nStart, nStop, sTag);
                       this.loadNameHash(sName, sTag);
                   }
                   else
                   {
                       sTag = isRolePreceeding(nStart-1);
                       if(!sTag.equals(""))
                       {
                           updateTextArray(nStart, nStop, sTag);
                           this.loadNameHash(sName, sTag);
                       }
                   }
               }
           }
       }
   }

   private void hashSecondLook(int index)
   {
       String sTag;
       String sName;
       int nStart;
       int nStop;
       nStart = index;
       nStop = findFullName(nStart+2);
       sName = m_sTextArray[index][0]+" "+m_sTextArray[index+1][0]+ " "+getFullName(nStart, nStop);
       if(nameHashcontains(sName))
       {
           sTag = getNameTag(sName);
           updateTextArray(nStart, nStop, sTag);
       }
       else if(nameHashcontains(m_sTextArray[nStart][0]))
       {
           sTag = getNameTag(m_sTextArray[nStart][0]);
           updateTextArray(nStart, nStop, sTag);
       }
       else
       {
           sTag = isRoleFollowing(nStop+1);
           if(!sTag.equals(""))
           {
               updateTextArray(nStart, nStop, sTag);
               this.loadNameHash(sName, sTag);
           }
           else
           {
               sTag = isRolePreceeding(nStart-1);
               if(!sTag.equals(""))
               {
                   updateTextArray(nStart, nStop, sTag);
                   this.loadNameHash(sName, sTag);
               }
               else
               {
                   sTag = isRolePartOfName(nStart, nStop);
                   if(!sTag.equals(""))
                   {
                       updateTextArray(nStart, nStop, sTag);
                       this.loadNameHash(sName, sTag);
                   }
               }
           }
       }
   }
   public void lookForUnmatchedFromRole()
   {
       Role role = Role.ROLE_PERSON;
       int nStop;
       for(int i=0; i<m_sTextArray.length;i++)
       {
           if(roleHashContains(m_sTextArray[i][0]))
           {
               String sTag = getRoleTag(m_sTextArray[i][0]).replaceAll("ROLE_", "");
               if(startsWithUpper(m_sTextArray[i+1][0])&& m_sTextArray[i+1][2].equals(""))
               {
                   nStop = findFullName(i+1);
                   switch(role.toRole(sTag))
                   {
                       case ROLE_PERSON :
                           if(hasArticleEnding(m_sTextArray[i][0]))
                           {
                               updateTextArray(i+1, nStop, sTag);
                           }
                           else
                           {
                               updateTextArray(i+1, nStop, "COMPANY");
                           }

                       case ROLE_COMPANY :
                           updateTextArray(i+1, nStop, sTag);
                       case ROLE_LOCATION :
                           updateTextArray(i+1, nStop, sTag);
                       default:
                           break;
                   }
               }
               if(startsWithUpper(m_sTextArray[i-1][0])&& m_sTextArray[i-1][2].equals(""))
               {
                    nStop = findFullName(i);
                    String sName = getFullName(i-1, nStop);
                    updateTextArray(i-1, nStop, sTag);
               }
           }
       }
   }
   public void  findFromRole()
   {
       String sName;
       int index;
       int nStart;
       int nStop;
       Role role = Role.ROLE_PERSON;
       String sTag;
       Iterator itr;
       itr = m_IndexList.iterator();
       while (itr.hasNext())
       {
           index = Integer.parseInt(itr.next().toString());
           
           //Ef Þegar búið að merkja nafn þá má henda index
           if(!m_sTextArray[index][2].equals(""))
           {
               //////itr.remove();
           }
           else
           {
               //geimi hvar nafn birjaði
               nStart = index;
               nStop = findFullName(nStart);//nStop = findFullName(nStart+1);
               sName =  getFullName(nStart, nStop);
               
               sTag = isRolePreceeding(nStart-1);
               if(!sTag.equals(""))
               {
                   updateTextArray(nStart, nStop, sTag);
                   this.loadNameHash(sName, sTag);
                   //////itr.remove();
               }
               else
               {
                   sTag = isRoleFollowing(nStop+1);
                   if(!sTag.equals(""))
                   {
                       updateTextArray(nStart, nStop, sTag);
                       this.loadNameHash(sName, sTag);
                       //////itr.remove();
                   }
                   else
                   {
                       sTag = isRolePartOfName(nStart, nStop);
                       if(!(sTag.equals("")))
                       {
                           updateTextArray(nStart, nStop, sTag);
                           this.loadNameHash(sName, sTag);
                           //////itr.remove();
                       }
                   }
               }
               //Role name, name
               if(index >1&&m_sTextArray[index-1][0].equals(","))
               {
                   if(isPartOfKnownName(index-2))
                   {
                       int nStartOfName = getStartOfKnownName(index-2);
                       if(nStartOfName > 0&&roleHashContains(m_sTextArray[nStartOfName-1][0]))
                       {
                           String sSwitch = getRoleTag(m_sTextArray[nStartOfName-1][0]);
                           switch(role.toRole(sSwitch))
                           {
                               case ROLE_PERSON :
                                   sTag = "PERSON";
                                   break;
                               case ROLE_COMPANY :
                                   sTag = "COMPANY";
                                   break;
                               case ROLE_LOCATION :
                                   sTag = "LOCATION";
                                   break;
                               case RELATION_PERSON :
                                   sTag = "PERSON";
                                   break;
                               case ROLE_EVENT :
                                   sTag = "EVENT";
                                   break;
                               default:
                                   sTag = "";
                                   break;
                            }
                            if(!sTag.equals(""))
                           {
                               updateTextArray(nStart, nStop, sTag);
                               this.loadNameHash(sName, sTag);
                           }
                       }
                   }
                   //Role noune, name
                   else if(index>3&&isNoun(m_sTextArray[index-2][1]))
                   {
                       if(roleHashContains(m_sTextArray[index-3][0]))
                       {
                           String sSwitch = getRoleTag(m_sTextArray[index-3][0]);
                           switch(role.toRole(sSwitch))
                           {
                               case ROLE_PERSON :
                                   sTag = "PERSON";
                                   break;
                               case ROLE_COMPANY :
                                   sTag = "COMPANY";
                                   break;
                               case ROLE_LOCATION :
                                   sTag = "LOCATION";
                                   break;
                               case RELATION_PERSON :
                                   sTag = "PERSON";
                                   break;
                               case ROLE_EVENT :
                                   sTag = "EVENT";
                                   break;
                               default:
                                   sTag = "";
                                   break;
                            }
                           if(!sTag.equals(""))
                           {
                               updateTextArray(nStart, nStop, sTag);
                               this.loadNameHash(sName, sTag);
                           }
                       }
                   }
               }
               //Relation pronoun Name
               if(index >2&&isPronoun(m_sTextArray[index-1][1]))
               {
                   if(roleHashContains(m_sTextArray[index-2][0]))
                   {
                       String sSwitch = getRoleTag(m_sTextArray[index-2][0]);
                       switch(role.toRole(sSwitch))
                       {
                           case RELATION_PERSON :
                               sTag = "PERSON";
                               break;
                           default:
                               sTag = "";
                               break;
                       }
                       if(!sTag.equals(""))
                       {
                           updateTextArray(nStart, nStop, sTag);
                           this.loadNameHash(sName, sTag);
                       }
                   }
               }
           }
       }
   }
  //Britney, Jones, Blöndal
   public void  findPersonFromRole()
   {
       String sName;
       int index;
       int nStart;
       int nStop;
       Role role = Role.ROLE_PERSON;
       String sTag;
       Iterator itr;
       itr = m_IndexList.iterator();
       while (itr.hasNext())
       {
           index = Integer.parseInt(itr.next().toString());

           //Ef Þegar búið að merkja nafn þá má henda index
           if(!m_sTextArray[index][2].equals(""))
           {
               //////itr.remove();
           }
           else
           {
               //geimi hvar nafn birjaði
               nStart = index;
               nStop = findFullName(nStart);//nStop = findFullName(nStart+1);
               sName =  getFullName(nStart, nStop);

               sTag = isRolePreceeding(nStart-1);
               if(sTag.equals("PERSON"))
               {
                   updateTextArray(nStart, nStop, sTag);
                   this.overwriteNameHash(sName, sTag);
               }
               else
               {
                   sTag = isRoleFollowing(nStop+1);
                   if(sTag.equals("PERSON"))
                   {
                       updateTextArray(nStart, nStop, sTag);
                       this.overwriteNameHash(sName, sTag);
                   }
               }
               //Role noun, person name
               if(index >1&&m_sTextArray[index-1][0].equals(","))
               {
                   if(isPartOfKnownName(index-2))
                   {
                       int nStartOfName = getStartOfKnownName(index-2);
                       if(nStartOfName > 0&&roleHashContains(m_sTextArray[nStartOfName-1][0]))
                       {
                           String sSwitch = getRoleTag(m_sTextArray[nStartOfName-1][0]);
                           switch(role.toRole(sSwitch))
                           {
                               case ROLE_PERSON :
                                   sTag = "PERSON";
                                   break;
                               case ROLE_COMPANY :
                                   sTag = "COMPANY";
                                   break;
                               case ROLE_LOCATION :
                                   sTag = "LOCATION";
                                   break;
                               case RELATION_PERSON :
                                   sTag = "PERSON";
                                   break;
                               case ROLE_EVENT :
                                   sTag = "EVENT";
                                   break;
                               default:
                                   sTag = "";
                                   break;
                            }
                            if(sTag.equals("PERSON"))
                           {
                               updateTextArray(nStart, nStop, sTag);
                               this.overwriteNameHash(sName, sTag);
                           }
                       }
                   }
                   else if(index>3&&isNoun(m_sTextArray[index-2][1]))
                   {
                       if(roleHashContains(m_sTextArray[index-3][0]))
                       {
                           String sSwitch = getRoleTag(m_sTextArray[index-3][0]);
                           switch(role.toRole(sSwitch))
                           {
                               case ROLE_PERSON :
                                   sTag = "PERSON";
                                   break;
                               case ROLE_COMPANY :
                                   sTag = "COMPANY";
                                   break;
                               case ROLE_LOCATION :
                                   sTag = "LOCATION";
                                   break;
                               case RELATION_PERSON :
                                   sTag = "PERSON";
                                   break;
                               case ROLE_EVENT :
                                   sTag = "EVENT";
                                   break;
                               default:
                                   sTag = "";
                                   break;
                            }
                           if(sTag.equals("PERSON"))
                           {
                               updateTextArray(nStart, nStop, sTag);
                               this.overwriteNameHash(sName, sTag);
                           }
                       }
                   }
               }
               //Relation pronoun Name
               if(index >2&&isPronoun(m_sTextArray[index-1][1]))
               {
                   if(roleHashContains(m_sTextArray[index-2][0]))
                   {
                       String sSwitch = getRoleTag(m_sTextArray[index-2][0]);
                       switch(role.toRole(sSwitch))
                       {
                           case RELATION_PERSON :
                               sTag = "PERSON";
                               break;
                           default:
                               sTag = "";
                               break;
                       }
                       if(!sTag.equals(""))
                       {
                           updateTextArray(nStart, nStop, sTag);
                           this.loadNameHash(sName, sTag);
                       }
                   }
               }
           }
       }
   }

   private String isRoleFollowing(int index)
   {
       Role role = Role.ROLE_PERSON;
       String sTag;
       if(index < m_nLineCount)
       {
           //Ef t.d. komma, hlaupa yfir
           while(index < m_nLineCount&&isInsicnificant(m_sTextArray[index][0])||isAdjective(m_sTextArray[index][1]))
           {
               index++;
           }
                //Role getur bara verið nafnorð
                if(index < m_nLineCount&&isNoun(m_sTextArray[index][1])&&roleHashContains(m_sTextArray[index][0]))
                {
                    sTag = getRoleTag(m_sTextArray[index][0]);
                    switch(role.toRole(sTag))
                    {
                        case ROLE_PERSON :
                            return "PERSON";
                        case ROLE_COMPANY :
                            return "COMPANY";
                        case ROLE_LOCATION :
                            return "LOCATION";
                        case RELATION_PERSON :
                            return "PERSON";
                        case ROLE_EVENT :
                            return "EVENT";
                        default:
                            return "";
                    }
                }
            return "";     
       }
       return "";
   }
   private String isRolePartOfName(int start, int stop)
   {
       Role role = Role.ROLE_PERSON;
       String sTag;
       for(int i=start; i<=stop; i++)
       {
            if(roleHashContains(m_sTextArray[i][0]))
            {
                sTag = getRoleTag(m_sTextArray[i][0]);
                //Role getur ekki verið hluti af mannsnafni
                if(sTag.equals("ROLE_LOCATION")&&!m_bFormOverFunction)
                {
                    return "";
                }
                switch(role.toRole(sTag))
                {
                    case ROLE_COMPANY :
                        return "COMPANY";
                    case ROLE_LOCATION :
                        return "LOCATION";
                    case ROLE_EVENT :
                       return "EVENT";
                    default:
                        return "";
                }
            }
       }
       return "";
   }
   private String isRolePreceeding(int index)
   {
       if(index >= 0)
       {
           Role role = Role.ROLE_PERSON;
           String sTag;
           if(isInsicnificant(m_sTextArray[index][0]))
           {
               index--;
           }
           if(isNoun(m_sTextArray[index][1])&&roleHashContains(m_sTextArray[index][0]))
           {
               sTag = getRoleTag(m_sTextArray[index][0]);

               switch(role.toRole(sTag))
               {
                   case ROLE_PERSON :
                       if(hasArticleEnding(m_sTextArray[index][1]))
                       {
                           return "PERSON";
                       }
                       else
                       {
                           return "COMPANY";
                       }
                   case ROLE_COMPANY :
                       return "COMPANY";
                   case ROLE_LOCATION :
                       return "LOCATION";
                   case RELATION_PERSON :
                       return "PERSON";
                   case ROLE_EVENT :
                       return "EVENT";
                   default:
                       return "";
               }
           }
           return "";
       }
       return "";
   }
   public void findFromAction()
   {
       String sName;
       int index;
       int nStart;
       int nStop;
       String sTag = "";
       Iterator itr;
       itr = m_IndexList.iterator();
       while (itr.hasNext())
       {
           index = Integer.parseInt(itr.next().toString());

           //Ef Þegar búið að merkja nafn þá má henda index
           if(!m_sTextArray[index][2].equals(""))
           {
               //////itr.remove();
           }
           else
           {
               //geimi hvar nafn birjaði
               nStart = index;
               nStop = findFullName(nStart);
               sName =  getFullName(nStart, nStop);

               if(index < m_sTextArray.length && isPersonAction(m_sTextArray[nStop+1][0]))
               {
                   sTag = "PERSON";
               }
               else if(index > 0 && isPersonAction(m_sTextArray[nStart-1][0]))
               {
                   sTag = "PERSON";
               }
               if(index < m_sTextArray.length && isCompanyAction(m_sTextArray[nStop+1][0]))
               {
                   sTag = "COMPANY";
               }
               else if(index > 0 && isCompanyAction(m_sTextArray[nStart-1][0]))
               {
                   sTag = "COMPANY";
               }

               if(!sTag.equals(""))
               {
                   updateTextArray(nStart, nStop, sTag);
                   this.loadNameHash(sName, sTag);
                   //////itr.remove();
                   sTag="";
               }
           }
       }
   }
   public void markMiscEntities()
   {
       int index;
       Iterator itr;
       itr = m_IndexList.iterator();
       String sTag = "MISC";
       while (itr.hasNext())
       {
           index = Integer.parseInt(itr.next().toString());

           if(m_sTextArray[index][2].equals(""))
           {
               updateTextArray(index, index, sTag);
           }
       }
   }
   //Ef tveir indexar standa saman, þá eru þeir hluti af sama nafni
   public void completePartlyFoundNames()
   {
       int index;
       Iterator itr;
       itr = m_IndexList.iterator();
       while (itr.hasNext())
       {
           index = Integer.parseInt(itr.next().toString());
           if(index > 0 && index < m_sTextArray.length-2&&m_IndexList.contains(index+1)&&
                   !m_IndexList.contains(index+2)&&!m_IndexList.contains(index-1))
           {
               //Ef tagið er það sama, þá þarf ekkert að gera
               if(!m_sTextArray[index][2].equals(m_sTextArray[index+1][2]))
               {
                   if(m_sTextArray[index][2].equals(""))
                   {
                       m_sTextArray[index][2] = m_sTextArray[index+1][2];
                   }
                   else
                   {
                       m_sTextArray[index+1][2] = m_sTextArray[index][2];
                   }
               }
           }
           /*
           if(m_sTextArray[index][2].equals(""))
           {
               if(index < m_sTextArray.length && !m_sTextArray[index+1][2].equals(""))
               {
                   while(index>0&&(m_IndexList.contains(index)||isForeign(m_sTextArray[index][1])))
                   {
                        m_sTextArray[index][2] = m_sTextArray[index+1][2];
                        index--;
                   }
                   //////itr.remove();
               }
               if(index > 0 && !m_sTextArray[index-1][2].equals(""))
               {
                   while(index<m_sTextArray.length&&(m_IndexList.contains(index)||isForeign(m_sTextArray[index][1])))
                   {
                        m_sTextArray[index][2] = m_sTextArray[index-1][2];
                        index++;
                   }
                   //////itr.remove();
               }            
           }*/
       }
   }
   /*Ekki öll skírnarnöfn merkt sem sérnöfn ef fremst í setningum. Steppa í gegnum
    * Texta arrayið og bý til lista af þekktum skýrnarnöfnum. Ef síðan annað orð í setningu
    * er stakt mannsnafn, og ekki á lista yfir þekkt skírnarnöfn, þá er fyrsta orð í setningunni
    * hluti af skírnarnafn
   */
   public void fixPersonNames()
    {
       for(int i=0; i<m_sTextArray.length-1; i++)
       {
           if(m_sTextArray[i][2].equals("PERSON")&&m_sTextArray[i+1][2].equals("PERSON"))
           {
               m_listFirstName.add(m_sTextArray[i][0]);
           }
       }
       for(int i=0; i<m_sTextArray.length-4; i++)
       {
           //Steingrímur J. Sigfússon
           if(m_sTextArray[i][2].equals("PERSON")&&m_sTextArray[i+1][2].equals("PERSON")&&
                   m_sTextArray[i+2][0].equals("")&&m_sTextArray[i+3][2].equals("PERSON"))
           {
               updateTextArray(i+2, i+2, "PERSON");
           }
           //Steingrímur J . Sigfússon
           if(m_sTextArray[i][2].equals("PERSON")&&isSingulUpper(m_sTextArray[i+1][0])&&
                   m_sTextArray[i+2][0].equals(".")&&
                   m_sTextArray[i+3][0].equals("")&&m_sTextArray[i+4][2].equals("PERSON"))
           {
               updateTextArray(i+1, i+3, "PERSON");
           }
           //Pabbi Britney heldur verndarhendi
           if((isStartOfSentance(i)&& m_sTextArray[i][2].equals("")&& isLastName(m_sTextArray[i+1][0]))||
                   (isStartOfSentance(i)&& m_sTextArray[i][2].equals("")&& m_sTextArray[i+1][2].equals("PERSON")&&
                   !startsWithUpper(m_sTextArray[i+2][0])&&!isFirstName(m_sTextArray[i+1][0])))
           {
               updateTextArray(i, i, "PERSON");
           }
       }
       
   }
   //Háskólinn í Reykjavík
   public void fixCompanyLocation()
   {
       for(int i=0; i<m_sTextArray.length-2; i++)
       {
           if(m_sTextArray[i][2].equals("COMPANY")&&isAdverb(m_sTextArray[i+1][1])&&
                   m_sTextArray[i+2][2].equals("LOCATION"))
           {
               if(isSchool(m_sTextArray[i][0])||isHospital(m_sTextArray[i][0]))
               {
                   overwriteTextArray(i+1, i+2, "COMPANY");
               }
           }
       }
   }
   //Nafnorð og lísingarorð sem byrja á litlumstaf geta eingöngu átt við fyrirtæki
   public void cleanup()
   {
       for(int i=0; i<m_sTextArray.length; i++)
       {
           if(!m_sTextArray[i][2].equals("")&&!startsWithUpper(m_sTextArray[i][0])&&
                   (isGenative(m_sTextArray[i][1])||isAdjective(m_sTextArray[i][1])))
           {
               if(!m_sTextArray[i][2].equals("COMPANY"))
               {
                   m_sTextArray[i][2]="";
               }
           }
           //Mannsnafn getur ekki verið Role eða relation
           if(m_sTextArray[i][2].equals("PERSON")&& m_sRoleHash.containsKey(m_sTextArray[i][0]))
           {
               m_sTextArray[i][2]="";
           }
           //Bankastræti 1
           if(i<m_sTextArray.length-1&&m_sTextArray[i][2].equals("LOCATION")&&isNumber(m_sTextArray[i+1][1]))
           {
               m_sTextArray[i+1][2]="LOCATION";
           }
       }
   }
   //Aldur á oftast við persónur
   public void findPersonByAge()
   {
       String sName;
       int index;
       int nStart;
       int nStop;
       String sTag = "PERSON";
       Iterator itr;
       itr = m_IndexList.iterator();
       while (itr.hasNext())
       {
           index = Integer.parseInt(itr.next().toString());

           //Ef Þegar búið að merkja nafn þá má henda index
           if(!m_sTextArray[index][2].equals(""))
           {
               //////itr.remove();
           }
            else
           {
               //geimi hvar nafn birjaði
               nStart = index;
               nStop = findFullName(nStart);
               sName =  getFullName(nStart, nStop);
               if(ageIsFollowing(nStop))
               {
                   updateTextArray(nStart, nStop, sTag);
                   this.loadNameHash(sName, sTag);
               }
            }
       }
   }
   //Athuga hvort nafn sé þekkt í öðru falli
   public void lookForDifferentCases()
   {
       String sName;
       String sNameTag;
       int index;
       String sTag = "";
       Iterator itr;
       itr = m_IndexList.iterator();
       while (itr.hasNext())
       {
           index = Integer.parseInt(itr.next().toString());

           //Ef Þegar búið að merkja nafn þá má henda index
           if(!m_sTextArray[index][2].equals(""))
           {
               //itr.remove();
           }
           else
           {
               sName =  m_sTextArray[index][0];
               sNameTag = m_sTextArray[index][1];
               sTag = tryDifferntCases(sName, sNameTag);
               //Hljóðvarp
               if(sTag.equals(""))
               {
                   String sSecondName = sName.replaceFirst("a", "ö");
                   sTag = tryDifferntCases(sSecondName, sNameTag);
               }
               if(sTag.equals(""))
               {
                   String sSecondName = sName.replaceFirst("a", "e");
                   sTag = tryDifferntCases(sSecondName, sNameTag);
               }
               if(sTag.equals(""))
               {
                   String sSecondName = sName.replaceFirst("e", "a");
                   sTag = tryDifferntCases(sSecondName, sNameTag);
               }
               if(sTag.equals(""))
               {
                   String sSecondName = sName.replaceFirst("ö", "e");
                   sTag = tryDifferntCases(sSecondName, sNameTag);
               }
               if(sTag.equals(""))
               {
                   String sSecondName = sName.replaceFirst("ö", "a");
                   sTag = tryDifferntCases(sSecondName, sNameTag);
               }
               if(sTag.equals(""))
               {
                   String sSecondName = sName.replaceFirst("a", "ö");
                   sTag = tryDifferntCases(sSecondName, sNameTag);
               }
               if(!sTag.equals(""))
               {
                   updateTextArray(index, index, sTag);
                   this.loadNameHash(sName, sTag);
                   ////itr.remove();
                   sTag="";
               }
           }
       }
   }
   private String tryDifferntCases(String sName, String sNameTag)
   {
       String sTag = "";
       if(sName.endsWith("s"))
       {
           sTag = endsWithS(sName.substring(0, sName.length()-1));
       }
       if(sName.endsWith("i"))
       {
           sTag = endsWithI(sName.substring(0, sName.length()-1));
       }
       if(sName.endsWith("a"))
       {
           sTag = endsWithA(sName.substring(0, sName.length()-1));
       }
       if(sName.endsWith("u"))
       {
           sTag = endsWithU(sName.substring(0, sName.length()-1));
       }
       if(sName.endsWith("ur"))
       {
           sTag = endsWithUR(sName.substring(0, sName.length()-2));
       }
       if(sName.endsWith("ar"))
       {
           sTag = endsWithAR(sName.substring(0, sName.length()-2));
       }
       if(sName.endsWith("nn"))
       {
           sTag = endsWithN(sName.substring(0, sName.length()-1));
       }
       if(sTag.equals(""))
       {
           sTag = noEnding(sName);
       }
       return sTag;

   }
   private String endsWithS(String sName)
   {              
       if(nameHashcontains(sName))
       {
           return getNameTag(sName);
       }
       String sNameI = sName.concat("i");
       if(nameHashcontains(sNameI))
       {
           return getNameTag(sNameI);
       }  
       String sNameN = sName.concat("n");
       if(nameHashcontains(sNameN))
       {
           return getNameTag(sNameN);
       }
       return "";
   }
   private String endsWithI(String sName)
   {              
       if(nameHashcontains(sName))
       {
           return getNameTag(sName);
       }
       String sNameA = sName.concat("a");
       if(nameHashcontains(sNameA))
       {
           return getNameTag(sNameA);
       }
       String sNameAR = sName.concat("ar");
       if(nameHashcontains(sNameAR))
       {
           return getNameTag(sNameAR);
       }
       String sNameS = sName.concat("s");
       if(nameHashcontains(sNameS))
       {
           return getNameTag(sNameS);
       }
       String sNameN = sName.concat("n");
       if(nameHashcontains(sNameN))
       {
           return getNameTag(sNameN);
       }
       String sNameUR = sName.concat("ur");
       if(nameHashcontains(sNameUR))
       {
           return getNameTag(sNameUR);
       }
       return "";
   }
   private String endsWithA(String sName)
   {              
       if(nameHashcontains(sName))
       {
           return getNameTag(sName);
       }
       String sNameI = sName.concat("i");
       if(nameHashcontains(sNameI))
       {
           return getNameTag(sNameI);
       }  
       String sNameU = sName.concat("u");
       if(nameHashcontains(sNameU))
       {
           return getNameTag(sNameU);
       }
       return "";
   }
   private String endsWithU(String sName)
   {              
       if(nameHashcontains(sName))
       {
           return getNameTag(sName);
       }
       String sNameA = sName.concat("a");
       if(nameHashcontains(sNameA))
       {
           return getNameTag(sNameA);
       }  
       String sNameAR = sName.concat("ar");
       if(nameHashcontains(sNameAR))
       {
           return getNameTag(sNameAR);
       }
       return "";
   }
   private String endsWithAR(String sName)
   {              
       if(nameHashcontains(sName))
       {
           return getNameTag(sName);
       }
       String sNameI = sName.concat("i");
       if(nameHashcontains(sNameI))
       {
           return getNameTag(sNameI);
       }  
       String sNameUR = sName.concat("ur");
       if(nameHashcontains(sNameUR))
       {
           return getNameTag(sNameUR);
       }
       String sNameU = sName.concat("u");
       if(nameHashcontains(sNameU))
       {
           return getNameTag(sNameU);
       }
       return "";
   }
   private String endsWithUR(String sName)
   {              
       if(nameHashcontains(sName))
       {
           return getNameTag(sName);
       }
       String sNameI = sName.concat("i");
       if(nameHashcontains(sNameI))
       {
           return getNameTag(sNameI);
       }
       String sNameS = sName.concat("s");
       if(nameHashcontains(sNameS))
       {
           return getNameTag(sNameS);
       }
       String sNameAR = sName.concat("ar");
       if(nameHashcontains(sNameAR))
       {
           return getNameTag(sNameAR);
       }
       return "";
   }
   private String endsWithN(String sName)
   {              
       if(nameHashcontains(sName))
       {
           return getNameTag(sName);
       }
       String sNameI = sName.concat("i");
       if(nameHashcontains(sNameI))
       {
           return getNameTag(sNameI);
       }  
       String sNameS = sName.concat("s");
       if(nameHashcontains(sNameS))
       {
           return getNameTag(sNameS);
       }
       return "";
   }
    private String noEnding(String sName)
   {              
       if(nameHashcontains(sName))
       {
           return getNameTag(sName);
       }
       String sNameA = sName.concat("a");
       if(nameHashcontains(sNameA))
       {
           return getNameTag(sNameA);
       }
       String sNameAR = sName.concat("ar");
       if(nameHashcontains(sNameAR))
       {
           return getNameTag(sNameAR);
       }
       String sNameS = sName.concat("s");
       if(nameHashcontains(sNameS))
       {
           return getNameTag(sNameS);
       }
       String sNameN = sName.concat("n");
       if(nameHashcontains(sNameN))
       {
           return getNameTag(sNameN);
       }
       String sNameUR = sName.concat("ur");
       if(nameHashcontains(sNameUR))
       {
           return getNameTag(sNameUR);
       }
       String sNameI = sName.concat("i");
       if(nameHashcontains(sNameI))
       {
           return getNameTag(sNameI);
       } 
       String sNameU = sName.concat("u");
       if(nameHashcontains(sNameU))
       {
           return getNameTag(sNameU);
       }
       return "";
   }
   private boolean tryDifferntCasesBolean(String sName, String sTag)
   {
       boolean bReturn = false;
       //if(sName.endsWith("s")&&isGenative(sTag))
       if(sName.endsWith("s"))
       {
           bReturn = endsWithSBolean(sName.substring(0, sName.length()-1));
       }
       if(sName.endsWith("i"))
       {
           bReturn = endsWithIBolean(sName.substring(0, sName.length()-1));
       }
       if(sName.endsWith("a"))
       {
           bReturn = endsWithABolean(sName.substring(0, sName.length()-1));
       }
       if(sName.endsWith("u"))
       {
           bReturn = endsWithUBolean(sName.substring(0, sName.length()-1));
       }
       //if(sName.endsWith("ur")&&isNomative(sTag))
       if(sName.endsWith("ur"))
       {
           bReturn = endsWithURBolean(sName.substring(0, sName.length()-2));
       }
       //if(sName.endsWith("ar")&&isGenative(sTag))
       if(sName.endsWith("ar"))
       {
           bReturn = endsWithARBolean(sName.substring(0, sName.length()-2));
       }
       if(sName.endsWith("nn"))
       {
           bReturn = endsWithNBolean(sName.substring(0, sName.length()-1));
       }
       if(!bReturn)
       {
           bReturn = noEndingBolean(sName);
       }
       return bReturn;

   }
   private boolean endsWithSBolean(String sName)
   {              
       if(m_NameList.contains(sName))
       {
           return true;
       }
       String sNameI = sName.concat("i");
       if(m_NameList.contains(sNameI))
       {
           return true;
       }  
       String sNameN = sName.concat("n");
       if(m_NameList.contains(sNameN))
       {
           return true;
       }
       return false;
   }
   private boolean endsWithIBolean(String sName)
   {              
       if(m_NameList.contains(sName))
       {
           return true;
       }
       String sNameA = sName.concat("a");
       if(m_NameList.contains(sNameA))
       {
           return true;
       }
       String sNameAR = sName.concat("ar");
       if(m_NameList.contains(sNameAR))
       {
           return true;
       }
       String sNameS = sName.concat("s");
       if(m_NameList.contains(sNameS))
       {
           return true;
       }
       String sNameN = sName.concat("n");
       if(m_NameList.contains(sNameN))
       {
           return true;
       }
       String sNameUR = sName.concat("ur");
       if(m_NameList.contains(sNameUR))
       {
           return true;
       }
       return false;
   }
   private boolean endsWithABolean(String sName)
   {              
       if(m_NameList.contains(sName))
       {
           return true;
       }
       String sNameI = sName.concat("i");
       if(m_NameList.contains(sNameI))
       {
           return true;
       }  
       String sNameU = sName.concat("u");
       if(m_NameList.contains(sNameU))
       {
           return true;
       }
       return false;
   }
   private boolean endsWithUBolean(String sName)
   {              
       if(m_NameList.contains(sName))
       {
           return true;
       }
       String sNameA = sName.concat("a");
       if(m_NameList.contains(sNameA))
       {
           return true;
       }  
       String sNameAR = sName.concat("ar");
       if(m_NameList.contains(sNameAR))
       {
           return true;
       }
       return false;
   }
   private boolean endsWithARBolean(String sName)
   {              
       if(m_NameList.contains(sName))
       {
           return true;
       }
       String sNameI = sName.concat("i");
       if(m_NameList.contains(sNameI))
       {
           return true;
       }  
       String sNameUR = sName.concat("ur");
       if(m_NameList.contains(sNameUR))
       {
           return true;
       }
       String sNameU = sName.concat("u");
       if(m_NameList.contains(sNameU))
       {
           return true;
       }
       String sNameS = sName.concat("s");
       if(m_NameList.contains(sNameS))
       {
           return true;
       }
       return false;
   }
   private boolean endsWithURBolean(String sName)
   {              
       if(m_NameList.contains(sName))
       {
           return true;
       }
       String sNameI = sName.concat("i");
       if(m_NameList.contains(sNameI))
       {
           return true;
       }  
       String sNameAR = sName.concat("ar");
       if(m_NameList.contains(sNameAR))
       {
           return true;
       }
       return false;
   }
   private boolean endsWithNBolean(String sName)
   {              
       if(m_NameList.contains(sName))
       {
           return true;
       }
       String sNameI = sName.concat("i");
       if(m_NameList.contains(sNameI))
       {
           return true;
       }  
       String sNameS = sName.concat("s");
       if(m_NameList.contains(sNameS))
       {
           return true;
       }
       return false;
   }
    private boolean noEndingBolean(String sName)
   {              
       if(m_NameList.contains(sName))
       {
           return true;
       }
       String sNameA = sName.concat("a");
       if(m_NameList.contains(sNameA))
       {
           return true;
       }
       String sNameAR = sName.concat("ar");
       if(m_NameList.contains(sNameAR))
       {
           return true;
       }
       String sNameS = sName.concat("s");
       if(m_NameList.contains(sNameS))
       {
           return true;
       }
       String sNameN = sName.concat("n");
       if(m_NameList.contains(sNameN))
       {
           return true;
       }
       String sNameUR = sName.concat("ur");
       if(m_NameList.contains(sNameUR))
       {
           return true;
       }
       String sNameI = sName.concat("i");
       if(m_NameList.contains(sNameI))
       {
           return true;
       } 
       String sNameU = sName.concat("u");
       if(m_NameList.contains(sNameU))
       {
           return true;
       }
       return false;
   }

    private boolean tryDifferntCasesFirstName(String sName)
   {
       boolean bReturn = false;
       //if(sName.endsWith("s")&&isGenative(sTag))
       if(sName.endsWith("s"))
       {
           bReturn = endsWithSFirstName(sName.substring(0, sName.length()-1));
       }
       if(sName.endsWith("i"))
       {
           bReturn = endsWithIFirstName(sName.substring(0, sName.length()-1));
       }
       if(sName.endsWith("a"))
       {
           bReturn = endsWithAFirstName(sName.substring(0, sName.length()-1));
       }
       if(sName.endsWith("u"))
       {
           bReturn = endsWithUBolean(sName.substring(0, sName.length()-1));
       }
       //if(sName.endsWith("ur")&&isNomative(sTag))
       if(sName.endsWith("ur"))
       {
           bReturn = endsWithURFirstName(sName.substring(0, sName.length()-2));
       }
       //if(sName.endsWith("ar")&&isGenative(sTag))
       if(sName.endsWith("ar"))
       {
           bReturn = endsWithARFirstName(sName.substring(0, sName.length()-2));
       }
       if(sName.endsWith("nn"))
       {
           bReturn = endsWithNBolean(sName.substring(0, sName.length()-1));
       }
       if(!bReturn)
       {
           bReturn = noEndingFirstName(sName);
       }
       return bReturn;

   }
   private boolean endsWithSFirstName(String sName)
   {
       if(m_listFirstName.contains(sName))
       {
           return true;
       }
       String sNameI = sName.concat("i");
       if(m_listFirstName.contains(sNameI))
       {
           return true;
       }
       String sNameN = sName.concat("n");
       if(m_listFirstName.contains(sNameN))
       {
           return true;
       }
       return false;
   }
   private boolean endsWithIFirstName(String sName)
   {
       if(m_listFirstName.contains(sName))
       {
           return true;
       }
       String sNameA = sName.concat("a");
       if(m_listFirstName.contains(sNameA))
       {
           return true;
       }
       String sNameAR = sName.concat("ar");
       if(m_listFirstName.contains(sNameAR))
       {
           return true;
       }
       String sNameS = sName.concat("s");
       if(m_listFirstName.contains(sNameS))
       {
           return true;
       }
       String sNameN = sName.concat("n");
       if(m_listFirstName.contains(sNameN))
       {
           return true;
       }
       String sNameUR = sName.concat("ur");
       if(m_listFirstName.contains(sNameUR))
       {
           return true;
       }
       return false;
   }
   private boolean endsWithAFirstName(String sName)
   {
       if(m_listFirstName.contains(sName))
       {
           return true;
       }
       String sNameI = sName.concat("i");
       if(m_listFirstName.contains(sNameI))
       {
           return true;
       }
       String sNameU = sName.concat("u");
       if(m_listFirstName.contains(sNameU))
       {
           return true;
       }
       return false;
   }
   private boolean endsWithUFirstName(String sName)
   {
       if(m_listFirstName.contains(sName))
       {
           return true;
       }
       String sNameA = sName.concat("a");
       if(m_listFirstName.contains(sNameA))
       {
           return true;
       }
       String sNameAR = sName.concat("ar");
       if(m_listFirstName.contains(sNameAR))
       {
           return true;
       }
       return false;
   }
   private boolean endsWithARFirstName(String sName)
   {
       if(m_listFirstName.contains(sName))
       {
           return true;
       }
       String sNameI = sName.concat("i");
       if(m_listFirstName.contains(sNameI))
       {
           return true;
       }
       String sNameUR = sName.concat("ur");
       if(m_listFirstName.contains(sNameUR))
       {
           return true;
       }
       String sNameU = sName.concat("u");
       if(m_listFirstName.contains(sNameU))
       {
           return true;
       }
       String sNameS = sName.concat("s");
       if(m_listFirstName.contains(sNameS))
       {
           return true;
       }
       return false;
   }
   private boolean endsWithURFirstName(String sName)
   {
       if(m_listFirstName.contains(sName))
       {
           return true;
       }
       String sNameI = sName.concat("i");
       if(m_listFirstName.contains(sNameI))
       {
           return true;
       }
       String sNameAR = sName.concat("ar");
       if(m_listFirstName.contains(sNameAR))
       {
           return true;
       }
       return false;
   }
   private boolean endsWithNFirstName(String sName)
   {
       if(m_listFirstName.contains(sName))
       {
           return true;
       }
       String sNameI = sName.concat("i");
       if(m_listFirstName.contains(sNameI))
       {
           return true;
       }
       String sNameS = sName.concat("s");
       if(m_listFirstName.contains(sNameS))
       {
           return true;
       }
       return false;
   }
    private boolean noEndingFirstName(String sName)
   {
       if(m_listFirstName.contains(sName))
       {
           return true;
       }
       String sNameA = sName.concat("a");
       if(m_listFirstName.contains(sNameA))
       {
           return true;
       }
       String sNameAR = sName.concat("ar");
       if(m_listFirstName.contains(sNameAR))
       {
           return true;
       }
       String sNameS = sName.concat("s");
       if(m_listFirstName.contains(sNameS))
       {
           return true;
       }
       String sNameN = sName.concat("n");
       if(m_listFirstName.contains(sNameN))
       {
           return true;
       }
       String sNameUR = sName.concat("ur");
       if(m_listFirstName.contains(sNameUR))
       {
           return true;
       }
       String sNameI = sName.concat("i");
       if(m_listFirstName.contains(sNameI))
       {
           return true;
       }
       String sNameU = sName.concat("u");
       if(m_listFirstName.contains(sNameU))
       {
           return true;
       }
       return false;
   }
    private boolean isFirstName(String sName)
    {
        if(m_listFirstName.contains(sName))
        {
            return true;
        }
        else
        {
            return tryDifferntCasesFirstName(sName);
        }
    }

   private void updateTextArray(int nStart, int nStop, String sTag)
   {
       for(int n=nStart; n<=nStop; n++)
       {
           if(m_sTextArray[n][2].equals(""))
           {
                m_sTextArray[n][2] = sTag;
           }
       }       
   }

   private void overwriteTextArray(int nStart, int nStop, String sTag)
   {
       for(int n=nStart; n<=nStop; n++)
       {
           m_sTextArray[n][2] = sTag;
       }
   }

   private int findStartOfName(int nStart)
   {
       boolean bKeepLooking = true;
       while(bKeepLooking && nStart>1)
       {
           //if(isForeign(m_sTextArray[nStart-1][1])||(startsWithUpper(m_sTextArray[nStart-1][0])&&(isProperNoun(m_sTextArray[nStart-1][1])||isNoun(m_sTextArray[nStart-1][1])||isAdjective(m_sTextArray[nStart-1][1]))))
           if(isForeign(m_sTextArray[nStart-1][1]))
           {
               nStart--;
           }
           else
           {
               bKeepLooking = false;
           }
       }
       return nStart;
   }

   private int findFullName(int nStart)
   {
       int nStop = nStart;
       boolean bKeepLooking = true;
       while(bKeepLooking && nStop<m_nLineCount)
       {
           if((isProperNounSameCase(m_sTextArray[nStop+1][1],m_sTextArray[nStart][1])||
                   isForeign(m_sTextArray[nStop+1][1])||m_sTextArray[nStop+1][0].equals("&"))||
                   isPartOfCompanyName(m_sTextArray[nStop+1][0]))
           {
               nStop++;
           }
           //Bandalag starfsmanna ríkis og bæja
           else if(nStop<m_nLineCount-4&&isGenativeNoun(m_sTextArray[nStop+1][1])&&
                   isGenativeNoun(m_sTextArray[nStop+2][1])&&
                   m_sTextArray[nStop+3][0].equals("og")&&isGenativeNoun(m_sTextArray[nStop+4][1]))
           {
               nStop = nStop + 4;
               bKeepLooking = false;
           }
           //Samband íslenskra sveitarfélaga
           else if(nStop<m_nLineCount-1&&isGenativeAdjective(m_sTextArray[nStop+1][1])&&isGenativeNoun(m_sTextArray[nStop+2][1]))
           {
               nStop = nStop + 2;
               bKeepLooking = false;
           }
           //Tryggingastofnu ríkisins, Bandalag háskólamanna
           else if(nStop<m_nLineCount-1&&isGenativeNoun(m_sTextArray[nStop+1][1])&&
                   (!startsWithUpper(m_sTextArray[nStop+2][0])||isEndSymbol(m_sTextArray[nStop+2][0])))
           {
               nStop++;
               bKeepLooking = false;
           }
           //Hafið bláa
           else if(isAdjective(m_sTextArray[nStop+1][1]))
           {
               nStop++;
               bKeepLooking = false;
           }
           
           //Hvíta húsinu, Litla Hraun
           else if(isAdjective(m_sTextArray[nStart][1])&&isNoun(m_sTextArray[nStop+1][1]))
           {
               nStop++;
               bKeepLooking = false;
           }
           //Samband íslenskra sveitarfélaga
           /*else if(isGenativeAdjective(m_sTextArray[nStop][1]) && isGenativeNoun(m_sTextArray[nStop+1][1]))
           {
               nStop = nStop + 2;
           }*/
           else
           {
               bKeepLooking = false;
           }          
       }

       return nStop;
   }
/*
 *    private int findFullName(int nStart)
   {
       int nStop = nStart;
       boolean bKeepLooking = true;
       while(bKeepLooking && nStop<m_nLineCount)
       {
           if((isProperNounSameCase(m_sTextArray[nStop][1],m_sTextArray[nStart][1])||
                   isForeign(m_sTextArray[nStop][1])||m_sTextArray[nStop][0].equals("&"))||
                   isPartOfCompanyName(m_sTextArray[nStop][0]))
           {
               nStop++;
           }
           //Bandalag starfsmanna ríkis og bæja
           else if(nStop<m_nLineCount-4&&isGenativeNoun(m_sTextArray[nStop][1])&&
                   isGenativeNoun(m_sTextArray[nStop+1][1])&&
                   m_sTextArray[nStop+2][0].equals("og")&&isGenativeNoun(m_sTextArray[nStop+3][1]))
           {
               nStop = nStop + 4;
               bKeepLooking = false;
           }
           //Samband íslenskra sveitarfélaga
           else if(nStop<m_nLineCount-1&&isGenativeAdjective(m_sTextArray[nStop][1])&&isGenativeNoun(m_sTextArray[nStop+1][1]))
           {
               nStop = nStop + 2;
               bKeepLooking = false;
           }
           //Tryggingastofnu ríkisins, Bandalag háskólamanna
           else if(nStop<m_nLineCount-1&&isGenativeNoun(m_sTextArray[nStop][1])&&
                   (!startsWithUpper(m_sTextArray[nStop+1][0])||isEndSymbol(m_sTextArray[nStop+1][0])))
           {
               nStop++;
               bKeepLooking = false;
           }
           //Hafið bláa
           else if(isAdjective(m_sTextArray[nStop][1]))
           {
               nStop++;
               bKeepLooking = false;
           }

           //Hvíta húsinu, Litla Hraun
           else if(isAdjective(m_sTextArray[nStart][1])&&isNoun(m_sTextArray[nStop+1][1]))
           {
               nStop++;
               bKeepLooking = false;
           }
           //Samband íslenskra sveitarfélaga
           /*else if(isGenativeAdjective(m_sTextArray[nStop][1]) && isGenativeNoun(m_sTextArray[nStop+1][1]))
           {
               nStop = nStop + 2;
           }
           else
           {
               bKeepLooking = false;
           }
       }

       return nStop-1;
   }
   */
   private String getFullName(int nStart, int nStop)
   {
       String sName = "";
       for(int i=nStart; i<=nStop; i++)
       {
           sName =  sName + " " +m_sTextArray[i][0];
       }
       return sName;
   }

    private boolean startsWithUpper(String sText)
    {
        return Pattern.compile("^[A-ZÁÉÐÍÓÚÝÞÆÖ]").matcher(sText).find();
    }
    private boolean isAllUpper(String sText)
    {
        return Pattern.compile("^[A-ZÁÉÐÍÓÚÝÞÆÖ]+[A-ZÁÉÐÍÓÚÝÞÆÖ]$").matcher(sText).find();
    }
    private boolean isProperNoun(String sText)
    {
        return Pattern.compile("^n.+[msö]$").matcher(sText).find();
    }
    private boolean isProperNounSameCase(String sTextSecond, String sTextFirst)
    {
        if(Pattern.compile("^n.+[msö]$").matcher(sTextSecond).find()&&
                Pattern.compile("^n.+[msö]$").matcher(sTextFirst).find())
        {
            if(sTextSecond.substring(0, 3).equals(sTextFirst.substring(0, 3)))
            {
                return true;
            }
        }
        return false;
    }
    private boolean isNoun(String sText)
    {
        return Pattern.compile("^n").matcher(sText).find();
    }
    private boolean isGenativeNoun(String sText)
    {
        return Pattern.compile("^n..eg?$").matcher(sText).find();
    }
    private boolean isPronoun(String sText)
    {
        return Pattern.compile("^f").matcher(sText).find();
    }
    private boolean isAdjective(String sText)
    {
        return Pattern.compile("^l").matcher(sText).find();
    }
    private boolean isVerb(String sText)
    {
        return Pattern.compile("^s").matcher(sText).find();
    }
    private boolean isGenativeAdjective(String sText)
    {
        return Pattern.compile("^l..e").matcher(sText).find();
    }
    private boolean isGenative(String sText)
    {
        return Pattern.compile("^...e").matcher(sText).find();
    }
    private boolean isNomative(String sText)
    {
        return Pattern.compile("^...n").matcher(sText).find();
    }
    private boolean isAdverb(String sText)
    {
        return Pattern.compile("^a").matcher(sText).find();
    }
    private boolean hasArticleEnding(String sText)
    {
        return Pattern.compile("^n...g").matcher(sText).find();
    }
    private boolean isConjunction(String sText)
    {
        return Pattern.compile("^c(n|t)?$").matcher(sText).find();
    }
    private boolean isInsicnificant(String sText)
    {
        return Pattern.compile(",|;|:").matcher(sText).find();
    }
    private boolean isForeign(String sText)
    {
        return Pattern.compile("^e$").matcher(sText).find();
    }
    private boolean isMissedCompanyEnding(String sText)
    {
        return Pattern.compile("^(hf.?|ehf.?|sf.?|inc.?|ltd.?|Group.?)$").matcher(sText).find();
    }
    private boolean isPartOfCompanyName(String sText)
    {
        return Pattern.compile("(.+[A-ZÁÉÐÍÓÚÝÞÆÖ].*|([A-ZÁÉÐÍÓÚÝÞÆÖ]|[a-záéðíóúýþæö])+[0-9].*|.*[0-9]([A-ZÁÉÐÍÓÚÝÞÆÖ]|[a-záéðíóúýþæö])+)").matcher(sText).find();
    }
    private boolean isEndOfList(String sText)
    {
        return Pattern.compile("\\.|,|:|;").matcher(sText).find();
    }
    private boolean isEndSymbol(String sText)
    {
        return Pattern.compile("\\.|,|:|;").matcher(sText).find();
    }
    private boolean isNumber(String sText)
    {
        return Pattern.compile("^t").matcher(sText).find();
    }
    private boolean isSingulUpper(String sText)
    {
        return Pattern.compile("^[A-ZÁÉÐÍÓÚÝÞÆÖ]$").matcher(sText).find();
    }
    private boolean isLastName(String sText)
    {
        return Pattern.compile("(son(ar)?|syni|sen|dótt[iua]r)$").matcher(sText).find();
    }
    private boolean isPartOfKnownName(int index)
    {
        if(m_sTextArray[index][2].equals(""))
        {
            return false;
        }
        return true;
    }
    private int getStartOfKnownName(int index)
    {
        while(index>1&&!m_sTextArray[index-1][2].equals(""))
        {
            index--;
        }
        return index;
    }
    private boolean isStartOfSentance(int index)
    {
        if(index > 0)
        {
            if(m_sTextArray[index-1][0].equals("")||isAbrivation(m_sTextArray[index-1][0])||
                    m_sTextArray[index-1][0].equals("„"))
            {
                return true;
            }
            return false;
        }
        return true;
    }
    private boolean isAbrivation(String sText)
    {
        return Pattern.compile("^hf\\.|^ehf\\.").matcher(sText).find();
    }
    private boolean isPersonAction(String sText)
    {
        return Pattern.compile("segir|sagði|mælti|orðaði|nefndi|skrifaði").matcher(sText).find();
    }
    private boolean isCompanyAction(String sText)
    {
        return Pattern.compile("leikur|lék|léku|spilar|spilaði|tapar|tapaði|vann").matcher(sText).find();
    }
    private boolean isSchool(String sText)
    {
        return Pattern.compile("skól(inn|a(n(n|um|s)))").matcher(sText).find();
    }
    private boolean isHospital(String sText)
    {
        return Pattern.compile("[Ss]júkrahús(ið|inu|sins)").matcher(sText).find();
    }
    private boolean ageIsFollowing(int index)
    {
        if(isInsicnificant(m_sTextArray[index+1][1]))
        {
            index++;
        }
        if(index < m_sTextArray.length-2)
        {
            if(isNumber(m_sTextArray[index+1][1])&&m_sTextArray[index+2][0].equals("ára"))
            {
                return true;
            }
            return false;
        }
        return false;
    }
     public void printRoleHash()
    {
        System.out.println("Roles");
        Iterator itr;
        TreeMap treeMap = new TreeMap(m_sRoleHash);
        itr = treeMap.keySet().iterator();
        while (itr.hasNext()) {
            String key = (String)itr.next();
            String value = treeMap.get(key).toString();
            System.out.println(key+" "+value);
       }
    }

    public void printNameHash()
    {
        System.out.println("Names");
        Iterator itr;
        TreeMap treeMap = new TreeMap(m_sNameHash);
        itr = treeMap.keySet().iterator();
        while (itr.hasNext()) {
            String key = (String)itr.next();
            String value = treeMap.get(key).toString();
            System.out.println(key+" "+value);
       }
    }

    public void printIndexList()
    {
        System.out.println("Index list" +m_IndexList.size());
        Iterator itr;
        itr = m_IndexList.iterator();
        int index;
        while (itr.hasNext()) {
            index = Integer.parseInt(itr.next().toString());
            System.out.println(index +" "+ m_sTextArray[index][0]);     
       }
    }

    public void printMarkedText()
    {
        try{
        // Create file
        //BufferedWriter writer;
        FileWriter fstream = new FileWriter(m_sOutFile);
        BufferedWriter out = new BufferedWriter(fstream);
        //writer = new BufferedWriter(new OutputStreamWriter(new FileWriter(m_sOutFile), "UTF8"));
        //out.write("Hello Java");
        String sText;
        String sTag;
        for(int i=0; i<m_nLineCount; i++)
        {
            sText = m_sTextArray[i][0];
            sTag = m_sTextArray[i][2];
            if(sTag.equals(""))
            {
                if(sText.equals(""))
                {
                    out.write("\n");
                }
                else
                {
                    out.write(sText+" ");
                }
            }
            else
            {
                out.write("["+sText+" ");
                while(m_sTextArray[i+1][2].equals(sTag))
                {
                    i++;
                    if(!m_sTextArray[i][0].equals(""))
                    {
                        out.write(m_sTextArray[i][0]+" ");
                    }
                }
                out.write(sTag+"] ");
            }     
        }
        out.close();
        }
        catch (Exception e)
        {
            System.out.print(e.getMessage());
        }
    }

    public enum Role
    {
        ROLE_PERSON, ROLE_COMPANY, ROLE_LOCATION, NOVALUE, RELATION_PERSON, ROLE_EVENT;

        public static Role toRole(String str)
        {
            try {
                return valueOf(str);
            }
            catch (Exception ex) {
                return NOVALUE;
            }
        }
    }
}
