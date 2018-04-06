package is.iclt.icenlp.core.icestagger;
import java.util.*;
import java.util.regex.*;

public class IceTagger extends Tagger {
    // 0 = do not use IceMorphy
    // 1 = use IceMorphy for unknown words and also to fill out tag profiles
    //     for known words
    // 2 = use IceMorphy only for unknown words
    protected int iceMorphyType = 0;

    public IceTagger(TaggedData taggedData, int posBeamSize, int neBeamSize)
    {
        super(taggedData, posBeamSize, neBeamSize);
        // Make sure that tags not in the training data get included
        for(String tagName: openTagArray) {
            try { taggedData.getPosTagSet().addTag(tagName); }
            catch(TagNameException e) {}
        }
    }

    public void setIceMorphyType(int x) {
        iceMorphyType = x;
    }

    public int getIceMorphyType() {
        return iceMorphyType;
    }

    private String tagGetName(int tag) {
        try {
            return taggedData.getPosTagSet().getTagName(tag);
        } catch(TagNameException e) {
            System.err.println("Error: unknown case tag ID: "+tag);
            return "______";
        }
    }

    private boolean tagIsVerb(String name) {
        return name.charAt(0) == 's' && name.charAt(1) != 'þ';
    }

    // Return a two-letter array with case + gender codes
    private char[] tagGetCaseGender(String name) {
        char codes[] = new char[2];
        codes[0] = '_'; // case
        codes[1] = '_'; // gender
        if(name.equals("ta")) return codes; // special case, do not handle
        int len = name.length();
        if(name.charAt(0) == 'n' || name.charAt(0) == 'l' ||
           name.charAt(0) == 'g')
        {
            // noun/adjective/article
            if(len >= 2) codes[1] = name.charAt(1);
            if(len >= 4) codes[0] = name.charAt(3);
        } else if(name.charAt(0) == 'f' || name.charAt(0) == 't') {
            // pronoun/numeral
            if(len >= 3) codes[1] = name.charAt(2);
            if(len >= 5) codes[0] = name.charAt(4);
        }
        return codes;
    }

    protected void computeOpenTags() {
        String[] names = taggedData.getPosTagSet().getTagNames();
        int[] tags = new int[names.length];
        HashSet<String> openTagSet = new HashSet<String>(Arrays.asList(
                openTagArray));
        int nTags = 0;
        for(int i=0; i<names.length; i++) {
            if(openTagSet.contains(names[i])) {
                tags[nTags++] = i;
            }
        }
        assert nTags > 0;
        for(int t=0; t<nTags-1; t++)
            assert(tags[t] < tags[t+1]);
        openTags = Arrays.copyOf(tags, nTags);
    }

    protected void guessTags(String wordForm, boolean firstWord)
    {
        if(iceMorphyType == 0) {
        } else if(iceMorphyType == 2) {
            Guesser.analyze(wordForm, firstWord, posLexicon,
                            taggedData.getPosTagSet(), true);
        } else if(iceMorphyType == 1) {
            Guesser.analyze(wordForm, firstWord, posLexicon,
                            taggedData.getPosTagSet(), false);
        } else {
            assert false;
        }
    }

    protected String getLemma(TaggedToken token) {
        return null;
    }

    private static final Pattern vowels =
        Pattern.compile("[aáeéiíoóuúyýæö]{1,2}");

    /**
     * Computes feature values given a certain POS tag and context.
     */
    protected int getPosFeats(
    TaggedToken[] sentence, int idx, int[] feats, double[] values, int nFeats,
    int posTag, int neTag, int neTypeTag, boolean hasLast, History last,
    boolean extend)
    {
        char[] head = new char[8];
        int f;
        TaggedToken ttok = sentence[idx];
        char isInitial = (idx == 0)? (char)1 : (char)0;
        char isFinal = (idx == sentence.length-1)? (char)1 : (char)0;
        char capitalization = ttok.token.isCapitalized()? (char)1 : (char)0;
        char tokType = (char)ttok.token.type;
        char tokType1a = (idx == sentence.length-1)? 0xffff :
                         (char)sentence[idx+1].token.type;
        String text = ttok.token.value;
        String textLower = ttok.textLower;
        String nextText =
            (idx == sentence.length-1)? "" : sentence[idx+1].token.value;
        String nextText2 =
            (idx >= sentence.length-2)? "" : sentence[idx+2].token.value;
        String lastLower = (idx == 0)? "" : sentence[idx-1].textLower;
        String lastLower2 = (idx < 2)? "" : sentence[idx-2].textLower;
        String nextLower =
            (idx == sentence.length-1)? "" : sentence[idx+1].textLower;
        String nextLower2 =
            (idx >= sentence.length-2)? "" : sentence[idx+2].textLower;

        if(!hasLast) {
            // POS + textLower + final?
            head[0] = 0x00;
            head[1] = (char)posTag;
            head[2] = isFinal;
            f = posPerceptron.getFeatureID(
                new String(head,0,3) + textLower, extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }

            // POS + textLower + capitalization + initial?
            head[0] = 0x01;
            head[1] = (char)posTag;
            head[2] = capitalization;
            head[3] = isInitial;
            f = posPerceptron.getFeatureID(
                new String(head,0,4) + textLower, extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }

            // POS + textLower + lastLower
            head[0] = 0x02;
            head[1] = (char)posTag;
            f = posPerceptron.getFeatureID(
                new String(head,0,2) + lastLower + "\n" + textLower, extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }

            // POS + textLower + nextLower
            head[0] = 0x03;
            head[1] = (char)posTag;
            f = posPerceptron.getFeatureID(
                new String(head,0,2) + textLower + "\n" + nextLower, extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }

            // POS + textLower + nextLower + nextLower2
            head[0] = 0x04;
            head[1] = (char)posTag;
            f = posPerceptron.getFeatureID(new String(head,0,2) +
                textLower + "\n" + nextLower + "\n" + nextLower2, extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }

            // POS + lastLower + textLower + nextLower
            head[0] = 0x05;
            head[1] = (char)posTag;
            f = posPerceptron.getFeatureID(new String(head,0,2) +
                lastLower + "\n" + textLower + "\n" + nextLower, extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }

            // POS + lastLower2 + lastLower + textLower
            head[0] = 0x06;
            head[1] = (char)posTag;
            f = posPerceptron.getFeatureID(new String(head,0,2) +
                lastLower2 + "\n" + lastLower + "\n" + textLower, extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }
            
            // POS + lastLower
            head[0] = 0x07;
            head[1] = (char)posTag;
            f = posPerceptron.getFeatureID(
                new String(head,0,2) + lastLower, extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }

            // POS + lastLower2
            head[0] = 0x08;
            head[1] = (char)posTag;
            f = posPerceptron.getFeatureID(
                new String(head,0,2) + lastLower2, extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }

            // POS + nextLower
            head[0] = 0x09;
            head[1] = (char)posTag;
            f = posPerceptron.getFeatureID(
                new String(head,0,2) + nextLower, extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }
            
            // POS + nextLower2
            head[0] = 0x0a;
            head[1] = (char)posTag;
            f = posPerceptron.getFeatureID(
                new String(head,0,2) + nextLower2, extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }

            // POS + prefixes + capitalization + initial?
            head[0] = 0x10;
            head[1] = (char)posTag;
            head[2] = capitalization;
            head[3] = isInitial;
            for(int i=1; i<=4 && i<textLower.length(); i++) {
                String prefix = textLower.substring(0,i);
                if(allowedPrefixes == null ||
                   allowedPrefixes.contains(prefix))
                {
                    f = posPerceptron.getFeatureID(new String(head,0,4) +
                        prefix, extend);
                    if(f >= 0) {
                        feats[nFeats] = f; values[nFeats] = 1.0; nFeats++;
                    }
                }
            }

            // POS + suffixes + capitalization + initial?
            head[0] = 0x11;
            head[1] = (char)posTag;
            head[2] = capitalization;
            head[3] = isInitial;
            for(int i=textLower.length()-5; i<textLower.length(); i++) {
                if(i<2) continue;
                String suffix = textLower.substring(i);
                if(allowedSuffixes == null ||
                   allowedSuffixes.contains(suffix))
                {
                    f = posPerceptron.getFeatureID(new String(head,0,4) +
                        suffix, extend);
                    if(f >= 0) {
                        feats[nFeats] = f; values[nFeats] = 1.0; nFeats++;
                    }
                }
            }

            // POS + dictionary
            head[0] = 0x12;
            head[1] = (char)posTag;
            for(int i=0; i<posDictionaries.size(); i++) {
                Dictionary dict = posDictionaries.get(i);
                String value = dict.map.get(text);
                String nextValue =
                    (i == sentence.length-1)? "" :
                    dict.map.get(nextText);
                String nextValue2 =
                    (i >= sentence.length-2)? "" :
                    dict.map.get(nextText2);
                head[2] = (char)i;
                String[] combinations = {
                    value,
                    (value == null || nextValue == null)? null :
                        value + "\n" + nextValue,
                    nextValue,
                    (nextValue == null || nextValue2 == null)? null :
                        nextValue + "\n" + nextValue2,
                    nextValue2
                };
                for(int j=0; j<combinations.length; j++) {
                    if(combinations[j] == null) continue;
                    head[3] = (char)j;
                    f = posPerceptron.getFeatureID(
                        new String(head,0,4) + combinations[j], extend);
                    if(f >= 0) {
                        feats[nFeats] = f; values[nFeats] = 1.0; nFeats++;
                    }
                }
            }

            // POS + embedding
            head[0] = 0x13;
            head[1] = (char)posTag;
            for(int i=0; i<posEmbeddings.size(); i++) {
                float[] value = posEmbeddings.get(i).map.get(textLower);
                if(value == null) continue;
                head[2] = (char)i;
                for(int j=0; j<value.length; j++) {
                    head[3] = (char)j;
                    f = posPerceptron.getFeatureID(
                        new String(head,0,4), extend);
                    if(f >= 0) {
                        feats[nFeats] = f; values[nFeats] = value[j]; nFeats++;
                    }
                }
            }

            // POS + token type + contains dash?
            head[0] = 0x20;
            head[1] = (char)posTag;
            head[2] = tokType;
            head[3] = (char)(textLower.contains("-")? 1 : 0);
            f = posPerceptron.getFeatureID(new String(head,0,4), extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }

            // POS + (current, next) token type
            head[0] = 0x21;
            head[1] = (char)posTag;
            head[2] = tokType;
            head[3] = tokType1a;
            f = posPerceptron.getFeatureID(new String(head,0,4), extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }
        } else {
            char posTag1b = 0xffff;
            char posTag2b = 0xffff;
            if(last != null) {
                posTag1b = (char)last.posTag;
                if(last.last != null) posTag2b = (char)last.last.posTag;
            }
            // The closest verb to the left, or null if none
            History verb=last;
            for(; verb != null; verb = verb.last) {
                if(tagIsVerb(tagGetName(verb.posTag))) break;
            }
            char[] cg = tagGetCaseGender(tagGetName(posTag));
            char tagCase = cg[0];
            char tagGender = cg[1];
            // Only use verb features if there is a verb, and if the current
            // word has case and/or gender features.
            if(verb != null && !(tagCase == '_' && tagGender == '_')) {
                // Stem features
                head[0] = 0xa0;

                String verbText = verb.textLower;
                int verbTag = verb.posTag;
                String verbTagName = tagGetName(verbTag);
                // Note: depending on the implementation of tagIsVerb(), there
                // might not be any perfect participle verbs, but the code
                // here assumes that there _could_ be
                boolean perfpart = verbTagName.charAt(1) == 'þ';
                String stem = verbText;
                Matcher m = vowels.matcher(verbText);
                if(m.find()) {
                    stem = verbText.substring(0,m.start()) + "*" +
                           verbText.substring(m.end());
                }

                int minRemove = 2, maxRemove = 3;
                if(!perfpart && verbTagName.length() >= 6 &&
                    // For present-tense verbs, only remove the last 1 or 2
                    // letters
                   verbTagName.charAt(5) == 'n')
                {
                    minRemove = 1; maxRemove = 2;
                } else if(!perfpart && verbTagName.charAt(1) == 's') {
                    // For supine verbs, remove only 2 letters
                    minRemove = 2; maxRemove = 2;
                }
                // Creature features for case/gender/tag combined with the
                // stem candidates
                for(int i=minRemove; i<=maxRemove; i++) {
                    if(stem.length() < 2+i) continue;
                    for(int j=0; j<3; j++) {
                        head[1] = (char)j;
                        if(j == 0) head[2] = tagCase;
                        else if(j == 1) head[2] = tagGender;
                        else if(j == 2) head[2] = (char)posTag;

                        // "stem", with vowel abstraction
                        f = posPerceptron.getFeatureID(new String(head,0,3) +
                            stem.substring(0, stem.length()-i), extend);
                        if(f >= 0) {
                            feats[nFeats] = f; values[nFeats] = 1.0; nFeats++;
                        }
                    }
                }

                // POS + tag of last verb
                head[0] = 0xa1;
                head[1] = (char)posTag;
                head[2] = (char)verbTag;
                f = posPerceptron.getFeatureID(new String(head,0,3), extend);
                if(f >= 0) {
                    feats[nFeats] = f; values[nFeats] = 1.0; nFeats++;
                }
            }

            // (previous, current) POS
            head[0] = 0x80;
            head[1] = (char)posTag;
            head[2] = posTag1b;
            f = posPerceptron.getFeatureID(new String(head,0,3), extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }

            // (previous2, previous, current) POS
            head[0] = 0x81;
            head[1] = (char)posTag;
            head[2] = posTag1b;
            head[3] = posTag2b;
            f = posPerceptron.getFeatureID(new String(head,0,4), extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }
            
            // (previous, current) POS + textLower
            head[0] = 0x82;
            head[1] = (char)posTag;
            head[2] = posTag1b;
            f = posPerceptron.getFeatureID(
                new String(head,0,3) + textLower, extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }

            // (previous, current) POS + textLower + nextLower
            head[0] = 0x83;
            head[1] = (char)posTag;
            head[2] = posTag1b;
            f = posPerceptron.getFeatureID(
                new String(head,0,3) + textLower + "\n" + nextLower, extend);
            if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }

            // (previous, current) POS + dictionary
            head[0] = 0x84;
            head[1] = (char)posTag;
            head[2] = posTag1b;
            for(int i=0; i<posDictionaries.size(); i++) {
                Dictionary dict = posDictionaries.get(i);
                String nextValue =
                    (i == sentence.length-1)? null :
                    dict.map.get(nextText);
                if(nextValue == null) continue;
                head[3] = (char)i;
                f = posPerceptron.getFeatureID(
                    new String(head,0,4) + nextValue, extend);
                if(f >= 0) {
                    feats[nFeats] = f; values[nFeats] = 1.0; nFeats++;
                }
            }
        }

        return nFeats;
    }

    private final static String[] openTagArray = {
            "lhensf",
            "lkensf",
            "lvensf",
            "lheþsf",
            "lheosf",
            "lkfnsf",
            "lkeosf",
            "lhfnsf",
            "lveosf",
            "lkeþsf",
            "lvfnsf",
            "lveþsf",
            "lkenvf",
            "lhfþsf",
            "lhfosf",
            "lkfþsf",
            "lvfosf",
            "lvfþsf",
            "lkfosf",
            "lhenvm",
            "lkenof",
            "lvenvf",
            "lvenof",
            "lheovm",
            "lkeþvf",
            "lheþvf",
            "lkenvm",
            "lvenvm",
            "lhenof",
            "lkeovf",
            "lhenvf",
            "lkfesf",
            "lveovf",
            "lkeþve",
            "lheovf",
            "lveþvf",
            "lhense",
            "lveesf",
            "lkenve",
            "lhenve",
            "lkfnvm",
            "lhfesf",
            "lveþve",
            "lheþve",
            "lheesf",
            "lvenve",
            "lheove",
            "lkeevf",
            "lkeesf",
            "lkense",
            "lkeþof",
            "lkfnof",
            "lheoof",
            "lveove",
            "lveoof",
            "lkfnse",
            "lhfnvm",
            "lveþof",
            "lkeovm",
            "lvfesf",
            "lkeove",
            "lhfove",
            "lvfnvm",
            "lveevf",
            "lkeoof",
            "lhfnof",
            "lheþof",
            "lveþvm",
            "lkfnvf",
            "lhfþof",
            "lvense",
            "lveovm",
            "lhfnvf",
            "lheþvm",
            "lheevf",
            "lhfþve",
            "lheþse",
            "lkfþvf",
            "lkfovf",
            "lkeþvm",
            "lhfovm",
            "lkfove",
            "lvfnof",
            "lhfnve",
            "lkfþve",
            "lkfnve",
            "lvfþvf",
            "lhfþvf",
            "lhfþvm",
            "lvfoof",
            "lvfnvf",
            "lhfovf",
            "lhfoof",
            "lhfnse",
            "lvfþof",
            "lvfovm",
            "lveevm",
            "lhfeve",
            "lvfnse",
            "lvfnve",
            "lkfþof",
            "lvfove",
            "lkfoof",
            "lhfevm",
            "lvfþve",
            "lkfevf",
            "lveeof",
            "lvfovf",
            "lkfþvm",
            "lkfovm",
            "lkeose",
            "lhfþse",
            "lheose",
            "lheeve",
            "lkeevm",
            "lkfevm",
            "lkfeve",
            "lvfþvm",
            "lheevm",
            "lhfevf",
            "lhfose",
            "lvfeve",
            "lkeeof",
            "lvfevf",
            "lveeve",
            "lkfeof",
            "lvfevm",
            "lkfþse",
            "lveose",
            "lkeeve",
            "lheeof",
            "lvfeof",
            "lkeþse",
            "lhfeof",
            "lkfese",
            "lkfose",
            "lvfþse",
            "lvfose",
            "lveþse",
            "lkfþsm",
            "lveese",
            "lvfese",
            "lhfese",
            "lhfþsm",
            "lheosm",
            "nxeþ-s",
            "nxen-s",
            "nxee-s",
            "nxeo-s",
            "nxen",
            "nxeþ",
            "nxeo",
            "nxee",
            "nxfo",
            "nxfn-s",
            "nken-s",
            "nken",
            "nheþ",
            "nveo",
            "nkeo",
            "nven",
            "nveþ",
            "nkeþ",
            "nheo",
            "nhen",
            "nkeog",
            "nhfþ",
            "nheog",
            "nven-s",
            "nkfn",
            "nvfo",
            "nkeng",
            "nheþg",
            "nvfþ",
            "nveng",
            "nhfo",
            "nkeþg",
            "nveþg",
            "nveog",
            "nkfþ",
            "nkfo",
            "nvfn",
            "nkee",
            "nvee",
            "nhfn",
            "nheng",
            "nkeþ-s",
            "nkee-s",
            "nhfe",
            "nkfe",
            "nhee",
            "nkeo-s",
            "nveþ-s",
            "nvfe",
            "nveeg",
            "nkeeg",
            "nhfog",
            "nheeg",
            "nkfng",
            "nheþ-s",
            "nhfng",
            "nvee-s",
            "nvfog",
            "nhfþg",
            "nveo-s",
            "nkfog",
            "nvfng",
            "nkfþg",
            "nvfþg",
            "nhee-s",
            "nkfeg",
            "nhfeg",
            "nkfn-s",
            "nheo-s",
            "nkfþ-s",
            "nvfeg",
            "nhen-s",
            "nkfe-s",
            "nkeþgs",
            "nkeogs",
            "nheþgs",
            "nkengs",
            "nveþgs",
            "nheegs",
            "nhengs",
            "nkeegs",
            "nkfo-s",
            "nhfþ-s",
            "nveogs",
            "nvengs",
            "nvfþ-s",
            "nheogs",
            "nhfþgs",
            "nveegs",
            "nhfe-s",
            "nvfo-s",
            "nhfn-s",
            "nhfegs",
            "nvfn-s",
            "nvfe-s",
            "nkfngs",
            "nhfo-s",
            "nhfngs",
            "nhfogs",
            "nvfþgs",
            "nvfngs",
            "nkfegs",
            "nkfþgs",
            "nkfogs",
            "nvfegs",
            "nkens",
            "nvfogs",
            "ta",
            "tfkfn",
            "tfhfe",
            "tfkfo",
            "tfhfo",
            "tfhfn",
            "tfvfo",
            "tfhfþ",
            "tfken",
            "tfvfn",
            "tfheþ",
            "tfkfþ",
            "tfhen",
            "tfkfe",
            "tfheo",
            "tfvfþ",
            "tfkeo",
            "tfven",
            "tfkeþ",
            "tfvfe",
            "tfveo",
            "tfveþ",
            "tfhee",
            "tfkee",
            "tfvee",
            "sbg2en",
            "sbm2en",
            "sbg2fn",
            "sfg3eþ",
            "sfg3en",
            "sfg3fþ",
            "sfg3fn",
            "svg3eþ",
            "sfg1en",
            "sfg1eþ",
            "sfm3eþ",
            "svg3en",
            "sfg2en",
            "sfm3en",
            "svg3fþ",
            "sfg1fn",
            "sfm3fþ",
            "sfg1fþ",
            "svg1eþ",
            "svg3fn",
            "sfg2fn",
            "sfm3fn",
            "svg1en",
            "sfg2eþ",
            "svm3eþ",
            "sfm1eþ",
            "svg2eþ",
            "svg2en",
            "svm3en",
            "svg1fþ",
            "sfm1en",
            "sfm1fþ",
            "svm3fþ",
            "sfg2fþ",
            "svg2fþ",
            "svg1fn",
            "sfm2en",
            "svm3fn",
            "sfm1fn",
            "svm2fn",
            "svm2fþ",
            "sng",
            "ssg",
            "snm",
            "ssm",
            "slg",
            //"sng--þ",
            "slm",
            "svm1eþ",
            "svg2fn",
            "sfm2fn",
            "sfm2eþ",
            "svm1fþ",
            "svm2en",
            "svm1en",
            "sfm2fþ",
            "svm2eþ",
            "svm1fn",
            "sþghen",
            "sþgken",
            "sþgven",
            "sþghfn",
            "sþgvfn",
            "sþgkfn",
            "sþmhen",
            "sþgheo",
            "sþmven",
            "sþgkeo",
            "sþmken",
            "sþgvfo",
            "sþgveo",
            "sþgkfo",
            "sþghfo",
            "sþmhfn",
            "sþmvfn",
            "sþmkfn",
            "aa",
            //"x",
            "e"
    };
}

