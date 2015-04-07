<html>
<%@ page import="java.util.*" %>
<%@ page import="com.manning.blogapps.chapter18.filecaster.*" %>
<%@ page import="org.apache.commons.fileupload.*" %>
<head>
<link href="blogapps.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="filecaster.js"></script>
<% 
FileCaster fileCaster = FileCasterImpl.getFileCaster(request);
List errors = new ArrayList();
List messages = new ArrayList();
if (request.getMethod().equals("POST")) {
   try {
      String removeFile = request.getParameter("removeFile");
      if (removeFile != null) {
         fileCaster.removeFileCast(removeFile);
         messages.add("SUCCESS: removed file-cast");
      } else {
         FileCast fileCast = new FileCast();	
         DiskFileUpload uploader = new DiskFileUpload();  
         fileCast.initFromFileUpload(uploader.parseRequest(request)); 
         errors.addAll(fileCast.validate());
         if (errors.size() == 0) {
            fileCaster.addFileCast(fileCast); 
	     }
      }
   } catch (Exception e) {
      errors.add("ERROR: processing posted data");
   }
} %>
</head>
<body>
<h1>FileCaster</h1>
<h2>Add a new file-cast</h2>

<% if (messages.size() > 0) { %>
<span class="messages">
<% for (int i=0; i<messages.size(); i++) {
   out.write(messages.get(0).toString() + "<br />"); } %>
<% } %>
</span>

<% if (errors.size() > 0) { %>
<span class="errors">
<% for (int i=0; i<errors.size(); i++) {
   out.write(errors.get(0).toString() + "<br />"); } %>
<% } %>
</span>

<p>Enter title and description to appear in newsfeed.
Upload a file or specify a file-cast at a remote URL.</p>

<form name="addFileCast" action="index.jsp" method="post" 
   enctype="multipart/form-data">
   <div class="row">
      <label class="leftcol" name="title">Title</label> 
      <input type="text" name="title" size="40" />
   </div>
   <div class="row">
      <label class="leftcol" name="description">Description</label> 
      <input type="text" name="description" size="40"URL />
   </div>
   <div class="row">
      <label class="leftcol" name="remoteUrl">Remote URL</label> 
      <input type="text" name="remoteUrl" size="40" onkeyup="validate()" />
   </div>
   <div class="row">
      <label class="leftcol" name="fileUpload">File Upload</label> 
      <input type="file" name="fileUpload" size="40" 
         onchange="validate()" onkeydown="validate()" />
   </div>
   <br />
   <div class="row">
      <span class="leftcol">&nbsp;</span> 
      <input name="add_button" type="submit" value="Add" disabled="true" />
   </div>
</form>

<h2>File-cast Archive</h2>

<p>Most recent file-casts in the archive are listed below.
To subscribe, right-click the orange XML icon below, copy 
the newsfeed link, and paste it into your newsfeed reader.</p>
<p><a href="rss.jsp"><img src="images/rssbadge.gif"" border=0" /></a></p>

<div style="padding: 10px; border: grey 1px dotted">
<% Iterator list = fileCaster.getRecentFileCasts(30).iterator();
while (list.hasNext()) { 
   FileCast loopcast = (FileCast)list.next(); 
   String url = loopcast.getUrl(
      fileCaster.getAbsoluteUrl() + "/uploads/");
   %>
   <span class="title">
      <b>Title</b>: <%= loopcast.getTitle() %>
   </span><br />
   <span class="details">
      <% if (loopcast.getDescription().length() > 0) {
         out.write(loopcast.getDescription() + "<br />"); } %> 
      <b>Content Type</b>: <%= loopcast.getContentType() %>
      <b>Content Length</b>: <%= loopcast.getContentLength() %><br />
      <b>Link</b>: <%= url %>
   </span><br />
   [<a href="<%= url %>">downoad</a>]
   [<a href="#" onclick=
      "removeFileCast('<%= loopcast.getSaveFilename() %>')">remove</a>]
   <br /><br />
<% } %>
</div>

<form name="removeForm" action="index.jsp" method="post">
   <input type="hidden" name="removeFile" value="" />
</form>

</body>
</html>
