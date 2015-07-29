#!/bin/sh
# Read a directory with many input files. Four mandatory parameters. Two optional parameters.
# The boolean flag to avoid the extraction of titles (DownloadFiles) is no considered, but it is set to true by default, so titles are always extracted by this script
#
# Mandatory parameters: input_directory_path; output_directory; download_mode; output_type
# Usage: ./taggerDir.sh data/sources/ data/documents nutchOutput rdfnt
#
# Optional fifth and sixth parameters: new_agrovoc_name; model_name
# Usage: ./taggerDir.sh data/sources/ data/documents listURL myoutput new_agrovoc_name my_model
# new_agrovoc_name and model_name are used to tell MAUI which vocabulary has to be used to index resources.
#  java -classpath ".:../lib/*" org.fao.oekc.autotagger.main.DownloadFiles ../work/test/crawler_result.txt ../work/output listURL
#  java -classpath ".:../lib/*" org.fao.oekc.autotagger.main.MauiAutoTaggerKey ../work/output new_agrovoc_name my_model
#  java -classpath ".:../lib/*" org.fao.oekc.autotagger.main.ProduceMappingTable ../work/test/crawler_result.txt ../work/output rdfnt
#

if [ $# -lt 4 ]  # number of arguments passed to script
then
    echo "****************************************************"
    echo "You must specify the path to the directory with a set of TXT files with the list of URLs, the output directory, the download mode ("listURL" means that the source file contains a list of URLs, "nutchOutput" means that the source file is the output file of a Nutch Crawler), and the output mode (rdfnt)"
    echo "Example of usage: ./tagger.sh data/sources/ data/documents nutchOutput rdfnt"
    echo "****************************************************"
else
	# In case of custom model and vocabulary, check parameter
	if [ $# -eq 6 ]
    then
    	java -classpath ".:../lib/*" -Xss64M -Xmx2048M org.fao.oekc.autotagger.support.CheckParameters $5 $6
    	exitval=$?
		if [ $exitval -ne "0" ] ; then
		    echo "vocabulary or model names are not correct!"
			exit
		fi
    fi

    # scan internal files and run the agrotagger
    LIST=`find $1 -type f` 
    
    # scan files
    for file in $LIST; do
		echo $file
		
		# Download the files
    	java -classpath ".:../lib/*" -Xss64M -Xmx2048M org.fao.oekc.autotagger.main.DownloadFiles $file $2 $3
    	
    	# Run MAUI to generate keywords
    	if [ $# -eq 6 ]
    	then
    		java -classpath ".:../lib/*" -Xss64M -Xmx2048M org.fao.oekc.autotagger.main.MauiAutoTaggerKey $2 $5 $6
   		else
    		java -classpath ".:../lib/*" -Xss64M -Xmx2048M org.fao.oekc.autotagger.main.MauiAutoTaggerKey $2
    	fi
    	
    	# Finalize the output
    	java -classpath ".:../lib/*" -Xss64M -Xmx2048M org.fao.oekc.autotagger.main.ProduceMappingTable $file $2 $4
    done
fi

exit
