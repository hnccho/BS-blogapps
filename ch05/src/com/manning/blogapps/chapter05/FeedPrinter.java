package com.manning.blogapps.chapter05;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/** Prints feed parsed by AnyFeedParser */
public class FeedPrinter {

	/** Argument 0 is file name of feed to parse and print */
    public static void main(String[] args) throws Exception {
        displayFeed(new AnyFeedParser().parseFeed(args[0]), 
                    new PrintWriter(System.out));
    }

    /** Print AnyFeedParser feed output to PrintWriter */
    public static void displayFeed(Map feed, PrintWriter pw) throws Exception {

    	pw.println("Newsfeed title:       " + feed.get("title"));
        pw.println("Newsfeed description: " + feed.get("description"));
        pw.println("Newsfeed link:        " + feed.get("link"));        

        List items = (List)feed.get("items");
        for (int i=0; i<items.size(); i++) {
            Map item = (Map)items.get(i);
            pw.println("--------------------------------------------------------");
            pw.println("--- Item[" + i + "] title:       " + item.get("title"));
            pw.println("--- Item[" + i + "] link:        " + item.get("link"));
            pw.println("--- Item[" + i + "] pubDate:     " + item.get("pubDate"));
            pw.println("--- Item[" + i + "] description: " + item.get("description"));
            pw.println("--- Item[" + i + "] content:     " + item.get("content"));
            pw.println("--- Item[" + i + "] summary:     " + item.get("summary"));
        }
        pw.flush();
    }
    
}
