This is the Unix application to run the agrotagger. Simply decompress the ZIP file into a directory in your filesystem and use the scripts in the "application" directory. 
There are some scripts:

- tagger.sh takes as parameters a file with a list of URLs (or the output of a webcrawler), the path to the output directory where to store results, the type of the input file ("listURL" or "nutchOutput"), and the type of the output ("rdfnt"). 
Example: ./tagger.sh data/sources/crawler_result.txt.0 data/documents nutchOutput rdfnt

- taggerDir.sh: as the previous one, but the first parameter is the path to a directory containing some input files. 
Example: ./taggerDir.sh ../work/splitted ../work/output nutchOutput rdfnt
	
- start.sh: a script to demonize taggerDir.sh, in order to allow you to close the command line window and let the application running. It needs to be configured to change locations.
