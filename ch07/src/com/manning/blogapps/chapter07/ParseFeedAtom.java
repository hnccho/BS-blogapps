package com.manning.blogapps.chapter07;

import com.manning.blogapps.chapter07.pubcontrol.PubControlModule;
import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.atom.*;
import com.sun.syndication.io.WireFeedInput;
import java.io.*;
import java.util.Iterator;

/**
 * Simple example that prints title, dates, links, date and content.
 */
public class ParseFeedAtom {
 
	public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("USAGE: ParseFeedAtom <feed-file-name>");
            return;
        }
        new ParseFeedAtom().parseFeed(new FileInputStream(args[0]));
    }
	
    public void parseFeed(InputStream is) throws Exception {
        WireFeedInput input = new WireFeedInput();                      //|#2
        WireFeed wireFeed = input.build(new InputStreamReader(is));     //|#3
        if (!(wireFeed instanceof Feed)) {               //|#4
            System.out.println("Not an Atom feed");
            return;
        }
        Feed feed = (Feed)wireFeed;                                     //|#5
        Iterator entries = feed.getEntries().iterator();
        while (entries.hasNext()) {                                     //|#6
            Entry entry = (Entry)entries.next();
            
            System.out.println("Entry id: " + entry.getId());           //|#7
            System.out.println("  Title:    " + entry.getTitle());
            System.out.println("  Modified: " + entry.getModified());
            System.out.println("  Updated:  " + entry.getUpdated());
            
            if (entry.getContents().size() > 0) {                       //|#8
                Content content = (Content)entry.getContents().get(0);
                System.out.print("  Content type=" + content.getType());
                if (content.getSrc() != null) {
                    System.out.println(" src=" + content.getSrc());
                } else {
                    System.out.println(" value=" + content.getValue());
                }
            }
            for (int i=0; i < entry.getAlternateLinks().size(); i++) {  //|#9
                Link link = (Link)entry.getAlternateLinks().get(i);
                System.out.println(
                        "  Link type=" + link.getType() +
                        " rel="      + link.getRel() +
                        " length="   + link.getLength() +
                        " hreflang=" + link.getHreflang() +
                        " href="     + link.getHref());
            }
            for (int i=0; i < entry.getOtherLinks().size(); i++) {      //|#10
                Link link = (Link)entry.getOtherLinks().get(i);
                System.out.println(
                        "  Link type=" + link.getType() +
                        " rel="      + link.getRel() +
                        " length="   + link.getLength() +
                        " hreflang=" + link.getHreflang() +
                        " href="     + link.getHref());
            }
            
            // added so we can test the PubControl module
            PubControlModule control = 
                (PubControlModule)entry.getModule(PubControlModule.URI);            
            if (control != null) {
                System.out.println("  PubControl draft=" +  control.getDraft());
            }
            
            System.out.println("\n");
        }
    }
    
}

