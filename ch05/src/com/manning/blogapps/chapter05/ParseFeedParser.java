package com.manning.blogapps.chapter05;

import java.io.FileInputStream;
import java.util.Date;

import org.apache.commons.feedparser.DefaultFeedParserListener;
import org.apache.commons.feedparser.FeedParser;
import org.apache.commons.feedparser.FeedParserException;
import org.apache.commons.feedparser.FeedParserFactory;
import org.apache.commons.feedparser.FeedParserListener;
import org.apache.commons.feedparser.FeedParserState;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

/** Parse-and-print example that uses Jakarta Feed Parser */
public class ParseFeedParser {
	
    public static void main(String[] args) throws Exception {
        new ParseFeedParser(args);
    }
    
    public ParseFeedParser(String[] args) throws Exception {
 
    	FileInputStream inputStream = new FileInputStream(args[0]);
        
        FeedParserListener listener = new DefaultFeedParserListener() {
            public void onItem(FeedParserState state, 
                String title, String link, 
                String description, String permalink) throws FeedParserException {
                System.out.print("\n");                    
                System.out.println("Title: " + title);                    
                System.out.println("Link: " + link);                    
                System.out.println("Description: " + description);                    
            }
            public void onCreated(FeedParserState state, Date date) 
                throws FeedParserException {
                System.out.println( "Date: " + date );
            }
        };
        SAXBuilder builder = new SAXBuilder();
        Document feedDoc = builder.build(inputStream); 
        
        FeedParser parser = FeedParserFactory.newFeedParser();
        parser.parse(listener, feedDoc);
    }
    
}
