agrotagger
==========

This application allows to index web documents, creating RDF triples that link a web URL to some URIs of a SKOS thesaurus. Currently, the tagging application is based on MAUI (https://code.google.com/p/maui-indexer/) and the used thesaurus is AGROVOC (http://aims.fao.org/standards/agrovoc/about).

Web documents to be indexed can be passed to the application through a file containing a list of URLs, or through a file containing the output of an Apache Nutch Web Crawler.

The application is a Java application based on three sub-application to be executed sequentially. Some bash scripts are provided to execute the application on a Unix environment.

Some information about the folders in the code:

- SourceCode: it's the Java code of the agrotagger
- Agrotagger: the Unix application to run the agrotagger. Simply decompress the ZIP file into a directory in your filesystem and use the scripts in the "application" directory. 
- WebCrawler:  the Unix application to run a custimized version of Apache Nutch Web Crawler (http://nutch.apache.org/). Simply decompress the TAR.GZ file into a directory in your filesystem and use the scripts in the "application" directory.
