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
package com.manning.blogapps.chapter10.examples;
import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.PutMethod;

/** 
 * Command-line program posts a file to a URL (w/basic digest auth)
 * Sets content-type is application/atom+xml unless file ends with .gif or .jpg.
 */
public class AuthPut {
	
    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.out.println(
                "USAGE: authput <username> <password> <filepath> <url>");
            System.exit(-1);
        }
        String credentials = args[0] + ":" + args[1];
        String filepath = args[2];
        String url = args[3];
        
        HttpClient httpClient = new HttpClient();
        EntityEnclosingMethod method = new PutMethod(url);
        method.setRequestHeader("Authorization", "Basic "
            + new String(Base64.encodeBase64(credentials.getBytes())));
        
        File upload = new File(filepath);
        method.setRequestHeader("name", upload.getName());

        String contentType = "application/atom+xml; charset=utf8";
        if (filepath.endsWith(".gif")) contentType = "image/gif";
        else if (filepath.endsWith(".jpg")) contentType = "image/jpg";
        method.setRequestHeader("Content-type", contentType);        
        
        method.setRequestBody(new FileInputStream(filepath));
        httpClient.executeMethod(method);  
        
        System.out.println(method.getResponseBodyAsString());
    }   
    
}
