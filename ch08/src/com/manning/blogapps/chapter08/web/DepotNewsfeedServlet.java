package com.manning.blogapps.chapter08.web;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.manning.blogapps.chapter08.filedepot.Depot;
import com.manning.blogapps.chapter08.filedepot.DepotNewsfeedWriter;
import com.manning.blogapps.chapter08.filedepot.FileDepot;

public class DepotNewsfeedServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,                   
        HttpServletResponse response) throws ServletException, IOException {  

        ServletContext application = this.getServletContext();    
        Depot depot = (Depot) application.getAttribute("depot");    
        if (depot == null) {                                        
            depot = new FileDepot(request.getRealPath("/depot"));    
            application.setAttribute("depot", depot);               
        }        
        try {
            String format = request.getParameter("format"); 
            if (format == null) format = "rss_2.0";  
            if (format.startsWith("rss")) {                
                response.setContentType("application/xml+rss;charset=utf-8"); 
            } else {
                response.setContentType("application/xml+atom;charset=utf-8"); 
            }
            
            String url = request.getRequestURL().toString();           
            String depotUrl = url.substring(0, url.lastIndexOf("/"));  
            DepotNewsfeedWriter depotWriter = new DepotNewsfeedWriter(depot);                        
            depotWriter.write(response.getWriter(), depotUrl, format); 
        } 
        catch (Exception ex) {
            String msg = "ERROR: generating newsfeed";
            log(msg, ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);
        }
    }
    
}
