/*
 * Copyright 2005-2006, Dave Johnson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.manning.blogapps.chapter10.examples;

import java.io.FileInputStream;
import java.util.Properties;

import com.manning.blogapps.chapter10.blogclient.Blog;
import com.manning.blogapps.chapter10.blogclient.BlogConnection;
import com.manning.blogapps.chapter10.blogclient.BlogConnectionFactory;
import com.manning.blogapps.chapter10.blogclient.BlogEntry;

public class BlogPoster {
    
    public static void main(String[] args) throws Exception {
        
        System.out.println("Blog Poster: Java Blog Client Library Edition");
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
        
        BlogConnection con = BlogConnectionFactory.getBlogConnection(
                "atom", target, username, password);
        Blog blog = (Blog)con.getBlogs().get(0);
        BlogEntry entry = blog.newEntry();
        entry.setTitle(title);
        entry.setContent(new BlogEntry.Content(description));
        entry.save();
        System.out.println("newPost result: " + entry.getId());
    }
    
}
