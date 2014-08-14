agrotagger
==========

The AgroTagger allows to index Web documents identifying main topics and creating RDF triples that link a Web URL to some AGROVOC URIs (http://aims.fao.org/standards/agrovoc/about). Currently, the tagging application is based on MAUI (https://code.google.com/p/maui-indexer/) and it uses as default AGROVOC in English. 

Web documents to be indexed can be passed to the application through a file containing a list of URLs, or through a file containing the output of an Apache Nutch Web Crawler.

The application is a Java application based on three sub-application to be executed sequentially. Some bash scripts are provided to execute the application on a Unix environment. Information about Java sub-applications and examples of usage are available in the [Java Applications](https://github.com/agrisfao/agrotagger/wiki/Java-Applications) page.

Some information about the folders in the code:

- *agrotagger*: the Unix application to run the AgroTagger.
- *crawler*:  the Unix application to run a custimized version of Apache Nutch Web Crawler (http://nutch.apache.org/). This application can produce an input file for the AgroTagger.
  
**Requirements**: the *agrotagger* application needs at least Java 6 to work properly.
