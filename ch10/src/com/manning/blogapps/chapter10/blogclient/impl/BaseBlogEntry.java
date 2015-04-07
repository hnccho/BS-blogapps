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
package com.manning.blogapps.chapter10.blogclient.impl;

import com.manning.blogapps.chapter10.blogclient.*;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** 
 * Base implementation of a blog entry.
 * @author Dave Johnson
 */
public abstract class BaseBlogEntry implements BlogEntry {
	
    protected String  id = null;
    protected Person  author = null;
    protected Content content = null;
    protected String  title = null;
    protected String  permalink = null;
    protected String  guid = null;
    protected String  summary = null;
    protected Date    modificationDate = null;
    protected Date    publicationDate = null;
    protected List    categories = new ArrayList();
    protected boolean draft = false;
    private Blog      blog = null;
    
    public BaseBlogEntry(Blog blog) {
        this.setBlog(blog);
    }   

    public String getId() {
        return id;
    }
    
    public String getPermalink() {
        return permalink;
    }  
    
    public String getGuid() {
        return guid;
    }
    
    public Person getAuthor() {
        return author;
    }
    public void setAuthor(Person author) {
        this.author = author;
    }

    public Content getContent() {
        return content;
    }
    public void setContent(Content content) {
        this.content = content;
    }
    
    public boolean getDraft() {
        return draft;
    }
    public void setDraft(boolean draft) {
        this.draft = draft;
    }
    
    public Date getPublicationDate() {
        return publicationDate;
    }    
    public void setPublicationDate(Date pubDate) {
        this.publicationDate = pubDate;
    }

    public Date getModificationDate() {
        return modificationDate;
    }
    public void setModificationDate(Date date) {
        modificationDate = date;
    }
        
    public String getTitle() {
        return title;
    }    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getSummary() {
        return summary;
    }    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public List getCategories() {
        return categories;
    }   
    public void setCategories(List categories) {
        this.categories = categories;
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        sw.write("guid = " + guid);
        return sw.toString();
    }

    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }
    
}
