package is.iclt.icenlp.core.icestagger;
import java.util.*;
import java.io.Serializable;

/**
 * Class mapping String features to ID numbers.
 */
class FeatureMap implements Serializable {
    private HashMap <String,Integer> featMap;

    /**
     * Creates a new, empty feature map.
     */
    FeatureMap() {
        featMap = new HashMap<String,Integer>();
    }

    /**
     * Returns the nmuber of distinct features in the map.
     */
    public int size() {
        return featMap.size();
    }

    /**
     * Returns the ID of a feature.
     *
     * @param s         feature string
     * @param extend    if <code>true</code>, create feature if none exists
     * @return          feature index, or -1 if <code>extend</code> is
     *                  <code>false</code> and the feature does not exist
     */
    public int getFeatureID(String s, boolean extend) {
        Integer feat = featMap.get(s);
        if(feat == null) {
            if(extend) {
                return addFeature(s);
            } else {
                return -1;
            }
        } else {
            return (int)feat;
        }
    }

    /**
     * Adds a new feature and returns its ID.
     *
     * @param s     feature string
     * @return      feature ID of the new feature
     */
    protected int addFeature(String s) {
        int n = size();
        featMap.put(s, new Integer(n));
        newFeature(n);
        return n;
    }

    /**
     * Prunes unnecessary features.
     *
     * The method <code>keepFeatureID<code> decides which features to keep,
     * and <code>copyFeature</code> is called whenever a feature's ID number
     * is changed. Subclasses should override these methods to e.g. copy
     * feature weight vectors during the pruning process.
     */
    protected void pruneFeatures() {
        Iterator<Map.Entry<String,Integer>> iter;
        int toID = 0;
        for(iter=featMap.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<String,Integer> e = iter.next();
            String s = e.getKey();
            int fromID = (int)e.getValue();
            if(keepFeatureID(fromID)) {
                if(fromID != toID) {
                    e.setValue(new Integer(toID));
                    copyFeature(fromID, toID);
                }
                toID++;
            } else {
                iter.remove();
            }
        }
    }

    /**
     * Moves information associated with feature fromID to toID.
     *
     * Subclasses should override this to make <code>pruneFeatures</code>
     * work as expected.
     *
     * @param fromID    feature ID to copy from
     * @param toID      feature ID to copy to
     */
    protected void copyFeature(int fromID, int toID) {
    }

    /**
     * Checks whether the feature with a given ID should be kept.
     *
     * Subclasses should override this to make <code>pruneFeatures</code>
     * work as expected.
     */
    protected boolean keepFeatureID(int ID) {
        return true;
    }

    /**
     * Called whenever a new feature is added.
     *
     * Subclasses should override this to extend their feature vectors.
     */
    protected void newFeature(int ID) {
    }
}

