#!/bin/sh
# Read a single input file. Four mandatory parameters. Three optional parameters.
#
# ###########################################################################################################################
# Mandatory parameters: input_file_path; output_directory; download_mode; output_type
# Usage: ./tagger.sh data/sources/crawler_result.txt data/documents nutchOutput rdfnt
#  java -classpath ".:../lib/*" org.fao.oekc.autotagger.main.DownloadFiles ../work/test/crawler_result.txt ../work/output nutchOutput
#  java -classpath ".:../lib/*" org.fao.oekc.autotagger.main.MauiAutoTaggerKey ../work/output
#  java -classpath ".:../lib/*" org.fao.oekc.autotagger.main.ProduceMappingTable ../work/test/crawler_result.txt ../work/output rdfnt
#
# ###########################################################################################################################
# Optional fifth parameter: output_filename  (even without file extension)
# Usage: ./tagger.sh data/sources/crawler_result.txt data/documents listURL rdfnt myoutput
#  java -classpath ".:../lib/*" org.fao.oekc.autotagger.main.DownloadFiles ../work/test/crawler_result.txt ../work/output listURL
#  java -classpath ".:../lib/*" org.fao.oekc.autotagger.main.MauiAutoTaggerKey ../work/output
#  java -classpath ".:../lib/*" org.fao.oekc.autotagger.main.ProduceMappingTable ../work/test/crawler_result.txt ../work/output rdfnt myoutput.tar.gz
#
# ###########################################################################################################################
# Optional sixth and seventh parameters: new_agrovoc_name; model_name (the fifth parameter "output_filename" can be null)
# new_agrovoc_name and model_name are used to tell MAUI which vocabulary has to be used to index resources. If names are not correct (i.e. the model and the voabulary are not in the application) the application stops.
# See the online documentation to know how to build a new model 
# Usage: ./tagger.sh data/sources/crawler_result.txt data/documents listURL rdfnt myoutput new_agrovoc_name my_model
#  java -classpath ".:../lib/*" org.fao.oekc.autotagger.main.DownloadFiles ../work/test/crawler_result.txt ../work/output listURL
#  java -classpath ".:../lib/*" org.fao.oekc.autotagger.main.MauiAutoTaggerKey ../work/output new_agrovoc_name my_model
#  java -classpath ".:../lib/*" org.fao.oekc.autotagger.main.ProduceMappingTable ../work/test/crawler_result.txt ../work/output rdfnt myoutput.tar.gz
#
# In this scenario, the fifth parameter output_filename can be null.
# Usage: ./tagger.sh data/sources/crawler_result.txt data/documents listURL rdfnt null new_agrovoc_name my_model
#

if [ $# -lt 4 ]  # number of arguments passed to script
then
    echo "****************************************************"
    echo "You must specify the path to the TXT file with the list of URLs, the output directory, the download mode ("listURL" means that the source file contains a list of URLs, "nutchOutput" means that the source file is the output file of a Nutch Crawler), and the output type (rdfnt)"
    echo "Example of usage: ./tagger.sh data/sources/crawler_result.txt.0 data/documents nutchOutput rdfnt"
    echo "****************************************************"
else
	# In case of custom model and vocabulary, check parameter
	if [ $# -eq 7 ]
    then
    	java -classpath ".:../lib/*" -Xss64M -Xmx2048M org.fao.oekc.autotagger.support.CheckParameters $6 $7
    	exitval=$?
		if [ $exitval -ne "0" ] ; then
		    echo "vocabulary or model names are not correct!"
			exit
		fi
    fi

	# Download the files
    java -classpath ".:../lib/*" -Xss64M -Xmx2048M org.fao.oekc.autotagger.main.DownloadFiles $1 $2 $3
    
    # Run MAUI to generate keywords
    if [ $# -eq 7 ]
    then
    	java -classpath ".:../lib/*" -Xss64M -Xmx2048M org.fao.oekc.autotagger.main.MauiAutoTaggerKey $2 $6 $7
    else
    	java -classpath ".:../lib/*" -Xss64M -Xmx2048M org.fao.oekc.autotagger.main.MauiAutoTaggerKey $2
    fi
    
    # Finalize the output
    if [ $# -ge 5 ]
    then
    	java -classpath ".:../lib/*" -Xss64M -Xmx2048M org.fao.oekc.autotagger.main.ProduceMappingTable $1 $2 $4 $5
    else
    	java -classpath ".:../lib/*" -Xss64M -Xmx2048M org.fao.oekc.autotagger.main.ProduceMappingTable $1 $2 $4
    fi
fi

exit



