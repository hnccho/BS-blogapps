package com.manning.blogapps.chapter08.web;
import java.io.*;
import java.io.StringWriter;
import java.util.Date;
import javax.servlet.*;
import javax.servlet.http.*;
import com.manning.blogapps.chapter08.*;
import com.manning.blogapps.chapter08.filedepot.*;

public class DepotNewsfeedServletCached extends HttpServlet {    //|#1
    LRUCache cache = new LRUCache(5, 5400);                      //|#1
                                                                 //|#1
    protected void doGet(                                        //|#1
    		HttpServletRequest request,  HttpServletResponse response)   //|#1
    		throws ServletException, IOException {                       //|#1

        ServletContext application = this.getServletContext();     //|#2
        Depot depot = (Depot) application.getAttribute("depot");   //|#2
        if (depot == null) {                                       //|#2
            depot = new FileDepot(request.getRealPath("/depot"));  //|#2
            application.setAttribute("depot", depot);              //|#2
        }
        depot.update();

        Date sinceDate = new Date(                                      //|#3
           request.getDateHeader("If-Modified-Since"));                 //|#3
        if (sinceDate != null) {                                        //|#3
           if (depot.getLastUpdateDate().compareTo(sinceDate) <= 0) {   //|#3              
              response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);  //|#3
              response.flushBuffer();                                   //|#3
              return;                                                   //|#3
           }
        }        
        try {
            response.setContentType("application/xml;charset=utf-8");  //|#4
            
            response.setDateHeader("Last-Modified",   //|#5
               depot.getLastUpdateDate().getTime());  //|#5
            response.setHeader(                                   //|#6
               "Cache-Control","max-age=5400, must-revalidate");  //|#6
            
            String url = request.getRequestURL().toString();          //|#7
            String depotUrl = url.substring(0, url.lastIndexOf("/")); //|#7
            if (cache.get(url) == null) {                             //|#7
                
               String format = request.getParameter("format");  //|#8
               if (format == null) format = "rss_2.0";          //|#8
               
               StringWriter stringWriter = new StringWriter();     //|#9
               DepotNewsfeedWriter depotWriter =                   //|#9
                   new DepotNewsfeedWriter(depot);                 //|#9
               depotWriter.write(stringWriter, depotUrl, format);  //|#9
               
               cache.put(request.getRequestURL().toString(),  //|#10
                   stringWriter.toString());                  //|#10
            }
            response.getWriter().write((String)cache.get(url));  //|#11
        } 
        catch (Exception ex) {
            String msg = "ERROR: generating newsfeed";
            log(msg, ex);
            response.sendError(
               HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);
        }
    }
    
}



