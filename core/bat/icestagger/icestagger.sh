RUNNER=is.iclt.icenlp.runner.RunIceStagger
DIST_DIR=../../dist

java -Xmx6G -ea -classpath $DIST_DIR/IceNLPCore.jar $RUNNER $*
