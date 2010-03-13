# rule_hand_written_utf8.txt contains handwritten lemmatisation rules
# rule_database_utf8.txt contains automatically generated lemmatisation rules
# rule_combined_utf8.txt combines these two files
cat rule_hand_written_utf8.txt rule_database_utf8.txt > rule_combined_utf8.txt
#cat rule_database_utf8.txt rule_hand_written_utf8.txt > rule_combined_utf8.txt
gzip rule_combined_utf8.txt
mv rule_combined_utf8.txt.gz rule_database_utf8.dat
