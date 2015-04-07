<%@ 
page import="com.manning.blogapps.chapter08.filedepot.*" %><%@ 
page import="com.manning.blogapps.chapter08.Utilities" %><%@
page import="java.io.*" %><%@
page import="java.util.*" %><%
String reqURL = request.getRequestURL().toString();
String baseURL = reqURL.substring(0, reqURL.lastIndexOf("/")); //|#2
Utilities utilities = new Utilities();
Depot depot = (FileDepot)application.getAttribute("depot");
if (depot == null) {
    depot = new FileDepot(request.getRealPath("/depot"));
	  application.setAttribute("depot", depot);
}           
Date sinceDate = new Date(request.getDateHeader("If-Modified-Since"));
if (sinceDate != null && depot.getLastUpdateDate().compareTo(sinceDate) <= 0) {
   response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
   response.flushBuffer();
   return;
} 
else {
   response.setDateHeader("Last-Modified", depot.getLastUpdateDate().getTime());
   response.setContentType("application/rss+xml;charset=utf-8");
}
%><?xml version="1.0" encoding="utf-8"?>
<rss version="2.0">
<channel>
    <language>en-us</language>
    <title>File Depot Newsfeed</title>
    <description>Recent Additions to File Depot</description>
    <link><%= baseURL %>/filedepot.jsp</link>
    <lastBuildDate><%= utilities.formatRfc822Date(depot.getLastUpdateDate()) %></lastBuildDate>
    <% Iterator files = depot.getFiles().iterator();
    while (files.hasNext()) { 
        File file = (File)files.next();
    %><item>
        <title><%= file.getName() %></title>
        <description><%= file.getName() %> is available for download</description>
        <link><%= baseURL + "/" + file.getName() %></link>
        <pubDate><%= utilities.formatRfc822Time(file.lastModified()) %></pubDate>
    </item><% } %>
</channel>
</rss>
