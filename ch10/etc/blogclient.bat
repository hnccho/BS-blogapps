@echo off
REM Script for running Chapter 10 Atom Protocol-based BlogClient
REM
REM First argument (required): fully-qualified name of class to be run
REM Up to five additional arguments (optional): as needed 

set _cp_=%_cp_%;.
set _cp_=%_cp_%;.\lib\ch10.jar
set _cp_=%_cp_%;.\lib\commons-codec-1.3.jar
set _cp_=%_cp_%;.\lib\commons-httpclient-2.0.2.jar
set _cp_=%_cp_%;.\lib\commons-lang-2.0.jar
set _cp_=%_cp_%;.\lib\commons-logging.jar
set _cp_=%_cp_%;.\lib\jdom.jar
set _cp_=%_cp_%;.\lib\rome-0.8.jar
set _cp_=%_cp_%;.\lib\xmlrpc-1.2-b1.jar

java -classpath %_cp_% com.manning.blogapps.chapter10.blogclientui.LoginFrame %1 %2 %3 %4 %5
