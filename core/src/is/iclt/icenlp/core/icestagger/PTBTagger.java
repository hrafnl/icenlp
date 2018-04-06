package is.iclt.icenlp.core.icestagger;
import java.util.*;

public class PTBTagger extends Tagger {
    static final long serialVersionUID = -5736227015510954918L;

    public PTBTagger(TaggedData taggedData, int posBeamSize, int neBeamSize)
    {
        super(taggedData, posBeamSize, neBeamSize);
    }

    protected String getLemma(TaggedToken token) {
        return null;
    }

    protected void computeOpenTags() {
        final String[] openTagArray = {
            "CD", "FW", "IN", "JJ", "JJR", "JJS", "NN", "NNS", "NNP", "NNPS",
            "RB", "RBR", "RBS", "SYM", "UH", "VB", "VBD", "VBG", "VBN", "VBP",
            "VBZ" };
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
}

