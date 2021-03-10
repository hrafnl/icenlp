# Usage: ./wordpl2sentpl.sh <file>
# Converts 'word/tag per line' to 'tagged sentence per line'

# If the following sed command does not work then use the awk command below
#sed -z 's/\(\S\+\)\n/\1\t/g' $1 | sed 's/\t/ /g' > $1".txt"

awk '{if (NF != 0) {sent = sent " " $0} else {print sent; sent = ""}}' $1 | sed 's/\t/ /g' | sed 's/^ //'> $1.txt
