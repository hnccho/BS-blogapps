@echo off
set _cp_=.\lib\mail.jar
set _cp_=%_cp_%;.\lib\activation.jar
set _cp_=%_cp_%;.\lib\jdom.jar
set _cp_=%_cp_%;.\lib\dom4j-1.4.jar
set _cp_=%_cp_%;.\lib\jaxen-full.jar
set _cp_=%_cp_%;.\lib\junit-3.8.1.jar
set _cp_=%_cp_%;.\lib\xmlrpc-1.2-b1.jar
set _cp_=%_cp_%;.\lib\ch05.jar
set _cp_=%_cp_%;.\lib\ch10.jar
set _cp_=%_cp_%;.\lib\ch14.jar
set _cp_=%_cp_%;.
java -classpath %_cp_% com.manning.blogapps.chapter14.MailBlogger
