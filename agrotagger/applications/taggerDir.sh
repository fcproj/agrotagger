#!/bin/sh
# Read a directory with many input files
#
# Parameters: input_directory_path; output_directory; download_mode
# Usage: ./tagger.sh data/sources/ data/documents nutchOutput rdfnt
#
# Optional parameter: output_filename (even without file extension)
# Usage: ./tagger.sh data/sources/ data/documents listURL rdfnt myoutput

if [ $# -lt 4 ]  # number of arguments passed to script
then
    echo "****************************************************"
    echo "You must specify the path to the directory with a set of TXT files with the list of URLs, the output directory, the download mode ("listURL" means that the source file contains a list of URLs, "nutchOutput" means that the source file is the output file of a Nutch Crawler), and the output mode (rdfnt)"
    echo "Example of usage: ./tagger.sh data/sources/ data/documents nutchOutput rdfnt"
    echo "****************************************************"
else
    # scan internal files and run the agrotagger
    LIST=`find $1 -type f` 
    # scan files
    for file in $LIST; do
	echo $file
    	java -classpath ".:../lib/*" -Xss64M -Xmx2048M org.fao.oekc.autotagger.main.DownloadFiles $file $2 $3
    	java -classpath ".:../lib/*" -Xss64M -Xmx2048M org.fao.oekc.autotagger.main.MauiAutoTaggerKey $2
    	if [ $# -eq 5 ]
    	then
    		java -classpath ".:../lib/*" -Xss64M -Xmx2048M org.fao.oekc.autotagger.main.ProduceMappingTable $file $2 $4 $5
   		else
    		java -classpath ".:../lib/*" -Xss64M -Xmx2048M org.fao.oekc.autotagger.main.ProduceMappingTable $file $2 $4
    	fi
    done
fi

exit



