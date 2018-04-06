package is.iclt.icenlp.core.icestagger;

/**
 * Class for evaluating the POS and NE tagging of sentences.
 */
public class Evaluation {
    public int posTotal, posCorrect;
    public int neTotal, neCorrect, neFound;

    public Evaluation() {
        posTotal = 0; posCorrect = 0;
        neTotal = 0; neCorrect = 0; neFound = 0;
    }

    public void evaluate(TaggedToken[] sent, TaggedToken[] goldSent) {
        assert(sent.length == goldSent.length);
        for(int i=0; i<sent.length; i++) {
            if(sent[i].posTag >= 0 && goldSent[i].posTag >= 0) {
                posTotal++;
                if(sent[i].posTag == goldSent[i].posTag) posCorrect++;
            }
            if(goldSent[i].neTag == TaggedData.NE_B) neTotal++;
            if(sent[i].neTag == TaggedData.NE_B) {
                neFound++;
                // TODO: consider separating NE detection and labeling
                if(goldSent[i].neTag == TaggedData.NE_B &&
                   goldSent[i].neTypeTag == sent[i].neTypeTag)
                {
                    for(int j=i+1; j<sent.length; j++) {
                        if(goldSent[i].neTag != TaggedData.NE_I) {
                            if(sent[i].neTag != TaggedData.NE_I)
                                neCorrect++;
                            break;
                        } else if(sent[i].neTag != TaggedData.NE_I) break;
                    }
                }
            }
        }
    }

    public double posAccuracy() {
        if(posTotal == 0) return 0.0;
        return (double)posCorrect / (double)posTotal;
    }

    public double nePrecision() {
        if(neFound == 0) return 0.0;
        return (double)neCorrect/(double)neFound;
    }

    public double neRecall() {
        if(neTotal == 0) return 0.0;
        return (double)neCorrect/(double)neTotal;
    }

    public double neFscore() {
        double p = nePrecision();
        double r = neRecall();
        if(p == 0.0 && r == 0.0) return 0.0;
        return 2.0*p*r / (p+r);
    }

    /**
     * Returns true iff the NE taggings of the two sentences are equal.
     */
    public boolean neEquals(TaggedToken[] sent, TaggedToken[] goldSent) {
        assert(sent.length == goldSent.length);
        for(int i=0; i<sent.length; i++) {
            if(sent[i].neTag != goldSent[i].neTag) return false;
            if(sent[i].neTypeTag != goldSent[i].neTypeTag) return false;
        }
        return true;
    }
}

