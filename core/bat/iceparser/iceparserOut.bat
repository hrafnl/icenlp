@echo off
java -classpath ..\..\dist\IceNLPCore.jar is.iclt.icenlp.runner.RunIceParserOut -i %1 -o %2 -p %3 %4 %5 %6
