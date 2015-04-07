@echo off
REM Script for running Chapter 7 examples
REM
REM First argument (required): fully-qualified name of class to be run
REM Up to five additional arguments (optional): as needed 

set _cp_=%_cp_%;.
set _cp_=%_cp_%;.\lib\ch07.jar
set _cp_=%_cp_%;.\lib\rome-0.8.jar
set _cp_=%_cp_%;.\lib\rome-fetcher-0.8.jar
set _cp_=%_cp_%;.\lib\content-0.4.jar
set _cp_=%_cp_%;.\lib\jdom.jar

java -classpath %_cp_% %1 %2 %3 %4 %5 %6
