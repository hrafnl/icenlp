# ---------------------
# Parameters:
# -i <input file name>
# -o <output file name>
# -l <gazette list file name>
# -g if greedy mode
# ---------------------
while getopts "i:o:l:g" flag
do
  #echo "$flag" $OPTIND $OPTARG
  if [ "$flag" = "i" ] ; then input=$OPTARG ; fi
  if [ "$flag" = "o" ] ; then output=$OPTARG ; fi
  if [ "$flag" = "l" ] ; then gazetteList=$OPTARG ; fi
  if [ "$flag" = "g" ] ; then greedy="-g" ; fi
done

echo "***********************************************"
echo "*   Icelandic Named Entity Recognition        *"
echo "*   Copyright (C) 2009, Aðalsteinn Tryggvason *"	
echo "***********************************************"
if [ -n "$input" ]  # Is the variable defined?
then
  echo "Input file: $input"
else
  echo "Input file is missing!"
  exit
fi
if [ -n "$output" ]  # Is the variable defined?
then
  echo "Output file: $output"
else
  echo "Output file is missing!"
  exit
fi

# Tag the input text
#../icetagger/icetagger.sh -i $input -o $input.tagged -lf 3
echo Tagging the input with IceTagger ... 
cat $input | ../icetagger/icetagger.sh -lf 3 -of 1 > $input.tagged
# Get number of tokens in the output
tagCount=$(wc -l $input.tagged | awk '{print $1}')
echo Tag count: $tagCount 
# Run NameScanner
echo Running NameScanner ...
java -Xmx128M -classpath ../../dist/IceNLPCore.jar is.iclt.icenlp.core.iceNER.NameScanner $input >$input.scanned
# Get number of tokens in the output
scanCount=$(wc -l $input.scanned | awk '{print $1}')
echo Scan count: $scanCount 
# Run NameFinder
echo Running NameFinder ...

if [ -n "$gazetteList" ]  # Is the variable defined?
then
	# Get number of tokens in the output
	listCount=$(wc -l $gazetteList | awk '{print $1}')
	echo Gazette list count: $listCount 
	java -Xmx128M -classpath ../../dist/IceNLPCore.jar is.iclt.icenlp.runner.RunNameFinder $input.tagged $tagCount $input.scanned $scanCount $output $listCount $gazetteList $greedy
else
	java -Xmx128M -classpath ../../dist/IceNLPCore.jar is.iclt.icenlp.runner.RunNameFinder $input.tagged $tagCount $input.scanned $scanCount $output $greedy
fi
