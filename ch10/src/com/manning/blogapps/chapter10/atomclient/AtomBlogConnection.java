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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import com.manning.blogapps.chapter10.blogclient.Blog;
import com.manning.blogapps.chapter10.blogclient.BlogClientException;
import com.manning.blogapps.chapter10.blogclient.BlogConnection;

/**
 * Atom implementation of BlogClient interface.
 * @author Dave Johnson
 */
public class AtomBlogConnection implements BlogConnection {

	private static Log logger = LogFactory.getLog(AtomBlogConnection.class);
    private HttpClient httpClient = new  HttpClient(); 
    private Map blogSites = new HashMap();
    
    /**
     * Create Atom blog client instance for specified URL and user account.
     * @param url        End-point URL of Atom service
     * @param username   Username of account 
     * @param password   Password of account
     */
    public AtomBlogConnection(String url, String username, String password) 
        throws BlogClientException {
        
        Document doc = null;
        try {
            URL feedURL = new URL(url);       
            Credentials creds = new UsernamePasswordCredentials(username, password);
            httpClient.getState().setAuthenticationPreemptive(true);
            httpClient.getState().setCredentials(null, feedURL.getHost(), creds);
            GetMethod method = new GetMethod(url);
            addAuthentication(method, username, password);        
            httpClient.executeMethod(method);
            SAXBuilder builder = new SAXBuilder();
            doc = builder.build(method.getResponseBodyAsStream());
        } catch (Throwable t) {
            throw new BlogClientException("Error connecting to blog server", t);
        }
        
        AtomService service = AtomService.documentToService(doc);
        Iterator iter = service.getWorkspaces().iterator();
        int count = 0;
        while (iter.hasNext())
        {
            AtomService.Workspace workspace = (AtomService.Workspace)iter.next();
            Blog blog = new AtomBlog(httpClient, username, password, workspace);
            blogSites.put(blog.getToken(), blog);
        }
    }
    
    /** 
     * Get user-blog tokens, one for each blog site available to user
     */
    public List getBlogs() {
        return new ArrayList(blogSites.values());
    }
    
    /** 
     * Get blog site for blog 
     */
    public Blog getBlog(String token) {
        return (AtomBlog)blogSites.get(token);
    }

    /**
     * No-op: Atom does not use appkey
     */
    public void setAppkey(String appkey) {
    }

    /**
     * Add authentication to request for username/password
     */
    public static void addAuthentication(HttpMethodBase method, String u, String p) {
        //method.setRequestHeader("X-WSSE", WSSEUtilities.generateWSSEHeader(u, p));
        method.setRequestHeader("Authorization", generateBASICHeader(u, p)); 
    }
    
    public static String generateBASICHeader(String username, String password) {
        String pair = username + ":" + password;
        String digest = new String(Base64.encodeBase64(pair.getBytes()));
        String header = "Basic " + digest;
        return header;
    }
    
}
