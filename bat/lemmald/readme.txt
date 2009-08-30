Running Lemmald
Use "./lemmald.sh -h" to get more help.
Use corresponding .bat file (instead of .sh) on Windows.

*****

Lemmatizing a file:
./lemmatize.sh -i testinput.txt -o myoutput.txt

Reads the plain text file testinput.txt and writes the result to myoutput.txt.

*****

Lemmatizing using pipe mode:

echo "Við erum æðislegar." | ./lemmatize.sh -pipeMode

prints the following lemmatized (and tagged) output to stdout (add > output.txt to write to file):

Við ég fp1fn
erum vera sfg1fn
æðislegar æðislegur lvfnsf
. . .


