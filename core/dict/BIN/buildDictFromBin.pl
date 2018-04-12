#!/usr/local/bin/perl
use Getopt::Std;

sub tokenize {
   my($lin);		# A line 	
   $lin = $_[0];	
   chomp($lin);			# get rid of newline at then end of line
   $lin =~ s/^\s+//g;           # replace leading white spaces with nothing
   # split the line using space as a delimeter
   @words = split /\s+/, $lin;
}

# Builds a concatenated tag string for each word.
# The input file is sorted. Same wordforms appear next to each other
sub hash { 		
   my($wrd, $tg); 	
   $wrd = $_[0];	# The word
   $tg = $_[1];		# The tag
   $tagStr = $hash_tag{$wrd};

   if ($tagStr eq "") {
   	$hash_tag{$wrd} = $tg; 	
   }
   else {
   $hash_tag{$wrd} = $hash_tag{$wrd} . "_" . $tg; 	# Concatenate tags 
  }
}


# Main program starts here
# This program takes an input file consisting of wordform and a tag 
$frequency=0;

getopts('i:o:f', \%opts);
if (exists($opts{i})) {
        $infile = $opts{i};
        #print ("Input file: $infile\n");
        open(INFILE, "$infile");
}
else { die "Missing input file\n"; }

if (exists($opts{o})) {
        $outfile = $opts{o};
        #print ("Output file: $outfile\n");
        open(OUTFILE, ">$outfile");
}
else { die "Missing output file\n"; }

if (exists($opts{f})) {
        $frequency=1;
        #print ("Generating dummy frequency info\n");
}

while ($line = <INFILE>) {
   &tokenize($line);	# tokenize the line, result is placed in the 
			# array @words
   $word = $words[0];
   $tag = $words[1];
   #print ("Reading $word $tag\r");

   &hash($word,$tag);	# hashes the line with word as a key and tag as the value
}

#Output

#Sort the keys (words) and iterate through them
  foreach $word (sort keys %hash_tag) {
	$tag = $hash_tag{$word};	# the tag(s) is the value
	if (! $frequency) {
	  print OUTFILE "$word=$tag\n";	# Becomes an entry in the dictionary
	}
	else {
	  if ($tag ne "") {	
   		@tags = split /_/, $tag;
		$count = $#tags+1;
		print OUTFILE "$word\t$count\t";  # The word and number of tags	
		for ($i=0; $i<$count; $i++) {  # Print each tag and frequency 1
		  print OUTFILE "$tags[$i]\t1";	# Becomes an entry in the dictionary
		  if ($i==$#tags) {print OUTFILE "\n";}
		  else {print OUTFILE "\t";}
		}
	  }
	}
  }

close(INFILE);
close(OUTFILE);
