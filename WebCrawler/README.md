This is the Unix application to run a custimized version of Apache Nutch Web Crawler (http://nutch.apache.org/). Simply decompress the TAR.GZ file into a directory in your filesystem and use the scripts in the "application" directory. 
There are some scripts:

- crawler_exec.sh: to execute the crawler. It requires the depth, an output directory, and the directory with the files containing URLs from where starting the crawling
	./crawler_exec.sh 5 ../work/output ../work/urls/
	
- start.sh: a script to demonize crawler_exec.sh, in order to allow you to close the command line window and let the application running. It needs to be configured to change locations.

The output of this application can be used as input for the agrotagger.
