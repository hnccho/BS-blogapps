package com.manning.blogapps.chapter05;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Map;

/** 
 * Reads a list of newsfeed URLs from a file (one per line), parses each to
 * determine blog title and URL and emits the results as list.opml.
 */
public class BuildOPML {
	
    public static void main(String[] args) throws Exception {
    	
        FileReader fr = new FileReader(args[0]);
        BufferedReader br = new BufferedReader(fr);
        String line = null;
        IFeedParser parser = new AnyFeedParser();
        
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version='1.0' encoding='UTF-8' ?>\n");
        sb.append("<opml version='1.0'>\n");
        sb.append("   <body>\n");
        sb.append("      <outline title=\"OPML\">\n");       
        while ((line = br.readLine()) != null) {
            try {
                line = line.trim();
                URL url = new URL(line);
                InputStreamReader isr = 
                    new InputStreamReader(url.openConnection().getInputStream());
                Map feed = parser.parseFeed(isr);
                sb.append(MessageFormat.format(
                    "         <outline title=\"{0}\" xmlUrl=\"{1} htmlUrl=\"{2}\">\n",
                    new Object[] {feed.get("title"), line, feed.get("link") }));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        sb.append("      </outline>\n   </body>\n</opml>");

        FileWriter fw = new FileWriter("list.opml");
        fw.write(sb.toString());
        fw.flush();
        fw.close();
    }
    
}
