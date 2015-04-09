package com.manning.blogapps.chapter18.filecaster;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

import com.manning.blogapps.chapter18.Utilities;

/**
 * FileCast storage and management.
 * @author Dave Johnson
 */
public class FileCasterImpl implements FileCaster {
	
    private String uploadsDir;
    private String metadataDir;  
    private String absoluteUrl;
    private Date lastModified;
    private ArrayList files = new ArrayList();
    
    public FileCasterImpl(
        String uploadDir, String metadataDir, String absoluteUrl) {
        this.uploadsDir = uploadDir;
        this.metadataDir = metadataDir;
        this.absoluteUrl = absoluteUrl;
        update();
    }  
    
    public static FileCaster getFileCaster(HttpServletRequest request) 
        throws Exception { 
        ServletContext ctx = request.getSession(true).getServletContext();
        Properties props = new Properties();
        props.load(ctx.getResourceAsStream("/WEB-INF/filecaster.properties"));
        FileCaster ret = (FileCaster)ctx.getAttribute("fileCaster");
        if (ret == null) {
           String uploadsDir = request.getRealPath("/uploads");
           String metadataDir = request.getRealPath("/metadata");
           String absoluteUrl = props.getProperty("absoluteUrl");
           ret = new FileCasterImpl(uploadsDir, metadataDir, absoluteUrl);  
           ctx.setAttribute("fileCaster", ret); 
        } 
        return ret;
    }
    
    public synchronized Date getLastUpdateDate() {
        return lastModified;
    }   
    
    public String getAbsoluteUrl() {
        return absoluteUrl;
    }
    
    public List getRecentFileCasts(int max) throws Exception {
        ArrayList allCasts = new ArrayList();
        ArrayList recentCasts = new ArrayList();
        File udir = new File(metadataDir);
        File[] files = udir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.getName().endsWith(".cast");
            }
        });
        if (files != null) for (int i=0; i<files.length; i++) {
            FileCast cast = new FileCast();
            cast.load(new FileInputStream(files[i]));
            allCasts.add(cast);
        }
        Collections.sort(allCasts, new FileCastComparator());
        Iterator all = allCasts.iterator();
        while (all.hasNext()) {
            FileCast cast = (FileCast) all.next();
            recentCasts.add(cast);
        }
        return recentCasts;
    }
    
    public void addFileCast(FileCast fileCast) throws Exception {     
        FileItem upload = fileCast.getUpload();
        if (upload != null) {
            String filePath = 
                uploadsDir + File.separator + fileCast.getFilename();
            FileOutputStream uploadOS = new FileOutputStream(filePath);
            Utilities.copyInputToOutput(upload.getInputStream(), uploadOS);
            uploadOS.flush();
            uploadOS.close();
        }
        FileOutputStream metadataOS = new FileOutputStream(
            metadataDir + File.separator + fileCast.getSaveFilename() + ".cast");
        fileCast.save(metadataOS);
        metadataOS.flush();
        metadataOS.close();   
        update();
    }  
    
    public void removeFileCast(String filename) throws Exception {   
        File uploadedFile = new File(
            uploadsDir + File.separator + filename);
        uploadedFile.delete();
        File castFile = new File(
            metadataDir + File.separator + filename + ".cast");
        castFile.delete();
        update();
    }
    
    private synchronized void update() {
        try {
            List casts = getRecentFileCasts(1);
            if (casts.size() > 0) {
                FileCast cast = (FileCast)casts.get(0);
                lastModified = cast.getUploadTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    class FileCastComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            FileCast file1 = (FileCast)o1;
            FileCast file2 = (FileCast)o2;
            if (file1.getUploadTime() == file2.getUploadTime()) return 0;
            else if (file1.getUploadTime().after(file2.getUploadTime())) return 1;
            else return -1;
        }
    }
    
}
