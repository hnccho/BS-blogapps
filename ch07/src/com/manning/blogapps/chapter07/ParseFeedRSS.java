package com.manning.blogapps.chapter07;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.rss.*;
import com.sun.syndication.io.WireFeedInput;
import java.io.*;
import java.util.Iterator;

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
        WireFeedInput input = new WireFeedInput();                  //|#3
        WireFeed wireFeed = input.build(new InputStreamReader(is)); //|#4
        if (!(wireFeed instanceof Channel)) {            //|#5
            System.out.println("Not an RSS feed");
            return;
        }
        Channel channel = (Channel)wireFeed;                        //|#6
        Iterator items = channel.getItems().iterator();
        while (items.hasNext()) {                                   //|#7
            Item item = (Item)items.next();
            
            System.out.println("Guid:        " + item.getGuid());   //|#8
            System.out.println("  Title:     " + item.getTitle());
            System.out.println("  Published: " + item.getPubDate());
            System.out.println("  Link:      " + item.getLink());
            
            if (item.getDescription() != null) {                    //|#9
                System.out.println("  Desc: "
                        + item.getDescription().getValue());
            }
            for (int i=0; i < item.getEnclosures().size(); i++) {   //|#10
                Enclosure enc = (Enclosure)item.getEnclosures().get(i);
                System.out.println(
                        "  Enclosure type=" + enc.getType() +
                        " length="          + enc.getLength() +
                        " url="             + enc.getUrl());
            }
            System.out.println("\n");
        }
    }
    
}
