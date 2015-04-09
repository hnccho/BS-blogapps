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
import java.io.InputStream;
import java.util.Hashtable;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import com.manning.blogapps.chapter10.blogclient.BlogClientException;
import com.manning.blogapps.chapter10.blogclient.BlogResource;

public class MetaWeblogResource extends MetaWeblogEntry implements BlogResource {
 
	private MetaWeblogBlog blog;
    private String name;
    private String contentType;
    private String url;
    private File uploadFile;

    public MetaWeblogResource(MetaWeblogBlog blog, 
        String name, String contentType, File uploadFile) {
        super(blog, new Hashtable());
        this.blog = blog;
        this.name = name;
        this.contentType = contentType;
        this.uploadFile = uploadFile;
    }
    
    public String getName() {
        return name;
    }
    
    /** Returns null; not supported by MetaWeblog API */
    public String getToken() {
        return null;
    }
    
    public String getURL() {
        return url;
    }
    
    public void setURL(String url) {
        this.url = url;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public InputStream getAsStream() throws BlogClientException {
        HttpClient httpClient = new HttpClient();
        GetMethod method = new GetMethod(url);
        try {
            httpClient.executeMethod(method);
        } catch (Exception e) {
            throw new BlogClientException("ERROR: error reading file");
        }
        if (method.getStatusCode() != 200) {
            throw new BlogClientException("ERROR HTTP status=" + method.getStatusCode());
        }
        try {
            return method.getResponseBodyAsStream();
        } catch (Exception e) {
            throw new BlogClientException("ERROR: error reading file");
        }
    } 
    
    public void save() throws BlogClientException {
        blog.saveResource(this);
    }
    
    public void update(File newFile) throws BlogClientException {
        this.uploadFile = newFile;
        save();
    }
    
    public File getUploadFile() {
        return uploadFile;
    }
    
    /** Does nothing; not supported by MetaWeblog API */
    public void delete() throws BlogClientException {
    }
    
}
