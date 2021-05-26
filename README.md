DWSlib [![Build Status](https://travis-ci.org/dwslab/dwslib.svg?branch=master)](https://travis-ci.org/dwslab/dwslib)
======



This library contains some functionality commonly used at the
[Data and Web Science Research Group](http://dws.informatik.uni-mannheim.de), University of Mannheim.

## Usage

If you use Maven for building your project, just add

```
<dependency>
  <groupId>de.uni-mannheim.informatik.dws.dwslab</groupId>
  <artifactId>dwslib</artifactId>
  <version>2.0.0</version>
</dependency>
```

to your pom.xml. 


Versioning of the dwslib is done according to the Semantic Versioning guidelines (http://semver.org/). This means that it is safe to include new releases which only changed in the MINOR and PATCH component without modifying your code. However, new major versions are allowed to break backward compatibility.

## Licence

<a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/"><img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by-nc/4.0/88x31.png" /></a><br /><span xmlns:dct="http://purl.org/dc/terms/" property="dct:title">dwslib</span> is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/">Creative Commons Attribution-NonCommercial 4.0 International License</a>.

## Content


### Framework
* Processor: Framework using multiple parallel threads to process a list of objects. The filling of the objectsToProcess and the actual processing has to be implemented. 

### Util
* DomainUtil: Collection of often used functionalities to process URLs (e.g. get PLD, Domain, Compress based on CC)
* InputUtil: Collection of often used functionalities to read input files (e.g. get all files in a directory, get input stream for file)
* FileUtil: Collection of often used functionalities to handle files.
* BufferedChunkingWriter: A BufferedWriter (Using GZIPOutputStream) taking care of chunking the output in multiple files.

### Virtuoso
* LoadURI: URI Shortener (both ways) based on prefix.cc list
* Query: Virtuoso Sparql Query Processor based on direct JDBC driver (not http sparql endpoint -> no 1mio line limit)

### CLI

For usage information call the following classes from command-line without arguments or with the -h option.
* QueryCLI: Command-line tool for querying Virtuoso triple stores via JDBC and writing the result to TSV files
* SplitGZIPFile: Command-line for splitting large GZIP files into smaller GZIP files of given size

### Others
* Collection: collection helper methods (sortHashMapByValue, ...)
* Counter: Counter for abitrary objects (python counter like)
* MyFileReader: utf-8 file reader line-by-line; utf-8 tab (or any other character) separated file reader
