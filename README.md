dwslib
======


CONTENT
=======

Framework
* Processor: Framework using multiple parallel threads to process a list of objects. The filling of the objectsToProcess and the actual processing has to be implemented. 

Util
* DomainUtil: Collection of often used functionalities to process URLs (e.g. get PLD, Domain, Compress based on CC)
* InputUtil: Collection of often used functionalities to read input files (e.g. get all files in a directory, get input stream for file)
* Tuple:

Virtuoso:
* LoadURI: URI Shortener (both ways) based on prefix.cc list
* Query: Virtuoso Sparql Query Processor based on direct JDBC driver (not http sparql endpoint -> no 1mio line limit)

Others:
* Collection: collection helper methods (sortHashMapByValue, ...)
* Counter: Counter for abitrary objects (python counter like)
* MyFileReader: utf-8 file reader line-by-line; utf-8 tab (or any other character) separated file reader
