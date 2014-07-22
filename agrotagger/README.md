This is the Unix application to run the agrotagger. Simply use the scripts in the "application" directory:

- *tagger.sh*: takes as parameters the path of a file with a list of URLs (or the output of a webcrawler), the path to the output directory where to store results, the type of the input file ("listURL" or "nutchOutput"), and the type of the output (currently only "rdfnt" is supported).  
Example: ``./tagger.sh data/sources/crawler_result.txt.0 data/documents nutchOutput rdfnt``

- *taggerDir.sh*: works as the previous one, but the first parameter is the path to a directory containing some input files.  
Example: ``./taggerDir.sh ../work/splitted ../work/output nutchOutput rdfnt``
	
- *start.sh*: a script to demonize *taggerDir.sh*, in order to allow you to close the command line window and let the application running. It needs to be configured to change locations.
