# Script for running Chapter 7 examples
#
# First argument (required): fully-qualified name of class to be run
# Up to five additional arguments (optional): as needed 

_cp_=${_cp_}:.
_cp_=${_cp_}:./lib/ch07.jar
_cp_=${_cp_}:./lib/rome-0.8.jar
_cp_=${_cp_}:./lib/rome-fetcher-0.8.jar
_cp_=${_cp_}:./lib/content-0.4.jar
_cp_=${_cp_}:./lib/jdom.jar

java -classpath ${_cp_} $1 $2 $3 $4 $5
