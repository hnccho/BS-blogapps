package com.manning.blogapps.chapter05;

import java.io.FileInputStream;
import java.util.Date;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

/** Parse-and-print example that parses RSS 1.0 */
public class ParseRSS10 {

	public static void main(String[] args) throws Exception {
        new ParseRSS10(args);
    }
	
    public ParseRSS10(String[] args) throws Exception {
    	
        FileInputStream inputStream = new FileInputStream(args[0]);
        
        SAXBuilder builder = new SAXBuilder();
        Namespace ns = Namespace.getNamespace("http://purl.org/rss/1.0/");
        Document feedDoc = builder.build(inputStream);         
        Element root = feedDoc.getRootElement();        
        
        Iterator<?> items = root.getChildren("item", ns).iterator();
        
        while (items.hasNext()) {
            
            Element item = (Element) items.next(); 
            System.out.println("Title: " + item.getChildText("title", ns));            
            System.out.println("Link: " + item.getChildText("link", ns));
            
            String dateString = item.getChildText("date",
                Namespace.getNamespace("http://purl.org/dc/elements/1.1/"));
            if (dateString != null) {
                Date date = ISO8601DateParser.parse(dateString);
                System.out.println("Date: " + date.toString());
            }
            System.out.println("Description: " + item.getChildText("description", ns));
            System.out.println("\n");
        }
    }
    
}
