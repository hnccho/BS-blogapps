package com.manning.blogapps.chapter07;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndLink;
import com.sun.syndication.feed.synd.SyndLinkImpl;
import com.sun.syndication.io.WireFeedOutput;

/**
 * Simple example that shows how to generate a feed.
 */
public class GenerateFeed {
 
	public static void main(String[] args) throws Exception {
		
        if (args.length < 1) {
            System.out.println("USAGE: GenerateFeed <feed-type>");
            return;
        }
        
        String feedType = args[0];
        
        SyndFeed feed = new SyndFeedImpl();                       
        feed.setTitle("Example feed");                           
        feed.setLink("http://example.com/feeds/blog1");
        feed.setLanguage("en");                             
        feed.setDescription("Example feed generated by ROME");
        feed.setUri("http://example.com/feeds/blog1");            
        feed.setPublishedDate(new Date());                        
        
        SyndLink selfLink = new SyndLinkImpl();                   
        selfLink.setHref("http://example.com/feeds/blog1.xml");
        selfLink.setRel("self");
        feed.setLinks(Collections.singletonList(selfLink));
        
        SyndEntry entry1 = new SyndEntryImpl();
        entry1.setUri("http://example.com/blog/entry1.html");     
        entry1.setTitle("Entry1");
        entry1.setLink("http://example.com/blog/entry1.html");
        entry1.setPublishedDate(new Date());
        entry1.setUpdatedDate(new Date());                        
        entry1.setAuthor("Nina");                                 
        
        SyndContent desc1 = new SyndContentImpl();                 
        desc1.setValue("Description for test entry #1");
        entry1.setDescription(desc1);
        
        SyndContent content1 = new SyndContentImpl();             
        content1.setValue("Content for test entry #1");
        entry1.setContents(Collections.singletonList(content1));
        
        List<SyndEntry> entries = new ArrayList<SyndEntry>();                           
        entries.add(entry1);
        feed.setEntries(entries);
        
        WireFeedOutput out = new WireFeedOutput();
        out.output(feed.createWireFeed(feedType), new PrintWriter(System.out));
    }
	
}
