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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;

import org.apache.xmlrpc.XmlRpcClient;

import com.manning.blogapps.chapter10.blogclient.BlogEntry;
import com.manning.blogapps.chapter10.blogclient.Blog;
import com.manning.blogapps.chapter10.blogclient.BlogClientException;
import com.manning.blogapps.chapter10.blogclient.BlogResource;
import java.util.Map;
import java.util.TreeMap;

/**
 * Blog implementation that uses a mix of Blogger and MetaWeblog API methods.\
 *
 * @author Dave Johnson
 */
public class MetaWeblogBlog implements Blog {
    private String blogid;
    private String name;
    private URL url;
    private String userName;
    private String password;
    private String appkey = "dummy";
    private XmlRpcClient xmlRpcClient = null;
    private Map collections;
    
    public String getName() { return name; }
    public String getToken() { return blogid; }
    public String toString() { return getName(); }
    
    private XmlRpcClient getXmlRpcClient() { 
        if (xmlRpcClient == null) {
            xmlRpcClient = new XmlRpcClient(url);
        }
        return xmlRpcClient; 
    }
    
    public MetaWeblogBlog(String blogid, String name, 
            URL url, String userName, String password) {
        this.blogid = blogid;
        this.name = name;
        this.url = url;
        this.userName = userName;
        this.password = password;
        this.collections = new TreeMap();
        collections.put("entries", 
            new MetaWeblogBlogCollection("entries", "Entries", "entry"));
        collections.put("resources", 
            new MetaWeblogBlogCollection("resources", "Resources", "*"));
    }

    public MetaWeblogBlog(String blogId, String name, 
            URL url, String userName, String password, String appkey) {
        this(blogId, name, url, userName, password);
        this.appkey = appkey;
    }

    public BlogEntry newEntry() {
        return new MetaWeblogEntry(this, new Hashtable());
    }

    public String saveEntry(BlogEntry entry) throws BlogClientException {
        Blog.Collection col = (Blog.Collection)collections.get("entries");
        return col.saveEntry(entry);
    }

    public BlogEntry getEntry(String id) throws BlogClientException {
        Vector params = new Vector ();
        params.addElement(id);
        params.addElement(userName);
        params.addElement(password);
        try {
            Hashtable result = (Hashtable)
                getXmlRpcClient().execute("metaWeblog.getPost", params);
            return new MetaWeblogEntry(this, result);
        } catch (Exception e) {
            throw new BlogClientException("ERROR: XML-RPC error getting entry", e);
        }
    }

    public void deleteEntry(String id) throws BlogClientException {
        Vector params = new Vector ();
        params.addElement(appkey);
        params.addElement(id);
        params.addElement(userName);
        params.addElement(password);
        params.addElement(Boolean.FALSE);
        try {
            getXmlRpcClient().execute("blogger.deletePost", params);
        } catch (Exception e) {
            throw new BlogClientException("ERROR: XML-RPC error getting entry", e);
        }
    }

    public Iterator getEntries() throws BlogClientException {
        return new EntryIterator();
    }

    public BlogResource newResource(String name, String contentType, File file) {
        return new MetaWeblogResource(this, name, contentType, file);
    } 

    public String saveResource(MetaWeblogResource resource) throws BlogClientException {  
        Blog.Collection col = (Blog.Collection)collections.get("resources");
        return col.saveResource(resource);
    }
    
    /** Returns null, MetaWeblog API does not support get resource. */
    public BlogResource getResource(String token) throws BlogClientException {
        return null;
    }
    
    /** Returns null, MetaWeblog API does not support get resources. */
    public Iterator getResources() throws BlogClientException {
        return new NoOpIterator();
    }

    /** Does nothing, MetaWeblog API does not support delete resource. */
    public void deleteResource(BlogResource resource) throws BlogClientException {
        // no-op
    }
    
    public List getCategories() throws BlogClientException {
        Vector params = new Vector ();
        params.addElement(blogid);
        params.addElement(userName);
        params.addElement(password); 
        ArrayList ret = new ArrayList();
        
        try {
            Object result = 
                getXmlRpcClient().execute ("metaWeblog.getCategories", params);
            if (result != null && result instanceof Hashtable) {
                // Standard MetaWeblog API style: struct of struts
                Hashtable catsmap = (Hashtable)result;
                Enumeration keys = catsmap.keys();
                while (keys.hasMoreElements()) {
                    String key = (String)keys.nextElement();
                    Hashtable catmap = (Hashtable)catsmap.get(key);
                    BlogEntry.Category category = new BlogEntry.Category(key);
                    category.setName((String)catmap.get("description"));
                    // catmap.get("htmlUrl"); 
                    // catmap.get("rssUrl");
                    ret.add(category);
                } 
            } else if (result != null && result instanceof Vector) {
                // Wordpress style: array of structs
                Vector vector = (Vector)result;
                Enumeration cats = vector.elements();
                while (cats.hasMoreElements()) {
                    Hashtable catmap = (Hashtable)cats.nextElement();
                    String categoryId = (String)catmap.get("categoryId");
                    String categoryName = (String)catmap.get("categoryName");
                    // catmap.get("description"); 
                    // catmap.get("htmlUrl"); 
                    // catmap.get("rssUrl");
                    BlogEntry.Category category = new BlogEntry.Category(categoryId);
                    category.setName(categoryName);
                    ret.add(category);
                } 
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    private Hashtable createPostStructure(BlogEntry entry) {
        return ((MetaWeblogEntry)entry).toPostStructure();
    }
        
    /** Returns the contents of the file in a byte array (from JavaAlmanac). */
    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
    
        // Get the size of the file
        long length = file.length();
    
        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
    
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    public List getCollections() throws BlogClientException {
        return new ArrayList(collections.values());
    }

    public Blog.Collection getCollection(String token) throws BlogClientException {
        return (Blog.Collection)collections.get(token);
    }
    
    //-------------------------------------------------------------------------
    public class MetaWeblogBlogCollection implements Blog.Collection {
        private String accept = null;
        private String title = null;
        private String token = null;
        
        /**
         * @param token  Identifier for collection, unique within blog
         * @param title  Title of collection
         * @param accept Content types accepted, either "entry" or "*"
         */
        public MetaWeblogBlogCollection(String token, String title, String accept) {
            this.accept = accept;
            this.title = title;
            this.token = token;
        }
        public String getTitle() {
            return title;
        }

        public String getToken() {
            return token;
        }

        public String getAccept() {
            return accept;
        }

        public boolean accepts(String ct) {
            if (accept.equals("*")) {
                // everything accepted
                return true;
            } else if (accept.equals("entry") && ct.equals("application/metaweblog+xml")) {
                // entries only accepted and "application/metaweblog+xml" means entry
                return true;
            }
            return false;
        }

        public Iterator getEntries() throws BlogClientException {
            Iterator ret = null;
            if (accept.equals("entry")) {
                ret = MetaWeblogBlog.this.getEntries();
            } else {
                ret = MetaWeblogBlog.this.getResources();
            }
            return ret;
        }

        public String saveEntry(BlogEntry entry) throws BlogClientException {
            String ret = entry.getId();
            if (entry.getId() == null) {
                Vector params = new Vector();
                params.addElement(blogid);
                params.addElement(userName);
                params.addElement(password);
                params.addElement(createPostStructure(entry));
                params.addElement(new Boolean(!entry.getDraft()));
                try {
                    ret = (String)getXmlRpcClient().execute("metaWeblog.newPost", params);
                } catch (Exception e) {
                    throw new BlogClientException("ERROR: XML-RPC error saving new entry", e);
                }
            } else {
                Vector params = new Vector();
                params.addElement(entry.getId());
                params.addElement(userName);
                params.addElement(password);
                params.addElement(createPostStructure(entry));
                params.addElement(new Boolean(!entry.getDraft()));
                try {
                    getXmlRpcClient().execute("metaWeblog.editPost", params);            
                } catch (Exception e) {
                    throw new BlogClientException("ERROR: XML-RPC error updating entry", e);
                }
            }
            return ret;
        }
        
        public String saveResource(BlogResource res) throws BlogClientException {
            MetaWeblogResource resource = (MetaWeblogResource)res;
            try {
                Hashtable resmap = new Hashtable();
                resmap.put("name", resource.getName());
                resmap.put("type", resource.getContent().getType());
                resmap.put("bits", getBytesFromFile(resource.getUploadFile()));        
                Vector params = new Vector();
                params.addElement(blogid);
                params.addElement(userName);
                params.addElement(password);
                params.addElement(resmap);                    
                Hashtable result = (Hashtable)
                    getXmlRpcClient().execute("metaWeblog.newMediaObject", params);
                String url = (String)result.get("url");
                ((MetaWeblogResource)resource).setURL(url);
                return url;
            } catch (Exception e) {
                throw new BlogClientException("ERROR: loading or uploading file"); 
            }
        }
    }  
    
    //-------------------------------------------------------------------------
    public class EntryIterator implements Iterator {
        private int pos = 0;
        private boolean eod = false;
        private static final int BUFSIZE = 30;
        private Vector results = null;
        public EntryIterator() throws BlogClientException {
            getNextEntries();
        }
        public boolean hasNext() {
            if (pos == results.size() && !eod) {
                try { getNextEntries(); } catch (Exception ignored) {}
            }
            return (pos < results.size());
        }
        public Object next() {
            Hashtable entryHash = (Hashtable)results.get(pos++);
            return new MetaWeblogEntry(MetaWeblogBlog.this, entryHash);
        }
        public void remove() {
            // not supported by MetaWeblog API
        }
        private void getNextEntries() throws BlogClientException {
            int requestSize = pos + BUFSIZE;
            Vector params = new Vector ();
            params.addElement(blogid);
            params.addElement(userName);
            params.addElement(password);
            params.addElement(new Integer(requestSize));
            try {
                results = (Vector)
                    getXmlRpcClient().execute("metaWeblog.getRecentPosts", params);
            } catch (Exception e) {
                throw new BlogClientException("ERROR: XML-RPC error getting entry", e);
            }
            if (results.size() < requestSize) eod = true;
        }
    }
    
    //-------------------------------------------------------------------------
    /**
     * No-op iterator.
     */
    public class NoOpIterator implements Iterator {
        public boolean hasNext() {
            return false;
        }
        public Object next() {
            return null;
        }        
        public void remove() {}
    }
    

}
