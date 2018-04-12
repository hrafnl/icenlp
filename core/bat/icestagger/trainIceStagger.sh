# A training corpus is needed for training IceStagger
# Each line consists of a <word tag> pair (white space between the two)
# An empty line is between sentences

# Let us assume you have downloaded the Icelandic Frequency Dictionary (Orðtíðnibók) from www.malfong.is as training data
# You will then probably have a file called otb.txt
# IceStagger assumes that files ending with .txt are files that need to be tokenized before they are used for training
# The otb.txt file is already tokenzied, and therefore you need to change the ending to something else, e.g. otb.plain

# You can then train IceStagger on otb.plain (resulting in the model otb.bin) with the following:

CORPUS_DIR=./corpora
MODELS_DIR=./models

./icestagger.sh -trainfile $CORPUS_DIR/otb.plain -modelfile $MODELS_DIR/otb.bin -positers 12 -lang is -train
