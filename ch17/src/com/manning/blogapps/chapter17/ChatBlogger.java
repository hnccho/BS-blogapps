package com.manning.blogapps.chapter17;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.relayirc.chatengine.Channel;
import org.relayirc.chatengine.Server;
import org.relayirc.chatengine.ServerAdapter;
import org.relayirc.chatengine.ServerEvent;

import com.ecyrd.jspwiki.FileUtil;
import com.ecyrd.jspwiki.TranslatorReader;
import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.WikiPage;
import com.ecyrd.jspwiki.providers.FileSystemProvider;
import com.manning.blogapps.chapter10.blogclient.Blog;
import com.manning.blogapps.chapter10.blogclient.BlogConnection;
import com.manning.blogapps.chapter10.blogclient.BlogConnectionFactory;
import com.manning.blogapps.chapter10.blogclient.BlogEntry;

public class ChatBlogger extends ServerAdapter {  
	
   private Blog blog;    
   private Server server;
   private WikiContext context;
   private String chat_channel;  
   private List categories = new ArrayList();
   
   public static void main(String args[]) throws Exception {  
       new ChatBlogger();
   }
   
   public ChatBlogger() throws Exception { 
      Properties config = new Properties();                        
      config.load(new FileInputStream("config.properties"));       
      chat_channel = config.getProperty("chat_channel");

      String blog_apitype =  config.getProperty("blog_apitype"); 
      String blog_username = config.getProperty("blog_username");
      String blog_password = config.getProperty("blog_password");
      String blog_id =       config.getProperty("blog_id");
      String blog_url =      config.getProperty("blog_url");
      String category =      config.getProperty("blog_category");
      if (category != null) {
         BlogEntry.Category cat = new BlogEntry.Category(category);
         cat.setName(category);
         categories.add(cat);
      }
      BlogConnection con = BlogConnectionFactory.getBlogConnection(     
         blog_apitype, blog_url, blog_username, blog_password);
      blog = con.getBlog(blog_id);                
      
      String wiki_url = config.getProperty("wiki_url");
      Properties wikiprops = new Properties();
      wikiprops.setProperty("jspwiki.fileSystemProvider.pageDir",".");  
      wikiprops.setProperty("jspwiki.basicAttachmentProvider.storageDir",".");      
      wikiprops.setProperty("jspwiki.baseURL", wiki_url);      
      wikiprops.setProperty("jspwiki.pageProvider", "com.manning.blogapps.chapter17.PageProvider");
      WikiEngine engine = new WikiEngine(wikiprops);  
      context = new WikiContext(engine, new WikiPage("dummy"));  
      
      String chat_hostname = config.getProperty("chat_hostname");
      String chat_port =     config.getProperty("chat_port");
      String chat_nick =     config.getProperty("chat_nick");
      String chat_altnick =  config.getProperty("chat_altnick");     
      int port = Integer.parseInt(chat_port);
      server = new Server(chat_hostname, port, "n/a", "n/a");  
      
      server.addServerListener(this);  
      server.connect(chat_nick, chat_altnick, chat_nick, chat_nick); 
   }
   
   public void onConnect(ServerEvent event) {  
      System.out.println("Connected!");
      server.sendJoin(chat_channel);    
   }
   
   public void onChannelJoin(ServerEvent event) {  
      Channel chan = (Channel)event.getChannel();
      System.out.println("Joined "+chan);
      chan.addChannelListener(new ChannelBlogger(this, server.getNick()));
   }
   
   public String blog(String title, String content) throws Exception { 
       StringReader reader = new StringReader(content);            
       TranslatorReader tr = new TranslatorReader(context, reader);  
       BlogEntry entry = blog.newEntry();  
       entry.setTitle(title);
       entry.setCategories(categories);
       entry.setContent(new BlogEntry.Content(FileUtil.readContents(tr)));  
       entry.save();      
       return entry.getId();  
   }
   
   public void undoLastBlog() throws Exception {     
       Iterator entries = blog.getEntries();
       BlogEntry entry = (BlogEntry)entries.next();  
       if (entry != null) entry.delete();
   }
   
   public void stop() {                   
      System.out.println("Stopping...");
      server.disconnect();  
   }
   
   public void onDisconnect(ServerEvent event) {
      System.out.println("Disconnected: good-bye!");
      System.exit(0);
   }
   
   public class PageProvider extends FileSystemProvider { 
      public boolean pageExists(String pageName) {
         return true;
      }
   }
   
}
