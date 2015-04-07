package com.manning.blogapps.chapter16;
import org.apache.tools.ant.*;
import com.manning.blogapps.chapter10.blogclient.*;
import com.manning.blogapps.chapter10.metaweblogclient.MetaWeblogConnection;
import com.manning.blogapps.chapter10.atomclient.AtomBlogConnection;
import java.util.*;

public class PostBlogEntryTask extends BaseBlogTask {  //|#1
    protected String title = null;     //|#2
    protected String category = null;
    protected String content = null;
    
    public void execute() throws BuildException {  //#3
        try {
            System.out.println("Posting to blog");           
            System.out.println("   username=" + username);
            System.out.println("   targeturl=" + targeturl);
            System.out.println("   apitype=" + apitype);
            System.out.println("   title=" + title);
            System.out.println("   content=" + content);
            System.out.println("   category=" + category);
            
            Blog blog = getBlog();
            BlogEntry entry = blog.newEntry();  //|#4
            
            if (title != null) {
                entry.setTitle(title);  //|#5
            }
            if (category != null) {                 
               List categories = new ArrayList(); 
               categories.add(
                   new BlogEntry.Category(category));
               entry.setCategories(categories);      //|#6
            }
            if (content != null) {
                entry.setContent(
                    new BlogEntry.Content(content)); //|#7
            }
            entry.setPublicationDate(new Date());    //|#8
            entry.save();                            //|#9
            String id = entry.getId();     
            System.out.println("New post id is " + id);  //|#10
            
        } catch (Exception e) {
            throw new BuildException(e);  //|#11
        }
    }
    public void setTitle(String title) { //|#12
        this.title = title;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
