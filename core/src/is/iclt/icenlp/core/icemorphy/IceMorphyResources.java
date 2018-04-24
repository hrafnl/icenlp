package is.iclt.icenlp.core.icemorphy;

import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: hrafn
 * Date: 11/15/12
 * Time: 5:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class IceMorphyResources {
    final String dictPathTagger = "/dict/icetagger/";
    public InputStream isDictionaryBase,isDictionary, isEndingsBase, isEndings, isEndingsProper, isPrefixes, isTagFrequency;

    public IceMorphyResources() {
        isDictionaryBase = getClass().getResourceAsStream( dictPathTagger + IceMorphyLexicons.dictionaryBase );
        isDictionary = getClass().getResourceAsStream( dictPathTagger + IceMorphyLexicons.dictionary );
        isEndingsBase = getClass().getResourceAsStream( dictPathTagger + IceMorphyLexicons.endingsBaseDictionary );
        isEndings = getClass().getResourceAsStream( dictPathTagger + IceMorphyLexicons.endingsDictionary );
        isEndingsProper = getClass().getResourceAsStream( dictPathTagger + IceMorphyLexicons.endingsProperDictionary );
        isPrefixes = getClass().getResourceAsStream( dictPathTagger + IceMorphyLexicons.prefixesDictionary );
        isTagFrequency = getClass().getResourceAsStream( dictPathTagger + IceMorphyLexicons.tagFrequencyFile );
    }

    public void setDictionary(InputStream isDictionary) {
        this.isDictionary = isDictionary;
    }
}
