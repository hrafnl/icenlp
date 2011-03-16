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

import java.util.*;
import java.io.*;

/**
 * N-grams information.
 * <br> Used by TriTagger.
 * @author Hrafn Loftsson
 */
public class Ngrams {
    private HashMap myHash;    // unigrams, bigrams and trigrams are keys.  Frequencies are values.
    private Vector myTags;   // tags in the tag
    //private Vector myTagsUpper;  // tags in the tag set for upper case words
    //private int numTags=0;
    private int corpusSize=0;
    private int numUnigrams=0;
    private int numBigrams=0;
    private int numTrigrams=0;
    private String mostFrequentTag;
    private double entropy;
    private double lambdaBi1, lambdaBi2;   // Used for smoothing contextual probabilities of bigrams
    private double lambdaTri1, lambdaTri2, lambdaTri3;   // Used for smoothing contextual probabilities of trigrams
    private double theta;                      // Used for smoothing unknown lexical probabilities
    public static final String boundaryTag="__$"; // The tag used for prepending in front and back of each sentence

    public Ngrams(String fileName, String lambdaFileName)
    throws IOException
    {

        init1();
        //BufferedReader ngrams = new BufferedReader(new FileReader(fileName));
        BufferedReader ngrams = FileEncoding.getReader(fileName);
        loadNgrams(ngrams);

        BufferedInputStream lambda = new BufferedInputStream(new FileInputStream(lambdaFileName));
        loadLambdas(lambda);
        init2();

    }

    public Ngrams( InputStream in_ngram, InputStream in_lambda ) throws IOException, NullPointerException
	{
		if( in_ngram == null || in_lambda == null)
			throw new NullPointerException( "InputStream was not initialized correctly (null)" );

        init1();
        //BufferedReader ngrams = new BufferedReader(new InputStreamReader(in_ngram));
        BufferedReader ngrams = FileEncoding.getReader(in_ngram);
        loadNgrams(ngrams);
        BufferedInputStream lambda = new BufferedInputStream(in_lambda);
        loadLambdas(lambda);
        init2();
    }

    private void init1()
    {
        myHash = new HashMap();
        myTags = new Vector();
        //myTagsUpper = new Vector();
    }

    private void init2()
    {
        //theta = computeTheta();
        // We have not been able to get the same number for theta as TnT does, despite following the
        // description in Brants paper
        // For now, we hard code theta the max number in the range given by Brants
        //theta = 0.03712932;
        theta = 0.10;
        entropy = computeEntropy();
    }



    public Vector getTags()
    {
        return myTags;
    }

    public double getTheta()
    {
        return theta;
    }

    public double getEntropy()
    {
        return entropy;
    }

    /*public double getThetaUpperCase()
    {
        return thetaUpper;
    } */

    public int getNumTags()
    {
        return myTags.size();
    }

    public int getNumUnigrams()
    {
       return numUnigrams;
    }
    public int getNumBigrams()
    {
       return numBigrams;
    }
    public int getNumTrigrams()
    {
       return numTrigrams;
    }

    public int corpusSize()
    {
        return corpusSize;
    }

    public String getMostFrequentTag()
    {
        return mostFrequentTag;
    }



    // Entropy(S) = - sum(1..n) [ p(i)*log(pi)}
    private double computeEntropy()
    {
        double sum = 0.0;
        int numTags = myTags.size();

        for (int i=0; i<numTags; i++)
        {
            String tag = (String)myTags.elementAt(i);
            if (!tag.equals(boundaryTag))
            {
                double probTag  =  getUnigramProb(tag);
                double logProbTag = Math.log(probTag)/Math.log(2.0);    // Math.log is natural log, base e
                sum += probTag*logProbTag;
            }
        }
        return -sum;
    }

    // theta is the standard deviation of the maximum likelihood probabilities of the tags (Brants, 2000)
    private double computeTheta()
    {
        double probTag=0.0;
        int numTags = myTags.size();
        double averageTagProb = (double)1.0/numTags;
        //System.out.println("Average tag probability: " + averageTagProb);
        // The following statements compute the same value as the statement above
        /*
        double tagProbTotal=0.0;
        for (int i=0; i<numTags; i++)
        {
            String tag = (String)myTags.elementAt(i);
            if (!tag.equals(boundaryTag))
            {
                probTag  =  getUnigramProb(tag);
                tagProbTotal += probTag;
            }
        }
        averageTagProb = tagProbTotal / (numTags);     // Subtract 1 because of the boundary tag
        */


        double sum=0.0;
        for (int i=0; i<numTags; i++)
        {
            String tag = (String)myTags.elementAt(i);
            if (!tag.equals(boundaryTag))
            {
                probTag = getUnigramProb(tag);
                //System.out.println(tag + "\t" + getFrequency(tag));
                //System.out.println(tag + "\t" + probTag);
                double x = probTag - averageTagProb;
                sum = sum + x*x;
            }
        }
        // Brants (2000) talks about using standard deviation but his formula is actually the variance
        //return Math.sqrt((double)sum/(numTags-2));              // Subtract 1 because of the boundary tag
        double theTheta = Math.sqrt(sum/(numTags-2));           // Subtract 1 because of the boundary tag
        // The above calculation of theta does not return a number close to what TnT returns.
        // The next statement is a hack!
        //theTheta = theTheta * 5.6;                              // A hack in order to g
        //System.out.println("Theta is: " +  theTheta);
        return theTheta;
    }

    private String getLambda(Properties parameters, String lambdaKey)
    {
        String lambdaValue = parameters.getProperty(lambdaKey);
        if (lambdaValue == null)
            throw new NullPointerException();
            //quit("Parameter " + lambdaKey + " is missing!");
        return lambdaValue;
    }


    private void loadLambdas(BufferedInputStream in)
    throws IOException
    {
        Properties parameters = new Properties();

        parameters.load(in);
        String lambdaBi1Str = getLambda(parameters, "lambdaBi1");
        String lambdaBi2Str = getLambda(parameters, "lambdaBi2");
        String lambdaTri1Str = getLambda(parameters, "lambdaTri1");
        String lambdaTri2Str = getLambda(parameters, "lambdaTri2");
        String lambdaTri3Str = getLambda(parameters, "lambdaTri3");

        lambdaBi1 = Double.parseDouble(lambdaBi1Str);
        lambdaBi2 = Double.parseDouble(lambdaBi2Str);
        lambdaTri1 = Double.parseDouble(lambdaTri1Str);
        lambdaTri2 = Double.parseDouble(lambdaTri2Str);
        lambdaTri3 = Double.parseDouble(lambdaTri3Str);
    }

    private void loadNgrams(BufferedReader input)
    throws IOException
    {
       String strs[];
       String key="";
       String value="";
       numUnigrams=0;
       numBigrams=0;
       numTrigrams=0;

       // The line looks like: w1 (w2) (w3) freq
       String currLine = input.readLine();
       int maxUniFreq=0;
       while (currLine != null)
       {
           if (currLine.length() != 0)   // Not an empty line
           {
              strs = currLine.split(" ");   // Split the line
              key = strs[0];
              if (strs.length == 2) {        // Then the key is a unigram, i.e. a tag label
                numUnigrams++;
                int unigramFreq = Integer.parseInt(strs[1]);
                myTags.add(key);
                if (!key.equals(boundaryTag))
                {
                    corpusSize = corpusSize + unigramFreq;
                    if (unigramFreq > maxUniFreq)
                    {
                        maxUniFreq = unigramFreq;
                        mostFrequentTag = key;
                    }
                }
              }
              else if (strs.length == 3)
                numBigrams++;
              else if (strs.length == 4)
                numTrigrams++;

              for (int i=1; i<strs.length-1;i++)
                 key = key + " " + strs[i];
              value = strs[strs.length-1]; // The last item is the frequencey
           }
           myHash.put((Object)key, (Object)value);
           currLine = input.readLine();
       }
       input.close();
       //System.out.println("Corpus size: " + corpusSize);
    }

    private int getFrequencyUsingKey(String key)
    {
        Object value = myHash.get(key);
        if (value == null)
            return 0;
        else
            return Integer.parseInt((String)value);
    }

    public int getFrequency(String str)
    {
        return getFrequencyUsingKey(str);
    }

    public int getFrequency(String str1, String str2)
    {
        return getFrequencyUsingKey(str1 + " " + str2);
    }

    public int getFrequency(String str1, String str2, String str3)
    {
        return getFrequencyUsingKey(str1 + " " + str2 + " " + str3);
    }

    // Returns the contextual probability P(tag2 | tag1)
    // Sequence is t1 t2
    // Uses Lidstone smoothing
    // P(tag2|tag1) = P(tag1,tag2)*P(tag1)
    /*public double contextualProb(String tag2, String tag1, int vocabularySize)
    {
       int bigramFreq = getFrequency(tag1, tag2);
       int unigramFreq = getFrequency(tag1);
       return (double)(bigramFreq+0.5)/(unigramFreq+(0.5*vocabularySize));  // Lidstone smoothing
    } */

    private double contextualProb(String tag2, String tag1)
    {
       int bigramFreq = getFrequency(tag1, tag2);
       int unigramFreq = getFrequency(tag1);
       if (unigramFreq == 0)
         return 0.0;
       else
         return (double)bigramFreq/unigramFreq;
    }

    // Returns the contextual probability P(tag2 | tag1)
    // Sequence is t1 t2
    // Uses linear interpolation of unigrams and bigrams
    public double contextualProbSmoothing(String tag2, String tag1)
    {
       double unigramProb, bigramProb;

       int bigramFreq = getFrequency(tag1, tag2);
       int unigramFreq = getFrequency(tag1);
       if (unigramFreq == 0)
         bigramProb=0.0;
       else
         bigramProb = (double)bigramFreq/unigramFreq;
       unigramProb = getUnigramProb(tag2);

       return lambdaBi1*unigramProb + lambdaBi2*bigramProb;
    }

    public double getUnigramProb(String tag)
    {
        return (double)getFrequency(tag)/corpusSize;
    }

    // Returns the contextual probability P(tag3 | tag1, tag2)
    // The sequence is tag1, tag2, tag3
    // Uses linear interpolation of unigrams, bigrams and trigrams
    public double contextualProbSmoothing(String tag3, String tag1, String tag2)
    {
        int bigramFreq, trigramFreq;

        double trigramProb, bigramProb, unigramProb;
       // Special case of the boundary tag at the start of a sentence
       // The bigram frequency is equal to the frequency of the boundary tag divided by 2
       // because the boundary tag is at the front and at end of each sentence in the corpus
       if (tag1.equals(boundaryTag) && tag2.equals(boundaryTag)) // Start of sentence
       {
            bigramFreq = getFrequency(boundaryTag)/2;
            trigramFreq = getFrequency(boundaryTag, tag3);
       }
       else
       {
         bigramFreq = getFrequency(tag1, tag2);
         trigramFreq = getFrequency(tag1, tag2, tag3);
       }

       if (bigramFreq == 0)
            trigramProb = 0.0;
       else
            trigramProb = (double)trigramFreq/bigramFreq;

       bigramProb = contextualProb(tag3, tag2);
       unigramProb = getUnigramProb(tag3);

       return lambdaTri1*unigramProb + lambdaTri2*bigramProb + lambdaTri3*trigramProb;
    }

    /*public double contextualProbSmoothing(String tag3, String tag1, String tag2, double unigramProb)
    {
        int bigramFreq, trigramFreq;

        double trigramProb, bigramProb;//, unigramProb;
       // Special case of the boundary tag at the start of a sentence
       // The bigram frequency is equal to the frequency of the boundary tag divided by 2
       // because the boundary tag is at the front and at end of each sentence in the corpus
       if (tag1.equals(boundaryTag) && tag2.equals(boundaryTag)) // Start of sentence
       {
            bigramFreq = getFrequency(boundaryTag)/2;
            trigramFreq = getFrequency(boundaryTag, tag3);
       }
       else
       {
         bigramFreq = getFrequency(tag1, tag2);
         trigramFreq = getFrequency(tag1, tag2, tag3);
       }

       if (bigramFreq == 0)
            trigramProb = 0.0;
       else
            trigramProb = (double)trigramFreq/bigramFreq;

       bigramProb = contextualProb(tag3, tag2);
       //unigramProb = getUnigramProb(tag3);

       return lambdaTri1*unigramProb + lambdaTri2*bigramProb + lambdaTri3*trigramProb;
    } */
}
