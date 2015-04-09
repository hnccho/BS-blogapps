package com.manning.blogapps.chapter07;       
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLink;
import com.sun.syndication.io.SyndFeedInput;

/**
 * Parses RSS or Atom via SyndFeed model, prints items.
 */
public class ParseFeed {
 
	public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("USAGE: ParseFeed <feed-file-name>");
            return;
        }
        new ParseFeed().parseFeed(new FileInputStream(args[0]));
    }
	
    public void parseFeed(InputStream is) throws Exception {
        
        SyndFeedInput input = new SyndFeedInput();               
        SyndFeed feed = input.build(new InputStreamReader(is));  
        
        Iterator<?> entries = feed.getEntries().iterator();
        while (entries.hasNext()) {                             
            SyndEntry entry = (SyndEntry)entries.next();
            
            System.out.println("Uri: " + entry.getUri());           
            System.out.println("  Link:      " + entry.getLink());  
            System.out.println("  Title:     " + entry.getTitle());
            System.out.println("  Published: " + entry.getPublishedDate());
            System.out.println("  Updated:   " + entry.getUpdatedDate());
            
            if (entry.getDescription() != null) {
                System.out.println("  Description: "               
                        + entry.getDescription().getValue());
            }
            if (entry.getContents().size() > 0) {               
                SyndContent content = (SyndContent)entry.getContents().get(0);
                System.out.print("  Content type=" + content.getType());
                System.out.println(" value=" + content.getValue());
            }
            for (int i=0; i < entry.getLinks().size(); i++) {     
                SyndLink link = (SyndLink)entry.getLinks().get(i);
                System.out.println(
                        "  Link type=" + link.getType() +
                        " length="   + link.getLength() +
                        " hreflang=" + link.getHreflang() +
                        " href="     + link.getHref());
            }
            System.out.println("\n");
        }
    }
    
}
