This is the Unix application to run the agrotagger. Simply use the scripts in the "application" directory:

- *tagger.sh*: takes as parameters the path of a file with a list of URLs (or the output of a webcrawler), the path to the output directory where to store results, the type of the input file ("listURL" or "nutchOutput"), and the type of the output (currently only "rdfnt" is supported).  
Example: ``./tagger.sh data/sources/crawler_result.txt data/documents nutchOutput rdfnt``  
There is also the possibility to express, as optional parameter, the name of the output file. Currently, the output file is a tar.gz file, but you don't need to define the extension, the system will automatically add the suffix .tar.gz to the filename.  
Example: ``./tagger.sh data/sources/list_urls.txt data/documents listURL rdfnt myoutput``  

- *taggerDir.sh*: works as the previous one, but the first parameter is the path to a directory containing some input files.  
Example: ``./taggerDir.sh ../work/splitted ../work/output nutchOutput rdfnt``  

**Requirements**: the application needs at least Java 6 to work properly.
