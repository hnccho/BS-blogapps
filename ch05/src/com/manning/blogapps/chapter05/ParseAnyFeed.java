package com.manning.blogapps.chapter05;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/** Parse-and-print example that uses AnyFeed Parser */
public class ParseAnyFeed {

	public static void main(String[] args) throws Exception {
        new ParseAnyFeed(args);
    }

	public ParseAnyFeed(String[] args) throws Exception {
		
        FileInputStream inputStream = new FileInputStream(args[0]);
        
		IFeedParser parser = new AnyFeedParser();   
		Map feedMap = parser.parseFeed(new InputStreamReader(inputStream));
		Iterator items = ((List)feedMap.get("items")).iterator();
		while (items.hasNext()) {   
		   Map itemMap = (Map)items.next();
		   System.out.println("Title: "       + (String)itemMap.get("title"));            
		   System.out.println("Link: "        + (String)itemMap.get("link"));  
		   System.out.println("Description: " + (String)itemMap.get("description"));        
		   System.out.println("Content: "     + (String)itemMap.get("content"));        
		   System.out.println("Date"          + itemMap.get("updated").toString());        
		}
		
    }
	
}
