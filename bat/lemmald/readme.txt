Running Lemmald using "lemmatize.sh"
Use corresponding .bat file (instead of .sh) on Windows.
Parameters:
-h  display help message
-i<file>  inputFile, if none is specified input is read from stdin
-o<file>  outputFile, if none is specified input is written to stdout
-lemmatizeTagged     reads tagged output and adds a lemma to each word

*****
Example 1:

Lemmatizing a plain text file:
./lemmatize.sh -i plaintext.txt -o myoutput.txt
Reads the plain text file plaintext.txt and writes the result to myoutput.txt.

Input:
Við erum æðislegar. Við kunnum alla dansana.

Output:
Við ég fp1fn
erum vera sfg1fn
æðislegar æðislegur lvfnsf
. . .

Við ég fp1fn
kunnum kvinna sfg1fþ
alla allur fokfo
dansana dans nkfog
. . .

*****
Example 2:

To lemmatize tagged input, with one token per line, each of which has a word form and a Part-Of-Speech tag, supply the parameter "-lemmatizeTagged".

./lemmatize.sh -i testinput.txt -o output.txt -lemmatizeTagged
(other version, where Lemmald reads from stdin and writes to stdout)
cat testinput.txt | ./lemmatize.sh -lemmatizeTagged

Input:
Ég fp1en
á sfg1en
stóran lkeosf
hund nkeo

Output:
Ég ég fp1en
á eiga sfg1en
stóran stór lkeosf
hund hundur nkeo

*****

