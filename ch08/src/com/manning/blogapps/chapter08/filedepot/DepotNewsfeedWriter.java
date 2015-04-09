package com.manning.blogapps.chapter08.filedepot;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndLink;
import com.sun.syndication.feed.synd.SyndLinkImpl;
import com.sun.syndication.io.WireFeedOutput;

public class DepotNewsfeedWriter {             

	private Depot depot;                        
    
	public DepotNewsfeedWriter(Depot depot) {   
        this.depot = depot;                     
    }
    
    public void write(                                     
            Writer writer, String baseURL, String format)  
            throws Exception { 

        SyndFeed feed = new SyndFeedImpl();                
        feed.setFeedType(format);                           
        feed.setLanguage("en-us");                         
        feed.setTitle("File Depot Newsfeed");               
        feed.setDescription("Newly uploaded files in the File Depot");      
        feed.setLink(baseURL);                              
        feed.setUri(baseURL + "/depot-newsfeed");           
        feed.setPublishedDate(depot.getLastUpdateDate());  
        
        SyndLink selfLink = new SyndLinkImpl();                                
        selfLink.setHref(feed.getUri());                    
        selfLink.setRel("self");                             
        feed.setLinks(Collections.singletonList(selfLink));  
        
        ArrayList<SyndEntry> entries = new ArrayList<SyndEntry>();            
        Iterator<?> files = depot.getFiles().iterator();   
        while (files.hasNext()) {                                   
            File file = (File) files.next();            
            
            SyndEntry entry = new SyndEntryImpl();                  
            String url = baseURL + file.getName();                  
            entry.setLink(url);                                     
            entry.setUri(url);                                      
            entry.setTitle(file.getName());                         
            entry.setPublishedDate(new Date(file.lastModified())); 
            entry.setUpdatedDate(new Date(file.lastModified()));   
                        
            SyndContent desciption = new SyndContentImpl();   
            desciption.setValue(                             
               "Click <a href='"+url+"'>" + file.getName()   
               + "</a> to download the file.");               
            entry.setDescription(desciption);                 
            
            entries.add(entry);   
        }
        feed.setEntries(entries);  
        
        WireFeedOutput output = new WireFeedOutput();  
        WireFeed wireFeed = feed.createWireFeed();     
        output.output(wireFeed, writer);               
    }
    
    public void main(String[] args) throws Exception {    
 
    	if (args.length < 3)  {                           
            System.out.println(                           
                "USAGE: DepotNewsfeedWriter "             
              + "[depotDir] [depotUrl] [file] [format]");  
            return;                                       
        }
    	
        String depotDir = args[0];                      
        Depot depot = new FileDepot(depotDir);               
        DepotNewsfeedWriter newsfeedWriter = new DepotNewsfeedWriter(depot);             

        String depotUrl = args[1];                      
        String filePath = args[2];                       
        String format = args[3];                         
        FileWriter writer = new FileWriter(filePath);    
        newsfeedWriter.write(writer, depotUrl, format);  
    }
    
}
