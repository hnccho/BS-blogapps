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
import java.io.*;
import org.apache.commons.codec.binary.Base64;    //|#1
import org.apache.commons.httpclient.HttpClient;    //|#2
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.PostMethod;

/** 
 * Command-line program posts a file to a URL (w/basic digest auth)
 * Sets content-type is application/atom+xml unless file ends with .gif or .jpg.
 */
public class AuthPost {
    public static void main(String[] args) throws Exception {
        if (args.length < 4) {    //|#3
            System.out.println(
                "USAGE: authpost <username> <password> <filepath> <url>");
            System.exit(-1);
        }
        String credentials = args[0] + ":" + args[1];
        String filepath = args[2];
        String url = args[3];
        
        HttpClient httpClient = new HttpClient();    //|#4
        EntityEnclosingMethod method = new PostMethod(url);    //|#5
        method.setRequestHeader("Authorization", "Basic "    //|#6
            + new String(Base64.encodeBase64(credentials.getBytes())));
        
        File upload = new File(filepath);    //|#7
        method.setRequestHeader("Title", upload.getName());   //|#8
        method.setRequestBody(new FileInputStream(upload));    //|#9

        String contentType = "application/atom+xml; charset=utf8";     //|#10
        if (filepath.endsWith(".gif")) contentType = "image/gif";      //|#10
        else if (filepath.endsWith(".jpg")) contentType = "image/jpg"; //|#10
        method.setRequestHeader("Content-type", contentType);          //|#10          
        
        httpClient.executeMethod(method);                      //|#11      
        System.out.println(method.getResponseBodyAsString());  //|#11
    }    
}
