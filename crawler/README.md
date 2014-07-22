This is the Unix application to run a custimized version of Apache Nutch Web Crawler (http://nutch.apache.org/). Simply use the scripts in the "application" directory:

- *crawler_exec.sh*: to execute the crawler. It requires three parameters: the depth of the crawling, an output directory path where to store discovered URLs, and the path to the directory with the files containing URLs from where starting the crawling.  
As an example: ``./crawler_exec.sh 5 ../work/output ../work/urls/``
	
- *start.sh*: a script to demonize *crawler_exec.sh*, in order to allow you to close the command line window and let the application running. It needs to be configured to change input paths.

The output file of this application can be used as input for the agrotagger.
