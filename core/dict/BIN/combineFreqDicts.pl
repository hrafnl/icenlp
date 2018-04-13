#!/usr/local/bin/perl

sub tokenize {
   my($lin);		# A line 	
   $lin = $_[0];	
   chomp($lin);			# get rid of newline at then end of line
   $lin =~ s/^\s+//g;           # replace leading white spaces with nothing
   # split the line using space
   my @tokens = split /\s+/, $lin;
   return @tokens;
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
# This program combines two frequency dictionaries, dict1 and dict2, into dict3 
# Words in dict2 that exists in dict1 are not part of the combined dict3. 

$dict1 = shift(@ARGV);	# get the file name
$dict2 = shift(@ARGV);	# get the file name
$dict3 = shift(@ARGV);	# get the file name
$flag = shift(@ARGV);	# get a flag

open(DICT1, "$dict1") or die "Can't open file $dict1\n";	# open the file
open(DICT2, "$dict2") or die "Can't open file $dict2\n";	# open the file
open(DICT3, ">$dict3") or die "Can't open file $dict3\n";	# open the file

&readDict1();
close(DICT1);

if ($flag eq "-nosuffix") 
{
	#  print "Marking a start of no suffix information\n";
	print DICT3 "[NOSUFFIXES]\n";
}

# Now read dict2
while ($line = <DICT2>) {
   if ($line !~ /^\s*$/) {
     my @tokens = &tokenize($line); 
     $word = $tokens[0];
     $tags = $tokens[1];
     if (!exists ($mapDict1{$word})) {
	print DICT3 $line;
     }
   }
}

close(DICT2);
close(DICT3);
