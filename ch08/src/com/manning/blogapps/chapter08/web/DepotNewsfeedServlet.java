package com.manning.blogapps.chapter08.web;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import com.manning.blogapps.chapter08.filedepot.*;

public class DepotNewsfeedServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,                   
        HttpServletResponse response) throws ServletException, IOException {  

        ServletContext application = this.getServletContext();    
        Depot depot = (Depot) application.getAttribute("depot");     //|#1
        if (depot == null) {                                        
            depot = new FileDepot(request.getRealPath("/depot"));    //|#2
            application.setAttribute("depot", depot);               
        }        
        try {
            String format = request.getParameter("format"); //|#3
            if (format == null) format = "rss_2.0";  
            if (format.startsWith("rss")) {                 //|#4
                response.setContentType("application/xml+rss;charset=utf-8"); 
            } else {
                response.setContentType("application/xml+atom;charset=utf-8"); 
            }
            
            String url = request.getRequestURL().toString();           //|#5
            String depotUrl = url.substring(0, url.lastIndexOf("/"));  //|#5
            DepotNewsfeedWriter depotWriter =                          //|#6
                new DepotNewsfeedWriter(depot);                        //|#6
            depotWriter.write(response.getWriter(), depotUrl, format); //|#6
        } 
        catch (Exception ex) {
            String msg = "ERROR: generating newsfeed";
            log(msg, ex);
            response.sendError(
               HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);
        }
    }
    
}
