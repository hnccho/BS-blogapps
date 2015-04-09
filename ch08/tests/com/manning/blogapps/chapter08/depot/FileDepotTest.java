package com.manning.blogapps.chapter08.depot;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import junit.framework.TestCase;

import com.manning.blogapps.chapter08.filedepot.FileDepot;

/**
 * @author Dave Johnson
 */
public class FileDepotTest extends TestCase {
	
    public void testFileListCount() throws Exception {
        
        FileDepot depot = new FileDepot("./web/depot");
        assertEquals(4, depot.getFiles().size());   
        
        DateFormat dateFormat = DateFormat.getDateTimeInstance(
                DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault());
        Date expectedDate = dateFormat.parse("September 27, 2004 10:58:19 AM EDT");
        assertEquals(expectedDate, depot.getLastUpdateDate());
        
        long lastLastModified = Long.MAX_VALUE;
        Iterator<?> files = depot.getFiles().iterator();
        while (files.hasNext()) {
            File file = (File) files.next();
            assertTrue(file.lastModified() <= lastLastModified);
            lastLastModified = file.lastModified();
        }
    }
    
    public static void main(String args[]) throws Exception {
        FileDepotTest fdt = new FileDepotTest();
        fdt.testFileListCount();
    }
    
}
