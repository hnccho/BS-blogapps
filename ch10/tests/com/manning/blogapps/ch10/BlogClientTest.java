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
package com.manning.blogapps.ch10;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.TestCase;

import com.manning.blogapps.chapter10.blogclient.*;
import com.manning.blogapps.chapter10.atomclient.*;

/**
 * Test blog client implementation.
 * @author Dave Johnson
 */
public class BlogClientTest extends TestCase {    
    String ENDPOINT_URL = "http://localhost:8080/roller/app";
    String USERNAME     = "admin";    
    String PASSWORD     = "admin";     
    String TESTDATA_DIR     = "/Users/dave/src/blogapps-oss/examples/java/ch10/testdata/";
    String UPLOAD_FILETYPE  = "image/jpg";    
    String UPLOAD_FILENAME1 = "cover-rssandatom.jpg";
    int    UPLOAD_FILESIZE1 = 5866; 
    String UPLOAD_FILENAME2 = "cover-projsp.jpg";
    int    UPLOAD_FILESIZE2 = 13543;    
    String VALID_CAT   = "/Music";
    String INVALID_CAT = "ZZZ_INVALID_ZZZ";
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(BlogClientTest.class);
    }

    public void testGetBlogs() throws Exception {
        BlogConnection client = getBlogConnection();
        List blogs = client.getBlogs();
        assertTrue(blogs.size() > 1);
        assertTrue(blogs.get(0) instanceof Blog);
    }
    
    public void testGetBlogSite() throws Exception {
        BlogConnection client = getBlogConnection();
        Blog blog = (Blog)client.getBlogs().get(0);
        assertNotNull(client.getBlog(blog.getToken()));
    }
    
    public void testPostAndDeleteWithCats() throws Exception {
        
        // save new entry with category
        BlogConnection client = getBlogConnection();
        Blog blog = (Blog)client.getBlogs().get(0);
        BlogEntry entry = blog.newEntry();
        entry.setTitle("Test title");
        entry.setContent(new BlogEntry.Content("Test content"));
        List cats = new ArrayList();
        cats.add(new BlogEntry.Category(INVALID_CAT));
        cats.add(new BlogEntry.Category(VALID_CAT));
        entry.setCategories(cats);
        entry.save();
        
        // verify that entry was saved with category
        BlogEntry fetched = blog.getEntry(entry.getToken());
        assertEquals(entry.getTitle(), fetched.getTitle());
        assertNotNull(entry.getCategories());
        assertEquals(1, entry.getCategories().size());
        BlogEntry.Category cat = (BlogEntry.Category)entry.getCategories().get(0);
        assertEquals(cat.getId(), VALID_CAT);
        
        // delete entry and verify it is gone
        fetched.delete();
        boolean fetchFailed = false;
        try {
            entry = blog.getEntry(fetched.getToken());
        } catch (Exception e) {
            fetchFailed = true;
        }
        assertTrue(fetchFailed);
    }
    
    public void testPutDeleteAndSaveDraft() throws Exception {
        
        // save new entry with draft status
        BlogConnection client = getBlogConnection();
        Blog blog = (Blog)client.getBlogs().get(0);
        BlogEntry entry = blog.newEntry();
        entry.setTitle("Test title");
        entry.setContent(new BlogEntry.Content("Test content"));
        entry.setDraft(true);
        entry.save();
        
        // verify that entry was saved as draft
        BlogEntry fetched = blog.getEntry(entry.getToken());
        assertEquals(entry.getTitle(), fetched.getTitle());
        assertTrue(entry.getDraft());
        
        // save as non-draft
        entry.setDraft(false);
        entry.save();
        
        // verify that entry was saved as non-draft
        fetched = blog.getEntry(entry.getToken());
        assertFalse(entry.getDraft());

        // delete entry and verify it is gone
        fetched.delete();
        boolean fetchFailed = false;
        try {
            entry = blog.getEntry(fetched.getToken());
        } catch (Exception e) {
            fetchFailed = true;
        }
        assertTrue(fetchFailed);
    }
    
    public void testCreateAndDeleteResources() throws Exception {
        BlogConnection client = getBlogConnection();
        Blog blog = (Blog)client.getBlogs().get(0);
        File file = new File(TESTDATA_DIR + UPLOAD_FILENAME1);
        
        int uploadCount = 10;
        
        // upload image ten times
        for (int i=0; i<uploadCount; i++) {
            String name = "_" + i + file.getName();
            BlogResource res = blog.newResource(name, UPLOAD_FILETYPE, file);
            res.save();
            assertNotNull(res);
        }
        
        // delete all uploaded images, based on file name
        int count = 0;
        Iterator resources = blog.getEntries();
        while (resources.hasNext()) {
            BlogEntry res = (BlogEntry)resources.next();
            if (   res.getContent() != null 
                && res.getContent().getSrc() != null
                && res.getContent().getSrc().endsWith(UPLOAD_FILENAME1)) {
                res.delete();
                count++;
            }
        }
        // assert that we deleted same number as we uploaded
        assertEquals(uploadCount, count);
        
        // make sure uploaded images are no longer there
        count = 0;
        resources = blog.getEntries();
        while (resources.hasNext()) {
            BlogEntry res = (BlogEntry)resources.next();
            if (   res.getContent() != null 
                && res.getContent().getSrc() != null
                && res.getContent().getSrc().endsWith(UPLOAD_FILENAME1)) {
                count++;
            }
        }      
        assertEquals(0, count);
    }

    public void testUpdateResource() throws Exception {
        BlogConnection client = getBlogConnection();
        Blog blog = (Blog)client.getBlogs().get(0);
        File file1 = new File(TESTDATA_DIR + UPLOAD_FILENAME1);
        File file2 = new File(TESTDATA_DIR + UPLOAD_FILENAME2);
        
        // upload file1 as res, ensure upload's size equals file1 size
        BlogResource res = blog.newResource("testfile.jpg", UPLOAD_FILETYPE, file1);
        res.save();
        assertEquals(UPLOAD_FILESIZE1, byteCount(res));
              
        // upload file2 as res, ensure upload's size equals file2 size
        res.update(file2);
        res = (BlogResource)blog.getEntry(res.getToken());
        assertEquals(UPLOAD_FILESIZE2, byteCount(res));
        
        // delete the one resource we created
        res.delete();
    }
    
    public void testEntryPaging() throws Exception {
        
        // create 35 entries
        List uris = new ArrayList();
        BlogConnection client = getBlogConnection();
        Blog blog = (Blog)client.getBlogs().get(0);        
        for (int i=0; i<35; i++) {
            BlogEntry entry = blog.newEntry();
            entry.setTitle("ZZZ Test title "+i);
            entry.setContent(new BlogEntry.Content("ZZZ Test content "+i));
            entry.save(); 
            uris.add(entry.getToken());
        }
        
        // page through entries make sure they're all there
        int count = 0;
        Iterator entries = blog.getEntries();
        while (entries.hasNext()) {
            BlogEntry entry = (BlogEntry)entries.next();
            if (entry.getTitle().startsWith("ZZZ")) count++;
        }
        assertEquals(35, count);

        Iterator tokens = uris.iterator();
        while (tokens.hasNext()) {
            BlogEntry entry = blog.getEntry((String)tokens.next());
            entry.delete(); 
        }
    }
    
    public void testResourcePaging() throws Exception {
        File file = new File(TESTDATA_DIR + UPLOAD_FILENAME1);
        
        // create 35 uploads
        List uris = new ArrayList();
        BlogConnection client = getBlogConnection();
        Blog blog = (Blog)client.getBlogs().get(0);        
        for (int i=0; i<35; i++) {
            String name = "_" + i + file.getName();
            BlogResource res = blog.newResource(name, UPLOAD_FILETYPE, file);
            res.save();
            assertNotNull(res);
            uris.add(res.getToken());
        }
        
        // page through entries make sure they're all there
        int count = 0;
        Iterator resources = blog.getEntries();
        while (resources.hasNext()) {
            BlogEntry resource = (BlogEntry)resources.next();
            if (resource instanceof BlogResource) count++;
        }
        assertEquals(35, count);
        
        Iterator tokens = uris.iterator();
        while (tokens.hasNext()) {
            BlogResource res = (BlogResource)blog.getEntry((String)tokens.next());
            res.delete(); 
        }
    }
    
    //------------------------------------------------------ supporting methods
    
    public long byteCount(BlogResource resource) throws Exception {
        File downloaded = File.createTempFile("downloaded","tmp");
        FileOutputStream fos = new FileOutputStream(downloaded);
        copyInputToOutput(resource.getAsStream(), fos);
        long ret = downloaded.length();
        downloaded.delete(); 
        return ret;
    }
    
    public BlogConnection getBlogConnection() throws Exception {
        return new AtomBlogConnection(ENDPOINT_URL, USERNAME, PASSWORD);
    }
    
    public static void copyInputToOutput(
            InputStream input,
            OutputStream output)
            throws IOException {
        BufferedInputStream in = new BufferedInputStream(input);
        BufferedOutputStream out = new BufferedOutputStream(output);
        byte buffer[] = new byte[8192];
        for (int count = 0; count != -1;) {
            count = in.read(buffer, 0, 8192);
            if (count != -1)
                out.write(buffer, 0, count);
        }
        try {
            in.close();
            out.close();
        } catch (IOException ex) {
            throw new IOException("Closing file streams, " + ex.getMessage());
        }
    }
}
