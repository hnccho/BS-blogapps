package com.manning.blogapps.chapter08.web;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.manning.blogapps.chapter08.LRUCache;
import com.manning.blogapps.chapter08.filedepot.FileDepot;

/**
 * Handles HTTP Conditional GET for File Depot.
 * @author Dave Johnson
 */
public class ConditionalGetFilter implements Filter { 
    
    /** Cache the update time, so we don't have to query for it */
    private LRUCache dateCache = new LRUCache(1, 2*60*60*1000); // 1 entry, 2 hour timeout 
    
    /** Hold onto FilterConfig so we can get ServletContext */
    private FilterConfig filterConfig = null;      
    public void init(FilterConfig config) throws ServletException {
        this.filterConfig = config; 
    }
    
    /** Handle HTTP Conditional GET */
    public void doFilter(
            ServletRequest req, ServletResponse res, FilterChain chain) 
            throws IOException, ServletException { 
        
        HttpServletResponse response = (HttpServletResponse)res;
        HttpServletRequest request = (HttpServletRequest)req;
        
        // If there is a depot, then 
        ServletContext application = filterConfig.getServletContext();
        FileDepot depot = (FileDepot) application.getAttribute("depot");
        if (depot != null) { 
            Date updated = (Date)dateCache.get("updated");
            if (updated == null) {
                depot.update();
                updated = depot.getLastUpdateDate();
            } 
            Date sinceDate = new Date(request.getDateHeader("If-Modified-Since"));
            if (sinceDate != null
                    && depot.getLastUpdateDate().compareTo(sinceDate) <= 0) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                response.flushBuffer();
                return;
            } 
        }
        chain.doFilter(req, res);
    }
    public void destroy() {}
    
}
