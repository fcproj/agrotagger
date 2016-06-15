This is the Unix application to run a custimized version of Apache Nutch Web Crawler (http://nutch.apache.org/). Simply use the scripts in the "application" directory:

- *crawler_exec.sh*: to execute the crawler. It requires three parameters: 
  * the depth of the crawling (i.e. the link depth from the root page that should be crawled)
  * an output directory path where to store discovered URLs
  * the path to the directory with the files containing URLs from where starting the crawling. This directory can contain any number of files. Each file contains a URL in each line. For instance, a file name can be "input.txt".

As an example: ``./crawler_exec.sh 5 ../work/output ../work/urls/``
	
- *start.sh*: a script to demonize *crawler_exec.sh*, in order to allow you to close the command line window and let the application running. It needs to be configured to change input paths.

The output file of this application can be used as input for AgroTagger.

There could be the need to run the Linux utility `dos2unix` in case of error `bad interpreter: No such file or directory`. Usually, `.sh` files and files in the directory `bin` may need this conversion.
