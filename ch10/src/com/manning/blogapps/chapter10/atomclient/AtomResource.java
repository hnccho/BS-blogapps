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
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import com.manning.blogapps.chapter10.blogclient.BlogClientException;
import com.manning.blogapps.chapter10.blogclient.BlogEntry;
import com.manning.blogapps.chapter10.blogclient.BlogResource;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Link;

public class AtomResource extends AtomEntry implements BlogResource {
 
	private String name;
    private File uploadFile;
    private String editMediaURI;
    
    public AtomResource(AtomBlog blog, String name, String contentType, File uploadFile) {
        super(blog);
        this.name = name;
        this.uploadFile = uploadFile;
        BlogEntry.Content rcontent = new BlogEntry.Content();
        rcontent.setType(contentType);
        setContent(rcontent);
    } 
    
    public AtomResource(AtomBlog blog, Entry entry, boolean partial) {
        super(blog, entry, partial);
    } 
    
    public String getName() {
        return name;
    }
    
    public InputStream getAsStream() throws BlogClientException {
        if (getContent() != null && getContent().getSrc() != null) {
            return ((AtomBlog)getBlog()).getResourceAsStream(this);
        } else if (uploadFile != null) {
            try {
                return new FileInputStream(uploadFile);
            } catch (Exception e) {
                throw new BlogClientException("ERROR: error opening file", e);
            }
        } else {
            throw new BlogClientException("ERROR: no input stream available");
        } 
    }
    
    public void save() throws BlogClientException {
        ((AtomBlog)getBlog()).saveResource(this);
    }
    
    public void update(File newFile) throws BlogClientException {
        if (null == getToken()) {
            throw new BlogClientException("ERROR: entry not saved");
        }
        uploadFile = newFile;
        ((AtomBlog)getBlog()).saveResource(this);
    }
    
    public void delete() throws BlogClientException {
        if (null == getToken()) {
            throw new BlogClientException("ERROR: entry not saved");
        }
        ((AtomBlog)getBlog()).deleteResource(this);
    }
    
    public File getUploadFile() {
        return uploadFile;
    }
    
    public String getEditMediaURI() {
        return editMediaURI;
    }
    
    public void copyFromRomeEntry(Entry entry) {
        super.copyFromRomeEntry(entry);
        List links = entry.getOtherLinks();
        if (links != null) {
            for (Iterator iter = links.iterator(); iter.hasNext();) {
                Link link = (Link)iter.next();
                if ("edit-media".equals(link.getRel())) {
                    editMediaURI = link.getHref();
                    break;
                }
            }
        }
    }
    
}




