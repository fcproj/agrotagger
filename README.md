This Github project contains two applications, which are stricly dependant:

- *agrotagger*: the Unix application to run the AgroTagger.
- *crawler*:  the Unix application to run a custimized version of Apache Nutch web crawler (http://nutch.apache.org/). This application can produce an input file for the AgroTagger.

AgroTagger
==========

AgroTagger allows to index web documents identifying main topics and creating RDF triples that link a Web URL to some AGROVOC URIs (http://aims.fao.org/standards/agrovoc/about). Currently, the tagging application is based on MAUI (https://code.google.com/p/maui-indexer/) and it uses as default AGROVOC in English. 

Web documents to be indexed can be passed to the application through a file containing a list of URLs, or through a file containing the output of an Apache Nutch web crawler.

The output of AgroTagger is an RDF NTRIPLE file (zipped in a tar.gz archive), which mainly contains the "dcterms:subject" predicate. The user can also choose a text file as output format: it will contain AGROVOC terms and not URIs. Other predicates can be activated using boolean flags. An example of the output RDF file is available in the [RDF] (https://github.com/agrisfao/agrotagger/wiki/Example-of-AgroTagger-output) page.

Web Crawler
==========
This is a customized version of Apache Nutch (http://nutch.apache.org/), a highly extensible, scalable and configurable open-source web crawler.

Installation
==========

**Requirements**: *agrotagger* and the web *crawler* need at least Java 6 to work properly.

The installation process simply requires to download the code. In fact, AgroTagger is a Java application based on three sub-applications to be executed sequentially. Some bash scripts are provided to execute AgroTagger on a Unix environment. Information about Java sub-applications and examples of usage are available in the [Java Applications](https://github.com/agrisfao/agrotagger/wiki/Java-Applications) page. Similarly, the web crawler is a Java application that can be executed also with bash scripts provided with the code.


  
