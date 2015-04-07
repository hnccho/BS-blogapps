package com.manning.blogapps.chapter07;

import com.sun.syndication.feed.module.DCModule;
import com.sun.syndication.feed.module.content.ContentModule;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLink;
import com.sun.syndication.io.SyndFeedInput;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * Simple examples that prints title, pub. date, description and content:encoded
 */
public class ParseFeedFunky {

	public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("USAGE: ParseFeedFunky <feed-file-name>");
            return; 
        }
        new ParseFeedFunky().parseFeed(new FileInputStream(args[0]));
    }
	
    public void parseFeed(InputStream is) throws Exception {
        
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new InputStreamReader(is));
        
        Iterator entries = feed.getEntries().iterator();
        while (entries.hasNext()) {
            SyndEntry entry = (SyndEntry)entries.next();
            
            System.out.println("Uri: " + entry.getUri());
            System.out.println("  Title:     " + entry.getTitle());
            System.out.println("  Link:      " + entry.getLink());
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
            ContentModule contentModule = (ContentModule)
            entry.getModule(ContentModule.URI);
            if (contentModule != null) {
                if (contentModule.getEncodeds().size() > 0) {
                    System.out.println("  content:encoded: "
                            + contentModule.getEncodeds().get(0));
                }               
            }
            DCModule dc = (DCModule)
            entry.getModule(DCModule.URI);
            if (dc != null) {
                System.out.println("   dc:date: " + dc.getDate());
                System.out.println("   dc:creator: " + dc.getCreator());
            }
            System.out.println("\n");
        }
    }
    
}
