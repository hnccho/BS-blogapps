package com.manning.blogapps.chapter14;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import com.manning.blogapps.chapter10.blogclient.*;

/**
 1- read configuration file:
 
    <mail-blogger>
         <codeword>CODEWORD</codeword>
         <mailbox>
             <username>MAIL USERNAME</username>
             <password>MAIL PASSWORD</password>
             <pop-server>POP SERVER</pop-server>
         </mailbox>
         <destination>
             <metaweblog-url>METAWEBLOG URL</metaweblog-url>
             <username>BLOG USERNAME</username>
             <password>BLOG PASSWORD</password>
             <blogid>BLOG ID</blogid>
             <category>CATEGORY 1</category>
             <category>CATEGORY N</category>
         </destination>
     </mail-blogger>
     
 2- get mail from mailbox
 3- for each mail message
     3.1- if first line is "MailBlogger:CODEWORD" then
         3.1.1- post rest of message to destination blog
         3.1.2- delete message from mailbox
*/
public class MailBlogger {
    String codeword;
    String mail_username;
    String mail_password;
    String mail_pop_server;
    String dest_metaweblog_url;
    String dest_username;
    String dest_password;
    String dest_blogid;
    List dest_categories = new ArrayList();

    public static void main(String[] args) throws Exception {
        MailBlogger mailBlogger = new MailBlogger();
        mailBlogger.InitFromXml();
        mailBlogger.run();
    }
    private void run() throws Exception {
        Properties props = new Properties();
        props.put("mail.store.protocol", "pop");       
        Session session = Session.getDefaultInstance(props);
        Store store = session.getStore("pop3");
        store.connect(mail_pop_server, mail_username, mail_password);
        
        Folder folder = store.getDefaultFolder();
        Folder inbox = folder.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);
        
        BlogConnection con = 
            BlogConnectionFactory. getBlogConnection(
                "metaweblog", dest_metaweblog_url, dest_username, dest_password);
        Blog blog = con.getBlog(dest_blogid);

        int count = 0;
        Message[] messages = inbox.getMessages();
        for (int i=0; i<messages.length; i++) {
            Message message = messages[i];
            String body = message.getContent().toString().trim();
            StringTokenizer toker = new StringTokenizer(body,"\n");
            if (body != null && body.startsWith("MailBlogger:")) {
                String cw = null;
                if (toker.hasMoreElements()) {
                    cw = toker.nextToken().trim();
                    if (cw.length() > 12) cw = cw.substring(12);                    
                }
                if (cw.equals(codeword)) {
                    int start = body.indexOf("\n");
                    String content = body.substring(start);
                    String title = message.getSubject();
                    System.out.println("title: "+title);
                    System.out.println("content: "+body);
                    BlogEntry entry = blog.newEntry();
                    entry.setCategories(dest_categories);
                    entry.setTitle(title);
                    entry.setPublicationDate(new Date());
                    BlogEntry.Content
                       entryContent = new BlogEntry.Content(content);
                    entry.setContent(entryContent);
                    entry.save();
                    message.setFlag(Flags.Flag.DELETED, true);
                    count++;
                }
            }
        }
        inbox.close(true); // true to expunge (i.e. delete marked items)
        store.close(); 
        System.out.println("Posted " + count + " blog entries");
    }
    private void InitFromXml() throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new FileInputStream("MailBlogger.config.xml"));
        Element root = doc.getRootElement();
        codeword = getString(root,"/mail-blogger/codeword");    
        mail_username = getString(root,"/mail-blogger/mailbox/username");    
        mail_password = getString(root,"/mail-blogger/mailbox/password");    
        mail_pop_server = getString(root,"/mail-blogger/mailbox/pop-server");   
        dest_metaweblog_url = getString(root,"/mail-blogger/destination/metaweblog-url");
        dest_username = getString(root,"/mail-blogger/destination/username");
        dest_password = getString(root,"/mail-blogger/destination/password");
        dest_blogid = getString(root,"/mail-blogger/destination/blogid");
        XPath itemsPath = XPath.newInstance("/mail-blogger/destination/category");            
        Iterator iter = itemsPath.selectNodes(root).iterator();
        while (iter.hasNext()) {
            Element element = (Element)iter.next();
            BlogEntry.Category cat = new BlogEntry.Category();
            cat.setName(element.getText());
            dest_categories.add(cat);
        } 
    }
    private String getString(Element elem, String path) throws Exception {
        XPath xpath = XPath.newInstance(path);
        Element e = (Element)xpath.selectSingleNode(elem);
        return e!=null ? e.getText() : null;
    }
}

