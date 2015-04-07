package com.manning.blogapps.chapter16;
import org.apache.tools.ant.*;
import com.manning.blogapps.chapter10.blogclient.*;
import java.util.*;

public abstract class BaseBlogTask extends Task {   //|#1
    protected String apitype = "metaweblog";  //|#2
    protected String targeturl = null;
    protected String appkey = null;         
    protected String username = null;
    protected String password = null;  
    protected String blogid = null;  

    protected Blog getBlog() throws Exception { //|#3
        BlogConnection con =   //|#4
            BlogConnectionFactory.getBlogConnection(
                apitype, targeturl, username, password);
        if (appkey != null) {          
            con.setAppkey(appkey); //|#5
        }
        return con.getBlog(blogid); //|#6
    }
    public void setApitype(String apitype) { //|#7
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
