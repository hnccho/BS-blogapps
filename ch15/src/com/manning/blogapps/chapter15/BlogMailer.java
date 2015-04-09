package com.manning.blogapps.chapter15;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import com.manning.blogapps.chapter05.AnyFeedParser;
import com.manning.blogapps.chapter05.IFeedParser;

/* 
<?xml version="1.0" encoding="utf-8" ?> 
<cross-poster>
    <subscription>
        <feed-url>BLOG1_FEED_URL</feed-url>
    </subscription>
    <subscription>
        <feed-url>BLOG2_FEED_URL</feed-url>
    </subscription>
    <destination>   
        <smtp-server>SMTP_SERVER</smtp-server>
        <username>MAIL_USERNAME</username>
        <password>MAIL_PASSWORD</password>
        <from-address>FROM_ADDRESS</from-address>
        <to-address>FROM_ADDRESS</to-address>
    </destination>
</cross-poster>
*/
public class BlogMailer {
	
    String smtp_server = null;
    String username = null;
    String password = null;
    String to_address = null;
    String from_address = null;
    List subscriptions = new ArrayList();

    public static void main(String[] args) throws Exception {
        BlogMailer blogMailer = new BlogMailer();
        blogMailer.initFromXml();
        blogMailer.run();
    }
    
    public void run() throws Exception {
    	
        System.out.println("BlogMailer for Java");
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, -72);
        Date since = cal.getTime();
        long sinceLong = since.getTime();
        
        IFeedParser feedParser = new AnyFeedParser();
        
        StringBuffer sb = new StringBuffer();
        
        Iterator subs = subscriptions.iterator();
        
        while (subs.hasNext()) {
            String sub = (String)subs.next();
            
            URL url = new URL(sub);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setIfModifiedSince(sinceLong);
            if (con.getLastModified() > sinceLong) {
                Map feedMap = feedParser.parseFeed(
                   new InputStreamReader(con.getInputStream()));
                List itemsList = (List)feedMap.get("items");
                Iterator items = itemsList.iterator();
                
                sb.append("-----------------------------\n");
                sb.append("Blog: ");
                sb.append(feedMap.get("title"));
                sb.append("\n\n");
                
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
                      sb.append("Title: ");
                      sb.append((String)itemMap.get("title"));
                      sb.append("\n");
                      sb.append(itemMap.get("link"));
                      sb.append("\n\n"); 
                  }
               }
            }
        }
        
        String text = sb.toString();
        if (text.trim().length() > 0) {
            
            // Create a mail session
            java.util.Properties props = new java.util.Properties();
            props.put("mail.smtp.host", smtp_server);
            props.put("mail.smtp.auth", "true");
            //props.put("mail.smtp.port", ""+smtpPort);
            Session session = Session.getDefaultInstance(props, null);
    
            // Construct the message
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from_address));
            msg.setRecipient(
                    Message.RecipientType.TO, new InternetAddress(to_address));
            msg.setSubject("Daily Blog Digest");
            msg.setText(text);
    
            // Send the message
            if (username != null && password != null) {
                Transport tr = session.getTransport("smtp");
                tr.connect(smtp_server, username, password);
                msg.saveChanges();  // don't forget this
                tr.sendMessage(msg, msg.getAllRecipients());
                tr.close();
            } else {
                Transport.send(msg);
            }
            System.out.println("Sent Daily Blog Digest:");
            System.out.println(sb.toString());
        }
        else {
            System.out.println("No new messages to send");
        }
    }
    
    private void initFromXml() throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new FileInputStream(
                "BlogMailer.config.xml"));
        Element root = doc.getRootElement();
        smtp_server = getString(root, "/blog-mailer/destination/smtp-server");
        username = getString(root, "/blog-mailer/destination/username");
        password = getString(root, "/blog-mailer/destination/password");
        from_address = getString(root, "/blog-mailer/destination/from-address");
        to_address = getString(root, "/blog-mailer/destination/to-address");
        XPath itemsPath = XPath.newInstance("/blog-mailer/subscription");
        Iterator iter = itemsPath.selectNodes(root).iterator();
        while (iter.hasNext()) {
            Element element = (Element) iter.next();
            String sub = getString(element,"feed-url");
            subscriptions.add(sub);
        }
    }
    
    private String getString(Element elem, String path) throws Exception {
        XPath xpath = XPath.newInstance(path);
        Element e = (Element)xpath.selectSingleNode(elem);
        return e!=null ? e.getText() : null;
    }
    
}

