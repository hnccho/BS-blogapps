@echo off
set _cp_=.\lib\ch17.jar
set _cp_=%_cp_%;.\lib\ch10.jar
set _cp_=%_cp_%;.\lib\chatengine.jar
set _cp_=%_cp_%;.\lib\JSPWiki.jar
set _cp_=%_cp_%;.\lib\jakarta-oro.jar
set _cp_=%_cp_%;.\lib\activation.jar
set _cp_=%_cp_%;.\lib\jdom.jar
set _cp_=%_cp_%;.\lib\log4j-1.2.4.jar
set _cp_=%_cp_%;.\lib\servlet-api.jar
set _cp_=%_cp_%;.\lib\commons-logging-2.0.jar
set _cp_=%_cp_%;.\lib\xmlrpc-1.2-b1.jar
set _cp_=%_cp_%;.
java -classpath %_cp_% com.manning.blogapps.chapter17.ChatBlogger

