CORPUS_DIR=../../../..//corpora/ot10/new
MODELS_DIR=../../models/icestagger
cp $CORPUS_DIR/otb.txt corpora/otb.train.plain

time java -Xmx6G -ea -classpath ../../dist/IceNLPCore.jar is.iclt.icenlp.runner.RunIceStagger -trainfile corpora/otb.train.plain -modelfile $MODELS_DIR/otb.bin -positers 12 -lang is -train
