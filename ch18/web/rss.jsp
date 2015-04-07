<?xml version="1.0" encoding="utf-8"?>
<%@ page import="java.util.*" %>
<%@ page import="com.manning.blogapps.chapter18.*" %>
<%@ page import="com.manning.blogapps.chapter18.filecaster.*" %>
<% 
FileCaster fileCaster = FileCasterImpl.getFileCaster(request);
Utilities utilities = new Utilities();
Date sinceDate = new Date(request.getDateHeader("If-Modified-Since"));
if (sinceDate != null 
   && fileCaster.getLastUpdateDate().compareTo(sinceDate) <= 0) {
   response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
   response.flushBuffer();
   return;
} else {
   response.setDateHeader("Last-Modified", 
      fileCaster.getLastUpdateDate().getTime());
   response.setContentType("application/rss+xml;charset=utf-8");
} %>
<rss version="2.0">
<channel>
    <language>en-us</language>
    <title>FileCaster</title>
    <description>Most recent file-casts</description>
    <link><%= fileCaster.getAbsoluteUrl() %></link>
    <lastBuildDate>
       <%= utilities.formatRfc822Date(fileCaster.getLastUpdateDate()) %>
    </lastBuildDate>
    <% Iterator fileCasts = fileCaster.getRecentFileCasts(30).iterator();
    while (fileCasts.hasNext()) { 
        FileCast fileCast = (FileCast)fileCasts.next();
        String url = fileCast.getUrl(
           fileCaster.getAbsoluteUrl() + "/uploads/");
        %>
        <item>
           <title><%= fileCast.getTitle() %></title>
           <description>
              <%= fileCast.getDescription() %>
              [&lt;a href="<%= url %>"&gt;Download&lt;/a&gt;]
           </description>
           <guid isPermaLink="true"><%= url %></guid>
           <link><%= url %></link>
           <pubDate>
              <%= utilities.formatRfc822Date(fileCast.getUploadTime()) %> 
           </pubDate>
           <enclosure url="<%= url %>" 
                     type="<%= fileCast.getContentType() %>" 
                     length="<%= fileCast.getContentLength() %>" />
       </item>
    <% } %>
</channel>
</rss>
