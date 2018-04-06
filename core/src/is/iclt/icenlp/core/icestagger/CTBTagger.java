package is.iclt.icenlp.core.icestagger;
import java.util.*;

public class CTBTagger extends Tagger {

    public CTBTagger(TaggedData taggedData, int posBeamSize, int neBeamSize)
    {
        super(taggedData, posBeamSize, neBeamSize);
    }

    /**
     * Builds the openTags array of tags for unknown words.
     */
    protected void computeOpenTags() {
        final String[] openTagArray = {
            "VA", "VC", "VE", "VV", "NR", "NT", "NN", "AD", "FW", "CD", "OD",
            "IJ", "JJ" };
        openTags = new int[openTagArray.length];
        TagSet tagSet = taggedData.getPosTagSet();
        for(int i=0; i<openTags.length; i++) {
            try {
                openTags[i] = tagSet.getTagID(openTagArray[i]);
            } catch(TagNameException e) {
                System.err.println("Open tag not in tagset: "+openTagArray[i]);
                System.exit(1);
            }
        }
    }
    
    /**
     * Computes feature values given a certain POS tag and context.
     */
    protected int getPosFeats(
    TaggedToken[] sentence, int idx, int[] feats, double[] values, int nFeats,
    int posTag, int neTag, int neTypeTag, boolean hasLast, History last,
    boolean extend)
    {
        char[] head = new char[8];
        int f;
        TaggedToken ttok = sentence[idx];
        char isFinal = (idx == sentence.length-1)? (char)1 : (char)0;
        String textLower = ttok.textLower;
        String lastLower = (idx == 0)? "" : sentence[idx-1].textLower;
        String lastLower2 = (idx < 2)? "" : sentence[idx-2].textLower;
        String nextLower =
            (idx == sentence.length-1)? "" : sentence[idx+1].textLower;
        String nextLower2 =
            (idx >= sentence.length-2)? "" : sentence[idx+2].textLower;

        if(!hasLast) {
            // POS + textLower + final?
            head[0] = 0x00;
            head[1] = (char)posTag;
            head[2] = isFinal;
            f = posPerceptron.getFeatureID(
                new String(head,0,3) + textLower, extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }

            // POS + textLower + lastLower
            head[0] = 0x01;
            head[1] = (char)posTag;
            f = posPerceptron.getFeatureID(
                new String(head,0,2) + lastLower + "\n" + textLower, extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }

            // POS + textLower + nextLower
            head[0] = 0x02;
            head[1] = (char)posTag;
            f = posPerceptron.getFeatureID(
                new String(head,0,2) + textLower + "\n" + nextLower, extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }

            // POS + textLower + nextLower + nextLower2
            head[0] = 0x03;
            head[1] = (char)posTag;
            f = posPerceptron.getFeatureID(new String(head,0,2) +
                textLower + "\n" + nextLower + "\n" + nextLower2, extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }

            // POS + lastLower + textLower + nextLower
            head[0] = 0x04;
            head[1] = (char)posTag;
            f = posPerceptron.getFeatureID(new String(head,0,2) +
                lastLower + "\n" + textLower + "\n" + nextLower, extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }

            // POS + lastLower2 + lastLower + textLower
            head[0] = 0x05;
            head[1] = (char)posTag;
            f = posPerceptron.getFeatureID(new String(head,0,2) +
                lastLower2 + "\n" + lastLower + "\n" + textLower, extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }

            // POS + suffixes
            head[0] = 0x06;
            head[1] = (char)posTag;
            for(int i=textLower.length()-4; i<textLower.length(); i++) {
                if(i<1) continue;
                f = posPerceptron.getFeatureID(new String(head,0,2) +
                    textLower.substring(i), extend);
                if(f >= 0) {
                    feats[nFeats] = f; values[nFeats] = 1.0; nFeats++;
                }
            }

            // POS + characters
            head[0] = 0x07;
            head[1] = (char)posTag;
            head[2] = (char)textLower.length();
            for(int i=0; i<textLower.length(); i++) {
                head[3] = (char)i;
                head[4] = textLower.charAt(i);
                f = posPerceptron.getFeatureID(new String(head,0,5) +
                    textLower.substring(i), extend);
                if(f >= 0) {
                    feats[nFeats] = f; values[nFeats] = 1.0; nFeats++;
                }
            }

            // POS + dictionary
            head[0] = 0x08;
            head[1] = (char)posTag;
            for(int i=0; i<posDictionaries.size(); i++) {
                Dictionary dict = posDictionaries.get(i);
                String value = dict.map.get(textLower);
                // String lastValue = (i == 0)? "" : dict.map.get(lastLower);
                String nextValue =
                    (i == sentence.length-1)? "" :
                    dict.map.get(nextLower);
                head[2] = (char)i;
                String[] combinations = {
                    value,
                    // (value == null || lastValue == null)? null :
                    //    lastValue + "\n" + value,
                    (value == null || nextValue == null)? null :
                        value + "\n" + nextValue,
                    nextValue
                };
                for(int j=0; j<combinations.length; j++) {
                    if(combinations[j] == null) continue;
                    head[3] = (char)j;
                    f = posPerceptron.getFeatureID(
                        new String(head,0,4) + combinations[j], extend);
                    if(f >= 0) {
                        feats[nFeats] = f; values[nFeats] = 1.0; nFeats++;
                    }
                }
            }

            // POS + embedding
            head[0] = 0x09;
            head[1] = (char)posTag;
            for(int i=0; i<posEmbeddings.size(); i++) {
                float[] value = posEmbeddings.get(i).map.get(textLower);
                if(value == null) continue;
                head[2] = (char)i;
                for(int j=0; j<value.length; j++) {
                    head[3] = (char)j;
                    f = posPerceptron.getFeatureID(
                        new String(head,0,4), extend);
                    if(f >= 0) {
                        feats[nFeats] = f; values[nFeats] = value[j]; nFeats++;
                    }
                }
            }
        } else {
            char posTag1b = 0xffff;
            char posTag2b = 0xffff;
            if(last != null) {
                posTag1b = (char)last.posTag;
                if(last.last != null) posTag2b = (char)last.last.posTag;
            }

            // (previous, current) POS
            head[0] = 0x80;
            head[1] = (char)posTag;
            head[2] = posTag1b;
            f = posPerceptron.getFeatureID(new String(head,0,3), extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }

            // (previous2, previous, current) POS
            head[0] = 0x81;
            head[1] = (char)posTag;
            head[2] = posTag1b;
            head[3] = posTag2b;
            f = posPerceptron.getFeatureID(new String(head,0,4), extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }
            
            // (previous, current) POS + textLower
            head[0] = 0x82;
            head[1] = (char)posTag;
            head[2] = posTag1b;
            f = posPerceptron.getFeatureID(
                new String(head,0,3) + textLower, extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }

            // (previous, current) POS + textLower + nextLower
            head[0] = 0x83;
            head[1] = (char)posTag;
            head[2] = posTag1b;
            f = posPerceptron.getFeatureID(
                new String(head,0,3) + textLower + "\n" + nextLower, extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }

        }

        return nFeats;
    }
}

