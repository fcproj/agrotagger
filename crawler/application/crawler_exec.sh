#!/bin/sh

thisfile=`which $0`
thisdir=`dirname $thisfile`
OPT_JARS="./lib/*.jar"
CP=""
for thing in $OPT_JARS
do
    if [ -f $thing ]; then       
        CP="${CP}:$thing"
    else if [ -d $thing ]; then  
        CP="$CP:$thing"
     fi
    fi
done
CP="$CP:."

if [ $# -ne 3 ]  # number of arguments passed to script
then
    echo "You need to specify the depth of the crawler, the output directory, and the directory with input urls"
else

    # execute nutch
    # parameters: depth, output_dir, input dir
    echo " --- Crawler depth \"$1\" - output dir \"$2\" - input files in \"$3\""
    ./bin/nutch crawl $3 -depth $1 -dir $2/crawl
    
    # READSEG
    echo " --- Merging segments"
    ./bin/nutch mergesegs $2/merge_segments -dir $2/crawl/segments/
    echo " --- Dump Outlinks"
    ./bin/nutch readseg -dump $2/merge_segments/* $2/dump-outlinks
    echo " --- Catting"
    cat $2/dump-outlinks/dump |egrep -a 'URL|toUrl' > $2/crawler_result.txt
    echo " --- Tar"
    tar cvfz $2/crawler_result.tar.gz $2/crawler_result.txt

    echo " --- Cleaning"
    rm -Rf $2/crawl
    rm -Rf $2/dump-outlinks
    rm -Rf $2/merge_segments
    rm -Rf $2/crawler_result.txt

fi

exit
