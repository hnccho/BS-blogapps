
set _cp_=.\lib\xmlrpc-1.2-b1.jar
set _cp_=%_cp_%;.\lib\commons-lang-2.0.jar
set _cp_=%_cp_%;.\lib\ch08.jar
set _cp_=%_cp_%;.\lib\Grabber.jar
set _cp_=%_cp_%;.
java -classpath %_cp_% com.manning.blogapps.extra02.Grabber %1 %2 %3
