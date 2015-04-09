package com.manning.blogapps.chapter16;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.tools.ant.BuildException;

import com.manning.blogapps.chapter10.blogclient.Blog;
import com.manning.blogapps.chapter10.blogclient.BlogEntry;

public class PostBlogEntryTask extends BaseBlogTask {  

	protected String title = null;     
    protected String category = null;
    protected String content = null;
    
    public void execute() throws BuildException {  
    	
        try {
            System.out.println("Posting to blog");           
            System.out.println("   username=" + username);
            System.out.println("   targeturl=" + targeturl);
            System.out.println("   apitype=" + apitype);
            System.out.println("   title=" + title);
            System.out.println("   content=" + content);
            System.out.println("   category=" + category);
            
            Blog blog = getBlog();
            BlogEntry entry = blog.newEntry();  
            
            if (title != null) {
                entry.setTitle(title);  
            }
            if (category != null) {                 
               List categories = new ArrayList(); 
               categories.add(new BlogEntry.Category(category));
               entry.setCategories(categories);      
            }
            if (content != null) {
                entry.setContent(new BlogEntry.Content(content)); 
            }
            entry.setPublicationDate(new Date());    
            entry.save();                            
            String id = entry.getId();     
            System.out.println("New post id is " + id);  
            
        } catch (Exception e) {
            throw new BuildException(e); 
        }
    }
    
    public void setTitle(String title) { 
        this.title = title;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
}
