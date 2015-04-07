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
import java.net.*;
import org.apache.commons.codec.binary.Base64;

/** 
 * Command-line program posts a file to a URL (w/basic digest auth)
 * Sets content-type is application/atom+xml unless file ends with .gif or .jpg.
 */
public class AuthPostJava {
    public static void main(String[] args) throws Exception {
        if (args.length < 4) {  
            System.out.println(
                "USAGE: authpost <username> <password> <filepath> <url>");
            System.exit(-1);
        }
        String credentials = args[0] + ":" + args[1];
        String filepath = args[2];
        
        URL url = new URL(args[3]);
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        
        conn.setRequestProperty("Authorization", "Basic "      
            + new String(Base64.encodeBase64(credentials.getBytes())));
        
        File upload = new File(filepath); 
        conn.setRequestProperty("name", upload.getName());  
        
        String contentType = "application/atom+xml; charset=utf8";    
        if      (filepath.endsWith(".gif")) contentType = "image/gif";     
        else if (filepath.endsWith(".jpg")) contentType = "image/jpg";
        conn.setRequestProperty("Content-type", contentType); 
        
        BufferedInputStream filein = 
            new BufferedInputStream(new FileInputStream(upload));
        BufferedOutputStream out = 
            new BufferedOutputStream(conn.getOutputStream());
        byte buffer[] = new byte[8192];
        for (int count = 0; count != -1;) {
            count = filein.read(buffer, 0, 8192);
            if (count != -1)
                out.write(buffer, 0, count);
        }
        filein.close();
        out.close();
        
        String s = null; 
        BufferedReader in = new BufferedReader(
            new InputStreamReader(conn.getInputStream())); 
        while ((s = in.readLine()) != null) {
            System.out.println(s);
        }
    }    
}
