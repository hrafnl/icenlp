SRX based sentence segmentizer.
There are two ways to run the tokenizer.
(use .bat instead of .sh on Windows)

1) Read from stdin and write result to stdout
Command: 

echo "Þetta er nr. 1 og a.m.k. fínt. Hvað er það? Farið e.t.v. þangað." | ./srxsegmentizer.sh

Output:

Þetta er nr. 1 og a.m.k. fínt. 
Hvað er það? 
Farið e.t.v. þangað.

2) Read from input text file and write result to another text file

./srxsegmentizer.sh testinput.txt output.txt


