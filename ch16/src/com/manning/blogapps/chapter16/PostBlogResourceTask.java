package com.manning.blogapps.chapter16;
import java.io.File;

import org.apache.tools.ant.BuildException;

import com.manning.blogapps.chapter10.blogclient.Blog;
import com.manning.blogapps.chapter10.blogclient.BlogResource;

public class PostBlogResourceTask extends BaseBlogTask { 
	
    private String filename = null;     
    private String contenttype = null;
    private String resourcename = null;
    private String urlproperty = "uploadurl";

    public void execute() throws BuildException {  
        try {
            System.out.println("Posting file resource to blog");  
            System.out.println("   username=" + username);
            System.out.println("   targeturl=" + targeturl);
            System.out.println("   apitype=" + apitype);
            System.out.println("   filename=" + filename);
            System.out.println("   contenttype=" + contenttype);
            System.out.println("   resourcename=" + resourcename);
            
            Blog blog = getBlog();           
            BlogResource resource = blog.newResource(
                resourcename, contenttype, new File(filename));  
            resource.save();
            getProject().setProperty(urlproperty, resource.getContent().getSrc()); 
            
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
    
    public void setFilename(String filename) {  
        this.filename = filename;
    }
    
    public void setContenttype(String contenttype) {
        this.contenttype = contenttype;
    }
    
    public void setResourcename(String resourcename) {
        this.resourcename = resourcename;
    }
    
    public void setUrlproperty(String urlproperty) {
        this.urlproperty = urlproperty;
    }
    
}