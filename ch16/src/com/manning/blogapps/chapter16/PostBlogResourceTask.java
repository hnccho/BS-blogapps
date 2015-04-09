package com.manning.blogapps.chapter16;
import java.io.*;
import org.apache.tools.ant.*;
import com.manning.blogapps.chapter10.blogclient.*;

public class PostBlogResourceTask extends BaseBlogTask {  //|#1
    private String filename = null;     //|#2
    private String contenttype = null;
    private String resourcename = null;
    private String urlproperty = "uploadurl";

    public void execute() throws BuildException {  //|#3
        try {
            System.out.println("Posting file resource to blog");  //|#4
            System.out.println("   username=" + username);
            System.out.println("   targeturl=" + targeturl);
            System.out.println("   apitype=" + apitype);
            System.out.println("   filename=" + filename);
            System.out.println("   contenttype=" + contenttype);
            System.out.println("   resourcename=" + resourcename);
            
            Blog blog = getBlog();            //|#5
            BlogResource resource = blog.newResource(
                resourcename, contenttype, new File(filename));  //|#6
            resource.save();
            getProject().setProperty(urlproperty, resource.getContent().getSrc()); //|#7
            
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
    public void setFilename(String filename) {  //|#8
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