package com.manning.blogapps.chapter08.filedepot;
import java.io.*;
import java.util.*;
import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.WireFeedOutput;

public class DepotNewsfeedWriter {              //|#1

	private Depot depot;                        //|#1
    
	public DepotNewsfeedWriter(Depot depot) {   //|#1
        this.depot = depot;                     //|#1
    }
    
    public void write(                                     //|#2
            Writer writer, String baseURL, String format)  //|#2
            throws Exception { 

        SyndFeed feed = new SyndFeedImpl();                 //|#3
        feed.setFeedType(format);                           //|#3
        feed.setLanguage("en-us");                          //|#3
        feed.setTitle("File Depot Newsfeed");               //|#3
        feed.setDescription(                                //|#3
            "Newly uploaded files in the File Depot");      //|#3
        feed.setLink(baseURL);                              //|#3
        feed.setUri(baseURL + "/depot-newsfeed");           //|#3
        feed.setPublishedDate(depot.getLastUpdateDate());   //|#3 
        
        SyndLink selfLink = new SyndLinkImpl();              //|#4                  
        selfLink.setHref(feed.getUri());                     //|#4
        selfLink.setRel("self");                             //|#4
        feed.setLinks(Collections.singletonList(selfLink));  //|#4
        
        ArrayList entries = new ArrayList();            //|#5
        Iterator files = depot.getFiles().iterator();   //|#5
        while (files.hasNext()) {                       //|#5            
            File file = (File) files.next();            //|#5
            
            SyndEntry entry = new SyndEntryImpl();                  //|#6
            String url = baseURL + file.getName();                  //|#6
            entry.setLink(url);                                     //|#6
            entry.setUri(url);                                      //|#6
            entry.setTitle(file.getName());                         //|#6
            entry.setPublishedDate(new Date(file.lastModified()));  //|#6
            entry.setUpdatedDate(new Date(file.lastModified()));    //|#6
                        
            SyndContent desciption = new SyndContentImpl();   //|#7
            desciption.setValue(                              //|#7
               "Click <a href='"+url+"'>" + file.getName()    //|#7
               + "</a> to download the file.");               //|#7
            entry.setDescription(desciption);                 //|#7
            
            entries.add(entry);    //|#8
        }
        feed.setEntries(entries);  //|#9
        
        WireFeedOutput output = new WireFeedOutput();  //|#10
        WireFeed wireFeed = feed.createWireFeed();     //|#10
        output.output(wireFeed, writer);               //|#10
    }
    
    public void main(String[] args) throws Exception {     //|#11
        if (args.length < 3)  {                            //|#11
            System.out.println(                            //|#11
                "USAGE: DepotNewsfeedWriter "              //|#11
              + "[depotDir] [depotUrl] [file] [format]");  //|#11
            return;                                        //|#11
        }
        String depotDir = args[0];                       //|#12
        Depot depot = new FileDepot(depotDir);           //|#12        
        DepotNewsfeedWriter newsfeedWriter =             //|#12
            new DepotNewsfeedWriter(depot);              //|#12

        String depotUrl = args[1];                       //|#13
        String filePath = args[2];                       //|#13
        String format = args[3];                         //|#13
        FileWriter writer = new FileWriter(filePath);    //|#13
        newsfeedWriter.write(writer, depotUrl, format);  //|#13
    }
    
}
