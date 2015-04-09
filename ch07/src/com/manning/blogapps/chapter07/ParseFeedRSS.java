package com.manning.blogapps.chapter07;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Enclosure;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.io.WireFeedInput;

/**
 * Simple example that prints title, link, date and description.
 */
public class ParseFeedRSS {

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
            System.out.println("USAGE: ParseFeedRSS <feed-file-name>");
            return;
        }
        new ParseFeedRSS().parseFeed(new FileInputStream(args[0]));
    }
	
    public void parseFeed(InputStream is) throws Exception {
        WireFeedInput input = new WireFeedInput();                  
        WireFeed wireFeed = input.build(new InputStreamReader(is)); 
        if (!(wireFeed instanceof Channel)) {            
            System.out.println("Not an RSS feed");
            return;
        }
        Channel channel = (Channel)wireFeed;                        
        Iterator items = channel.getItems().iterator();
        while (items.hasNext()) {                                   
            Item item = (Item)items.next();
            
            System.out.println("Guid:        " + item.getGuid());   
            System.out.println("  Title:     " + item.getTitle());
            System.out.println("  Published: " + item.getPubDate());
            System.out.println("  Link:      " + item.getLink());
            
            if (item.getDescription() != null) {                    
                System.out.println("  Desc: "
                        + item.getDescription().getValue());
            }
            for (int i=0; i < item.getEnclosures().size(); i++) {   
                Enclosure enc = (Enclosure)item.getEnclosures().get(i);
                System.out.println(
                        " Enclosure type=" + enc.getType() +
                        " length="         + enc.getLength() +
                        " url="            + enc.getUrl());
            }
            System.out.println("\n");
        }
    }
    
}
