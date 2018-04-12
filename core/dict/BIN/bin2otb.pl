#!/usr/local/bin/perl 
use Getopt::Std;
use Encode;
use Switch;
use locale;

sub tokenize {
   my($lin, $delim, @tokens);
   $lin = $_[0];                        # The line
   $delim = $_[1];                      # The delimter
   chomp($lin);                         # get rid of newline at then end of line
   $lin =~ s/\s+/ /g;                   # collapse white spaces
   $lin =~ s/^\s+//g;                   # Replace leading white spaces with nothing
   @tokens = split /$delim/, $lin;      # split the line using a delimeter
   return @tokens;
}

# Checks if the word class specified by the user matches the word class found in the input
sub checkWordClass {
  my($userClass,$inputClass);
  $userClass = $_[0];
  $inputClass = $_[1];

  if (($userClass eq "no") && ($inputClass eq "hk" || $inputClass eq "kk" || $inputClass eq "kvk")) { return 1;}
  elsif ($userClass eq $inputClass) {return 1;}
  else {return 0;}
}

# Maps the info from BIN to a OTB tag
sub mapNoun {
  my($binClass,$binTag,$binPart);
  $binClass = $_[0];
  $binTag = $_[1]; 
  $binPart = $_[2]; 
  # $binTag looks like: "NFFT", "ÞGFETgr"

  my $otbGender="";
  my $otbNumber="";
  my $otbCase="";
  my $otbArticle="";
  my $otbProperNounType="";
  my $binArticle="";
  my($binNumber, $binCase);
  my $otbTag;	# the return tag

  $otbGender = $mapGender{$binClass};
  $binCase = substr($binTag,0,2); # First two letters
  if (exists ($mapCase{$binCase})) {
	$otbCase = $mapCase{$binCase};
  	$binNumber = substr($binTag,2,2);  
	$otbNumber = $mapNumber{$binNumber};
	if (length($binTag) == 6) {
  		$binArticle = substr($binTag,4,2);  
		$otbArticle = $mapArticle{$binArticle};
	}
  }
  else {
    	$binCase = substr($binTag,0,3); # First three letters
	$otbCase = $mapCase{$binCase};
  	$binNumber = substr($binTag,3,2);  
	$otbNumber = $mapNumber{$binNumber};
	if (length($binTag) == 7) {
  		$binArticle = substr($binTag,5,2);  
		$otbArticle = $mapArticle{$binArticle};
	}
  }
  # Proper nouns
  if ($binPart eq "ism" || $binPart eq "fyr" || $binPart eq $ornefni
	|| $binPart eq $lond || $binPart eq $gotur)
  {
	if ($binArticle eq "") {
		$otbArticle = "-"; 
	}
	$otbProperNounType = $mapProperNoun{$binPart};
  	#if ($binPart eq $ornefni
	#{
	#	print "Proper noun: $word -> n$otbGender$otbNumber$otbCase$otbArticle$otbProperNounType\n"; 
	#}
  }
	
  #print "$otbGender $otbNumber $otbCase $otbArticle\n";
  if ($otbArticle ne "") { 
     if ($otbProperNounType ne "") {
	$otbTag = "n".$otbGender.$otbNumber.$otbCase.$otbArticle.$otbProperNounType;     }
     else {
	$otbTag = "n".$otbGender.$otbNumber.$otbCase.$otbArticle; 
     }
  }
  else { $otbTag = "n".$otbGender.$otbNumber.$otbCase; }
  
  return $otbTag;
}

# Maps the info from BIN to a OTB tag
sub mapAdj {
  my($binTag,$binWord);
  $binTag = $_[0]; 
  $binWord = $_[1]; 
  # $binTag looks like: "FSB-KK-ÞFET","EVB-HK-ÞGFFT" 

  my $otbGender="";
  my $otbNumber="";
  my $otbCase="";
  my $otbDeclension="";
  my $otbComparison="";
  my($binGender, $binNumber, $binCase, $binDeclension, $binComparison);
  my $otbTag=""; # the return tag

  my @features = split /-/, $binTag;
  #if ($#features != 2) {die "Unexpected tag string: $binWord $binTag";}
  if ($#features != 2) { return $otbTag; }
  my $compDecl = $features[0];
  my $gender = $features[1];
  my $caseNum = $features[2];
  
  $binComparison = substr($compDecl,0,1);
  $binDeclension = substr($compDecl,1,2);
  $otbComparison = $mapComparison{$binComparison};
  $otbDeclension = $mapDeclension{$binDeclension};

  $otbGender = $mapGender{$gender};
  $binCase = substr($caseNum,0,2); # First two letters
  
  if (exists ($mapCase{$binCase})) {
	$otbCase = $mapCase{$binCase};
  	$binNumber = substr($caseNum,2,2);  
	$otbNumber = $mapNumber{$binNumber};
  }
  else {
    	$binCase = substr($caseNum,0,3); # First three letters
	$otbCase = $mapCase{$binCase};
  	$binNumber = substr($caseNum,3,2);  
	$otbNumber = $mapNumber{$binNumber};
  }
  # Special case: Change indeclinable adjectives
  if ((($otbDeclension eq "v") || $otbDeclension eq "s") && exists ($specMap{$binWord})) {
	$otbDeclension = "o";
	#print "Indeclinable: $binWord\n";
  }
  
  $otbTag = "l".$otbGender.$otbNumber.$otbCase.$otbDeclension.$otbComparison; 
  
  return $otbTag;
}

sub mapAdverb {
  my($binTag);
  $binTag = $_[0]; 
  # $binTag looks like: "ao_FST","ao_MST", "ao_EST" 

  my $otbComparison="";
  my $binComparison;
  my $otbTag=""; # the return tag

  my @features = split /_/, $binTag;
  if ($#features != 1) { return $otbTag; }
  my $comparison = $features[1];
  
  $binComparison = substr($comparison,0,1);
  $otbComparison = $mapComparison{$binComparison};

  # Special case: Remove the frumstig 
  if ($otbComparison eq "f") {
	$otbComparison = "";
  }
  
  $otbTag = "aa".$otbComparison; 
  return $otbTag;
}

# Maps the info from BIN to a OTB tag
sub mapVerb {
  my($binTag);
  $binTag = $_[0]; 
  # $binTag looks like: "GM-FH-NT-1P-ET", "LHÞT-SB-KK-NFET", 
  #	"GM-NH", "GM-BH-ET", "GM-SAGNB", "LH-NT"

  my $otbGender="";
  my $otbPerson="";
  my $otbNumber="";
  my $otbCase="";
  my $otbMood="";
  my $otbVoice="";
  my $otbTense="";
  my $binGender="";
  my $binPerson="";
  my $binNumber="";
  my $binCase="";
  my $binMood="";
  my $binVoice="";
  my $binTense="";
  my $otbTag="";	# the return tag
  my $pastParticiple=0; # flag

  my @features = split /-/, $binTag;
  my $numFeatures = $#features+1;
  if ($numFeatures <= 1) {die "Unexpected tag string: $binTag";}

  $binVoice = $features[0];
  $binMood = $features[1];
  # Special cases, lýsingarháttur nútíðar
  if ($binVoice eq "LH") {$binVoice="GM"; $binMood="LH"};

  
  switch ($numFeatures) {
	# Boðháttur: GM-BH-ET, GM-BH-FT, sleppa GM-BH-ST
	case 3	{	$binNumber = $features[2];	
		 	$binPerson = "2P";
		 	$binTense = "NT";
			if ($binNumber eq "ST") {return $otbTag;}
		}
	# Lýsingarháttur þátíðar: LHÞT-SB-KK-NFET",
	case 4	{
			my $declension = $features[1];	
			my $gender = $features[2];	
  			$otbGender = $mapGender{$gender};

			my $caseNum = $features[3];	# NFET
  			$binCase = substr($caseNum,0,2); # First two letters

			# Only interested in SB and nominative
			if ($declension ne "SB" ||  $binCase ne "NF") {	  
  				return $otbTag;
			}

  			if (exists ($mapCase{$binCase})) {
				$otbCase = $mapCase{$binCase};
  				$binNumber = substr($caseNum,2,2);  
  			}
  			else {
    				$binCase = substr($caseNum,0,3); # First three letters
				$otbCase = $mapCase{$binCase};
  				$binNumber = substr($caseNum,3,2);  
  			}
  			$pastParticiple=1; 
		} 	
	# Framsöguháttur/Viðtengingarháttur: "GM-FH-NT-1P-ET"
	case 5  { 	$binTense = $features[2];
			$binPerson = $features[3];
			$binNumber = $features[4];
		  
		}
	# OP-MM-VH-ÞT-2P-FT
	case 6  {	
			if ($binTag =~ m/^OP/) {
				$binVoice = $features[1];
				$binMood = $features[2];
				$binTense = $features[3];
				$binPerson = $features[4];
				$binNumber = $features[5];
			}
			else {return $otbTag;}	
		}
  } 
  $otbVoice = $mapVoice{$binVoice};
  $otbMood = $mapMood{$binMood};

  if (exists ($mapPerson{$binPerson})) 	{$otbPerson = $mapPerson{$binPerson};}
  if (exists ($mapNumber{$binNumber})) 	{$otbNumber = $mapNumber{$binNumber};}
  if (exists ($mapTense{$binTense})) 	{$otbTense = $mapTense{$binTense};}

  if (!$pastParticiple) {
	$otbTag = "s".$otbMood.$otbVoice.$otbPerson.$otbNumber.$otbTense; 
  }
  else
  {
	$otbTag = "s".$thorn."g".$otbGender.$otbNumber.$otbCase; 
  }
  return $otbTag;
}

sub readSpecFile {
  my($line,$count);
  $count=1;
  while ($line = <SPECFILE>) {
    if ($line !~ /^\s*$/)
    {
	chomp($line);
        $specMap{$line} = $count;
	$count++;
    }
  }
}

sub isProper
{
  my($theWord, $thePattern);
  $theWord = $_[0]; 

  if ($unicode) {
	$theWord = decode_utf8($theWord);
	$thePattern = decode_utf8("^[A-ZÁÉÍÓÝÚÞÆÖ]"); 
  }
  else { $thePattern = "^[A-ZÁÉÍÓÝÚÞÆÖ]";}

  if ($theWord =~ m/$thePattern/) {
	return 1;
  } 
  else { return 0; }
}

sub readArgs() {
  getopts('i:o:l:p:s:u', \%opts);

  print "\n";
  #print "$opts{u}\n";
  if ($opts{u}) {
	$unicode=1;
	print "Using utf8 encoding\n";
  }
  if (exists($opts{i})) {
        $infile = $opts{i};
        print ("Input file: $infile\n");
        open(INFILE, "$infile");
  }
  else {die "Missing input file\n";}

  if (exists($opts{o})) {
        $outfile = $opts{o};
        print ("Output file: $outfile\n");
        open(OUTFILE, ">$outfile");
  }
  else {die "Missing output file\n";}

  if (exists($opts{l})) {
        $linesLimit = $opts{l};
        print ("Lines requested: $linesLimit\n");
  }
  if (exists($opts{p})) {
        $otbTagPattern = $opts{p};
        print ("OTB tag pattern: $otbTagPattern\n");
	if ($unicode) { $otbTagPattern = decode_utf8($otbTagPattern); }
  }
  if (exists($opts{s})) {
        $specAdjFile = $opts{s};
        print ("Indeclinable adjectives: $specAdjFile\n");
	open(SPECFILE,"$specAdjFile");
	&readSpecFile; 
	close(SPECFILE);
  }
  print "\n";
}

# Aðalforritið byrjar hér.  
# Forritið les "dump" skrá úr BÍN á sniðinu:
# ORÐ;ID;ORÐFLOKKUR;HLUTI BÍN;ORÐMYND;GREININGARSTRENGUR
# og skilar úttaksskrá sem inniheldur orðmynd og greiningarstreng úr markamengi # OTB  
# Meðhöndlar eingöngu nafnorð, lýsingarorð og sagnorð

$delimeter=";"; # delimter between fields in the input
my @fields;	# the fields in the current line
my $binClass;	# The (word)class in the input file
my $binTag;	# The tag string from input file
my $binPart;	# "Hluti BIN" from input file
my $otbTagStr;	# The OTB tag string
$otbTagPattern=""; # Supplied by the user if only interested in specific OTB tags
$inputLines=0;  # The number of lines read from the input
$linesLimit=0;	# The number of lines the user want to be generated in the output
$matchedLines=0;  # The number of lines matching the input criteria 
$specAdjFile="";  # File listing indeclinable adjectives
#%specMap;	  # A hash containing the indeclinable adjectives
$unicode=0;	  # Flag indicating if the input file is utf8

# Arguments:
# -i <input file>, -o <output file>, -w <word class>, -l <lines>, 
# -f <BIN tag filter> -s <special file: obeygjanleg lysingarord>
# -u (if unicode input file)
# -lem (if get lemma)
&readArgs();


# Mapping between BIN and OTB
%mapGender = ("hk"=>"h", "kk"=>"k", "kvk"=>"v", "HK"=>"h", "KK"=>"k", "KVK"=>"v");
%mapNumber = ("ET"=>"e", "FT"=>"f");
if ($unicode) { 
	$tholfall = decode_utf8("ÞF");
	$thagufall = decode_utf8("ÞGF");
	$oou = decode_utf8("ö");
	$thorn = decode_utf8("þ");
	$capThorn = decode_utf8("Þ");
        $ornefni = decode_utf8("örn");
        $lond = decode_utf8("lönd");
        $gotur = decode_utf8("göt");
        $lhtht = decode_utf8("LHÞT");
	$thatid = decode_utf8("ÞT");
}
else  {
	$tholfall = "ÞF";
	$thagufall = "ÞGF";
	$oou = "ö";
	$thorn = "þ";
	$capThorn = "Þ";
        $ornefni = "örn";
        $lond = "lönd";
        $gotur = "göt";
	$lhtht = "LHÞT";
	$thatid = "ÞT";
}
%mapCase = ("NF"=>"n", $tholfall=>"o", $thagufall=>$thorn, "EF"=>"e");
%mapArticle = ("gr"=>"g");
%mapProperNoun = ("ism"=>"m", "fyr"=>"s", $ornefni=>$oou, $lond=>$oou, $gotur=>$oou);
%mapComparison = ("F"=>"f", M=>"m", E=>"e");
%mapDeclension = ("SB"=>"s", "VB"=>"v", "ST"=>"v");
%mapVoice = ("GM"=>"g", "MM"=>"m");
%mapMood = ("FH"=>"f", "VH"=>"v", "BH"=>"b", "NH"=>"n", "LH"=>"l", $lhtht=>$thorn, "SAGNB"=>"s", "SAGNB2"=>"s");
%mapPerson = ("1P"=>"1", "2P"=>"2", "3P"=>"3");
%mapTense = ("NT"=>"n", $thatid=>$thorn);


while ($line = <INFILE>) {
 if ($linesLimit && ($matchedLines >= $linesLimit)) { last;}
 $inputLines++;
 @fields = &tokenize($line, $delimeter);  # tokenize the line
 if ($#fields == 5) {		# if six fields found
    $lemma = $fields[0];
    $binClass = $fields[2];
    $binPart = $fields[3];
    $word = $fields[4];
    $binTag = $fields[5];
    if ($unicode) {
    	# Tell perl that the $binTag contains Unicode data in UTF-8 encoding.
    	$binTag = decode_utf8( $binTag );
    	$binPart = decode_utf8( $binPart );
    }
    $otbTagStr="";
	 
    if (($binClass eq "hk" || $binClass eq "kvk" || $binClass eq "kk") &&
	(!isProper($word) || $binPart =~ m/ism/  # Mannanöfn
			  || $binPart =~ m/fyr/ # fyrirtæki
			  || $binPart =~ m/$ornefni/ # örnefni
			  || $binPart =~ m/$lond/  # lönd
			  || $binPart =~ m/$gotur/ # götur
	)
       )
    {
  	$otbTagStr = &mapNoun($binClass,$binTag,$binPart);
	#print "$word: $otbTagStr\n"
    }
    elsif ($binClass eq "lo") {
  	$otbTagStr = &mapAdj($binTag,$word);
    }
    elsif ($binClass eq "so") {
  	$otbTagStr = &mapVerb($binTag);
    }
    elsif ($binClass eq "ao") {
  	$otbTagStr = &mapAdverb($binTag);
	#print "$word: $otbTagStr\n"
    }
    
    if ( ($otbTagStr ne "") && 
	(($otbTagPattern eq "") || ($otbTagStr =~ m/$otbTagPattern/))) {
    	if ($unicode) { 
		$otbTagStr = encode_utf8($otbTagStr);
	}
	#if ($getLemma) { print OUTFILE "$word $lemma $otbTagStr\n";}
	#else { print OUTFILE "$word $otbTagStr\n";}
	print OUTFILE "$word $lemma $otbTagStr\n";
     	$matchedLines++;
    }

    if ($inputLines % 100000 == 0) { 
    	print "Lines read: $inputLines - Matched lines: $matchedLines\n"; 
    }
  }
}
print "Lines read: $inputLines - Matched lines: $matchedLines\n"; 
close(INFILE);
close(OUTFILE);
