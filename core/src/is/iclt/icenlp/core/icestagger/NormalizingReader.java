package is.iclt.icenlp.core.icestagger;
import java.io.*;
import javax.swing.text.Segment;
import java.text.Normalizer;

/**
 * Wrapper to Reader objects that performs unicode composition normalization.
 */
class NormalizingReader extends Reader {
    private Reader reader;
    private Normalizer.Form form;

    /**
     * Creates a new normalizing reader.
     *
     * @param reader        Reader to wrap
     * @param canonical     Use canonical composition (NFKC)?
     */
    NormalizingReader(Reader reader, boolean canonical) {
        this.reader = reader;
        this.form = canonical? Normalizer.Form.NFKC : Normalizer.Form.NFC;
    }

    public int read(char[] cbuf, int off, int len) throws IOException {
        // This is a really ugly hack to make sure that a few characters extra
        // will not cause an overflow in cbuf
        len = len/2;
        int nRead = this.reader.read(cbuf, off, len);
        if(nRead < 0) return nRead;
        Segment cbufUsed = new Segment(cbuf, off, nRead);
        String normalized = Normalizer.normalize(cbufUsed, this.form);
        int normalizedLen = normalized.length();
        normalized.getChars(0, normalizedLen, cbuf, off);
        return normalizedLen;
    }

    public void close() throws IOException {
        this.reader.close();
    }
}

