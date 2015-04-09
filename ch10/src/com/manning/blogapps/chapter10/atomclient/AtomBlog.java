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
package com.manning.blogapps.chapter10.atomclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import com.manning.blogapps.chapter10.blogclient.Blog;
import com.manning.blogapps.chapter10.blogclient.BlogClientException;
import com.manning.blogapps.chapter10.blogclient.BlogEntry;
import com.manning.blogapps.chapter10.blogclient.BlogResource;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.io.WireFeedInput;

/**
 * Atom blog implementation, wraps an Atom workspace.
 * @author Dave Johnson
 */
public class AtomBlog implements Blog {
	
    private Log logger = LogFactory.getLog(AtomBlog.class);
    private HttpClient httpClient = new HttpClient();
    private String name = null;
    private String username = null;
    private String password = null;    
    private Blog.Collection entriesCollection = null;
    private Blog.Collection resourcesCollection = null;
    private Map collections = new TreeMap();
  
    public String getName() { return name; }
    public String getToken() { return entriesCollection.getToken(); }
    public String toString() { return getName(); }
    
    /** 
     * Create BlogSite usign specified HTTPClient, user account and workspace.
     * Fetches Atom Service document from server to determine collection URI.s 
     */
    public AtomBlog(HttpClient httpClient, String username, String password, 
            AtomService.Workspace workspace) {
        
        this.httpClient = httpClient; 
        this.username = username;
        this.password = password;
        this.name = workspace.getTitle();
        Iterator members = workspace.getCollections().iterator();
        while (members.hasNext()) {
            AtomService.Collection col = (AtomService.Collection) members.next();
            if ("entry".equals(col.getAccept()) && entriesCollection == null) {
                // first entry collection is primary entry collection 
                entriesCollection = new AtomBlogCollection(col);
            }
            else if (col.getAccept() != null && !col.getAccept().equals("entry") && resourcesCollection == null) {
                // first non-entry collection is primary resource collection
                resourcesCollection = new AtomBlogCollection(col);
            } 
            collections.put(col.getHref(), new AtomBlogCollection(col));
        }    
    }  
    
    public BlogEntry newEntry() throws BlogClientException { 
        if (entriesCollection == null) throw new BlogClientException("No entry collection");
        AtomEntry entry = new AtomEntry(this);
        BlogEntry.Person author = new BlogEntry.Person();
        author.setName(username);
        entry.setAuthor(author);
        return entry;
    }  
    
    public BlogEntry getEntry(String editURI) throws BlogClientException {      
        GetMethod method = new GetMethod(editURI);
        AtomBlogConnection.addAuthentication(method, username, password);
        try {
            httpClient.executeMethod(method);         
            if (method.getStatusCode() != 200) {
                throw new BlogClientException("ERROR HTTP status code=" + method.getStatusCode());
            }
            String body = method.getResponseBodyAsString();
            Entry romeEntry = RomeUtilities.parseEntry(new StringReader(body));
            if (!isMediaEntry(romeEntry)) {
                return new AtomEntry(this, romeEntry, false);
            } else {
                return new AtomResource(this, romeEntry, false);
            }
        } catch (Exception e) {
            throw new BlogClientException("ERROR: getting or parsing entry", e);
        }
    }
    
    public Iterator getEntries() throws BlogClientException {
        if (entriesCollection == null) throw new BlogClientException("No entry collection");
        return new EntryIterator(entriesCollection.getToken());
    }   
    
    public Iterator getResources() throws BlogClientException {
        if (resourcesCollection == null) throw new BlogClientException("No entry collection");
        return new EntryIterator(resourcesCollection.getToken());
    }   
    
    public String saveEntry(BlogEntry entry) throws BlogClientException {
        if (entriesCollection == null) throw new BlogClientException("No entry collection");
        return entriesCollection.saveEntry(entry);
    } 
    
    public void deleteEntry(BlogEntry entry) throws BlogClientException {
        DeleteMethod method = new DeleteMethod(entry.getToken());
        AtomBlogConnection.addAuthentication(method, username, password);
        try {
            httpClient.executeMethod(method);          
        } catch (IOException ex) {
            throw new BlogClientException("ERROR: deleting entry", ex);
        }          
    }

    /** Returns empty list, Atom doesn't support get categories (yet) */
    public List getCategories() throws BlogClientException {
        return new ArrayList();
    }
    
    public BlogResource newResource(
        String name, String contentType, File file) throws BlogClientException {
        if (resourcesCollection == null) { 
            throw new BlogClientException("No resource collection");
        }
        return new AtomResource(this, name, contentType, file);
    }

    public String saveResource(BlogResource res) throws BlogClientException {
        if (resourcesCollection == null) throw new BlogClientException("No resource collection");
        return resourcesCollection.saveResource(res);
    }
         
    public void deleteResource(BlogResource resource) throws BlogClientException {
        deleteEntry((BlogEntry)resource);
    }
    
    InputStream getResourceAsStream(BlogResource resource) throws BlogClientException {
        GetMethod method = new GetMethod(resource.getContent().getSrc());
        try {
            httpClient.executeMethod(method);
            if (method.getStatusCode() != 200) {
                throw new BlogClientException("ERROR HTTP status=" + method.getStatusCode());
            }
            return method.getResponseBodyAsStream();
        } catch (IOException e) {
            throw new BlogClientException("ERROR: getting resource", e);
        }
    }    
    
    private boolean isMediaEntry(Entry entry) {
        boolean mediaEntry = false;
        List links = entry.getOtherLinks();
        for (Iterator it = links.iterator(); it.hasNext();) {
            Link link = (Link) it.next();
            if ("edit-media".equals(link.getRel())) {
                mediaEntry = true;
                break;
            }
        }
        return mediaEntry;
    } 

    public List getCollections() throws BlogClientException {
        return new ArrayList(collections.values());
    }

    public Blog.Collection getCollection(String token) throws BlogClientException {
        return (Blog.Collection)collections.get(token);
    } 
     
    public class AtomBlogCollection implements Blog.Collection {
        private AtomService.Collection col = null;
        
        public AtomBlogCollection(AtomService.Collection col) {
            this.col = col;            
        }
        
        public String getTitle() {
            return col.getTitle();
        }
        
        public String getToken() {
            return col.getHref();
        }

        public String getAccept() {
            return col.getAccept();
        }

        public boolean accepts(String ct) {
            String entryType = "application/atom+xml";
            boolean entry = entryType.equals(ct);
            if (entry && null == col.getAccept()) {
                return true;
            } else if (entry && "entry".equals(col.getAccept())) {
                return true;
            } else if (entry && entryType.equals(col.getAccept())) {
                return true;
            } else {
                String[] rules = col.getAccept().split(",");
                for (int i=0; i<rules.length; i++) {
                    String rule = rules[i].trim();
                    if (rule.equals(ct)) return true;
                    int slashstar = rule.indexOf("/*");
                    if (slashstar > 0) {
                        rule = rule.substring(0, slashstar + 1);
                        if (ct.startsWith(rule)) return true;
                    }
                }
            }
            return false;
        }

        public Iterator getEntries() throws BlogClientException {
            return new EntryIterator(col.getHref()); 
        }
 
        public String saveResource(BlogResource res) throws BlogClientException {
            EntityEnclosingMethod method = null;
            try {
                AtomResource resource = (AtomResource)res;
                if (resource.getToken() == null) {
                    // New resource, so POST file to server
                    method = new PostMethod(resourcesCollection.getToken());
                    method.setRequestBody(new FileInputStream(resource.getUploadFile()));
                    method.setRequestHeader(
                        "Content-type", resource.getContent().getType());                
                } 
                else if (resource.getToken() != null && resource.getUploadFile() != null) {
                    // existing resource and new file, so PUT file to edit-media URI
                    method = new PutMethod(resource.getEditMediaURI());
                    method.setRequestBody(new FileInputStream(resource.getUploadFile()));
                    method.setRequestHeader(
                        "Content-type", resource.getContent().getType());
                } 
                else if (resource.getToken() != null && resource.getUploadFile() == null) {
                    // existing resource and NO new file, so PUT entry to edit URI
                    method = new PutMethod(resource.getToken());
                    StringWriter sw = new StringWriter();
                    RomeUtilities.serializeEntry(resource.copyToRomeEntry(), sw);
                    method.setRequestBody(sw.toString());
                    method.setRequestHeader(
                        "Content-type", "application/atom+xml; charset=utf8"); 
                }
                AtomBlogConnection.addAuthentication(method, username, password);
                method.addRequestHeader("Title", resource.getName());
                httpClient.executeMethod(method); 
            } catch (Exception e) {
                throw new BlogClientException("ERROR: saving resource");
            }
            if (method.getStatusCode() != 201) { 
                throw new BlogClientException("ERROR HTTP status=" + method.getStatusCode());
            }
            Header locationHeader = method.getRequestHeader("location");
            return locationHeader == null ? null : locationHeader.getValue(); 
        }
      
        public String saveEntry(BlogEntry entry) throws BlogClientException {
            EntityEnclosingMethod method = null;
            boolean create = (entry.getToken() == null);
            if (create) {
                method = new PostMethod(col.getHref());
            } else {
                method = new PutMethod(entry.getToken());
            }
            AtomBlogConnection.addAuthentication(method, username, password);
            StringWriter sw = new StringWriter();
            try {
                RomeUtilities.serializeEntry(((AtomEntry)entry).copyToRomeEntry(), sw);
                method.setRequestBody(sw.toString());
                method.setRequestHeader(
                    "Content-type", "application/atom+xml; charset=utf8");        
                httpClient.executeMethod(method);
                String body = method.getResponseBodyAsString();
                if (method.getStatusCode() != 200 && method.getStatusCode() != 201) {
                    throw new BlogClientException(
                        "ERROR HTTP status=" + method.getStatusCode() + " : " + body);
                }
            } catch (Exception e) {
                throw new BlogClientException("ERROR: saving entry", e);
            }
            Header locationHeader = method.getRequestHeader("location");
            return locationHeader == null ? null : locationHeader.getValue();        
        }
    }
    
    public class EntryIterator implements Iterator {
        int maxEntries = 20;
        int offset = 0;
        Iterator members = null;
        Feed col = null;
        String collectionURI;
        String nextURI;
        
        public EntryIterator(String uri) throws BlogClientException {
            collectionURI = uri;
            nextURI = uri;
            getNextEntries();
        }
        public boolean hasNext() {
            if (!members.hasNext()) {
                try { 
                    getNextEntries(); 
                } catch (Exception ignored) {
                    logger.error("ERROR getting next entries", ignored);
                }
            }
            return members.hasNext();
        }
        public Object next() {
            if (hasNext()) {
                Entry romeEntry = (Entry)members.next();
                if (!isMediaEntry(romeEntry)) {
                    return new AtomEntry(AtomBlog.this, romeEntry, true);
                } else { 
                    return new AtomResource(AtomBlog.this, romeEntry, true);
                }
            }
            throw new NoSuchElementException();
        }
        public void remove() {
            // optional method, not implemented
        }
        private void getNextEntries() throws BlogClientException {  
            if (nextURI == null) return;
            
            GetMethod colGet = new GetMethod(nextURI);
            AtomBlogConnection.addAuthentication(colGet, username, password);
            try {
                httpClient.executeMethod(colGet);
                SAXBuilder builder = new SAXBuilder();
                String s = colGet.getResponseBodyAsString();
                Document doc = builder.build(new StringReader(s));

                WireFeedInput feedInput = new WireFeedInput();
                col = (Feed)feedInput.build(doc);
            } catch (Exception e) {
                throw new BlogClientException("ERROR: fetching or parsing next entries", e);
            }
            members = col.getEntries().iterator();
            offset += col.getEntries().size();

            nextURI = null;
            List altLinks = col.getOtherLinks();
            if (altLinks != null) {
                Iterator iter = altLinks.iterator();
                while (iter.hasNext()) {
                    Link link = (Link)iter.next();
                    if ("next".equals(link.getRel())) {
                        nextURI = link.getHref();
                    }
                }
            }
        }
    }
    
}
