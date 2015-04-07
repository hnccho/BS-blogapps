@echo off
set _CP=.
set _CP=%_CP%;.\lib\ch12.jar
set _CP=%_CP%;.\lib\jaxen-full.jar
set _CP=%_CP%;.\lib\jdom.jar
set _CP=%_CP%;.\lib\dom4j-1.4.jar
java -classpath %_CP% com.manning.blogapps.chapter12.Tapi2opml %1 %2 %3
