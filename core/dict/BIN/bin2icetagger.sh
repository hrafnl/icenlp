# Creates a dictionary from BIN data for IceTagger: one line for each word form with the associated tags

DICT_DIR=../icetagger

echo "Create a dictionary from BIN for IceTagger..."
perl buildDictFromBin.pl -i bin2.sort.out -o bin.dict
echo "Combine two dictionaries, otb and BIN for IceTagger ..."
perl combineDicts.pl $DICT_DIR/otb.dict bin.dict $DICT_DIR/otbBin.dict
