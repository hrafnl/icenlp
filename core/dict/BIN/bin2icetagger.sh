# Creates a dictionary from BIN data: one line for each word form with the associated tags

DICT_DIR=../icetagger
echo Creating a dictionary for IceTagger ...
echo "  Skip the lemma from BIN ..."
awk '{print $1"\t"$3}' < bin.out > bin2.out
echo "  Sort the BIN data ..."
sort bin2.out | uniq > bin2.out.sort
echo "  Create a dictionary from BIN..."
perl buildDictFromBin.pl -i bin2.out.sort -o bin.dict
echo "  Combine two dictionaries, otb and BIN ..."
perl combineDicts.pl $DICT_DIR/otb.dict bin.dict $DICT_DIR/otbBin.dict

rm bin2.out bin2.out.sort
