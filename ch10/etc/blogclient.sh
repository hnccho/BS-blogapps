# Script for running Chapter 10 Atom Protocol-based BlogClient
#
# First argument (required): fully-qualified name of class to be run
# Up to five additional arguments (optional): as needed 

_cp_=${_cp_}:.
_cp_=${_cp_}:./lib/ch10.jar
_cp_=${_cp_}:./lib/commons-codec-1.3.jar
_cp_=${_cp_}:./lib/commons-httpclient-2.0.2.jar
_cp_=${_cp_}:./lib/commons-lang-2.0.jar
_cp_=${_cp_}:./lib/commons-logging.jar
_cp_=${_cp_}:./lib/jdom.jar
_cp_=${_cp_}:./lib/rome-0.8.jar
_cp_=${_cp_}:./lib/xmlrpc-1.2-b1.jar

java -classpath ${_cp_} com.manning.blogapps.chapter10.blogclientui.LoginFrame $1 $2 $3 $4 $5
