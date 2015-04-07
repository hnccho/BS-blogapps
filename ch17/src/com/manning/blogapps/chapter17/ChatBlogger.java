package com.manning.blogapps.chapter17;
import java.io.*;
import java.util.*;
import com.ecyrd.jspwiki.*;
import org.relayirc.chatengine.*;
import com.ecyrd.jspwiki.providers.FileSystemProvider;
import com.manning.blogapps.chapter10.blogclient.*;

public class ChatBlogger extends ServerAdapter {  //|#1
   private Blog blog;    //|#2
   private Server server;
   private WikiContext context;
   private String chat_channel;  //|#3
   private List categories = new ArrayList();
   
   public static void main(String args[]) throws Exception {  //|#4
       new ChatBlogger();
   }
   public ChatBlogger() throws Exception {  //|#5
      Properties config = new Properties();                        
      config.load(new FileInputStream("config.properties"));  //|#6       
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
      BlogConnection con = BlogConnectionFactory.getBlogConnection(  //|#7   
         blog_apitype, blog_url, blog_username, blog_password);
      blog = con.getBlog(blog_id);                
      
      String wiki_url = config.getProperty("wiki_url");
      Properties wikiprops = new Properties();
      wikiprops.setProperty(
        "jspwiki.fileSystemProvider.pageDir",".");  
      wikiprops.setProperty(
        "jspwiki.basicAttachmentProvider.storageDir",".");      
      wikiprops.setProperty(
        "jspwiki.baseURL", wiki_url);      
      wikiprops.setProperty("jspwiki.pageProvider",
        "com.manning.blogapps.chapter17.PageProvider");
      WikiEngine engine = new WikiEngine(wikiprops);  
      context = new WikiContext(engine, new WikiPage("dummy"));  //|#8
      
      String chat_hostname = config.getProperty("chat_hostname");
      String chat_port =     config.getProperty("chat_port");
      String chat_nick =     config.getProperty("chat_nick");
      String chat_altnick =  config.getProperty("chat_altnick");     
      int port = Integer.parseInt(chat_port);
      server = new Server(chat_hostname, port, "n/a", "n/a");  //|#9
      
      server.addServerListener(this);  //|#10
      server.connect(chat_nick, chat_altnick, chat_nick, chat_nick); //#11
   }
   public void onConnect(ServerEvent event) {  //|#12
      System.out.println("Connected!");
      server.sendJoin(chat_channel);    //|#13
   }
   public void onChannelJoin(ServerEvent event) {  //|#14
      Channel chan = (Channel)event.getChannel();
      System.out.println("Joined "+chan);
      chan.addChannelListener(
         new ChannelBlogger(this, server.getNick()));
   }
   public String blog(String title, String content) throws Exception { //|#15
       StringReader reader = new StringReader(content);            
       TranslatorReader tr = new TranslatorReader(context, reader);  
       BlogEntry entry = blog.newEntry();  //|#16
       entry.setTitle(title);
       entry.setCategories(categories);
       entry.setContent(
          new BlogEntry.Content(FileUtil.readContents(tr)));  //|#17
       entry.save();      //|#18
       return entry.getId();  //|#19
   }
   public void undoLastBlog() throws Exception {     //|#20
       Iterator entries = blog.getEntries();
       BlogEntry entry = (BlogEntry)entries.next();  //|#21
       if (entry != null) entry.delete();
   }
   public void stop() {                   //|#22
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
