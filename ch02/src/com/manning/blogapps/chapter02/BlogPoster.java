package com.manning.blogapps.chapter02;

import java.util.*;
import java.io.*;

import org.apache.xmlrpc.XmlRpcClient;

public class BlogPoster {
    
    public static void main(String[] args) throws Exception {
        
        System.out.println("Blog Poster: Java Edition");
        String title = null;
        String description = null;
        if (args.length == 1) {
            description = args[0];
        } else if (args.length == 2) {
            title = args[0];
            description = args[1];
        } else {
            System.out.println("USAGE: blogposter [description] ");
            System.out.println("USAGE: blogposter [title] [description]");
            return;
        }
        
        Properties config = new Properties();
        config.load(new FileInputStream("config.properties"));
        String username = config.getProperty("username");
        String password = config.getProperty("password");
        String blogid = config.getProperty("blogid");
        String target = config.getProperty("target");
        
        Hashtable<String, Object> post = new Hashtable<String, Object>();
        post.put("dateCreated", new Date());
        if (title != null) post.put("title", title);
        post.put("description", description);
            
        Vector<Serializable> params = new Vector<Serializable>();
        params.addElement(blogid);
        params.addElement(username);
        params.addElement(password);
        params.addElement(post); // content
        params.addElement(Boolean.TRUE); // publish
            
        XmlRpcClient xmlrpc = new XmlRpcClient(target);
        String result = (String)xmlrpc.execute("metaWeblog.newPost", params);
        System.out.println("newPost result: " + result);
    }
    
}
