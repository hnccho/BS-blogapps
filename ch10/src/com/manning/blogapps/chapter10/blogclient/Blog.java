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
package com.manning.blogapps.chapter10.blogclient;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a blog which has collections of entries, uploaded file 
 * resources and categories. 
 * <p />
 * You can access the entry and resource collections using getCollections() and 
 * getCollection(String name) or you use the blog client notion of "primary"
 * collections.
 * <p /> 
 * The first collection in a blog that accepts blog entris is considered
 * to be the primary entries collection. The first collection that accepts
 * uploaded files of any type is considered to be the primary resource 
 * collection. Use the getEntries() and getResources() methods to acccess 
 * the primary entries and resources collections respectively.
 *
 * @author Dave Johnson
 */
public interface Blog {
    
    /** Token can be used to fetch this blog again */
    public String getToken();
    
    /** Name of this blog */
    public String getName();
        
    /** 
     * Get entries in <em>primary entries collection</em> (first collection 
     * that accepts entries). Note that entries may be partial, so don't 
     * try to update and save them: to update and entry, first fetch it 
     * with getEntry(), change fields, then call entry.save(); 
     */
    public Iterator getEntries() throws BlogClientException;

    /** 
     * Get entries in <em>primary resources collection</em> (first collection 
     * that accepts anything other than entries).
     */
    public Iterator getResources() throws BlogClientException;

    /** Get a single BlogEntry by token */
    public BlogEntry getEntry(String token) throws BlogClientException;
    
    /** 
     * Create new entry, but DO NOT save it to the server. To save an entry
     * to a collection, use the collection's saveEntry() method.
     */
    public BlogEntry newEntry() throws BlogClientException;

    /** 
     * Create new resource, but DO NOT save it to the server. To save a resource
     * to a collection, use the collection's saveResource() method. 
     */
    public BlogResource newResource(String name, String type, File file) 
        throws BlogClientException;

    /** 
     * Returns list of available categories (BlogEntry.Category objects) 
     */
    public List getCategories() throws BlogClientException;    
    
    /** 
     * Gets listing of the BlogCollections available in the blog,
     * including the primary collections.
     */
    public List getCollections() throws BlogClientException;
    
    /** Get collection by token */
    public Collection getCollection(String token) throws BlogClientException;
        
    /**
     * Represents a collection on a blog server.
     */
    public interface Collection {
        
        /** Title of collection */
        public String getTitle();
        
        /** Token that can be used to fetch collection */
        public String getToken();
        
        /** Comma separated list of content-types accepted (or "entries") */
        public String getAccept();
        
        /** Returns true if collection will accept the specified blog entry */
        public boolean accepts(String contentType);
        
        /** 
         * Get entries in this collection.
         * @returns List of BlogEntries (and/or BlogResources)
         */
        public Iterator getEntries() throws BlogClientException;
        
        /** Save or update entry (or resource) in this collection */
        public String saveEntry(BlogEntry entry) throws BlogClientException;

        /** Save or update entry (or resource) in this collection */
        public String saveResource(BlogResource resource) throws BlogClientException;
    }
    
}
