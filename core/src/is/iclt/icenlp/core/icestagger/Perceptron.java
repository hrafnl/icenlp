package is.iclt.icenlp.core.icestagger;
import java.util.*;
import java.io.Serializable;

class Perceptron extends FeatureMap implements Serializable {
    static final long serialVersionUID = -8602541367629766824L;

    private double[] sumWeight;
    private double[] oldSumWeight;
    private double[] curWeight;
    private double[] bestWeight;
    private int[] updateCount;
    private int[] bestUpdateCount;

    public Perceptron() {
        super();
    }

    /**
     * Updates the feature weights for the given feature IDs.
     *
     * @param feats     vector of active feature IDs
     * @param values    vector of the features' values
     * @param nFeats    only use the first nFeats indices of
     *                  the <code>feats</code> array
     * @param positive  if true, increase weights, otherwise decrease
     */
    public void updateWeights(
    int[] feats, double values[], int nFeats, boolean positive) {
        assert nFeats >= 0 && nFeats <= feats.length &&
               nFeats <= values.length;
        assert updateCount != null;
        assert curWeight != null;

        /*
        for(int i=0; i<160; i++) {
            String s = "\u0204" + Character.toString((char)i) + "0";
            Integer f = featMap.get(s);
            if(f == null) continue;
            for(int j=0; j<nFeats; j++) {
                int feat = feats[j];
                if(feat == (int)f) {
                    if(positive) {
                        System.err.println(
                        "++ POS "+i+": "+curWeight[(int)f]);
                    } else {
                        System.err.println(
                        "-- POS "+i+": "+curWeight[(int)f]);
                    }
                }
            }
        }
        */
        if(positive) {
            for(int i=0; i<nFeats; i++) {
                int feat = feats[i];
                updateCount[feat]++;
                curWeight[feat] += values[i];
            }
        } else {
            for(int i=0; i<nFeats; i++) {
                int feat = feats[i];
                updateCount[feat]++;
                curWeight[feat] -= values[i];
            }
        }
    }

    /**
     * Updates the feature weights for the given feature IDs.
     *
     * @param feats     vector of active feature IDs
     * @param values    vector of the features' values
     * @param positive  if true, increase weights, otherwise decrease
     */
    public void updateWeights(
    int[] feats, double[] values, boolean positive) {
        updateWeights(feats, values, feats.length, positive);
    }

    /**
     * Computes the score of a feature vector.
     *
     * If curWeight is null (after <code>endTraining</code> is called), or the
     * <code>average<code> parameter is true, the averaged weights will be
     * used. Otherwise no averaging is used.
     *
     * @param feats     vector of active feature IDs
     * @param values    vector of the features' values
     * @param nFeats    only use the first nFeats indices of
     *                  the <code>feats</code> array
     * @param average   always use averaged weights
     * @return          score of feature vector
     */
    public double score(
    int[] feats, double[] values, int nFeats, boolean average) {
        double sum = 0.0;
        assert nFeats >= 0 && nFeats <= feats.length &&
               nFeats <= values.length;
        if(curWeight == null || average) {
            for(int i=0; i<nFeats; i++) {
                if (feats[i] < sumWeight.length)
                    sum += sumWeight[feats[i]] * values[i];
            }
        } else {
            for(int i=0; i<nFeats; i++) {
                if (feats[i] < curWeight.length)
                    sum += curWeight[feats[i]]*values[i];
            }
        }
        return sum;
    }

    /**
     * Computes the score of a feature vector.
     *
     * @param feats     vector of active feature IDs
     * @param values    vector of the features' values
     * @param average   always use averaged weights
     * @return          score of feature vector
     */
    public double score(int[] feats, double[] values, boolean average) {
        return score(feats, values, feats.length, average);
    }

    /**
     * Starts training by creating empty feature weights.
     */
    public void startTraining() {
        sumWeight = new double[0x1000];
        curWeight = new double[0x1000];
        updateCount = new int[0x1000];
        bestWeight = null;
        bestUpdateCount = null;
    }

    /**
     * Extends feature vectors when a new feature is added (if necessary).
     */
    protected void newFeature(int ID) {
        if(ID >= sumWeight.length) {
            int newLen = sumWeight.length + 0x1000;
            sumWeight = Arrays.copyOf(sumWeight, newLen);
            curWeight = Arrays.copyOf(curWeight, newLen);
            updateCount = Arrays.copyOf(updateCount, newLen);
        }
    }

    /**
     * Adds current weight vector to average weight vector.
     */
    public void accumulateWeights() {
        for(int i=0; i<sumWeight.length; i++) sumWeight[i] += curWeight[i];
    }

    /**
     * Marks the current averaged weight vector as the best one.
     * This could be called when the weight vector results in a new record on
     * a development set.
     */
    public void makeBestWeight() {
        bestWeight = Arrays.copyOf(sumWeight, size());
        bestUpdateCount = Arrays.copyOf(updateCount, size());
    }

    /**
     * Ends training.
     */
    public void endTraining() {
        // If there is a better weight vector than the current, use it
        if(bestWeight != null) {
            sumWeight = bestWeight;
            updateCount = bestUpdateCount;
        }
        bestWeight = null;
        bestUpdateCount = null;
        // Remove unnecessary features, as defined by copyFeature and
        // keepFeatureID below
        oldSumWeight = Arrays.copyOf(sumWeight, size());
        super.pruneFeatures();
        curWeight = null;
        oldSumWeight = null;
        updateCount = null;
        // Truncate the weight vector, due to the decreased number of features
        sumWeight = Arrays.copyOf(sumWeight, size());
    }

    protected void copyFeature(int fromID, int toID) {
        sumWeight[toID] = oldSumWeight[fromID];
    }

    protected boolean keepFeatureID(int ID) {
        return (ID < sumWeight.length &&
                sumWeight[ID] != 0 &&
                updateCount[ID] > 0);
    }
}

