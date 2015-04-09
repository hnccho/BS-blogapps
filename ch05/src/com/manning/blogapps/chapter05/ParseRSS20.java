package com.manning.blogapps.chapter05;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/** Parse-and-print example that parses RSS 2.0 */
public class ParseRSS20 {

	public static void main(String[] args) throws Exception {
        new ParseRSS20(args);
    }
	
    public ParseRSS20(String[] args) throws Exception {
    	
        FileInputStream inputStream = new FileInputStream(args[0]);
        
		SAXBuilder builder = new SAXBuilder();          
		Document feedDoc = builder.build(inputStream);  
		Element root = feedDoc.getRootElement();        
		Element channel = root.getChild("channel");    
		   
		Iterator<?> items = channel.getChildren("item").iterator();   
		
		SimpleDateFormat rfc822_format =                                 
		   new SimpleDateFormat( "EEE, dd MMM yyyy hh:mm:ss z" );  
		
		while (items.hasNext()) {   
		   Element item = (Element) items.next();  
		
		   System.out.println("Title: " + item.getChildText("title"));           
		   System.out.println("Link: " + item.getChildText("link"));  
		            
		   String dateString = item.getChildText("pubDate");  
		   if (dateString != null) {
		      Date date = rfc822_format.parse(dateString);
		      System.out.println("Date: " + date.toString());
		   }
		   System.out.println("Description: " + item.getChildText("description"));
		   System.out.println("\n");
		}
    }

}
