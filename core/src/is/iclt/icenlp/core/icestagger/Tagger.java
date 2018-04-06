package is.iclt.icenlp.core.icestagger;
import java.util.*;
import java.io.Serializable;

public class Tagger implements Serializable {
    static final long serialVersionUID = 2066690554149210181L;

    // During training, words with fewer occurences than this in the training
    // set are considered unknown. Empirically, 3 seems to be a good value.
    protected final static int countLimit = 3;
    // If true, the countLimit value is used to create "unknown" words.
    protected boolean trainingMode;
    // A list of POS tags each word form can take.
    protected Lexicon posLexicon;
    // Lists of possible POS tags for each token type
    protected int[][] tokTypeTags;
    // Perceptrons for the two tasks: POS and NER.
    protected Perceptron posPerceptron;
    protected Perceptron nePerceptron;
    // Reading the POS and NER tag formats.
    protected TaggedData taggedData;
    // Beam sizes for the tagging algorithms.
    protected int posBeamSize;
    protected int neBeamSize;
    // Word representations for the two tasks.
    protected ArrayList<Dictionary> posDictionaries;
    protected ArrayList<Embedding> posEmbeddings;
    protected ArrayList<Dictionary> neDictionaries;
    protected ArrayList<Embedding> neEmbeddings;
    // Array of POS tags that unknown words can have.
    protected int[] openTags;
    // Whether or not this tagger performs the given task.
    protected boolean hasPos, hasNE;
    // Should known words be extended?
    protected boolean extendLexicon = true;

    // Maximum number of training iterations.
    protected int maxPosIters = 16;
    protected int maxNEIters = 16;
    // Maximum number of features per decision, don't be cheap here.
    protected static final int maxFeats = 0x80;
    // Interval (in tokens) at which to accumulate weight vector.
    protected static final int accumulateLimit = 0x1000;

    protected HashSet<String> allowedPrefixes = null;
    protected HashSet<String> allowedSuffixes = null;

    public void setMaxPosIters(int n) {
        maxPosIters = n;
    }

    public void setMaxNEIters(int n) {
        maxNEIters = n;
    }

    public void setExtendLexicon(boolean x) {
        extendLexicon = x;
    }

    /**
     * Creates a new tagger.
     *
     * train() will initialize the tagger further.
     */
    public Tagger(TaggedData taggedData, int posBeamSize, int neBeamSize) {
        trainingMode = false;
        hasPos = false;
        hasNE = false;
        posPerceptron = null;
        nePerceptron = null;
        this.taggedData = taggedData;
        this.posBeamSize = posBeamSize;
        this.neBeamSize = neBeamSize;
        posDictionaries = new ArrayList<Dictionary>();
        posEmbeddings = new ArrayList<Embedding>();
        neDictionaries = new ArrayList<Dictionary>();
        neEmbeddings = new ArrayList<Embedding>();
        posLexicon = new Lexicon();
        computeOpenTags();
    }

    public Lexicon getPosLexicon() {
        return posLexicon;
    }

    public void setPosDictionaries(ArrayList<Dictionary> dictionaries) {
        posDictionaries = dictionaries;
    }

    public void setNEDictionaries(ArrayList<Dictionary> dictionaries) {
        neDictionaries = dictionaries;
    }

    public void setPosEmbeddings(ArrayList<Embedding> embeddings) {
        posEmbeddings = embeddings;
    }

    public void setNEEmbeddings(ArrayList<Embedding> embeddings) {
        neEmbeddings = embeddings;
    }

    /**
     * Returns the TaggedData instance used by this tagger.
     *
     * @return  TaggedData instance
     */
    public TaggedData getTaggedData() {
        return taggedData;
    }

    /**
     * Annotates a token with its lemma form, given its POS tag.
     *
     * The default is to not use lemmas at all, but inflectional languages
     * should override this method.
     */
    protected String getLemma(TaggedToken token) {
        return null;
    }

    /**
     * Constructs POS tag lexicon, and generalized token lexicon.
     */
    public void buildLexicons(TaggedToken[][] sents) {
        boolean[][] hasTag =
            new boolean[Token.TOK_TYPES][taggedData.getPosTagSet().size()];
        for(TaggedToken[] sent : sents) {
            for(TaggedToken tok : sent) {
                if(tok.posTag >= 0) {
                    hasTag[tok.token.type][tok.posTag] = true;
                    posLexicon.addEntry(tok.token.value, tok.lf, tok.posTag,1);
                }
            }
 
        }
        // Create an array tokTypeTags[type] for each token type, which
        // contains all the POS tags that have occured with this token type
        // in the training corpus
        tokTypeTags = new int[Token.TOK_TYPES][];
        for(int tokType=0; tokType<Token.TOK_TYPES; tokType++) {
            int nTags = 0;
            for(int tag : openTags) {
                if(hasTag[tokType][tag]) nTags++;
            }
            if(nTags == 0) {
                // If this tag type is not in the corpus, allow all open POS
                // tags
                tokTypeTags[tokType] = openTags;
            } else {
                tokTypeTags[tokType] = new int[nTags];
                int j=0;
                for(int tag : openTags) {
                    if(hasTag[tokType][tag]) tokTypeTags[tokType][j++] = tag;
                }
                assert(j == nTags);
                for(int k=0; k<j-1; k++)
                    assert(tokTypeTags[tokType][k]<tokTypeTags[tokType][k+1]);
            }
        }
        /*
        for(int tokType=0; tokType<Token.TOK_TYPES; tokType++) {
            System.out.print(tokType + ":");
            for(int tag : tokTypeTags[tokType]) {
                System.out.print(" " + tag);
            }
            System.out.println("");
        }
        */
    }

    /**
     * Initializes the openTags list to contain all tags.
     * TODO: consider using a tag-frequency heuristic to do this in a better
     * way.
     */
    protected void computeOpenTags() {
        openTags = new int[taggedData.getPosTagSet().size()];
        for(int i=0; i<openTags.length; i++) openTags[i] = i;
    }

    /**
     * Trains the tagger.
     *
     * @param trainSents    sentences used as training data
     * @param devSents      sentences used to check the accuracy curve
     */
    public void train(
    TaggedToken[][] trainSents, TaggedToken[][] devSents) {
        hasPos = false;
        hasNE = false;
        for(TaggedToken[] sent : trainSents) {
            for(TaggedToken tok : sent) {
                if(tok.posTag >= 0) hasPos = true;
                if(tok.neTag >= 0) hasNE = true;
            }
        }
        trainingMode = true;
        if(hasPos) {
            posPerceptron = new Perceptron();
            trainPos(trainSents, devSents);
        }
        if(hasNE) {
            nePerceptron = new Perceptron();
            trainNE(trainSents, devSents);
        }
        trainingMode = false;
    }

    protected void trainPos(
    TaggedToken[][] trainSents, TaggedToken[][] devSents) {
        posPerceptron.startTraining();

        // Create a list of integers 0 to trainSents.length-1 (inclusive),
        // which will be the order than sentences are processed during a
        // training iteration. This may be permuted at each iteration.
        ArrayList<Integer> trainOrder =
            new ArrayList<Integer>(trainSents.length);
        for(int i=0; i<trainSents.length; i++) trainOrder.add(new Integer(i));

        // TODO: cache training set features+values

        // The peak accuracy on the development set.
        int bestIter = 0;
        double bestAccuracy = 0.0;

        for(int iter=0; iter<maxPosIters; iter++) {
            // Randomly reorder the sequence of training sentences.
            // Collections.shuffle(trainOrder);
            System.err.println("Starting POS iteration " + iter);
            // Number of tokens since last weight accumulation.
            int tokenCount = 0;
            Evaluation trainEvaluation = new Evaluation();
            for(int sentIdx : trainOrder) {
                TaggedToken[] trainSent = trainSents[sentIdx];
                // If the sentence is not POS tagged, skip it
                if(trainSent.length == 0 || trainSent[0].posTag < 0)
                    continue;
                TaggedToken[] taggedSent = new TaggedToken[trainSent.length];
                for(int i=0; i<trainSent.length; i++)
                    taggedSent[i] = new TaggedToken(trainSent[i]);
                tagPos(taggedSent, false);
                int oldPosCorrect = trainEvaluation.posCorrect;
                trainEvaluation.evaluate(taggedSent, trainSent);
                // Only perform weight updates if the sentence was incorrectly
                // tagged
                if(trainEvaluation.posCorrect !=
                   oldPosCorrect + trainSent.length)
                {
                    posUpdateWeights(taggedSent, trainSent);
                }
                // Check if it is time to accumulate perceptron weights.
                tokenCount += trainSent.length;
                if(tokenCount > accumulateLimit) {
                    posPerceptron.accumulateWeights();
                    tokenCount = 0;
                }
            }
            System.err.println("Training set accuracy: " +
                trainEvaluation.posAccuracy());

            if(devSents == null) {
                if(iter == maxPosIters-1) posPerceptron.makeBestWeight();
                continue;
            }

            Evaluation devEvaluation = new Evaluation();
            for(int sentIdx=0; sentIdx<devSents.length; sentIdx++) {
                TaggedToken[] devSent = devSents[sentIdx];
                TaggedToken[] taggedSent = new TaggedToken[devSent.length];
                for(int i=0; i<devSent.length; i++) {
                    taggedSent[i] = new TaggedToken(devSent[i]);
                }
                trainingMode = false;
                tagPos(taggedSent, true);
                trainingMode = true;
                devEvaluation.evaluate(taggedSent, devSent);
            }
            double devAccuracy = devEvaluation.posAccuracy();
            System.err.println("Development set accuracy: " + devAccuracy);
            if((devAccuracy-bestAccuracy)/devAccuracy > 0.00025) {
                bestAccuracy = devAccuracy;
                bestIter = iter;
                posPerceptron.makeBestWeight();
            } else if(devAccuracy > bestAccuracy) {
                posPerceptron.makeBestWeight();
            } else if(bestIter <= iter-3) {
                System.err.println("Accuracy not increasing, we are done.");
                break;
            }

        }
        posPerceptron.endTraining();
    }
    
    /**
     * Update the weights according to the target and model taggings.
     */
    private void posUpdateWeights(
    TaggedToken[] taggedSent, TaggedToken[] trainSent) {
        assert(taggedSent.length == trainSent.length);
        History[] taggedHistory = sentToHistory(taggedSent);
        History[] trainHistory = sentToHistory(trainSent);
        int[] feats = new int[maxFeats];
        double[] values = new double[maxFeats];
        for(int i=0; i<taggedSent.length; i++) {
            int nFeats;
            History tagged = taggedHistory[i];
            History train = trainHistory[i];
            // Compute feature values for negative example.
            nFeats = getPosFeats(
                taggedSent, i, feats, values, 0, tagged.posTag,
                tagged.neTag, tagged.neTypeTag, false, null, true);
            nFeats = getPosFeats(
                taggedSent, i, feats, values, nFeats, tagged.posTag,
                tagged.neTag, tagged.neTypeTag, true, tagged.last, true);
            posPerceptron.updateWeights(feats, values, nFeats, false);

            // TODO: consider caching this
            // Compute feature values for positive example.
            nFeats = getPosFeats(
                trainSent, i, feats, values, 0, train.posTag,
                train.neTag, train.neTypeTag, false, null, true);
            nFeats = getPosFeats(
                trainSent, i, feats, values, nFeats, train.posTag,
                train.neTag, train.neTypeTag, true, train.last, true);
            posPerceptron.updateWeights(feats, values, nFeats, true);
        }
    }

    /**
     * Creates a linked History vector from a sentence.
     *
     * @param sent      input sentence
     * @return          History array representing the same contents
     */
    public History[] sentToHistory(TaggedToken[] sent) {
        History[] history = new History[sent.length];
        for(int i=0; i<sent.length; i++) {
            TaggedToken tok = sent[i];
            history[i] = new History(
                tok.token.value, tok.textLower,
                tok.lf, tok.posTag, tok.neTag, tok.neTypeTag, 0.0,
                (i == 0)? null : history[i-1]);
        }
        return history;
    }

    /**
     * Tag a sentence with POS and NE information.
     *
     * @param sentence      input sentence, will not be modified
     * @param average       if true, use the averaged perceptron
     * @param preserve      if true, do not overwrite tags
     * @return              tagged version of input sentence
     */
    public TaggedToken[] tagSentence(
    TaggedToken[] sentence, boolean average, boolean preserve) {
        TaggedToken[] taggedSentence = new TaggedToken[sentence.length];
        for(int i=0; i<sentence.length; i++) {
            taggedSentence[i] = new TaggedToken(sentence[i]);
            taggedSentence[i].posTag = -1;
        }
        if(hasPos) tagPos(taggedSentence, average);
        for(int i=0; i<sentence.length; i++) {
            if(preserve && sentence[i].posTag >= 0)
                taggedSentence[i].posTag = sentence[i].posTag;
        }
        if(hasNE) tagNE(taggedSentence, average);
        for(int i=0; i<sentence.length; i++) {
            if(preserve && sentence[i].neTag >= 0) {
                taggedSentence[i].neTag = sentence[i].neTag;
                taggedSentence[i].neTypeTag = sentence[i].neTypeTag;
            }
            if((!preserve) || taggedSentence[i].lf == null) {
                taggedSentence[i].lf = getLemma(taggedSentence[i]);
            }
        }
        return taggedSentence;
    }

    protected void tagPos(TaggedToken[] sentence, boolean average) {
        History[] beam = new History[posBeamSize];
        History[] nextBeam = new History[posBeamSize];
        int[] feats = new int[maxFeats];
        double[] values = new double[maxFeats];
        beam[0] = null;
        int beamUsed = 1, nextBeamUsed;
        for(int i=0; i<sentence.length; i++) {
            TaggedToken ttok = sentence[i];
            String text = ttok.token.value;
            String textLower = ttok.textLower;
            nextBeamUsed = 0;
            int[] possibleTags = possiblePosTags(sentence, i);
            int neTag = sentence[i].neTag;
            int neTypeTag = sentence[i].neTypeTag;
            assert possibleTags.length > 0;
            /*
            if(!trainingMode) {
            System.out.print(textLower + "   ");
            for(int l=0; l<possibleTags.length; l++)
                System.out.print(" " + possibleTags[l]);
            System.out.println("");
            }
            */
            // First, go through all possible tags.
            for(int posTag : possibleTags) {
                // Get history-independent features.
                int nLocalFeats = getPosFeats(
                    sentence, i, feats, values, 0, posTag, neTag, neTypeTag,
                    false, null, false);
                // Then go through the available histories.
                for(int j=0; j<beamUsed; j++) {
                    History history = beam[j];
                    // Get history-dependent features.
                    int nFeats = getPosFeats(
                        sentence, i, feats, values, nLocalFeats, posTag,
                        neTag, neTypeTag, true, history, false);
                    // Get the score of all features for the local decision.
                    double score = posPerceptron.score(
                        feats, values, nFeats, average);
                    // Compute the local + history score.
                    if(history != null) score += history.score;
                    /*
                    if(!trainingMode) {
                        for(int q=0; q<nFeats; q++)
                            System.err.print(feats[q]+"="+values[q]+" ");
                        System.err.println(score);
                    }
                    */
                    // If the beam is empty, always add this decision.
                    if(nextBeamUsed == 0) {
                        nextBeam[0] = new History(
                            text, textLower,
                            ttok.lf, posTag, neTag, neTypeTag, score,
                            history);
                        nextBeamUsed = 1;
                    } else {
                        // Otherwise, only add it if the score is higher than
                        // the lowest score currently in the beam.
                        if(score > nextBeam[nextBeamUsed-1].score) {
                            int l = nextBeamUsed-1;
                            // If the beam has space left, make an extra copy
                            // of the smallest element. In the following step
                            // the smallest element will be deleted.
                            if(nextBeamUsed < posBeamSize) {
                                nextBeam[l+1] = nextBeam[l];
                                nextBeamUsed++;
                            }
                            l--;
                            // Move histories with lower scores than the
                            // current one step to the right, until we find
                            // the right place to insert the current history.
                            while(l >= 0 && score>nextBeam[l].score) {
                                nextBeam[l+1] = nextBeam[l];
                                l--;
                            }
                            // Create and insert the new history.
                            nextBeam[l+1] = new History(
                                text, textLower, ttok.lf,
                                posTag, neTag, neTypeTag, score, history);
                        } else if(nextBeamUsed < posBeamSize) {
                            nextBeam[nextBeamUsed++] = new History(
                                text, textLower, ttok.lf,
                                posTag, neTag, neTypeTag, score, history);
                        }
                    }
                }
            }
            System.arraycopy(nextBeam, 0, beam, 0, nextBeamUsed);
            beamUsed = nextBeamUsed;
        }
        /*
        if(!trainingMode) {
            for(int i=0; i<beamUsed; i++) {
                System.out.println("Score of "+i+": "+beam[i].score);
            }
        }
        */
        // Copy the annotation of the best history to the sentence.
        History history = beam[0];
        for(int i=0; i<sentence.length; i++) {
            sentence[sentence.length-(i+1)].posTag = history.posTag;
            history = history.last;
        }
        assert(history == null);
    }

    // May be implemented by a subclass
    protected void guessTags(String wordForm, boolean firstWord) {
        return;
    }

    /** Returns an array of possible POS tags for a given token.
     *
     * @param tokens        we will look at tokens[idx]
     * @param idx           see above
     * @return              array of possible POS tags
     */
    protected int[] possiblePosTags(TaggedToken[] sent, int idx) {
        String textLower = sent[idx].textLower;
        if (!trainingMode)
            guessTags(sent[idx].token.value, (idx==0));

        Lexicon.Entry[] entries = posLexicon.getEntries(textLower);
        if(entries == null) {
            return tokTypeTags[sent[idx].token.type];
        }
        int[] tags = new int[entries.length];
        int nTags = 0;
        int nSeen = 0;
        int lastTag = -1;
        for(Lexicon.Entry entry : entries)
            nSeen += entry.n;
        // Go through the list of entries (sorted by tag ID), and put its
        // unique elements into the tag array.
        for(Lexicon.Entry entry : entries) {
            // If extendLexicon is false, and this is a known word (as
            // estimated by its non-zero count), then skip any entry that does
            // not occur in the training data.
            if(nSeen > 0 && !extendLexicon && entry.n == 0) continue;
            // Add any tag that has not already been added.
            if(entry.tag != lastTag) {
                tags[nTags++] = entry.tag;
                lastTag = entry.tag;
            }
        }
        for(int t=0; t<nTags-1; t++)
            assert(tags[t] < tags[t+1]);
        // If the word form is frequent enough (or we are not in training
        // mode), return the lexicon tags directly.
        if(!trainingMode || nSeen >= countLimit) {
            if(nTags != tags.length) return Arrays.copyOf(tags, nTags);
            else return tags;
        }
        // Otherwise, merge the lexicon tags with the array of open tags
        int[] possibleTags = tokTypeTags[sent[idx].token.type];
        int[] lexiconTags = tags;
        int i=0, j=0, k=0;
        tags = new int[nTags + possibleTags.length];
        for(;j<possibleTags.length && k<nTags;i++) {
            if(possibleTags[j] < lexiconTags[k]) {
                tags[i] = possibleTags[j++];
            } else if(possibleTags[j] == lexiconTags[k]) {
                tags[i] = possibleTags[j++]; k++;
            } else {
                tags[i] = lexiconTags[k++];
            }
        }
        if(j<possibleTags.length) {
            for(;j<possibleTags.length;j++) tags[i++] = possibleTags[j];
        } else {
            for(;k<nTags;k++) tags[i++] = lexiconTags[k];
        }
        nTags = i;
        for(int t=0; t<nTags-1; t++)
            assert(tags[t] < tags[t+1]);
        if(nTags != tags.length) return Arrays.copyOf(tags, nTags);
        else return tags;
    }

    protected void trainNE(
    TaggedToken[][] trainSents, TaggedToken[][] devSents) {
        nePerceptron.startTraining();

        // Create a list of integers 0 to trainSents.length-1 (inclusive),
        // which will be the order than sentences are processed during a
        // training iteration. This may be permuted at each iteration.
        ArrayList<Integer> trainOrder =
            new ArrayList<Integer>(trainSents.length);
        for(int i=0; i<trainSents.length; i++) trainOrder.add(new Integer(i));

        // The peak accuracy on the development set.
        int bestIter = 0;
        double bestAccuracy = 0.0;

        for(int iter=0; iter<maxNEIters; iter++) {
            // Randomly reorder the sequence of training sentences.
            // Collections.shuffle(trainOrder);
            System.err.println("Starting NE iteration " + iter);
            // Number of tokens since last weight accumulation.
            int tokenCount = 0;
            Evaluation trainEvaluation = new Evaluation();
            for(int sentIdx : trainOrder) {
                TaggedToken[] trainSent = trainSents[sentIdx];
                // If this sentence does not contain NE tags, skip it.
                if(trainSent.length == 0 || trainSent[0].neTag < 0)
                    continue;
                TaggedToken[] taggedSent = new TaggedToken[trainSent.length];
                for(int i=0; i<trainSent.length; i++)
                    taggedSent[i] = new TaggedToken(trainSent[i]);
                tagNE(taggedSent, false);
                trainEvaluation.evaluate(taggedSent, trainSent);
                // Only perform weight updates if the sentence was incorrectly
                // tagged
                if(!trainEvaluation.neEquals(taggedSent, trainSent)) {
                    neUpdateWeights(taggedSent, trainSent);
                }
                // Check if it is time to accumulate perceptron weights.
                tokenCount += trainSent.length;
                if(tokenCount > accumulateLimit) {
                    nePerceptron.accumulateWeights();
                    tokenCount = 0;
                }
            }
            System.err.println("Training set F-score: " +
                trainEvaluation.neFscore());

            if(devSents == null) {
                if(iter == maxNEIters-1) nePerceptron.makeBestWeight();
                continue;
            }

            Evaluation devEvaluation = new Evaluation();
            for(int sentIdx=0; sentIdx<devSents.length; sentIdx++) {
                TaggedToken[] devSent = devSents[sentIdx];
                TaggedToken[] taggedSent = new TaggedToken[devSent.length];
                for(int i=0; i<devSent.length; i++) {
                    taggedSent[i] = new TaggedToken(devSent[i]);
                }
                trainingMode = false;
                tagNE(taggedSent, true);
                trainingMode = true;
                devEvaluation.evaluate(taggedSent, devSent);
            }
            double devAccuracy = devEvaluation.neFscore();
            System.err.println("Development set F-score: " + devAccuracy);
            if((devAccuracy-bestAccuracy)/devAccuracy > 0.00025) {
                bestAccuracy = devAccuracy;
                bestIter = iter;
                nePerceptron.makeBestWeight();
            } else if(bestIter <= iter-3) {
                System.err.println("F-score not increasing, we are done.");
                break;
            }

        }
        nePerceptron.endTraining();
    }

    /**
     * Update the weights according to the target and model taggings.
     *
     * TODO: this could be merged with posUpdateWeights
     */
    private void neUpdateWeights(
    TaggedToken[] taggedSent, TaggedToken[] trainSent) {
        assert(taggedSent.length == trainSent.length);
        History[] taggedHistory = sentToHistory(taggedSent);
        History[] trainHistory = sentToHistory(trainSent);
        int[] feats = new int[maxFeats];
        double[] values = new double[maxFeats];
        for(int i=0; i<taggedSent.length; i++) {
            int nFeats;
            History tagged = taggedHistory[i];
            History train = trainHistory[i];
            // Compute feature values for negative example.
            nFeats = getNEFeats(
                taggedSent, i, feats, values, 0, tagged.posTag,
                tagged.neTag, tagged.neTypeTag, tagged.last, true);
            nePerceptron.updateWeights(feats, values, nFeats, false);

            // TODO: consider caching this
            // Compute feature values for positive example.
            nFeats = getNEFeats(
                trainSent, i, feats, values, 0, train.posTag,
                train.neTag, train.neTypeTag, train.last, true);
            nePerceptron.updateWeights(feats, values, nFeats, true);
        }
    }

     void tagNE(TaggedToken[] sentence, boolean average) {
        History[] beam = new History[neBeamSize];
        History[] nextBeam = new History[neBeamSize];
        int[] feats = new int[maxFeats];
        double[] values = new double[maxFeats];
        beam[0] = null;
        int beamUsed = 1, nextBeamUsed;
        for(int i=0; i<sentence.length; i++) {
            TaggedToken ttok = sentence[i];
            String text = ttok.token.value;
            String textLower = ttok.textLower;
            nextBeamUsed = 0;
            int posTag = sentence[i].posTag;
            for(int neTag=0; neTag<TaggedData.NE_TAGS; neTag++) {
                // Can not start with an I tag.
                if(i == 0 && neTag == TaggedData.NE_I) continue;
                // Go through the available histories.
                for(int j=0; j<beamUsed; j++) {
                    History history = beam[j];
                    // O -> I transitions not allowed
                    if((history == null ||
                        history.neTag == TaggedData.NE_O) &&
                       neTag == TaggedData.NE_I)
                        continue;
                    int minType = -1, maxType = -1;
                    if(neTag == TaggedData.NE_I) {
                        minType = history.neTypeTag;
                        maxType = history.neTypeTag;
                    } else if(neTag == TaggedData.NE_B) {
                        minType = 0;
                        maxType = taggedData.getNETypeTagSet().size()-1;
                    }
                    for(int neTypeTag=minType; neTypeTag<=maxType; neTypeTag++)
                    {
                        int nFeats = getNEFeats(
                            sentence, i, feats, values, 0,
                            posTag, neTag, neTypeTag, history, false);
                        // Get the score of all features for the local
                        // decision.
                        double score = nePerceptron.score(
                            feats, values, nFeats, average);
                        // Compute the local + history score.
                        if(history != null) score += history.score;
                        // If the beam is empty, always add this decision.
                        if(nextBeamUsed == 0) {
                            nextBeam[0] = new History(
                                text, textLower,
                                ttok.lf, posTag, neTag, neTypeTag, score,
                                history);
                            nextBeamUsed = 1;
                        } else {
                            // Otherwise, only add it if the score is higher
                            // than the lowest score currently in the beam.
                            if(score > nextBeam[nextBeamUsed-1].score) {
                                int l = nextBeamUsed-1;
                                // If the beam has space left, make an extra
                                // copy of the smallest element. In the
                                // following step the smallest element will
                                // be deleted.
                                if(nextBeamUsed < neBeamSize) {
                                    nextBeam[l+1] = nextBeam[l];
                                    nextBeamUsed++;
                                }
                                l--;
                                // Move histories with lower scores than the
                                // current one step to the right, until we find
                                // the right place to insert the current
                                // history.
                                while(l >= 0 && score>nextBeam[l].score) {
                                    nextBeam[l+1] = nextBeam[l];
                                    l--;
                                }
                                // Create and insert the new history.
                                nextBeam[l+1] = new History(
                                    text, textLower, ttok.lf,
                                    posTag, neTag, neTypeTag, score, history);
                            } else if(nextBeamUsed < neBeamSize) {
                                nextBeam[nextBeamUsed++] = new History(
                                    text, textLower, ttok.lf,
                                    posTag, neTag, neTypeTag, score, history);
                            }
                        }
                    }
                }
            }
            System.arraycopy(nextBeam, 0, beam, 0, nextBeamUsed);
            beamUsed = nextBeamUsed;
        }
        /*
        if(!trainingMode) {
            for(int i=0; i<beamUsed; i++) {
                System.out.println("Score of "+i+": "+beam[i].score);
            }
        }
        */
        // Copy the annotation of the best history to the sentence.
        History history = beam[0];
        for(int i=0; i<sentence.length; i++) {
            sentence[sentence.length-(i+1)].neTag = history.neTag;
            sentence[sentence.length-(i+1)].neTypeTag = history.neTypeTag;
            history = history.last;
        }
        assert(history == null);
    }

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

    /**
     * Computes feature values given a certain NE tag and context.
     */
    protected int getNEFeats(
    TaggedToken[] sentence, int idx, int[] feats, double[] values, int nFeats,
    int posTag, int neTag, int neTypeTag, History last, boolean extend)
    {
        char[] head = new char[8];
        int f;
        char isInitial = (idx == 0)? (char)1 : (char)0;
        char isFinal = (idx == sentence.length-1)? (char)1 : (char)0;
        TaggedToken ttok = sentence[idx];
        char tokType = (char)ttok.token.type;
        char capitalization = ttok.token.isCapitalized()? (char)1 : (char)0;
        int posTag1b = (idx == 0)? 0xffff : sentence[idx-1].posTag;
        int posTag2b = (idx < 2)? 0xffff : sentence[idx-2].posTag;
        int posTag1a =
            (idx == sentence.length-1)? 0xffff : sentence[idx+1].posTag;
        String text = ttok.token.value;
        String textLower = ttok.textLower;
        String lastLower = (idx == 0)? "" : sentence[idx-1].textLower;
        String lastLower2 = (idx < 2)? "" : sentence[idx-2].textLower;
        String nextLower =
            (idx == sentence.length-1)? "" : sentence[idx+1].textLower;
        String nextLower2 =
            (idx >= sentence.length-2)? "" : sentence[idx+2].textLower;

        // tag + type + POS
        head[0] = 0x00;
        head[1] = (char)neTag;
        head[2] = (char)neTypeTag;
        head[3] = (char)posTag;
        f = nePerceptron.getFeatureID(
            new String(head,0,4), extend);
        if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }
        
        // tag + type + (previous, current) POS
        head[0] = 0x01;
        head[1] = (char)neTag;
        head[2] = (char)neTypeTag;
        head[3] = (char)posTag;
        head[4] = (char)posTag1b;
        f = nePerceptron.getFeatureID(
            new String(head,0,5), extend);
        if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }
        
        // tag + type + (current, next) POS
        head[0] = 0x02;
        head[1] = (char)neTag;
        head[2] = (char)neTypeTag;
        head[3] = (char)posTag;
        head[4] = (char)posTag1a;
        f = nePerceptron.getFeatureID(
            new String(head,0,5), extend);
        if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }
        
        // tag + type + textLower
        head[0] = 0x03;
        head[1] = (char)neTag;
        head[2] = (char)neTypeTag;
        f = nePerceptron.getFeatureID(
            new String(head,0,3) + textLower, extend);
        if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }
        
        // tag + type + textLower + nextLower
        head[0] = 0x04;
        head[1] = (char)neTag;
        head[2] = (char)neTypeTag;
        f = nePerceptron.getFeatureID(
            new String(head,0,3) + textLower + "\n" + nextLower, extend);
        if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }
        
        // tag + type + lastLower + textLower
        head[0] = 0x04;
        head[1] = (char)neTag;
        head[2] = (char)neTypeTag;
        f = nePerceptron.getFeatureID(
            new String(head,0,3) + lastLower + "\n" + textLower, extend);
        if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }

        // dictionaries
        head[0] = 0x08;
        head[1] = (char)neTag;
        head[2] = (char)neTypeTag;
        for(int i=0; i<neDictionaries.size(); i++) {
            Dictionary dict = neDictionaries.get(i);
            String value = dict.map.get(textLower);
            String lastValue = (i == 0)? "" : dict.map.get(lastLower);
            String nextValue =
                (i == sentence.length-1)? "" :
                dict.map.get(nextLower);
            head[3] = (char)i;
            String[] combinations = {
                value,
                (value == null || lastValue == null)? null :
                    lastValue + "\n" + value,
                (value == null || nextValue == null)? null :
                    value + "\n" + nextValue,
                nextValue
            };
            for(int j=0; j<combinations.length; j++) {
                if(combinations[j] == null) continue;
                head[4] = (char)j;
                f = nePerceptron.getFeatureID(
                    new String(head,0,5) + combinations[j], extend);
                if(f >= 0) {
                    feats[nFeats] = f; values[nFeats] = 1.0; nFeats++;
                }
            }
        }

        // embeddings
        head[0] = 0x09;
        head[1] = (char)neTag;
        head[2] = (char)neTypeTag;
        for(int i=0; i<neEmbeddings.size(); i++) {
            float[] value = neEmbeddings.get(i).map.get(textLower);
            if(value == null) continue;
            head[3] = (char)i;
            for(int j=0; j<value.length; j++) {
                head[4] = (char)j;
                f = nePerceptron.getFeatureID(
                    new String(head,0,5), extend);
                if(f >= 0) {
                    feats[nFeats] = f; values[nFeats] = value[j]; nFeats++;
                }
            }
        }
        
        // tag + type + token type
        head[0] = 0x0a;
        head[1] = (char)neTag;
        head[2] = (char)neTypeTag;
        head[3] = tokType;
        f = nePerceptron.getFeatureID(
            new String(head,0,4), extend);
        if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }

        char neTag1b = 0xffff;
        char neTag2b = 0xffff;
        if(last != null) {
            neTag1b = (char)last.neTag;
            if(last.last != null) neTag2b = (char)last.last.neTag;
        }

        // (previous, current) tag + type
        head[0] = 0x80;
        head[1] = (char)neTag;
        head[2] = (char)neTag1b;
        head[3] = (char)neTypeTag;
        f = nePerceptron.getFeatureID(
            new String(head,0,4), extend);
        if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }

        // (previous, current) tag + type
        head[0] = 0x81;
        head[1] = (char)neTag;
        head[2] = (char)neTag1b;
        head[3] = (char)neTag2b;
        head[4] = (char)neTypeTag;
        f = nePerceptron.getFeatureID(
            new String(head,0,5), extend);
        if(f >= 0) { feats[nFeats] = f; values[nFeats] = 1.0; nFeats++; }
        
        return nFeats;
    }

    protected static class History {
        public final String text;
        public final String textLower;
        public final String lf;
        public final int posTag;
        public final int neTag;
        public final int neTypeTag;
        public final double score;
        public final History last;

        public History(
        String text, String textLower, String lf, int posTag, int neTag,
        int neTypeTag, double score, History last)
        {
            this.text = text;
            this.textLower = textLower;
            this.lf = lf;
            this.posTag = posTag;
            this.neTag = neTag;
            this.neTypeTag = neTypeTag;
            this.score = score;
            this.last = last;
        }
    }
}

