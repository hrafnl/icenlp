IceRules.rul contains the local rules used by IceTagger
genIceRules.java is a program which generates java code using IceRules.rul as input

If only IceRules.rul is changed then run
java genIceRules IceRules.rul > ../../src/is/iclt/icenlp/core/icetagger/IceLocalRules.java

If genIceRules.flex is changed then run:
1. jflex genIceRules.flex
2. javac genIceRules.java
