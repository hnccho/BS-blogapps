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

import com.manning.blogapps.chapter10.metaweblogclient.MetaWeblogConnection;
import com.manning.blogapps.chapter10.atomclient.AtomBlogConnection;

/**
 * Entry point to the Blogapps blog client library.
 *
 * @author Dave Johnson
 */
public class BlogConnectionFactory {
   /**
    * Create a connection to a blog server.
    * @param type     Connection type, must be "atom" or "metaweblog"
    * @param url      End-point URL to connect to
    * @param username Username for login to blog server
    * @param password Password for login to blog server
    */
   public static BlogConnection getBlogConnection(
      String type, String url, String username, String password) 
      throws BlogClientException {
      BlogConnection blogConnection = null;
      if (type == null || type.equals("metaweblog")) {
         blogConnection = new MetaWeblogConnection(url, username, password);
      } else if (type == null || type.equals("atom")) {
         blogConnection = new AtomBlogConnection(url, username, password);
      } else {
          throw new BlogClientException("Type must be 'atom' or 'metaweblog'");
      }
      return blogConnection;
   }
   
}
