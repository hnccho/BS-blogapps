
_cp_=./lib/xmlrpc-1.2-b1.jar
_cp_=${_cp_}:./lib/commons-lang-2.0.jar
_cp_=${_cp_}:./lib/ch08.jar
_cp_=${_cp_}:./lib/grabber.jar
_cp_=${_cp_}:.
java -classpath ${_cp_} com.manning.blogapps.extra02.Grabber $1 $2 $3
