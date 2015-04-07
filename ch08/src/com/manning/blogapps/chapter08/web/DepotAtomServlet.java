package com.manning.blogapps.chapter08.web;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.servlet.VelocityServlet;

import com.manning.blogapps.chapter08.Utilities;
import com.manning.blogapps.chapter08.filedepot.Depot;
import com.manning.blogapps.chapter08.filedepot.FileDepot;

/**
 * Produce Atom feed for FileDepot using Velocity Template engine.
 * @author Dave Johnson
 */
public class DepotAtomServlet extends VelocityServlet {

    public Template handleRequest( HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   Context context)
                                   throws Exception {

        // Look for depot and utilities in application scope and create
        // a new one if necessary
        ServletContext application = this.getServletContext();
        Depot depot = (Depot)application.getAttribute("depot");
        if (depot == null) {
            depot = new FileDepot(request.getRealPath("/depot"));
            application.setAttribute("depot", depot);
        }
        
        // Load depot and data items into Velocity context 
        String requestUrl = request.getRequestURL().toString();
        String depotUrl = requestUrl.substring(0, requestUrl.lastIndexOf("/"));
        context.put("depot", depot);
        context.put("selfUrl", requestUrl);
        context.put("depotUrl", depotUrl);
        context.put("utilities", new Utilities());
        
        // Set Last-Modified needed for HTTP Conditional GET
        response.setDateHeader("Last-Modified", depot.getLastUpdateDate().getTime());
        response.setContentType("application/atom+xml");
        
        // Return template to Velocity for execution
        return getTemplate("/atom.vm");
    }
    
}