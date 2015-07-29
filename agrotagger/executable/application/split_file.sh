#!/bin/sh
# Split an input text file in many files, given the number of lines in the output files
# Parameters: inumber_of_lines_output; input_file_path
# Output: a set of splitted files, in a directory "splitted", at the same level of the source file
# Usage:  ./split_file.sh 25000 ../work/crawler_result.txt

if [ $# -ne 2 ]  # number of arguments passed to script
then
	echo "****************************************************"
    echo "You must specify the maximum number of lines in the splitted files, and the full path to the input file
    echo "Example of usage: ./split_file.sh 50000 data/sources/crawler_result.txt
    echo "****************************************************"
else
    java -classpath ".:../lib/*" -Xss64M -Xmx2048M org.fao.oekc.autotagger.support.CrawlerFileSplit $1 $2
fi

exit