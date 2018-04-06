package is.iclt.icenlp.core.icestagger;
import java.util.*;
import java.io.*;

/**
 * Lexicon containing possible tags for every word form.
 * Note that the <code>Entry</code> lists in <code>lexicon</code> sholud be
 * sorted by ascending tag ID, as is done by the <code>sortLexicon</code>
 * method. This allows lists of tags to be merged efficiently.
 */
public class Lexicon implements Serializable {
    static final long serialVersionUID = 206015441661126717L;

    private HashMap<String,ArrayList<Entry>> lexicon;

    /**
     * Constructor creating an empty lexicon.
     */
    public Lexicon() {
        clear();
    }

    public void clear() {
        this.lexicon = new HashMap<String,ArrayList<Entry>>();
    }

    /**
     * Returns the number of entries in the lexicon.
     */
    public int size() {
        return lexicon.size();
    }

    /**
     * Add another possible tag entry to the given word form.
     * If no entry exists for the given word form, a new is created.
     * The list of entries for each word form is kept sorted by tag ID.
     *
     * @param wf    word form
     * @param tag   tag ID
     * @param n     number of occurences in training corpus
     */
    public void addEntry(String wf, String lf, int tag, int n) {
        String wf_lower = wf.toLowerCase();
        ArrayList<Entry> entries = this.lexicon.get(wf_lower);
        if(entries == null) {
            entries = new ArrayList<Entry>(4);
            this.lexicon.put(wf_lower, entries);
        }
        this.addEntry(entries, lf, tag, n);
    }

    /**
     * Add another possible tag entry to a list of entries.
     * This is used internally when an <code>Entry</code> list has been found
     * in the lexicon, and an item is to be added to it.
     */
    private void addEntry(
    ArrayList<Entry> entries, String lf, int tag, int n) {
        for(int i=0; i<entries.size(); i++) {
            Entry entry = entries.get(i);
            // TODO: investigate the consequences of having reversed this all
            // along, leading to incorrectly sorted entry lists
            if(entry.tag > tag) {
                entries.add(i, new Entry(lf, tag, n));
                return;
            } else if(tag == entry.tag) {
                String newLf = (lf == null)? entry.lf : lf;
                entries.set(i, new Entry(newLf, tag, n+entry.n));
                return;
            }
        }
        entries.add(new Entry(lf, tag, n));
    }

    /**
     * Get the entries of possible tags for this word form.
     *
     * @param wf    word form
     * @return      array of Entry objects for possible tags, or null
     */
    public Entry[] getEntries(String wf) {
        ArrayList<Entry> entries = this.lexicon.get(wf.toLowerCase());
        if(entries == null) return null;
        Entry[] entriesArray = new Entry[entries.size()];
        return (Entry[])entries.toArray(entriesArray);
    }

    /**
     * Get the number of times this word form has been seen.
     *
     * @param wf    word form
     * @return      number of observations in lexicon/training corpus
     */
    public int wfCount(String wf) {
        ArrayList<Entry> entries = this.lexicon.get(wf.toLowerCase());
        if(entries == null) return 0;
        int n = 0;
        for(Entry entry : entries) n += entry.n;
        return n;
    }

    /**
     * Check if the word form is in the lexicon.
     * Note that it does not have to be seen in the training data to end up
     * in the lexicon.
     *
     * @param wf    word form
     * @return      true iff the word form is in the lexicon
     */
    public boolean inLexicon(String wf) {
        return this.lexicon.containsKey(wf.toLowerCase());
    }

    /**
     * If a word form has only one of <code>tag1</code> and <code>tag2</code>,
     * add the other.
     */
    public void interpolate(int tag1, int tag2) {
        HashSet<String> suffixSet = new HashSet<String>();
        // Find the set of word forms that are ambiguous between tag1 and tag2
        for(Map.Entry<String,ArrayList<Entry>> wfEntries:
            this.lexicon.entrySet()) {
            String wf = wfEntries.getKey();
            ArrayList<Entry> entries = wfEntries.getValue();
            Entry entry1 = null, entry2 = null;
            for(Entry entry : entries) {
                if(entry.tag == tag1) entry1 = entry;
                else if(entry.tag == tag2) entry2 = entry;
            }
            if(entry1 != null && entry2 != null) {
                suffixSet.add(wf);
            }
        }
        // Then go through all word forms that end with any of these words,
        // and add tag1 if tag2 exists, or vice versa.
        for(Map.Entry<String,ArrayList<Entry>> wfEntries:
            this.lexicon.entrySet()) {
            String wf = wfEntries.getKey();
            ArrayList<Entry> entries = wfEntries.getValue();
            Entry entry1 = null, entry2 = null;
            for(Entry entry : entries) {
                if(entry.tag == tag1) entry1 = entry;
                else if(entry.tag == tag2) entry2 = entry;
            }
            if(entry1 == null && entry2 == null) continue;
            if(entry1 != null && entry2 != null) continue;
            boolean hasSuffix = false;
            for(int i=2; i<wf.length(); i++) {
                if(suffixSet.contains(wf.substring(i))) {
                    hasSuffix = true;
                    break;
                }
            }
            if(!hasSuffix) continue;
            if(entry1 != null && entry2 == null) {
                this.addEntry(entries, entry1.lf, tag2, 0);
            } else if(entry1 == null && entry2 != null) {
                this.addEntry(entries, entry2.lf, tag1, 0);
            }
        }
    }

    /**
     * Read a complete lexicon from a file.
     * Each lexicon entry is on a line, containing the following tab-separated
     * fields:
     *      word form    lemma   tag name    count
     *
     * If the <code>extend</code> parameter is <code>false</code>, entries for
     * words that were already in the lexicon are ignored.
     *
     * @param reader    a BufferedReader to read the data from
     * @param tagset    a TagSet used to translate tag names to tag IDs
     * @param extend    extend tags for known words?
     * @throws IOException          from the reader
     * @throws TagNameException     if an invalid tag name is used in the file
     */
    public void fromReader(
        BufferedReader reader, TagSet tagset, boolean extend)
    throws IOException, TagNameException {
        String line;
        HashSet<String> known = null;
        if(!extend) known = new HashSet<String>(lexicon.keySet());
        while((line = reader.readLine()) != null) {
            String[] fields = line.split("\t");
            if(fields.length >= 4) {
                String wf = fields[0];
                if(!extend && known.contains(wf.toLowerCase())) continue;
                String lf = fields[1];
                int tag = tagset.getTagID(fields[2], true);
                int n = Integer.parseInt(fields[3]);
                this.addEntry(wf, lf, tag, n);
            }
        }
        //this.sortLexicon();
    }
    
    public void fromFile(String filename, TagSet tagset, boolean extend)
    throws IOException, TagNameException {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                new FileInputStream(filename), "UTF-8"));
        fromReader(reader, tagset, extend);
        reader.close();
    }

    /**
     * Sort the Entry lists in the lexicon by tag ID.
     */
    /*
    private void sortLexicon() {
        for(ArrayList<Entry> entries: this.lexicon.values()) {
            Collections.sort(entries, new Comparator<Entry>() {
                public int compare(Entry o1, Entry o2) {
                    return o1.tag - o2.tag;
                }
            });
        }
    }
    */

    /**
     * A single lemma/tag/count tuple, each word form may have several of
     * these.
     */
    public static class Entry implements Serializable {
        public final int tag;
        public final int n;
        public final String lf;
        /**
         * Class constructor.
         *
         * @param lf    lemma
         * @param tag   tag ID
         * @param n     number of occurences in training corpus
         */
        Entry(String lf, int tag, int n) {
            this.lf = lf; this.tag = tag; this.n = n;
        }
    }
}

