# This shell assumes that the user has a copy of the Icelandic Frequency Dictionary (otb.plain)
# and a copy of BIN (The Database of Icelandic Inflections)

CORPUS_DIR=./corpora
MODELS_DIR=./models

./icestagger.sh -trainfile $CORPUS_DIR/otb.plain -lexicon $CORPUS_DIR/bin.lexicon -modelfile $MODELS_DIR/otbBIN.bin -positers 12 -lang is -train
