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
package com.manning.blogapps.chapter10.metaweblogclient;

import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

import com.manning.blogapps.chapter10.blogclient.BlogConnection;
import com.manning.blogapps.chapter10.blogclient.Blog;
import com.manning.blogapps.chapter10.blogclient.BlogClientException;

/**
 * BlogClient implementation that uses a mix of Blogger and MetaWeblog API methods.
 * @author Dave Johnson
 */
public class MetaWeblogConnection implements BlogConnection {
    private URL url = null;
    private String userName = null;
    private String password = null;
    private XmlRpcClient client = null;
    private String appkey = "null";
    private Map blogs = null;
    
    private XmlRpcClient getXmlRpcClient() { return client; }
        
    public MetaWeblogConnection(String url, String userName, String password) 
            throws BlogClientException {
        this.userName = userName;
        this.password = password;
        try {
            this.url = new URL(url);
            this.client = new XmlRpcClient(url);
            blogs = createBlogMap();
        } catch (Throwable t) {
            throw new BlogClientException("ERROR connecting to server", t);
        }
    }
    public List getBlogs() {
        return new ArrayList(blogs.values());
    }
    private Map createBlogMap() throws XmlRpcException, IOException {
        Map blogMap = new HashMap();
        Vector params = new Vector();
        params.addElement(appkey);
        params.addElement(userName);
        params.addElement(password);
        Vector results = (Vector)
            getXmlRpcClient().execute("blogger.getUsersBlogs", params);
        Iterator iter = results.iterator();
        int i=0;
        while (iter.hasNext()) {
            Hashtable blog = (Hashtable)iter.next();
            String blogid = (String)blog.get("blogid");
            String name = (String)blog.get("blogName");
            blogMap.put(blogid, 
                new MetaWeblogBlog(blogid, name, url, userName, password));
        }
        return blogMap;
    }
    public Blog getBlog(String token) {
        return (Blog)blogs.get(token);
    }
    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }
}

