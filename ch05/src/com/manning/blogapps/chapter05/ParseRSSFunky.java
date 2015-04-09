package com.manning.blogapps.chapter05;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

/** Parse-and-print example that handles funky RSS 2.0 */
public class ParseRSSFunky {

	public static void main(String[] args) throws Exception {
        new ParseRSSFunky(args);
    }

	public ParseRSSFunky(String[] args) throws Exception {
		
        FileInputStream inputStream = new FileInputStream(args[0]);
        
        SAXBuilder builder = new SAXBuilder();
        Document feedDoc = builder.build(inputStream);         
        Element root = feedDoc.getRootElement();       
        Element channel = root.getChild("channel");
        
        SimpleDateFormat rfc822_format = new SimpleDateFormat( "EEE, dd MMM yyyy hh:mm:ss z" );        
        Iterator<?> items = channel.getChildren("item").iterator();
        
		Namespace dcNS = Namespace.getNamespace(           
		      "dc","http://purl.org/dc/elements/1.1/");    
		Namespace contentNS = Namespace.getNamespace(      
		      "content","http://purl.org/rss/1.0/modules/content/");
		
		while (items.hasNext()) {   
		   Element item = (Element) items.next(); 
		
		   System.out.println("Title: " + item.getChildText("title"));          
		   System.out.println("Link: " + item.getChildText("link"));  
		            
		   String dateString = item.getChildText("pubDate");  
		   if (dateString != null) {
		      Date date = rfc822_format.parse(dateString);
		      System.out.println("Date: " + date.toString());
		   } 
		   else if ((dateString = item.getChildText("date", dcNS)) != null) {  
		      Date date = ISO8601DateParser.parse(dateString);
		      System.out.println("Date: " + date.toString());
		   }
		   String description = item.getChildText("description");
		   if (description == null) {
		      description = item.getChildText("encoded", contentNS);  
		   }
		   System.out.println("Description: " + description);
		   System.out.println("\n");
		}
    }

}
