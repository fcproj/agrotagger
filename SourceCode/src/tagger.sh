#!/bin/sh
# Read a single input file
# Parameters: input_file_path; output_directory; download_mode
# Usage: ./tagger.sh data/sources/crawler_result.txt.0 data/documents nutchOutput rdfnt
# java -classpath ".:../lib/*" org.fao.oekc.autotagger.main.DownloadFiles ../work/test/crawler_result_23.txt ../work/output nutchOutput
# java -classpath ".:../lib/*" org.fao.oekc.autotagger.main.MauiAutoTaggerKey ../work/output
# java -classpath ".:../lib/*" org.fao.oekc.autotagger.main.ProduceMappingTable ../work/test/crawler_result_23.txt ../work/output rdfnt

if [ $# -ne 4 ]  # number of arguments passed to script
then
	echo "****************************************************"
    echo "You must specify the path to the TXT file with the list of URLs, the output directory, the download mode ("listURL" means that the source file contains a list of URLs, "nutchOutput" means that the source file is the output file of a Nutch Crawler), and the output type (rdfnt)"
    echo "Example of usage: ./tagger.sh data/sources/crawler_result.txt.0 data/documents nutchOutput rdfnt"
    echo "****************************************************"
else
    java -classpath ".:../lib/*" -Xss64M -Xmx2048M org.fao.oekc.autotagger.main.DownloadFiles $1 $2 $3
    java -classpath ".:../lib/*" -Xss64M -Xmx2048M org.fao.oekc.autotagger.main.MauiAutoTaggerKey $2
    java -classpath ".:../lib/*" -Xss64M -Xmx2048M org.fao.oekc.autotagger.main.ProduceMappingTable $1 $2 $4
fi

exit



