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

import java.util.Date;
import java.util.List;
        
/** 
 * Represents a blog entry. 
 * 
 * @author Dave Johnson
 */
public interface BlogEntry {
    
    /** Get token for use in Blog.getEntry() */
    public String getToken();
    
    /** Save entry, if new saves to <em>primary entries collection</em> */
    public void save() throws BlogClientException;
    
    /** Delete this entry from blog server */
    public void delete() throws BlogClientException;

    /** Permanent link to this entry (assigned by server) */
    public String getPermalink(); 
    
    /** Blog is associated with a blog */
    public Blog getBlog();
    
    /** List of BlogEntry.Category objects */
    public List getCategories();
    public void setCategories(List categories);

    public String getId();
    public String getGuid();

    public String getTitle();
    public void setTitle(String title);
    
    public String getSummary();
    public void setSummary(String summary);
    
    public Content getContent();
    public void setContent(Content content);
        
    public boolean getDraft();
    public void setDraft(boolean draft);
    
    public Person getAuthor();
    public void setAuthor(Person author);
    
    public Date getPublicationDate();
    public void setPublicationDate(Date date);   
    
    public Date getModificationDate();
    public void setModificationDate(Date date);
        
    public class Content {
        String type = "HTML";
        String value = null;
        String src = null;
        public Content() {}
        public Content(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
        public String getSrc() {
            return src;
        }
        public void setSrc(String src) {
            this.src = src;
        }
    }
    public class Person {
        String name;
        String email;
        String url;
        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }
        public String toString() {
            return name;
        }
    }
    public class Category {
        String id;
        String name;
        String url;
        public Category() {}
        public Category(String id) {
            this.id = id;
            this.name = id;
        }
        public boolean equals(Object obj) {
            Category other = (Category)obj;
            if (obj == null) return false;
            if (getId() != null && other.getId() != null 
                    && getId().equals(other.getId())) return true;
            return false;
        }
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }
        public String toString() {
            return name!=null ? name : id;
        }
    } 
    
}
