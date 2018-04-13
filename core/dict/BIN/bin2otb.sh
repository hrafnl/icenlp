# Read the SHsnid.csv BIN file and select rows from it that match the given tag pattern
echo Reading SHsnid.csv ...
perl bin2Otb.pl -i SHsnid.csv -o bin.tmp -l 6000000 -p "n...(g|-m)?$|l...|s[lns][gm]$|s[fv]....|sþg..n|aa[me]?" -u
echo Remove named entity info from proper nouns ...
sed 's/\(n...[-g]\)\([msö]\)$/\1s/' < bin.tmp > bin.out

echo "Skip the lemma from BIN ..."
awk '{print $1"\t"$3}' < bin.out > bin2.out
echo "Sort the BIN data ..."
sort bin2.out | uniq > bin2.sort.out
rm bin.tmp
