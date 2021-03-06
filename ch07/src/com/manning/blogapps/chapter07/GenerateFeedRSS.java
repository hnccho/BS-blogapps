package com.manning.blogapps.chapter07;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.io.WireFeedOutput;

/**
 * Simple example that shows how to generate a feed.
 */
public class GenerateFeedRSS {

	public static void main(String[] args) throws Exception {

		if (args.length < 1) {
            System.out.println("USAGE: GenerateRSS <feed-type>");
            return;
        }
		
        Channel channel = new Channel();  
        channel.setFeedType(args[0]);
        channel.setLanguage("en-us");                
        channel.setTitle("Example feed");         
        channel.setDescription("Example feed generated by ROME");  
        channel.setLink("http://example.com/feeds/blog1");                                           
        channel.setPubDate(new Date()); 
                   
        List<Item> items = new ArrayList<Item>();
        
        Item item1 = new Item();
        item1.setTitle("Entry1");
        item1.setLink("http://example.com/blog/entry1.html");
        item1.setPubDate(new Date());   
        item1.setAuthor("nina@example.com");
        
        Description desc = new Description();
        desc.setValue("Summary for test entry #1");
        item1.setDescription(desc); 
        
        items.add(item1); 
        
        channel.setItems(items);  
        
        WireFeedOutput out = new WireFeedOutput();
        out.output(channel, new PrintWriter(System.out));
    }
	
}
