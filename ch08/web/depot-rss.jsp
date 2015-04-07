<?xml version="1.0" encoding="utf-8"?>                        <%-- |#1 --%>
<?xml-stylesheet href="atomfeed1.css" type="text/css"?><%@   
page import="com.manning.blogapps.chapter08.filedepot.*" %><%@       
page import="com.manning.blogapps.chapter08.Utilities" %><%@    
page import="java.io.*" %><%@
page import="java.util.*" %><%
String reqURL = request.getRequestURL().toString();
String baseURL = reqURL.substring(0, reqURL.lastIndexOf("/")); //|#2
Utilities utilities = new Utilities();          
Depot depot = (FileDepot)application.getAttribute("depot");  //|#3
if (depot == null || utilities == null) {                    //|#3
    depot = new FileDepot(request.getRealPath("/depot"));    //|#3
	  application.setAttribute("depot", depot);                //|#3
} 
response.setContentType("application/rss+xml;charset=utf-8");    //|#4
%><rss version="2.0">                                               <%-- |#5 --%>
<channel>                                                           <%-- |#5 --%>
    <title>File Depot Newsfeed</title>                              <%-- |#5 --%>          
    <description>Recent Additions to File Depot</description>       <%-- |#5 --%>
    <link><%= utilities.escapeXml(baseURL) %></link>                 <%-- |#5 --%>
    <lastBuildDate>                                                 <%-- |#5 --%>
       <%= utilities.formatRfc822Date(depot.getLastUpdateDate()) %> <%-- |#5 --%>
    </lastBuildDate>                                                <%-- |#5 --%>
    <% Iterator files = depot.getFiles().iterator();                   //|#6
    while (files.hasNext()) {                                          //|#6
        File file = (File)files.next();
        String url = utilities.escapeXml(baseURL + file.getName());    //|#7
    %><item> 
        <title><%= file.getName() %></title>                        <%-- |#8 --%>
        <link><%= url %></link>                                     <%-- |#8 --%>
        <pubDate>                                                   <%-- |#8 --%>
           <%= utilities.formatRfc822Time(file.lastModified()) %>   <%-- |#8 --%>
        </pubDate>                                                  <%-- |#8 --%>
        <description>         
           <%=                                                         //|#9
           utilities.escapeXml("Click <a href='" + url + "'>"          //|#9
               + file.getName() + "</a> to download the file.")        //|#9
           %>                                                     
        </description>
    </item><% } %>
</channel>
</rss>
