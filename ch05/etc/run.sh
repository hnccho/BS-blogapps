# Script for running Chapter 5 examples
#
# First argument (required): fully-qualified name of class to be run
# Up to five additional arguments (optional): as needed 

_cp_=${_cp_}:.
_cp_=${_cp_}:./lib/ch05.jar
_cp_=${_cp_}:./lib/rome-0.8.jar
_cp_=${_cp_}:./lib/jdom.jar
_cp_=${_cp_}:./lib/xml-apis.jar
_cp_=${_cp_}:./lib/xercesImpl.jar
_cp_=${_cp_}:./lib/commons-feedparser-0.5.0-RC1.jar
_cp_=${_cp_}:./lib/saxpath.jar
_cp_=${_cp_}:./lib/jaxen-full.jar
_cp_=${_cp_}:./lib/log4j-1.2.4.jar

java -classpath ${_cp_} $1 $2 $3 $4 $5
