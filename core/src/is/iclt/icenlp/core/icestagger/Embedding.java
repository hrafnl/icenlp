package is.iclt.icenlp.core.icestagger;
import java.util.*;
import java.io.*;

/**
 * Dictionary containing a mapping of words to real-valued vectors.
 */
public class Embedding implements Serializable {
    static final long serialVersionUID = 1009927046375089653L;

    public HashMap<String,float[]> map;

    /**
     * Constructor creating an empty lexicon.
     */
    public Embedding() {
        map = new HashMap<String,float[]>();
    }

    /**
     * Read a complete dictionary from a file.
     * Each entry is on a line, containing a tab-separated key/value pair.
     *
     * @param reader    a BufferedReader to read the data from
     * @throws IOException          from the reader
     */
    public void fromReader(BufferedReader reader) throws IOException {
        String line;
        while((line = reader.readLine()) != null) {
            String[] fields = line.split("\t");
            assert(fields.length >= 2);
            float[] value = new float[fields.length-1];
            for(int i=0; i<value.length; i++) {
                value[i] = Float.parseFloat(fields[i+1]);
            }
            map.put(fields[0].toLowerCase(), value);
        }
    }

    public void fromFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                new FileInputStream(filename), "UTF-8"));
        fromReader(reader);
        reader.close();
    }

    /**
     * Scale the embeddings so that the standard deviation is sigma.
     * Turian et al. (2010) recommend sigma ~ 0.1
     */
    public void rescale(float sigma) {
        double t0 = 0.0, t1 = 0.0, t2 = 0.0;
        for(float[] v : map.values()) {
            t0 += (double)v.length;
            for(float x : v) {
                t1 += (double)x;
            }
        }
        double avg = t1 / t0;
        for(float[] v : map.values()) {
            t0 += (double)v.length;
            for(float x : v) {
                double d = (double)x - avg;
                t2 += d*d;
            }
        }
        double variance = t2 / t0;
        double stddev = Math.sqrt(variance);
        float scale = sigma / (float)stddev;
        for(float[] v : map.values()) {
            for(int i=0; i<v.length; i++) {
                v[i] *= scale;
            }
        }
    }
}

