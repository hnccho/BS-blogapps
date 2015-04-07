package com.manning.blogapps.chapter07;

import com.manning.blogapps.chapter07.pubcontrol.PubControlModule;
import com.manning.blogapps.chapter07.pubcontrol.PubControlModuleImpl;
import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.feed.atom.Person;
import com.sun.syndication.io.WireFeedOutput;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Simple example that shows how to generate a feed.
 */
public class GenerateFeedAtom {
 
	public static void main(String[] args) throws Exception {  
        
        String feedUrl = "http://example.com/feeds/blog1";
        Feed feed = new Feed();  
        feed.setFeedType("atom_1.0");
        feed.setId("http://example.com/feeds/blog1");                
        feed.setUpdated(new Date());     
        feed.setTitle("Example feed");    
        
        Link selfLink = new Link();
        selfLink.setHref("http://example.com/feeds/blog1.xml"); 
        selfLink.setRel("self");
        feed.setOtherLinks(Collections.singletonList(selfLink));
        
        Content subtitle = new Content();
        subtitle.setValue("Example feed generated by ROME");
        feed.setSubtitle(subtitle);        
                   
        List entries = new ArrayList();
        
        Entry entry1 = new Entry();
        entry1.setTitle("Entry1");
        entry1.setId("http://example.com/blog/entry1.html");  
        
        Link altLink = new Link();
        altLink.setHref("http://example.com/blog/entry1.html");
        entry1.setAlternateLinks(Collections.singletonList(altLink));
        
        entry1.setPublished(new Date());         
        entry1.setUpdated(new Date());         
        Person author = new Person();
        author.setName("Nina");
        entry1.setAuthors(Collections.singletonList(author));        

        Content summary1 = new Content(); 
        summary1.setValue("Summary for test entry #1");
        entry1.setSummary(summary1);           
        
        Content content1 = new Content(); 
        content1.setValue("Content for test entry #1");
        entry1.setContents(Collections.singletonList(content1));   
        
        // added so we can test the PubControl module        
        PubControlModule pubControl = new PubControlModuleImpl();
        pubControl.setDraft(true);
        entry1.getModules().add(pubControl);
                      
        entries.add(entry1); 
        
        feed.setEntries(entries);  
        
        WireFeedOutput out = new WireFeedOutput();
        out.output(feed, new PrintWriter(System.out));
    }
	
}
