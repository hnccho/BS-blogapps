package com.manning.blogapps.chapter18.filecaster;
import java.util.Date;
import java.util.List;

/** Interface to FileCast management */
public interface FileCaster {
    
    /** Get most recent FileCasts, up to limit */
    public abstract List getRecentFileCasts(int max) 
       throws Exception;
    
    /** Get date of most recent FileCast */
    public abstract Date getLastUpdateDate()
        throws Exception;
    
    /** Get absolute URL of FileCaster web site */
    public abstract String getAbsoluteUrl()
        throws Exception;
    
    /** Add FileCast to the colllection */
    public void addFileCast(FileCast fileCast) 
        throws Exception;
        
    /** Remove a FileCast from the collection */
    public void removeFileCast(String filename) 
        throws Exception;
}