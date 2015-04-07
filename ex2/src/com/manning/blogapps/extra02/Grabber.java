package com.manning.blogapps.extra02;

import com.manning.blogapps.chapter10.blogclient.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import org.apache.commons.lang.StringEscapeUtils;

public class Grabber {
    public static void main(String[] ags) throws Exception { 
        new Grabber(); 
    }
    public Grabber() throws Exception {
        
        Properties config = new Properties();                        
        config.load(new FileInputStream("config.properties"));   

        String download_dir =  config.getProperty("download_dir"); 
        String blog_apitype =  config.getProperty("blog_apitype"); 
        String blog_username = config.getProperty("blog_username");
        String blog_password = config.getProperty("blog_password");
        String blog_id =       config.getProperty("blog_id");
        String blog_url =      config.getProperty("blog_url");
        int    max_entries =   Integer.parseInt(config.getProperty("max_entries"));
      
        File dldir = new File(download_dir);
        dldir.mkdirs();
        FileWriter indexfw = new FileWriter(
            dldir.getAbsolutePath() + File.separator + "grabber-index.html");
        PrintWriter indexpw = new PrintWriter(indexfw);
        indexpw.println("<html><head><title>Index of posts</title></head>");
        indexpw.println("<body><h1>Index of posts</h1><ul>");
         
        BlogConnection con = BlogConnectionFactory.getBlogConnection(
           blog_apitype, blog_url, blog_username, blog_password);
        Blog blog = con.getBlog(blog_id);
        Iterator entries = blog.getEntries();
        int count = 0; 
        while (entries.hasNext() && count < max_entries) {
            
            BlogEntry entry = (BlogEntry)entries.next();
            Date pubDate = entry.getPublicationDate();
            SimpleDateFormat format8 = new SimpleDateFormat("yyyyMMdd-HHmm");            
            String dateString = format8.format(pubDate);
            String filename = dateString+".html";
            FileWriter fw = new FileWriter(
                dldir.getAbsolutePath() + File.separator + filename);
            
            PrintWriter pw = new PrintWriter(fw);
            pw.println("<html><head><title>"+entry.getTitle()+"</title></head>");
            pw.println("<body><h1>"+entry.getTitle()+"</h1>");
            pw.println("<p id=\"permalink\">"+entry.getPermalink()+"</p>");
            pw.println(
               StringEscapeUtils.unescapeHtml(entry.getContent().getValue()));
            pw.println("</body></html>");
            pw.flush();
            pw.close();
            
            indexpw.println("<li><a href=\"" + filename + "\">" 
                    + entry.getTitle() + " [" + dateString + "]</a></li>");
            count++;
        }
        indexpw.println("</ul></body></html>");
        indexpw.flush();
        indexpw.close();
    }
}
