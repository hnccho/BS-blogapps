#!/bin/bash
if [ -n "$1" ] && [ -n "$2" ]; then 
    java -cp .:./lib/ch02.jar:./lib/xmlrpc-1.2-b1.jar com.manning.blogapps.chapter02.BlogPoster "${1}" "${2}"
else 
    java -cp .:./lib/ch02.jar:./lib/xmlrpc-1.2-b1.jar com.manning.blogapps.chapter02.BlogPoster "${1}"
fi

