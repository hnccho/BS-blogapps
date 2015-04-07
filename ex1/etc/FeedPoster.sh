
_cp_=./lib/mail.jar
_cp_=${_cp_}:./lib/activation.jar
_cp_=${_cp_}:./lib/jdom.jar
_cp_=${_cp_}:./lib/dom4j-1.4.jar
_cp_=${_cp_}:./lib/jaxen-full.jar
_cp_=${_cp_}:./lib/junit-3.8.1.jar
_cp_=${_cp_}:./lib/xmlrpc-1.2-b1.jar
_cp_=${_cp_}:./lib/ch05.jar
_cp_=${_cp_}:./lib/ch08.jar
_cp_=${_cp_}:./lib/FeedPoster.jar
java -classpath ${_cp_} com.manning.blogapps.extra01.FeedPoster $1 $2 $3
