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
@echo Output path: %3%
IF "%4%"=="" GOTO start
IF "%4%" == "-l" @echo Writing output as one phrase per line 

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
java -classpath %JAR% %PACKAGE%.Preprocess %1% > %3%\preprocess.out
java -classpath %JAR% %PACKAGE%.Phrase_MWE %3%\preprocess.out > %3%\phrase_MWE.out
java -classpath %JAR% %PACKAGE%.Phrase_MWEP1 %3%\phrase_MWE.out > %3%\phrase_MWEP1.out
java -classpath %JAR% %PACKAGE%.Phrase_MWEP2 %3%\phrase_MWEP1.out > %3%\phrase_MWEP2.out
java -classpath %JAR% %PACKAGE%.Phrase_AdvP %3%\phrase_MWEP2.out > %3%\phrase_AdvP.out
java -classpath %JAR% %PACKAGE%.Phrase_AP %3%\phrase_AdvP.out > %3%\phrase_AP.out
java -classpath %JAR% %PACKAGE%.Case_AP %3%\phrase_AP.out > %3%\case_AP.out
java -classpath %JAR% %PACKAGE%.Phrase_APs %3%\case_AP.out > %3%\phrase_APs.out
java -classpath %JAR% %PACKAGE%.Phrase_NP %3%\phrase_APs.out > %3%\phrase_NP.out
java -classpath %JAR% %PACKAGE%.Phrase_VP %3%\phrase_NP.out > %3%\phrase_VP.out
java -classpath %JAR% %PACKAGE%.Case_NP %3%\phrase_VP.out > %3%\case_NP.out
java -classpath %JAR% %PACKAGE%.Phrase_NPs %3%\case_NP.out > %3%\phrase_NPs.out
java -classpath %JAR% %PACKAGE%.Phrase_PP %3%\phrase_NPs.out > %3%\phrase_PP.out
java -classpath %JAR% %PACKAGE%.Clean1 %3%\phrase_PP.out > %3%\clean1.out

java -classpath %JAR% %PACKAGE%.Func_TIMEX %3%\clean1.out > %3%\func_timex.out
java -classpath %JAR% %PACKAGE%.Func_QUAL %3%\func_timex.out > %3%\func_qual.out
java -classpath %JAR% %PACKAGE%.Func_SUBJ %3%\func_qual.out > %3%\func_subj.out
java -classpath %JAR% %PACKAGE%.Func_COMP %3%\func_subj.out > %3%\func_comp.out
java -classpath %JAR% %PACKAGE%.Func_OBJ %3%\func_comp.out > %3%\func_obj.out
java -classpath %JAR% %PACKAGE%.Func_OBJ2 %3%\func_obj.out > %3%\func_obj2.out
java -classpath %JAR% %PACKAGE%.Func_OBJ3 %3%\func_obj2.out > %3%\func_obj3.out
java -classpath %JAR% %PACKAGE%.Func_SUBJ2 %3%\func_obj3.out > %3%\func_subj2.out
java -classpath %JAR% %PACKAGE%.Clean2 %3%\func_subj2.out > %3%\clean2.out

@echo off
IF "%4%"=="" GOTO clean
IF NOT "%4%" == "-l" GOTO clean 

java -classpath %JAR% %PACKAGE%.Phrase_Per_Line %3%\clean2.out > %3%\phrase_per_line.out
copy phrase_per_line.out %3%\%2%
goto end

:clean
copy clean2.out %3%\%2%

:end
