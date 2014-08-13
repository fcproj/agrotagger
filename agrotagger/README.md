This is tha Java code, together with some the bash scripts to run the agrotagger. Simply use the scripts in the "application" directory:

- *tagger.sh*: it needs 4 mandatory parameters: the path to a file with a list of URLs (or the output of a webcrawler), the path to the output directory where to store results, the type of the input file ("listURL" or "nutchOutput"), and the type of the output (currently only "rdfnt" is supported).  
Example: ``./tagger.sh data/sources/crawler_result.txt data/documents nutchOutput rdfnt``  
  
There is also the possibility to express, as optional parameter, the name of the output file. Currently, the output file is a tar.gz file, but you don't need to define the extension, the system will automatically add the suffix .tar.gz to the filename.  
Example: ``./tagger.sh data/sources/list_urls.txt data/documents listURL rdfnt myoutput``  
  
From v1.2.1, there is also the possibility to specify other two optional parameters: the name of a new AGROVOC SKOS file and the name of a new MAUI model. If names are not correct (i.e. the model and the voabulary are not in the application) the application stops. See the online documentation (https://github.com/agrisfao/agrotagger/wiki/How-to-use-an-updated-AGROVOC-thesaurus) to know how to build a new model.  
Example: ``./tagger.sh data/sources/crawler_result.txt data/documents listURL rdfnt myoutput new_agrovoc_name my_model``  
  
In this last scenario, the fifth parameter (output_filename) can be null.  
Example: ``./tagger.sh data/sources/crawler_result.txt data/documents listURL rdfnt null new_agrovoc_name my_model``

- *taggerDir.sh*: works as the previous one, but the first parameter is the path to a directory containing some input files.  
Example: ``./taggerDir.sh ../work/splitted ../work/output nutchOutput rdfnt``  

**Requirements**: the application needs at least Java 6 to work properly.
