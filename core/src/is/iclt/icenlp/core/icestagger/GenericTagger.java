package is.iclt.icenlp.core.icestagger;
import java.util.*;

public class GenericTagger extends Tagger {
    static final long serialVersionUID = -8389542359832823L;

    public GenericTagger(
    TaggedData taggedData, int posBeamSize, int neBeamSize) {
        super(taggedData, posBeamSize, neBeamSize);
    }

    public void train(
    TaggedToken[][] trainSents, TaggedToken[][] devSents) {
        System.err.println("POS lexicon size before: " + posLexicon.size());
        buildLexicons(trainSents);
        System.err.println("POS lexicon size after: " + posLexicon.size());
        super.train(trainSents, devSents);
    }

    protected String getLemma(TaggedToken token) {
        return null;
    }

    protected void computeOpenTags() {
        final int nTags = taggedData.getPosTagSet().size();
        openTags = new int[nTags];
        for(int i=0; i<nTags; i++) openTags[i] = i;
    }
}

