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

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.manning.blogapps.chapter10.blogclient.BlogClientException;
import com.manning.blogapps.chapter10.blogclient.BlogEntry;
import com.manning.blogapps.chapter10.blogclient.impl.BaseBlogEntry;


/** 
 * MetaWeblog API implementation of an entry.
 * @author David M Johnson
 */
public class MetaWeblogEntry extends BaseBlogEntry {
    
    MetaWeblogEntry(MetaWeblogBlog blog, Hashtable entryMap) {
        super(blog);
        id = (String)entryMap.get("postid");
        
        content = new Content((String)entryMap.get("description"));
        
        // let's pretend MetaWeblog API has a content-type
        content.setType("application/metaweblog+xml");
        
        // no way to tell if entry is draft or not
        draft = false;

        title = (String)entryMap.get("title");
        publicationDate = (Date)entryMap.get("dateCreated");        
        guid = (String)entryMap.get("guid");
        permalink = (String)entryMap.get("permaLink");
        
        categories = new ArrayList();
        Vector catVector = (Vector)entryMap.get("categories");
        if (catVector != null) {
            for (int i=0; i<catVector.size(); i++) {
                Category cat = new Category((String)catVector.get(i));
                categories.add(cat);
            }
        }
    }
    
    public String getToken() {
        return id;
    }
    
    public boolean equals(Object o) {
        if (o instanceof MetaWeblogEntry) {
            MetaWeblogEntry other = (MetaWeblogEntry)o;
            if (other.id != null && id != null) {
                return other.id.equals(id);
            }
        }
        return false;
    }
        
    public void save() throws BlogClientException {
        id = ((MetaWeblogBlog)getBlog()).saveEntry(this);
    }

    public void delete() throws BlogClientException {
        ((MetaWeblogBlog)getBlog()).deleteEntry(id);
    }

    public Hashtable toPostStructure() {
        Hashtable struct = new Hashtable();       
        if (getTitle() != null) {
            struct.put("title", getTitle());
        }
        if (getContent() != null && getContent().getValue() != null) {
            struct.put("description", getContent().getValue());
        }
        if (getCategories() != null && getCategories().size() > 0) {
            Vector catArray = new Vector();
            List cats = getCategories();
            for (int i=0; i<cats.size(); i++) {
                BlogEntry.Category cat = (BlogEntry.Category)cats.get(i);
                catArray.add(cat.getName());
            }
            struct.put("categories", catArray);
        }
        if (getPublicationDate() != null) {
            struct.put("dateCreated", getPublicationDate());
        }
        if (getId() != null) {
            struct.put("postid", getId());                        
        }
        return struct;
    }
    
}
