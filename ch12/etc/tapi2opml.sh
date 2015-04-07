#
_CP=.:.
_CP=${_CP}:./lib/ch12.jar
_CP=${_CP}:./lib/jaxen-full.jar
_CP=${_CP}:./lib/jdom.jar
_CP=${_CP}:./lib/dom4j-1.4.jar
java -classpath ${_CP} com.manning.blogapps.chapter12.Tapi2opml $1 $2 $3
