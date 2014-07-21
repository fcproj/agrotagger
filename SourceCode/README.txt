============================================================================================================================================
============================================================================================================================================
============================================================================================================================================
PURPOSE (v3.5):
The purpose of this application is to index some documents with the English Agrovoc Thesaurus. 
Starting from a list of URLs of TXT, HTML or PDF documents, the application generates an RDF file (NTRIPLES, DOC_URL dct:subject AGROV_URI). 
The application is composed of three sub-applications that need to be executed sequentially.
The output of the application is a ZIP file, stored in the output location defined by the user as "output directory without the ending slash"

NB. In the future, the output will be also a TXT file (containing the mapping DOCUMENT_URL=LIST_OF_EXTRACTED_KEYWORDS)

============================================================================================================================================
============================================================================================================================================
============================================================================================================================================
APPLICATIONS:

The package org.fao.oekc.autotagger.main contains the three main applications you can use to automatic tag documents.
These applications should be exectuted sequentially, since the output of one application is needed as input of another one.
At the end of this file, an example of usage is provided.

- DownloadFiles: Starting from a text file containing the list of files to be downloaded (NewLine separator), downloads these 
  files in a specific directory and converts the file to TXT (supported files: PDF, TXT, HTML). 
  The constructor requires 3 parameters:
	1. input text file location (e.g. data/sources/crawler_result.txt)
	2. the output directory without the ending slash (e.g. data/documents). The output directory will contain the output Zip file. The 
	   application checks if this directory exists: if not, it creates the directory; if yes, it does NOT delete the content.
	   This directory is alse used as temporary container for downloaded files and RDF generation. But, after the execution of 
	   ProduceMappingTable, only output ZIP files will be there.
	3. download mode; a mode equals to "listURL" means that the source file contains a list of URLs, one per line; a mode equals to 
	  "nutchOutput" means that the source file is the output file of a Nutch Crawler (you can see the two samples in data/sources).
  The application generates a mapping file DOC_URL=DOC_NAME, located in the same directory of the input text file (param. 1), with the same 
  name and a suffix "_mapping". This file will be deleted after the execution of ProduceMappingTable.

- MauiAutoTaggerKey: Given the path with text files downloaded by DownloadFiles, it generates the .key files with agrovoc en tagging. 
  Thus, the parameter should be the output directory of the DownloadFiles application (step 2), where there are downloaded files.

- ProduceMappingTable: this application reads the outputs of MauiAutoTaggerKey and generates an output file, which is a ZIP file of 
  NTRIPLES RDF files. The application requires 3 command line parameters:
	1. input text file location (e.g. data/sources/crawler_result.txt), the same as DownloadFiles (step 1)
	2. the output directory without the ending slash, the same as DownloadFiles (step 2)
	3. the output format {rdfnt}. In the future it will support also txt mode.
  It is important to run this application after the DownloadFiles ones, that generate a mapping file DOC_URL=DOC_NAME. 
  The application needs to find in the output directory the .key files produced by MAUI in the application MauiAutoTaggerKey.
  
  The output mode "rdfnt" creates NTRIPLES: the predicate is dcterms:subject, the subject is the document UTL, the object is an AGROVOC URI.
  Currently, an output file can contain only 100.000 triples: more triples will cause more output files, zipped in a single file.

  At the end, the output directory contains only the output ZIP file, and all temporary files (downloaded files, .key, _mapping) are deleted.

============================================================================================================================================
============================================================================================================================================
============================================================================================================================================
DIRECTORIES:
Mandatory:
- data/stopwords: to use MAUI
- data/vocabularies: vocabularies to use MAUI and mapping AGROVOC URIs to English strings
- fao780 : MAUI model trained by 780 FAO bibliographic documents
Optional:
- data/documents: it can be used as output file by the user, but other directories can be specified
- data/sources: files with URLs list and mapping files URLs->downloaded files names (two samples are provided)
- data/test: for testing purposes (can be removed)

============================================================================================================================================
============================================================================================================================================
============================================================================================================================================
NOTE: 10 GB of PDF corresponds to ~400 MB of TXT

============================================================================================================================================
============================================================================================================================================
============================================================================================================================================
EXAMPLE OF USAGE:
- java org.fao.oekc.autotagger.main.DownloadFiles data/sources/crawler_result.txt.0 data/documents nutchOutput
	{Downloads PDF, HTML, TXT files and convert them to TXT in the directory data/documents. Produces a mapping file DOC_URL=DOC_NAME}
- java org.fao.oekc.autotagger.main.MauiAutoTaggerKey data/documents/docKeys
	{Creates .key files but needs txt files downloaded by the previous application in the directory data/documents/docKeys}
- java org.fao.oekc.autotagger.main.ProduceMappingTable data/sources/crawler_result.txt.0 data/documents rdfnt
	{Produce the output, an RDF N3 file in the output directory (in the example data/documents)}