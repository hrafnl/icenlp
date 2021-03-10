# Usage: ./wordpl2sentpl.sh <file>
# Converts 'word/tag per line' to 'tagged sentence per line'
sed -z 's/\(\S\+\)\n/\1\t/g' $1 | sed 's/\t/ /g' > $1".txt"
