This is the Unix application to run a custimized version of Apache Nutch Web Crawler (http://nutch.apache.org/). Simply use the scripts in the `application` directory:

- *crawler_exec.sh*: to execute the crawler. It requires three parameters: 
  * the depth of the crawling (i.e. the link depth from the root page that should be crawled)
  * an output directory path where to store discovered URLs
  * the path to the directory with the files containing URLs from where starting the crawling. This directory can contain any number of files. Each file contains a URL in each line. For instance, a file name can be "input.txt".

> As an example: ``./crawler_exec.sh 5 ../work/output ../work/urls/`` (note: directories must exist in the filesystem)
	
- *start.sh*: a script that demonizes *crawler_exec.sh*, in order to allow users to close the command line window and let the application running. It needs to be configured to change paths to input and output directories.

The output file of this application can be used as input for [AgroTagger](https://github.com/fcproj/agrotagger/tree/master/agrotagger).

There could be the need to run the Linux utility `dos2unix` in case of error `bad interpreter: No such file or directory`. Usually, `.sh` files and files in the directory `bin` may need this conversion.
