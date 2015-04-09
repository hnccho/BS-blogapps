
package com.manning.blogapps.chapter17;
import org.relayirc.chatengine.Channel;
import org.relayirc.chatengine.ChannelAdapter;
import org.relayirc.chatengine.ChannelEvent;

public class ChannelBlogger extends ChannelAdapter {  
	
   private ChatBlogger chatBlogger;
   private String nick;
   private long lastBlogTime = 0L;
   public static final String TITLE = "title:";
   public static final String CONTENT = "content:";

   public ChannelBlogger(
           ChatBlogger chatBlogger, String nick) {  
       this.nick = nick;
       this.chatBlogger = chatBlogger;
   }
   
   public void onMessage(ChannelEvent event) {        
       Channel channel = (Channel)event.getSource();  
       try {
           String line = (String)event.getValue();
           if (line.startsWith(nick+" exit")) {     
               chatBlogger.stop(); 
           } 
           else if (line.startsWith(nick+" undo")) {         
               long currentTime = System.currentTimeMillis();
               if (currentTime - lastBlogTime < 30*1000) {
                  channel.sendPrivMsg("Performing undo");                      
                  chatBlogger.undoLastBlog();
                  lastBlogTime = 0L;
               } 
               else {
                  channel.sendPrivMsg("Too late to undo");
               }
           } 
           else if (line.startsWith(nick+" ")) {     
               String input = line.substring(nick.length()+1);
               int titleloc = input.indexOf(TITLE);
               int contentloc = input.indexOf(CONTENT);
               if (titleloc != -1 && contentloc != -1) {  
                   String title = input.substring(
                       titleloc +TITLE.length(),contentloc-1);
                   String content = input.substring(
                       contentloc + CONTENT.length()); 
                   
                   String id = chatBlogger.blog(title, content); 
                   lastBlogTime = System.currentTimeMillis();
                   channel.sendPrivMsg("Posted [" + id + "]");
               } else {
                   channel.sendPrivMsg("Ignoring, need title: and content:");
               }
           } 
           else if (line.startsWith(nick)) {   
               channel.sendPrivMsg("Unknown command");
           }
       } catch (Exception e) {
           if (e.getCause() != null) {
               System.out.println(e.getCause().getMessage());
           } else {
               e.printStackTrace();
           }
           channel.sendPrivMsg("ERROR processing command");
       }           
    }
   
}


