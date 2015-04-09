/*
 * Copyright 2005, Dave Johnson
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

import com.manning.blogapps.chapter10.blogclient.impl.BaseBlogEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.manning.blogapps.chapter10.blogclient.*;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Link;

/**
 * Atom implementation of BlogEntry and BlogResource interfaces.
 * @author Dave Johnson
 */
public class AtomEntry extends BaseBlogEntry implements BlogEntry {

	protected String editURI = null;
    protected String postURI = null;
    protected List unkownElements = null;
    protected boolean partial = false;
    private List foreignMarkup = new ArrayList();
    
    public AtomEntry(AtomBlog blog) {
        super(blog);
    }

    public AtomEntry(AtomBlog blog, Entry entry, boolean partial) {   
        super(blog);
        this.partial = partial;
        copyFromRomeEntry(entry);
    }
    
    public AtomEntry(AtomBlog blog, Entry entry, boolean partial, List foreignMarkup) {   
        this(blog, entry, partial);
        this.foreignMarkup = foreignMarkup;
    }
    
    public boolean equals(Object o) {
        if (o instanceof AtomEntry) {
            AtomEntry other = (AtomEntry)o;
            if (other.editURI != null && editURI != null) {
                return other.editURI.equals(editURI);
            }
        }
        return false;
    }
    
    public void save() throws BlogClientException {
        ((AtomBlog)getBlog()).saveEntry(this); 
    }
    public void delete() throws BlogClientException {
        ((AtomBlog)getBlog()).deleteEntry(this); 
    }
    
    public String getToken() {
        return editURI;
    }
    public void setToken(String token) {
        this.editURI = token;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getEditURI() {
        return editURI;
    }
    public void setEditURI(String href) {
        editURI = href;
    }
    public String getPostURI() {
        return postURI;
    }  
    public List getForeignMarkup() {
        return foreignMarkup;
    }   
    public void setForeignMarkup(List foreignMarkup) {
        this.foreignMarkup = foreignMarkup;
    }
    
    public void copyFromRomeEntry(Entry entry) {
        id = entry.getId();
        title = entry.getTitle();        
        List links = entry.getOtherLinks();
        if (links != null) {
            for (Iterator iter = links.iterator(); iter.hasNext();) {
                Link link = (Link)iter.next();
                if ("edit".equals(link.getRel())) {
                    editURI = link.getHref();
                    break;
                }
            }
        }
        List contents = entry.getContents();
        com.sun.syndication.feed.atom.Content romeContent = null;
        if (contents != null && contents.size() > 0) {
            romeContent = (com.sun.syndication.feed.atom.Content)contents.get(0);
        }
        if (romeContent != null) {
            content = new BlogEntry.Content(romeContent.getValue());
            content.setType(romeContent.getType());
            content.setSrc(romeContent.getSrc());
        }  
        if (entry.getCategories() != null) {
            List cats = new ArrayList();
            List romeCats = entry.getCategories();
            for (Iterator iter=romeCats.iterator(); iter.hasNext();) {
                com.sun.syndication.feed.atom.Category romeCat = 
                    (com.sun.syndication.feed.atom.Category)iter.next();
                BlogEntry.Category cat = new BlogEntry.Category();
                cat.setId(romeCat.getTerm());
                cat.setUrl(romeCat.getScheme());
                cat.setName(romeCat.getLabel());
                cats.add(cat);
            }
            categories = cats;
        }
        List authors = entry.getAuthors();
        if (authors!=null && authors.size() > 0) {
            com.sun.syndication.feed.atom.Person romeAuthor = 
                (com.sun.syndication.feed.atom.Person)authors.get(0);
            if (romeAuthor != null) {
                author = new Person();
                author.setName(romeAuthor.getName());
                author.setEmail(romeAuthor.getEmail());
                author.setUrl(romeAuthor.getUrl());
            }    
        }    
        publicationDate = entry.getPublished();
        modificationDate = entry.getModified();
        
        PubControlModule control = 
            (PubControlModule)entry.getModule("http://purl.org/atom/app#");
        if (control != null && control.getDraft() != null) {
            draft = control.getDraft().booleanValue();
        } else {
            draft = false;
        }
    }
    public Entry copyToRomeEntry() {
        Entry entry = new Entry();
        if (id != null) {
            entry.setId(id);
        }
        entry.setTitle(title);        
        if (author != null) {
            com.sun.syndication.feed.atom.Person person = 
                new com.sun.syndication.feed.atom.Person();
            person.setName(author.getName());
            person.setEmail(author.getEmail());
            person.setUrl(author.getUrl());
            List authors = new ArrayList();
            authors.add(person);
            entry.setAuthors(authors);
        }
        if (content != null) {
            com.sun.syndication.feed.atom.Content romeContent = 
                new com.sun.syndication.feed.atom.Content();
            romeContent.setValue(content.getValue());
            romeContent.setType(content.getType());
            List contents = new ArrayList();
            contents.add(romeContent);
            entry.setContents(contents);
        }
        if (categories != null) {
            List romeCats = new ArrayList();
            for (Iterator iter=categories.iterator(); iter.hasNext();) {
                BlogEntry.Category cat = (BlogEntry.Category)iter.next();
                com.sun.syndication.feed.atom.Category romeCategory =
                    new com.sun.syndication.feed.atom.Category();
                romeCategory.setTerm(cat.getId());
                romeCategory.setScheme(cat.getUrl());
                romeCategory.setLabel(cat.getName());
                romeCats.add(romeCategory);
            }
            entry.setCategories(romeCats);
        }
        entry.setPublished((publicationDate == null) ? new Date() : publicationDate);
        entry.setModified((modificationDate == null) ? new Date() : modificationDate);
        
        List modules = new ArrayList();
        PubControlModule control = new PubControlModuleImpl();
        control.setDraft(new Boolean(draft));
        modules.add(control);
        entry.setModules(modules);
        
        return entry;
    }
    
}
