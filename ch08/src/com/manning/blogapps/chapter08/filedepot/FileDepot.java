package com.manning.blogapps.chapter08.filedepot;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Depot of files avialable in reverse chronological order.
 * @author Dave Johnson
 */
public class FileDepot implements Depot {

	private String path;
    private ArrayList files = new ArrayList();
    private Date lastModified = null;
    
    /** Create file depot in existing directory */
    public FileDepot(String path) {
        this.path = path;
        update();
    }
    
    public synchronized void update() {
        File depotDir = new File(path);
        String[] fileNames = depotDir.list();
        for (int i = 0; i < fileNames.length; i++) {
            File file = new File(depotDir.getPath() + File.separator + fileNames[i]);
            files.add(file);
            Date fileLastModified = new Date(file.lastModified());
            if (lastModified == null || lastModified.compareTo(fileLastModified) < 0) {
                lastModified = fileLastModified;
            }
        }
        Collections.sort(files, new FileComparator());
    }
    
    /** Get files sorted in reverse chronological order */
    public synchronized Collection getFiles() {
        return files;
    }
    
    public synchronized Date getLastUpdateDate() {
        return lastModified;
    }

    /** Helps to sort files in reverse chrono order */
    class FileComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            File file1 = (File)o1;
            File file2 = (File)o2;
            if (file1.lastModified() == file2.lastModified()) return 0;
            else if (file1.lastModified() < file2.lastModified()) return 1;
            else return -1;
        }
    }
    
}
