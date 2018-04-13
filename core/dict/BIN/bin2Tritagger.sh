# Creates a dictionary from BIN data for TriTagger: one line for each word form with the associated tags

MODEL_DIR=../../ngrams/models

echo "Create a dictionary from BIN for TriTagger..."
perl buildDictFromBin.pl -i bin2.sort.out -o bin.freq.dict -f
echo "Combine two dictionaries, otb and BIN for TrTagger ..."
perl combineFreqDicts.pl $MODEL_DIR/otb.lex bin.freq.dict $MODEL_DIR/otbBin.lex -nosuffix
echo "Copy ngram info ..."
cp $MODEL_DIR/otb.ngram $MODEL_DIR/otbBin.ngram
cp $MODEL_DIR/otb.lambda $MODEL_DIR/otbBin.lambda
