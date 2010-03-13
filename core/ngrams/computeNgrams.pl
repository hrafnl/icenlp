#!/usr/bin/env perl

#
# This program reads a corpus (Brill format: one (token,tag) pair per line
# And outputs unigrams, bigrams and trigrams along with frequencies

sub updateNgrams {
# @tokens = t-1 t0 t1 t2 ... tn tn+1	

# We don't want bigrams og trigrams consisting of two boundary tags
   for ($i=0; $i<=$#tokens; $i++) {
         $uniHash{$tokens[$i]}++;
         if (($i>0) && (($tokens[$i-1] ne $boundaryTag) || ($tokens[$i] ne $boundaryTag))) {
            $biHash{$tokens[$i-1] . " " . $tokens[$i]}++;
         }
         if (($i>1) && (($tokens[$i-2] ne $boundaryTag) || ($tokens[$i-1] ne $boundaryTag))) {
            $triHash{$tokens[$i-2] . " " . $tokens[$i-1] . " " . $tokens[$i]}++;
         }
    }
}

sub processOneSentence {
      # Add boundaries to the front and to the back
      
      $sentence = $front . $sentence . $back;
      
      @tokens=split(/\s+/, $sentence);    
      &updateNgrams;
      
      $sentence = "";
      $eos=0;
}

$numTokens=0;
$numUnigrams=0;
$numBigrams=0;
$numTrigrams=0;
$emptyLines=0;

$infile = shift(@ARGV);         # the input corpus file 
$outfile = shift(@ARGV);        # the output ngram file
$outfile2 = shift(@ARGV);       # the output lambda file
$emptyStr = shift(@ARGV);       # empty lines between sentences in the corpus
if ($emptyStr eq "-e") {printf STDERR "Empty lines\n"; $emptyLines = 1};

open(INFILE, "$infile");    
open(NGRAM, ">$outfile");     
open(LAMBDA, ">$outfile2");     

# This programs assumes one (token,tag) pair per line
# An empty line between sentences is used as a sentence segmentizer
# If an empty line is not found then [!?.;] are used

$eos=0;
$boundaryTag = "__\$";
#$front = $boundaryTag . " " . $boundaryTag . " ";
$front = $boundaryTag . " ";
$back = " " . $boundaryTag;
$sentence="";
$numSentences=0;

while ($line=<INFILE>) {
  
  if ($line !~ /^\s*$/)		# If not an empty line 
  {
     $numTokens++;  
     chomp($line);
     @tokens=split(/\s+/, $line);
     $tag = $tokens[1];
     $sentence = $sentence . $tag . " ";
     if ((!$emptyLines) && ($line =~ /^[;!\?\.]/) )	# End of sentence
     {
        &processOneSentence();
        $numSentences++;
     }
  }
  else {
    &processOneSentence();
    $numSentences++;
  }
 
  if ($numTokens%100==0) {
     	printf STDERR "%d\r", $numTokens;
  }
}

printf STDERR "\n Printing unigrams ...";
foreach $i (keys %uniHash) {
  printf NGRAM "%s %d\n", $i, $uniHash{$i};
  $numUnigrams++;
}

$lambdaBi[1]=0;  
$lambdaBi[2]=0;  

$numTokensExtra = $numTokens + (2*$numSentences);  # 2 extra tokens in each sentence

printf STDERR "\n Printing bigrams and computing smoothing parameters ...";
foreach $biGram (keys %biHash) {
  $f12 = $biHash{$biGram};
  printf NGRAM "%s %d\n", $biGram, $biHash{$biGram};
  
  @tags=split(/\s+/, $biGram);  
  $numBigrams++;
  # Based on (Brants, 2000) TnT paper
  
    $f1 = $uniHash{$tags[0]};
    $f2 = $uniHash{$tags[1]};
      
    $ratio[2] = $f1 == 1 ? 0 : (($f12-1.0)/($f1-1.0)); 
    $ratio[1] = $numTokensExtra == 1 ? 0 : (($f2-1.0)/($numTokensExtra-1.0)); 
    #$ratio[1] = $numTokens == 1 ? 0 : (($f2-1.0)/($numTokens-1.0)); 
      
    $maxIdx=2;
    if ($ratio[1]>$ratio[$maxIdx]) {
  	$maxIdx=1;
    }  
    $lambdaBi[$maxIdx] += $f12;
  
}
# Normalize
$tot = $lambdaBi[1] + $lambdaBi[2];
$lambdaBi[1] = $lambdaBi[1]/$tot;
$lambdaBi[2] = $lambdaBi[2]/$tot;


printf STDERR "\n Printing trigrams and computing smoothing parameters ...\n";
$lambdaTri[1]=0;  
$lambdaTri[2]=0;  
$lambdaTri[3]=0;  

foreach $triGram (keys %triHash) {
  $f123 = $triHash{$triGram};
  printf NGRAM "%s %d\n", $triGram, $f123;
  
  # Based on (Brants, 2000) TnT paper
   
  @tags=split(/\s+/, $triGram);  
  $numTrigrams++;
  
    $f12 = $biHash{$tags[0] . " " . $tags[1]};
    $f23 = $biHash{$tags[1] . " " . $tags[2]};
    $f2 = $uniHash{$tags[1]};
    $f3 = $uniHash{$tags[2]};
    
    $ratio[3] = $f12 == 1 ? 0 : (($f123-1.0)/($f12-1.0)); 
    $ratio[2] = $f2 == 1 ? 0 : (($f23-1.0)/($f2-1.0)); 
    $ratio[1] = $numTokensExtra == 1 ? 0 : (($f3-1.0)/($numTokensExtra-1.0)); 
    #$ratio[1] = $numTokens == 1 ? 0 : (($f3-1.0)/($numTokens-1.0)); 
    
    $maxIdx=3;
    if ($ratio[2]>$ratio[$maxIdx]) {
    	$maxIdx=2;
    }
    if ($ratio[1]>$ratio[$maxIdx]) {
        $maxIdx=1;
    }
    $lambdaTri[$maxIdx] += $f123;
}

# Normalize
$tot = $lambdaTri[1] + $lambdaTri[2] + $lambdaTri[3];
$lambdaTri[1] = $lambdaTri[1]/$tot;
$lambdaTri[2] = $lambdaTri[2]/$tot;
$lambdaTri[3] = $lambdaTri[3]/$tot;

printf LAMBDA "lambdaBi1=$lambdaBi[1]\n";
printf LAMBDA "lambdaBi2=$lambdaBi[2]\n";
printf LAMBDA "lambdaTri1=$lambdaTri[1]\n";
printf LAMBDA "lambdaTri2=$lambdaTri[2]\n";
printf LAMBDA "lambdaTri3=$lambdaTri[3]\n";

print "Corpus size: $numTokens\n";
print "Unigrams: $numUnigrams\n";
print "Bigrams: $numBigrams\n";
print "Trigrams: $numTrigrams\n";
print "Sentences: $numSentences\n";
print "Lambda1 trigrams: $lambdaTri[1]\n";
print "Lambda2 trigrams: $lambdaTri[2]\n";
print "Lambda3 trigrams: $lambdaTri[3]\n";
print "Lambda1 bigrams: $lambdaBi[1]\n";
print "Lambda2 bigrams: $lambdaBi[2]\n";

close(INFILE);
close(NGRAM);
close(LAMBDA);
