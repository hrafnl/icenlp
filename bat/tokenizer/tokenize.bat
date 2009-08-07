@echo off
set p1=%1
shift
set p2=%1
shift
set p3=%1
shift
set p4=%1
shift
set p5=%1
shift
set p6=%1
shift
set p7=%1
shift
set p8=%1
shift
set p9=%1
shift
set p10=%1
shift
set p11=%1
shift
set p12=%1
@echo on
java -classpath ..\..\dist\IceNLPCore.jar is.iclt.icenlp.runner.RunTokenizer %p1% %p2% %p3% %p4% %p5% %p6% %p7% %p8% %p9% %p10% %p11% %p12% 

