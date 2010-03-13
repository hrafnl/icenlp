#!/usr/local/bin/perl

sub tokenize {
   my($lin);		# A line 	
   $lin = $_[0];	
   
# Line consists of a frequence, word and a tag

   chomp($lin);			# get rid of newline at then end of line
   $lin =~ s/^\s+//g;           # replace leading white spaces with nothing
   #print "$lin\n";
   # split the line using space as a delimeter
   @tok = split /\s+/, $lin;
}

sub hashWordTag {
   my($wrd, $tg); 	
   $wrd = $_[0];	# The word
   $tg = $_[1];		# The tag
   
   # Keep the tags for each word in a string
   $tagStr = $wordTagList{$wrd};
   if ($tagStr eq "") {
       	$wordTagList{$wrd} = $tg; 	
   }
   else {
      $wordTagList{$wrd} = $wordTagList{$wrd} . " " . $tg; 	# Concatenate tags 
   }

}

# Builds a concatenated tag string for each word.
# Since the input file is sorted by frequency the most frequent tag appears first in each tag string
# Additional special lexicon entries are built from number tokens 
sub hash { 		
   my($frq, $wrd, $tg); 	
   $frq = $_[0];	# The frequency
   $wrd = $_[1];	# The word
   $tg = $_[2];		# The tag
   
   # Register this frequency for the (word,tag) pair
   $wordCount{$wrd} = $wordCount{$wrd} + $frq;
   $tagCount{$tg} = $tagCount{$tg} + $frq;
   $wordTagCount{$wrd}{$tg} = $frq;
   
   &hashWordTag($wrd,$tg);	# Add the tag to the list of tags for the word
   
   # Special entries
   $special = 0;
   if ($wrd =~ /^\d+[\.\-,\/:]+\d+/){	# Mathces tokens that contain sequence of digits
      	$key = "\@CARDSEPS";		# separated by dots, dashes, etc. E.g. "4-2"	
      	$special = 1;
   }
   elsif ($wrd =~ /^\d+\./)	{	# Mathces tokens that contain sequence of digits
         	$key = "\@CARDPUNCT";	# followed by punctuation, e.g. "42."
         	$special = 1;
   }
   elsif ($wrd =~ /^\d+$/)	{	# Mathces tokens that contain sequence of digits
      	$key = "\@CARD";		# e.g. "42"
      	$special = 1;
   }
   elsif ($wrd =~ /^\d+.+/) {		# Mathces tokens that contain sequence of digits
        $key = "\@CARDSUFFIX";		# followed by any suffix
        $special = 1;
   }
   elsif ($wrd =~ /^\d*\D+\d+/) {		# Mathces tokens include a digit
        $key = "\@CARDSUFFIX";		
        $special = 1;
   }
      
   if ($special) {
        $specialCount{$key} += $frq;
        $specialTagCount{$key}{$tg} += $frq;
	#print DEBUG "$key: $wrd\n";
   }
   
}


# Main program starts here
# This program takes an input file where each line consists of a frequence, word and a tag
# and builds a dictionary with each line of the form:
# w1 t11 f11 t12 f12 ... t1i f1i
# w2 t21 f21 t22 f22 ... t2j f2j
# ...
# where w is a word, t is a tag and f is a frequency

$infile = shift(@ARGV);		# get the file name
$outfile = shift(@ARGV);	# get the file name
open(INFILE, "$infile");	# open the file
open(OUTFILE, ">$outfile");	# open the file
#open(DEBUG, ">debug.txt");	# open the file

$c=0;
while ($line = <INFILE>) {
   &tokenize($line);	# tokenize the line, result is placed in the 
			# array @tok
   $c++;
   if ($c%100==0) {
   	printf "%d lines\r", $c; 
   }
   
   $freq = $tok[0];
   $word = $tok[1];
   $tag = $tok[2];

   &hash($freq,$word,$tag);	# hashes the line with word as a key and tag as the value
}

#Output
# First print the special tokens
foreach $key (sort keys %specialCount) { # This hash maps a key to a frequency
   print OUTFILE "$key $specialCount{$key}";

   # Get the tag hash for $key;
   %tags = %{ $specialTagCount{$key} };
   
   # For each $tag in %tags print out the ($key,$tag) value
   foreach $tag ( sort {$tags{$b} <=> $tags{$a}} 
   			keys %tags ) { # The tags for the $key
   	printf OUTFILE " %s %d", $tag, $specialTagCount{$key}{$tag};
   }
   printf OUTFILE "\n";
}


#Sort the words and iterate through them
  $c=0;
  foreach $w (sort keys %wordCount) {
     $c++;
     printf "%d words    \r", $c if $c%10==0;
     if (length($w) > 0) {
        
  	print OUTFILE "$w $wordCount{$w}";
  	
  	# Then for each tag encountered for the word ...
  	@tags = split / /, $wordTagList{$w};
     	foreach $t (@tags) {
      	  printf OUTFILE " %s %d", $t, $wordTagCount{$w}{$t};	
        }
        printf OUTFILE "\n";
     }
  }
  
  printf OUTFILE "\n";
  printf "\n";
  
close(INFILE);
close(OUTFILE);
#close(DEBUG);
