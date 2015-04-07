package com.manning.blogapps.chapter13;

import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
import com.manning.blogapps.chapter10.blogclient.BlogEntry;
import com.manning.blogapps.chapter10.blogclient.Blog;
import com.manning.blogapps.chapter10.blogclient.BlogConnection;
import com.manning.blogapps.chapter10.blogclient.BlogConnectionFactory;
import java.io.InputStreamReader;

/* 
<?xml version="1.0" encoding="utf-8" ?> 
<cross-poster>
    <subscription>
        <feed-url>http://rollerweblogger.org/rss/roller</feed-url>
        <author>David M Johnson</author>
    </subscription>
    <destination>   
        <metaweblog-url>http://localhost:8080/roller/xmlrpc</metaweblog-url>
        <username>admin</username>
        <password>admin</password>
        <blogid>adminblog</blogid>
    </destination>
</cross-poster>
*/
public class CrossPoster {
    static String metaweblog_url = null;
    static String username = null;
    static String password = null;
    static String blogid = null;
    static List categories = new ArrayList();
    static List subscriptions = new ArrayList();

    public static class Subscription {
        public String feed_url;
        public String author;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("CrossPoster for Java");
        initFromXml();
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, -24); 
        Date since = cal.getTime();
        long sinceLong = since.getTime();
        
        BlogConnection blogcon = BlogConnectionFactory.getBlogConnection(
            "metaweblog", metaweblog_url, username, password);
        Blog blogSite = blogcon.getBlog(blogid);
                
        IFeedParser feedParser = new AnyFeedParser();
        
        int count = 0;
        Iterator subs = subscriptions.iterator();
        while (subs.hasNext()) {
            Subscription sub = (Subscription)subs.next();
            
            URL url = new URL(sub.feed_url);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setIfModifiedSince(sinceLong);
            if (con.getLastModified() > sinceLong) {
                
                Map feedMap = feedParser.parseFeed(
                   new InputStreamReader(con.getInputStream()));
                
                List itemsList = (List)feedMap.get("items");
                Iterator items = itemsList.iterator();
                
                while (items.hasNext()) {
                   Map itemMap = (Map) items.next();
                    
                   Date pubDate = (Date)itemMap.get("pubDate");
                   if (itemMap.get("pubDate") == null) {
                      pubDate = (Date)itemMap.get("dc:date");
                   }
                   if (pubDate.after(since)) {
                      String content = (String)itemMap.get("description");
                      if (content == null) {
                         content = (String)itemMap.get("content:encoded");
                      }
                      BlogEntry entry = blogSite.newEntry();
                      entry.setTitle((String)itemMap.get("title"));
                      entry.setPublicationDate(pubDate);
                      BlogEntry.Content 
                         contentObject = new BlogEntry.Content(content);
                      entry.setContent(contentObject);
                      entry.setCategories(categories);
                      entry.save();
                      count++;
                  }
               }
            }
        }
        System.out.println("Posted " + count + " entries");
    }
    
    private static void initFromXml() throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new FileInputStream(
                "CrossPoster.config.xml"));
        Element root = doc.getRootElement();
        metaweblog_url = getString(root, "/cross-poster/destination/metaweblog-url");
        username = getString(root, "/cross-poster/destination/username");
        password = getString(root, "/cross-poster/destination/password");
        blogid = getString(root, "/cross-poster/destination/blogid");
        String category = getString(root, "/cross-poster/destination/category");
        if (category != null) {
            BlogEntry.Category cat = new BlogEntry.Category(category);
            cat.setName(category);
            categories.add(cat);
        }
        XPath itemsPath = XPath.newInstance("/cross-poster/subscription");
        Iterator iter = itemsPath.selectNodes(root).iterator();
        while (iter.hasNext()) {
            Element element = (Element) iter.next();
            Subscription sub = new Subscription();
            sub.author = getString(element,"author");
            sub.feed_url = getString(element,"feed-url");
            subscriptions.add(sub);
        }
    }
    
    private static String getString(Element elem, String path) throws Exception {
        XPath xpath = XPath.newInstance(path);
        Element e = (Element)xpath.selectSingleNode(elem);
        return e!=null ? e.getText() : null;
    }
}

