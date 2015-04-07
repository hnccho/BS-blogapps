@echo off
REM Script for running Chapter 5 examples
REM
REM First argument (required): fully-qualified name of class to be run
REM Up to five additional arguments (optional): as needed 

set _cp_=%_cp_%;.
set _cp_=%_cp_%;.\lib\ch05.jar
set _cp_=%_cp_%;.\lib\rome-0.8.jar
set _cp_=%_cp_%;.\lib\jdom.jar
set _cp_=%_cp_%;.\lib\xml-apis.jar
set _cp_=%_cp_%;.\lib\xercesImpl.jar
set _cp_=%_cp_%;.\lib\commons-feedparser-0.5.0-RC1.jar
set _cp_=%_cp_%;.\lib\saxpath.jar
set _cp_=%_cp_%;.\lib\jaxen-full.jar
set _cp_=%_cp_%;.\lib\log4j-1.2.4.jar

java -classpath %_cp_% %1 %2 %3 %4 %5 %6
