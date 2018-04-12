#!/usr/local/bin/perl

sub tokenize {
   my($lin);		# A line 	
   $lin = $_[0];	
   chomp($lin);			# get rid of newline at then end of line
   $lin =~ s/^\s+//g;           # replace leading white spaces with nothing
   # split the line using =
   my @tokens = split /=/, $lin;
   return @tokens;
}

sub addTags { 		
   my($theWord, $tagsToAddStr); 	
   my $currentTagsStr;
   $theWord = $_[0];	# The word
   $tagsToAddStr = $_[1]; # The tag string
   my $tAdd;
   my $tOld;

   $currentTagsStr = $mapDict1{$theWord};
	
   my @tagsAdd = split /_/, $tagsToAddStr;		# array for tags to add
   my @tagsCurrent = split /_/, $currentTagsStr;	# array for current tags
	
   #print "Tags to add: $theWord $tagsToAddStr\n";
   #print "Current tags: $theWord $currentTagsStr\n";
   # Loop through the tags to add and add each missing tag 
   for ($i=0; $i<=$#tagsAdd; $i++)		
   {
	 my $tAdd = $tagsAdd[$i];
	 $found = 0;
	 for ($j=0; $j<=$#tagsCurrent; $j++) {
	     $tOld = $tagsCurrent[$j];
	     if ($tOld eq $tAdd) {
	    	$found = 1;
	   }
	 }
	 if (!$found) {
	     if ($tAdd =~ m/^n/) {	# Only nouns
	     	push(@tagsCurrent, $tAdd);
	     	$currentTagsStr = $currentTagsStr . "_" . $tAdd;
	     	$mapDict1{$theWord} = $currentTagsStr;
	     	print "Added tag $tAdd for word $theWord\n";
	     }
	 }
    }
}

sub readDict1 {
  my($line, @tokens);
  while ($line = <DICT1>) {
    if ($line !~ /^\s*$/) {
	print DICT3 $line;
        @tokens = &tokenize($line);
        $word = $tokens[0];
        $tags = $tokens[1];
        $mapDict1{$word} = $tags;
    }
  }
}

# Main program starts here
# This program combines two dictionaries, dict1 and dict2, into dict3. 
# Words in dict2 that exists in dict1 are not part of the combined dict3. 


$dict1 = shift(@ARGV);	# get the file name
$dict2 = shift(@ARGV);	# get the file name
$dict3 = shift(@ARGV);	# get the file name

open(DICT1, "$dict1");	# open the file
open(DICT2, "$dict2");	# open the file
open(DICT3, ">$dict3");	# open the file

&readDict1();
close(DICT1);


# Now read dict2
while ($line = <DICT2>) {
   if ($line !~ /^\s*$/) {
     my @tokens = &tokenize($line); 
     $word = $tokens[0];
     $tags = $tokens[1];
     if (!exists ($mapDict1{$word})) {
	print DICT3 $line;
     }
     # Else add tags from the word to the word in dict1
     # This increases the ambiguity rate considerable
     #else {		
	# &addTags($word, $tags);
     #}
   }
}

#Print out dict1
#Sort the keys (words) and iterate through them
#foreach $word (sort keys %mapDict1) {
#   $tagStr = $mapDict1{$word};	# the tagStr is the value
#   print DICT3 "$word=$tagStr\n";	# Becomes an entry in the dictionary
#}

close(DICT2);
close(DICT3);
