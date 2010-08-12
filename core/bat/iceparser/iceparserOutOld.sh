echo "****************************************************"
echo "*                                                  *"
echo "*  IceParser - An incremental finite-state parser  *"
echo "*                for Icelandic text                *"
echo "*  Version 1.1                                     *"
echo "*  (c) Hrafn Loftsson, 2006-2010                   *"
echo "*                                                  *"
echo "****************************************************"

echo "This shell should not be used any longer "
echo "The source however shows how you can run a single transducer independently "
echo "Please use iceParserOut.sh instead "
exit

# When each transducer reads from standard input and writes to standard output (as is the case here)
# it uses the java.io.FileReader class which in turn uses the platforms default encoding automatically.
# This means that you have to make sure that the encoding of your input file matches the platform encoding.

date

echo Input file: $1
echo Output file: $2
echo Output path: $3

if [ "$4" = "-l" ]  # Phrase_per_line flag
then
  echo Writing output as one phrase per line 
fi


# The parser comprises the following transducers:
# Preprocess:   preprocessing
# Phrase_MWEP1: labels multiword expressions consisting of (prep, adverb) pairs
# Phrase_MWEP2: labels multiword expressions consisting of (adverb, prep) pairs
# Phrase_MWE:	  labels other multiword expressions, bigrams and trigrams
# Phrase_AdvP:  labels adverbial phrases 
# Phrase_AP:    labels adjectival phrases 
# Case_AP: 	  labels the case of adjectival phrases
# Phrase_APs:   labels a sequence of adjectival phrases 
# Phrase_NP:    labels noun phrases 
# Phrase_NPs:   labels a sequence of noun phrases 
# Phrase_VP:    labels verb phrases 
# Case_NP: 	  lables the case of noun phrases
# Phrase_PP:    labels prepositional phrases 
# Clean1, Clean2:  cleans the output file
# Phrase_Per_Line: prints one phrase per line

# Func_TIMEX:	labels temporal expressions
# Func_QUAL:	labels qualifier noun phrases
# Func_SUBJ:	labels subjects
# Func_SUBJ2:	labels subjects
# Func_COMP:	labels complements
# Func_OBJ:	labels objects
# Func_OBJ2:	labels objects
# Func_OBJ3:	labels objects

JAR=../../dist/IceNLPCore.jar
PACKAGE=is.iclt.icenlp.core.iceparser
java -classpath $JAR $PACKAGE.Preprocess $1 > $3/preprocess.out
java -classpath $JAR $PACKAGE.Phrase_MWE $3/preprocess.out > $3/phrase_MWE.out
java -classpath $JAR $PACKAGE.Phrase_MWEP1 $3/phrase_MWE.out > $3/phrase_MWEP1.out
java -classpath $JAR $PACKAGE.Phrase_MWEP2 $3/phrase_MWEP1.out > $3/phrase_MWEP2.out
java -classpath $JAR $PACKAGE.Phrase_AdvP $3/phrase_MWEP2.out > $3/phrase_AdvP.out
java -classpath $JAR $PACKAGE.Phrase_AP $3/phrase_AdvP.out > $3/phrase_AP.out
java -classpath $JAR $PACKAGE.Case_AP $3/phrase_AP.out > $3/case_AP.out
java -classpath $JAR $PACKAGE.Phrase_APs $3/case_AP.out > $3/phrase_APs.out
java -classpath $JAR $PACKAGE.Phrase_NP $3/phrase_APs.out > $3/phrase_NP.out
java -classpath $JAR $PACKAGE.Phrase_VP $3/phrase_NP.out > $3/phrase_VP.out
java -classpath $JAR $PACKAGE.Case_NP $3/phrase_VP.out > $3/case_NP.out
java -classpath $JAR $PACKAGE.Phrase_NPs $3/case_NP.out > $3/phrase_NPs.out
java -classpath $JAR $PACKAGE.Phrase_PP $3/phrase_NPs.out > $3/phrase_PP.out
java -classpath $JAR $PACKAGE.Clean1 $3/phrase_PP.out > $3/clean1.out

java -classpath $JAR $PACKAGE.Func_TIMEX $3/clean1.out > $3/func_timex.out
java -classpath $JAR $PACKAGE.Func_QUAL $3/func_timex.out > $3/func_qual.out
java -classpath $JAR $PACKAGE.Func_SUBJ $3/func_qual.out > $3/func_subj.out
java -classpath $JAR $PACKAGE.Func_COMP $3/func_subj.out > $3/func_comp.out
java -classpath $JAR $PACKAGE.Func_OBJ $3/func_comp.out > $3/func_obj.out
java -classpath $JAR $PACKAGE.Func_OBJ2 $3/func_obj.out > $3/func_obj2.out
java -classpath $JAR $PACKAGE.Func_OBJ3 $3/func_obj2.out > $3/func_obj3.out
java -classpath $JAR $PACKAGE.Func_SUBJ2 $3/func_obj3.out > $3/func_subj2.out
java -classpath $JAR $PACKAGE.Clean2 $3/func_subj2.out > $3/clean2.out

if [ "$4" = "-l" ]  # phrase_per_line flag
then
	java -classpath $JAR $PACKAGE.Phrase_Per_Line $3/clean2.out > $3/phrase_per_line.out
	cp $3/phrase_per_line.out $3/$2
else
	cp $3/clean2.out $3/$2
fi

date

