@echo ****************************************************
@echo *                                                  *
@echo *  IceParser - An incremental finite-state parser  *
@echo *                for Icelandic text                *
@echo *  Version 1.1                                     *
@echo *  Copyright (C) 2006-2009, Hrafn Loftsson         *
@echo *                                                  *
@echo ****************************************************

@echo off
REM When each transducer reads from standard input and writes to standard output (as is the case here)
REM it uses the java.io.FileReader class which in turn uses the platforms default encoding automatically.
REM This means that you have to make sure that the encoding of your input file matches the platform encoding.

@echo Input file: %1%
@echo Output file: %2%
IF "%3%"=="" GOTO start
IF "%3%" == "-l" @echo Writing output as one phrase per line 

:start
REM The parser comprises the following transducers:
REM Preprocess:   preprocessing
REM Phrase_MWEP1: labels multiword expressions consisting of (prep, adverb) pairs
REM Phrase_MWEP2: labels multiword expressions consisting of (adverb, prep) pairs
REM Phrase_MWE:	  labels other multiword expressions, bigrams and trigrams
REM Phrase_AdvP:  labels adverbial phrases 
REM Phrase_AP:    labels adjectival phrases 
REM Case_AP: 	  labels the case of adjectival phrases
REM Phrase_APs:   labels a sequence of adjectival phrases 
REM Phrase_NP:    labels noun phrases 
REM Phrase_NPs:   labels a sequence of noun phrases 
REM Phrase_VP:    labels verb phrases 
REM Case_NP: 	  lables the case of noun phrases
REM Phrase_PP:    labels prepositional phrases 
REM Clean1, Clean2:  cleans the output file
REM Phrase_Per_Line: prints one phrase per line

REM Func_TIMEX:	labels temporal expressions
REM Func_QUAL:	labels qualifier noun phrases
REM Func_SUBJ:	labels subjects
REM Func_SUBJ2:	labels subjects
REM Func_COMP:	labels complements
REM Func_OBJ:	labels objects
REM Func_OBJ2:	labels objects
REM Func_OBJ3:	labels objects

set JAR=..\..\dist/IceNLPCore.jar
set PACKAGE=is.iclt.icenlp.core.iceparser
@echo on
java -classpath %JAR% %PACKAGE%.Preprocess %1% > preprocess.out
java -classpath %JAR% %PACKAGE%.Phrase_MWE preprocess.out > phrase_MWE.out
java -classpath %JAR% %PACKAGE%.Phrase_MWEP1 phrase_MWE.out > phrase_MWEP1.out
java -classpath %JAR% %PACKAGE%.Phrase_MWEP2 phrase_MWEP1.out > phrase_MWEP2.out
java -classpath %JAR% %PACKAGE%.Phrase_AdvP phrase_MWEP2.out > phrase_AdvP.out
java -classpath %JAR% %PACKAGE%.Phrase_AP phrase_AdvP.out > phrase_AP.out
java -classpath %JAR% %PACKAGE%.Case_AP phrase_AP.out > case_AP.out
java -classpath %JAR% %PACKAGE%.Phrase_APs case_AP.out > phrase_APs.out
java -classpath %JAR% %PACKAGE%.Phrase_NP phrase_APs.out > phrase_NP.out
java -classpath %JAR% %PACKAGE%.Phrase_VP phrase_NP.out > phrase_VP.out
java -classpath %JAR% %PACKAGE%.Case_NP phrase_VP.out > case_NP.out
java -classpath %JAR% %PACKAGE%.Phrase_NPs case_NP.out > phrase_NPs.out
java -classpath %JAR% %PACKAGE%.Phrase_PP phrase_NPs.out > phrase_PP.out
java -classpath %JAR% %PACKAGE%.Clean1 phrase_PP.out > clean1.out

java -classpath %JAR% %PACKAGE%.Func_TIMEX clean1.out > func_timex.out
java -classpath %JAR% %PACKAGE%.Func_QUAL func_timex.out > func_qual.out
java -classpath %JAR% %PACKAGE%.Func_SUBJ func_qual.out > func_subj.out
java -classpath %JAR% %PACKAGE%.Func_COMP func_subj.out > func_comp.out
java -classpath %JAR% %PACKAGE%.Func_OBJ func_comp.out > func_obj.out
java -classpath %JAR% %PACKAGE%.Func_OBJ2 func_obj.out > func_obj2.out
java -classpath %JAR% %PACKAGE%.Func_OBJ3 func_obj2.out > func_obj3.out
java -classpath %JAR% %PACKAGE%.Func_SUBJ2 func_obj3.out > func_subj2.out
java -classpath %JAR% %PACKAGE%.Clean2 func_subj2.out > clean2.out

@echo off
IF "%3%"=="" GOTO clean
IF NOT "%3%" == "-l" GOTO clean 

java -classpath %JAR% %PACKAGE%.Phrase_Per_Line clean2.out > phrase_per_line.out
copy phrase_per_line.out %2%
goto end

:clean
copy clean2.out %2%

:end
