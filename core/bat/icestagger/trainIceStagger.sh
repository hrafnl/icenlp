# A training corpus is needed for training IceStagger
# Each line consists of a <word tag> pair (white space between the two)
# An empty line is between sentences

# Let us assume you have downloaded the Icelandic Frequency Dictionary (Orðtíðnibók) from www.malfong.is as training data
# You will then probably have a file called otb.txt
# IceStagger assumes that files ending with .txt are files that need to be tokenized before they are used for training
# The otb.txt file is already tokenzied, and therefore you need to change the ending to something else, e.g. otb.plain

# You can then train IceStagger on otb.plain (resulting in the model otb.bin) with the following:

CORPUS_DIR=./corpora
DIST_DIR=../../dist
MODELS_DIR=./models
RUNNER=is.iclt.icenlp.runner.RunIceStagger

time java -Xmx6G -ea -classpath $DIST_DIR/IceNLPCore.jar $RUNNER -trainfile $CORPUS_DIR/otb.plain -modelfile $MODELS_DIR/otb.bin -icemorphy 1 -positers 10 -lang is -train
