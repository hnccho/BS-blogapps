package com.manning.blogapps.chapter08.web;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.manning.blogapps.chapter08.LRUCache;
import com.manning.blogapps.chapter08.filedepot.Depot;
import com.manning.blogapps.chapter08.filedepot.DepotNewsfeedWriter;
import com.manning.blogapps.chapter08.filedepot.FileDepot;

public class DepotNewsfeedServletCached extends HttpServlet {    
	LRUCache cache = new LRUCache(5, 5400);                      
                                                                
    protected void doGet(                                        
    		HttpServletRequest request,  HttpServletResponse response)   
    		throws ServletException, IOException {                      

        ServletContext application = this.getServletContext();    
        Depot depot = (Depot) application.getAttribute("depot");  
        if (depot == null) {                                      
            depot = new FileDepot(request.getRealPath("/depot"));  
            application.setAttribute("depot", depot);             
        }
        depot.update();

        Date sinceDate = new Date(request.getDateHeader("If-Modified-Since"));                 
        if (sinceDate != null) {                                        
           if (depot.getLastUpdateDate().compareTo(sinceDate) <= 0) {                
              response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);  
              response.flushBuffer();                                  
              return;                                                   
           }
        }        
        try {
            response.setContentType("application/xml;charset=utf-8");  
            
            response.setDateHeader("Last-Modified", depot.getLastUpdateDate().getTime()); 
            response.setHeader("Cache-Control","max-age=5400, must-revalidate");  
            
            String url = request.getRequestURL().toString();          
            String depotUrl = url.substring(0, url.lastIndexOf("/")); 

            if (cache.get(url) == null) {                             
               String format = request.getParameter("format");  
               if (format == null) format = "rss_2.0";          
               
               StringWriter stringWriter = new StringWriter();    
               DepotNewsfeedWriter depotWriter = new DepotNewsfeedWriter(depot);                 
               depotWriter.write(stringWriter, depotUrl, format); 
               
               cache.put(request.getRequestURL().toString(), stringWriter.toString());                 
            }
            response.getWriter().write((String)cache.get(url));  
        } 
        catch (Exception ex) {
            String msg = "ERROR: generating newsfeed";
            log(msg, ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);
        }
    }
    
}



