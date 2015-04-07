@echo off
set _cp_=%_cp_%;.
set _cp_=%_cp_%;.\lib\ch10.jar
set _cp_=%_cp_%;.\lib\commons-codec-1.3.jar
set _cp_=%_cp_%;.\lib\commons-httpclient-2.0.2.jar
set _cp_=%_cp_%;.\lib\commons-lang-2.0.jar
set _cp_=%_cp_%;.\lib\commons-logging.jar
java -classpath %_cp_% com.manning.blogapps.chapter10.examples.AuthGet %1 %2 %3 %4 %5
