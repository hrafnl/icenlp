lt-proc ../../../../apertium-is-en/is-en.automorf.bin | 
java -Xmx256M -classpath ../../dist/IceNLPCore.jar is.iclt.icenlp.runner.RunIceTaggerApertium -tm ../../dict/icetagger/otb.apertium.dict -x apertium -sf -of 1 #|
#apertium-pretransfer | 
#apertium-transfer ../../../../apertium-is-en/apertium-is-en.is-en.t1x ../../../../apertium-is-en/is-en.t1x.bin ../../../../apertium-is-en/is-en.autobil.bin  | 
#apertium-interchunk ../../../../apertium-is-en/apertium-is-en.is-en.t2x  ../../../../apertium-is-en/is-en.t2x.bin | 
#apertium-postchunk ../../../../apertium-is-en/apertium-is-en.is-en.t3x ../../../../apertium-is-en/is-en.t3x.bin | 
#lt-proc -d ../../../../apertium-is-en/is-en.autogen.bin | 
#lt-proc -p ../../../../apertium-is-en/is-en.autopgen.bin
