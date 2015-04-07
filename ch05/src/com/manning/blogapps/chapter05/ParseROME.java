package com.manning.blogapps.chapter05;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import com.sun.syndication.feed.module.DCModule;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;

/** Parse-and-print example that uses ROME */
public class ParseROME {
	
    public static void main(String[] args) throws Exception {
        new ParseROME(args);
    }
    
    public ParseROME(String[] args) throws Exception {
        FileInputStream inputStream = new FileInputStream(args[0]);
        
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new InputStreamReader(inputStream));        
        Iterator items = feed.getEntries().iterator();       
        while (items.hasNext()) { 
            SyndEntry item = (SyndEntry)items.next(); 
            System.out.println("Title: " + item.getTitle());            
            System.out.println("Link: " + item.getLink());
            System.out.println("Date: " + item.getPublishedDate());
            System.out.println("Description: " + item.getDescription());
            System.out.println("\n");
        }
    }
    
}
