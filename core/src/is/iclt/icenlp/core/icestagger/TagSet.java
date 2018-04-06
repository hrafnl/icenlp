package is.iclt.icenlp.core.icestagger;
import java.io.Serializable;
import java.util.*;

/**
 * Class representing a tagset for any annotation type.
 */
public class TagSet implements Serializable {
    protected HashMap<String,Integer> tagID;
    protected ArrayList<String> tagName;

    /**
     * Creates an empty tagset object.
     */
    public TagSet() {
        tagID = new HashMap<String,Integer>();
        tagName = new ArrayList<String>();
    }

    /**
     * Returns the number of tags.
     *
     * @return      number of tags
     */
    public int size() {
        return tagName.size();
    }

    /**
     * Returns the set of tag names.
     *
     * @return      array of names, with index i corresponding to tag ID i
     */
    public String[] getTagNames() {
        String[] names = new String[tagName.size()];
        return tagName.toArray(names);
    }

    /**
     * Converts a string representation to a tag ID number.
     *
     * @param s     string representation of tag
     * @return      tag ID
     * @throws TagNameException if the tag name does not exist
     */
    public int getTagID(String s) throws TagNameException {
        Integer ID = tagID.get(s);
        if(ID == null) {
            throw new TagNameException("Unknown tag name: " + s);
        }
        return (int)ID;
    }

    /**
     * Adds a new tag with the given string representation.
     * If the tag exists, this is equivalent to <code>getTagID</code>.
     * Otherwise, a new ID will be created and returned.
     *
     * @param s     string representation of tag
     * @return      tag ID
     * @throws TagNameException if the string representation is invalid
     */
    public int addTag(String s) throws TagNameException {
        Integer ID = tagID.get(s);
        if(ID == null) {
            int newID = tagID.size();
            tagID.put(s, new Integer(newID));
            tagName.add(s);
            return newID;
        }
        return (int)ID;
    }

    /**
     * Converts a string representation to a tag ID number.
     * If <code>extend</code> is <code>true</code>, this calls
     * <code>getTagID(s)</code>, otherwise <code>addTag(s)</code>
     *
     * @param s         string representation of tag
     * @param extend    if true, unknown tags are created
     * @return          tag ID
     * @throws TagNameException if the tag name does not exist
     */
    public int getTagID(String s, boolean extend) throws TagNameException {
        if(extend) return addTag(s);
        else return getTagID(s);
    }

    /**
     * Converts a tag ID to string representation.
     *
     * @param ID    tag ID
     * @return      string representation of tag
     * @throws TagNameException if the tag ID does not exist
     */
    public String getTagName(int ID) throws TagNameException {
        if(ID < 0 || ID >= tagName.size()) {
            throw new TagNameException("Invalid tag ID: " + ID);
        }
        return tagName.get(ID);
    }
}

