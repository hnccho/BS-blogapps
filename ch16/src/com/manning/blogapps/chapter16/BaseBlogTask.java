package com.manning.blogapps.chapter16;
import org.apache.tools.ant.Task;

import com.manning.blogapps.chapter10.blogclient.Blog;
import com.manning.blogapps.chapter10.blogclient.BlogConnection;
import com.manning.blogapps.chapter10.blogclient.BlogConnectionFactory;

public abstract class BaseBlogTask extends Task {   
 
	protected String apitype = "metaweblog";  
    protected String targeturl = null;
    protected String appkey = null;         
    protected String username = null;
    protected String password = null;  
    protected String blogid = null;  

    protected Blog getBlog() throws Exception { 
        BlogConnection con =   
            BlogConnectionFactory.getBlogConnection(
                apitype, targeturl, username, password);
        if (appkey != null) {          
            con.setAppkey(appkey); 
        }
        return con.getBlog(blogid); 
    }
    
    public void setApitype(String apitype) { 
        this.apitype = apitype;
    }
    
    public void setTargeturl(String targeturl) {
        this.targeturl = targeturl;
    }
    
    public void setAppkey(String appkey) {  
        this.appkey = appkey;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setBlogid(String blogid) {
        this.blogid = blogid;
    }
    
}
