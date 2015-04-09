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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.binary.Base64;

/** Command-line program gets from a URL (w/basic digest auth) */
public class AuthGetJava {
    
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {  
            System.out.println("USAGE: authget <username> <password> <url>");
            System.exit(-1);
        }
        String credentials = args[0] + ":" + args[1]; 
        
        URL url = new URL(args[2]);        
        URLConnection conn = url.openConnection();
        
        conn.setRequestProperty("Authorization", "Basic "      
            + new String(Base64.encodeBase64(credentials.getBytes())));
        
        BufferedReader in = new BufferedReader(
            new InputStreamReader(conn.getInputStream())); 
        
        String s = null; 
        while ((s = in.readLine()) != null) {
            System.out.println(s);
        }
    }
    
}
