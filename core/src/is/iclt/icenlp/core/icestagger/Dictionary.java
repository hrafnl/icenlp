package is.iclt.icenlp.core.icestagger;
import java.util.*;
import java.io.*;

/**
 * Dictionary containing a mapping of words to class strings.
 */
public class Dictionary implements Serializable {
    static final long serialVersionUID = -5515648578366080550L;

    public HashMap<String,String> map;

    /**
     * Constructor creating an empty lexicon.
     */
    public Dictionary() {
        map = new HashMap<String,String>();
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
            assert(fields.length == 2);
            map.put(fields[0].toLowerCase(), fields[1]);
        }
    }

    public void fromFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                new FileInputStream(filename), "UTF-8"));
        fromReader(reader);
        reader.close();
    }
}

