package com.manning.blogapps.extra01;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import com.manning.blogapps.chapter05.AnyFeedParser;
import com.manning.blogapps.chapter05.IFeedParser;
import com.manning.blogapps.chapter10.blogclient.Blog;
import com.manning.blogapps.chapter10.blogclient.BlogConnection;
import com.manning.blogapps.chapter10.blogclient.BlogConnectionFactory;
import com.manning.blogapps.chapter10.blogclient.BlogEntry;

public class FeedPoster {
	
    String title = null;
    int days = -1;
    String url = null;
    String username = null;
    String password = null;
    String blogid = null;
    List subscriptions = new ArrayList();
    
    public static class Subscription {
        public String feed_url;
    }
    
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("USAGE: FeedPoster <config-file>");
            System.exit(-1);
        }
        FeedPoster feedPoster = new FeedPoster();
        feedPoster.initFromXml(args[0]);
        feedPoster.run();
    }
    
    public void run() throws Exception {
        System.out.println("FeedPoster for Java");
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, -1 * days * 24);
        Date since = cal.getTime();
        long sinceLong = since.getTime();
        
        int count = 0;
        
        BlogConnection blogcon = 
            BlogConnectionFactory.getBlogConnection("metaweblog", url, username, password);
        Blog blogSite = blogcon.getBlog(blogid);

        IFeedParser feedParser = new AnyFeedParser();
        StringBuffer sb = new StringBuffer();
        sb.append("<ul>\n");
        
        Iterator subs = subscriptions.iterator();       
        while (subs.hasNext()) {
            Subscription sub = (Subscription)subs.next();
            
            URL url = new URL(sub.feed_url);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setIfModifiedSince(sinceLong);            
            if (con.getLastModified() > sinceLong) {                
                Map feedMap = feedParser.parseFeed(new InputStreamReader(con.getInputStream()));
                
                List itemsList = (List)feedMap.get("items");
                Iterator items = itemsList.iterator();                   
                while (items.hasNext()) {
                   Map itemMap = (Map) items.next();
                   Date pubDate = (Date)itemMap.get("pubDate");
                   if (pubDate.after(since)) {
                      count++;
                      String content = (String)itemMap.get("description");
                      if (content == null) {
                         content = (String)itemMap.get("content");
                      }
                      sb.append("<li><a href=\"");
                      sb.append((String)itemMap.get("link"));
                      sb.append("\">");
                      sb.append((String)itemMap.get("title"));
                      sb.append("</a><br /> ");
                      sb.append(content);
                      sb.append("</li>\n"); 
                  }
               }
            }
        }
        sb.append("</ul>\n");
        String text = sb.toString();
        if (count > 0) {
            BlogEntry entry = blogSite.newEntry();
            Date today = new Date();
            SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy");
            entry.setTitle(title + " [" + df.format(today) + "]");
            entry.setPublicationDate(new Date());
            BlogEntry.Content 
               contentObject = new BlogEntry.Content(text);
            System.out.println(text);
            entry.setContent(contentObject);
            entry.save();
            System.out.println("Posted links: " + count);
        }
        else {
            System.out.println("Nothing to send");
        }
    }
    
    private void initFromXml(String configfile) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new FileInputStream(configfile));
        Element root = doc.getRootElement();
        title =    getString(root, "/feed-poster/title");
        days =     getInt(root,    "/feed-poster/days");
        url =      getString(root, "/feed-poster/destination/metaweblog-url");
        username = getString(root, "/feed-poster/destination/username");
        password = getString(root, "/feed-poster/destination/password");
        blogid =   getString(root, "/feed-poster/destination/blogid");
        XPath itemsPath = XPath.newInstance("/feed-poster/subscription");
        Iterator iter = itemsPath.selectNodes(root).iterator();
        while (iter.hasNext()) {
            Element element = (Element) iter.next();
            Subscription sub = new Subscription();
            sub.feed_url = getString(element,"feed-url");
            subscriptions.add(sub);
        }
    }
    
    private static String getString(Element elem, String path) throws Exception {
        XPath xpath = XPath.newInstance(path);
        Element e = (Element)xpath.selectSingleNode(elem);
        return e!=null ? e.getText() : null;
    }
    
    private static int getInt(Element elem, String path) throws Exception {
        return Integer.parseInt(getString(elem, path));
    }
    
}

