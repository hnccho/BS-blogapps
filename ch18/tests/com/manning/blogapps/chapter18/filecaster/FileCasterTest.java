package com.manning.blogapps.chapter18.filecaster;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

import junit.framework.TestCase;

/**
 * Tests for FileCasterImpl and FileCast.
 */
public class FileCasterTest extends TestCase {
    String uploadDir = "./test-sandbox/uploads";
    String metadataDir = "./test-sandbox/metadata";
    String testUploadDir = "./test-sandbox/testuploads";
    protected void setUp() throws Exception {
        File udir = new File(uploadDir);
        File[] files = udir.listFiles();
        if (files != null) for (int i=0; i<files.length; i++) {
            assertTrue(files[i].delete());
        }
        File mdir = new File(metadataDir);
        File[] metaFiles = mdir.listFiles();
        if (metaFiles != null) for (int i=0; i<metaFiles.length; i++) {
            assertTrue(metaFiles[i].delete());
        }
    }
    public void testFileCastPersistence() throws Exception {
        Date updateTime = new Date();        
        FileCast fc1 = new FileCast();
        fc1.setRemoteUrl("http://example.com");
        fc1.setTitle("This is a title");
        fc1.setDescription("This is a description");
        fc1.setUploadTime(updateTime);
        
        String saveFileName = "test-sandbox/fc1.xml";
        FileOutputStream os = new FileOutputStream(saveFileName);
        fc1.save(os);
        os.close();
        
        FileCast fc2 = new FileCast();
        FileInputStream is = new FileInputStream(saveFileName);
        fc2.load(is);
        is.close();
        
        assertEquals(fc1.getTitle(), fc2.getTitle());
        assertEquals(fc1.getDescription(), fc2.getDescription());
        //assertEquals(fc1.getUploadTime(), fc2.getUploadTime());
    }
    public void testAddRemoveFileCastXML() throws Exception {

        FileCaster caster = new FileCasterImpl(uploadDir, metadataDir,"");
        
        Date updateTime = new Date();        
        FileCast fc1 = new FileCast();
        fc1.setFilename("Mirrors57.jpg");
        fc1.setRemoteUrl("http://example.com");
        fc1.setTitle("This is a title");
        fc1.setDescription("This is a description");
        fc1.setUploadTime(updateTime);

        caster.addFileCast(fc1);
        
        Date updateTime2 = new Date();        
        FileCast fc2 = new FileCast();
        fc2.setFilename("fc2.txt");
        fc2.setRemoteUrl("http://example.com");
        fc2.setTitle("This is a title2");
        fc2.setDescription("This is a description2");
        fc2.setUploadTime(updateTime2);
        
        caster.addFileCast(fc2);        
        
        assertEquals(2, caster.getRecentFileCasts(10).size());
        
        caster.removeFileCast("");
        caster.removeFileCast("");
        assertEquals(0, caster.getRecentFileCasts(10).size());
    }
    public void testAddRemoveFileCastFile() throws Exception {
        Date updateTime = new Date();        
        FileCast fc1 = new FileCast();
        fc1.setFilename("fc1.txt");
        fc1.setRemoteUrl("http://example.com");
        fc1.setTitle("This is a title");
        fc1.setDescription("This is a description");
        fc1.setUploadTime(updateTime);
        
        Date updateTime2 = new Date();        
        FileCast fc2 = new FileCast();
        fc2.setFilename("fc2.txt");
        fc2.setRemoteUrl("http://example.com");
        fc2.setTitle("This is a title2");
        fc2.setDescription("This is a description2");
        fc2.setUploadTime(updateTime2);
        
        FileCaster caster = new FileCasterImpl(uploadDir, metadataDir,"");
    
        //caster.addFileCast(fc1, 
           // new FileInputStream(testUploadDir + File.separator + "Leo.jpg"));
        
        caster.addFileCast(fc2);   
        
        assertEquals(2, caster.getRecentFileCasts(10).size());
        
        caster.removeFileCast("");
        caster.removeFileCast("");
        assertEquals(0, caster.getRecentFileCasts(10).size());
    }
    public void testURLToFilename() {
        FileCast cast = new FileCast();
        cast.setRemoteUrl(
           "http://example.com/path1/path2?param1=a&param2=b");
        assertEquals(
           "example_com_path1_path2_param1_a_param2_b", 
           cast.getSaveFilename());
    }
    /* public void testFileListCount() throws Exception {
        
        FileCaster depot = new FileCasterImpl("./web/depot");
        assertEquals(4, depot.getFiles().size());   
        
        DateFormat dateFormat = DateFormat.getDateTimeInstance(
                DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault());
        Date expectedDate = dateFormat.parse("September 27, 2004 10:58:19 AM EDT");
        assertEquals(expectedDate, depot.getLastUpdateDate());
        
        long lastLastModified = Long.MAX_VALUE;
        Iterator files = depot.getFiles().iterator();
        while (files.hasNext())
        {
            File file = (File) files.next();
            assertTrue(file.lastModified() <= lastLastModified);
            lastLastModified = file.lastModified();
        }
    }
    
    public static void main(String args[]) throws Exception {
        FileDepotTest fdt = new FileDepotTest();
        fdt.testFileListCount();
    } */
}
