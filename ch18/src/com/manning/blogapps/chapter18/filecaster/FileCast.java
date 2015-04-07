package com.manning.blogapps.chapter18.filecaster;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.fileupload.FileItem;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

/** 
 * Represents a local or remote file to be made 
 * available in a newsfeed enclosure. 
 * Loads and saves this format:
 * <file-cast>
 *    <title></title>
 *    <description></description>
 *    <remote-url></remote-url>
 *    <local-path></local-path>
 *    <content-type></content-type>
 *    <upload-time></upload-time>
 * </file-cast>
 */
public class FileCast {
    private String title;
    private String description;
    private String filename;
    private String remoteUrl;
    private String contentType;
    private long contentLength = -1;
    private Date uploadTime;
    private FileItem upload = null;
    
    public FileCast() {}

    public void initFromFileUpload(List items) {
        setUploadTime(new Date());
        Iterator iter = items.iterator();
        while (iter.hasNext()) {
            FileItem item = (FileItem)iter.next();
            if (item.isFormField()) {
                if (item.getFieldName().equals("title")) {
                   setTitle(item.getString());
                } 
                else if (item.getFieldName().equals("description")) {
                   setDescription(item.getString());
                }
                else if (item.getFieldName().equals("remoteUrl")) {
                   setRemoteUrl(item.getString());
                }
            }
            else {
               upload = item;
               if (upload.getName() != null 
                   && upload.getName().trim().length() > 0) {
                   setFilename(upload.getName());
                   setContentType(upload.getContentType());
                   setContentLength(upload.getSize()); 
               }
            }
        }
    }
    
    public void load(FileInputStream is) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(is);
        Element root = doc.getRootElement();
        title = getString(root, "/file-cast/title");
        description = getString(root, "/file-cast/description");
        remoteUrl   = getString(root, "/file-cast/remote-url");
        filename    = getString(root, "/file-cast/filename");
        contentType = getString(root, "/file-cast/content-type");
        contentLength = getLong(root, "/file-cast/content-length");
        uploadTime  = getDate(root, "/file-cast/upload-time");
    }
    
    public void save(FileOutputStream os) throws Exception {
        Document doc = new Document();
        Element root = new Element("file-cast");
        doc.setRootElement(root);
        if (title != null) {
            root.addContent(new Element("title").setText(title));
        }
        if (description != null) {
            root.addContent(new Element("description").setText(description));
        }
        if (remoteUrl != null) {
            root.addContent(new Element("remote-url").setText(remoteUrl));
        }
        if (filename != null) {
            root.addContent(new Element("filename").setText(filename));
        }
        if (contentType != null) {
            root.addContent(new Element("content-type").setText(contentType));
        }
        if (contentLength != -1) {
            root.addContent(new Element("content-length").setText(""+contentLength));
        }
        if (uploadTime != null) {
            DateFormat format = DateFormat.getDateTimeInstance(
               DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault());
            String dateString = format.format(uploadTime);
            root.addContent(new Element("upload-time").setText(dateString));
        }
        XMLOutputter outputter = new XMLOutputter();
        outputter.output(doc, os);
    }
    
    public List validate() {
        List errors = new  ArrayList();
        if (remoteUrl != null && remoteUrl.trim().length() > 0) {
            try {
                HttpURLConnection con = (HttpURLConnection)
                new URL(remoteUrl).openConnection();
                con.setRequestMethod("HEAD");
                if (con.getResponseCode() != 200) {
                    errors.add("ERROR: error accessing remote URL");
                }
                else {
                    setContentType(con.getContentType());
                    setContentLength(con.getContentLength());
                }
            } catch (Exception e) {
                errors.add("ERROR: remote URL is not a valid URL");
            }
        }
        if (getSaveFilename() == null) {
            errors.add("ERROR: you must upload a file or specify a remote URL");
        }
        return errors;
    }
        
    /** Returns filename or converts URL to usable filename. */
    public String getSaveFilename() {
        String saveFilename = filename;
        if (saveFilename == null && remoteUrl != null) {
            try {
                URL url = new URL(remoteUrl);
                StringBuffer sb = new StringBuffer();
                sb.append(url.getHost().replaceAll("\\.","_"));
                sb.append(url.getPath().replaceAll("/","_"));
                sb.append("_");
                if (url.getQuery() != null) {
                    sb.append(url.getQuery().replaceAll("[?&=]","_"));
                }
                saveFilename = sb.toString();
            } catch (Exception ignored) {}
        }
        return saveFilename;
    }

    /** Get URL link to the FileCast */
    public String getUrl(String uploadsUrl) {
        return remoteUrl == null ? uploadsUrl + filename : remoteUrl;  
    }
    
    /** Get name for display in FileCaster web UI */
    public String getDisplayName() {
        return filename == null ? remoteUrl : filename;
    }
    
    /** URL to file on remove server (only if not local file) */
    public String getRemoteUrl() {
        return remoteUrl;
    }
    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }
        
    /** MIME type of content */
    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    /** Size of filecast in bytes */
    public long getContentLength() {
        return contentLength;
    }
    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    /** Time that the file was added to the system */
    public Date getUploadTime() {
        return uploadTime;
    }
    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }
    
    /** Title for file in newsfeed */
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    
    /** Description to be used for file in newsfeed */
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    /** File name, must be valid for inclusion in URL */
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    /** Get uploaded file (or null if upload request not processed) */
    public FileItem getUpload() {
        return upload;
    }
    public void setUpload(FileItem upload) {
        this.upload = upload;
    }
    
    /** XPath helper */
    protected String getString(Element elem, String path) 
        throws JDOMException {
        XPath xpath = XPath.newInstance(path);
        Element e = (Element)xpath.selectSingleNode(elem);
        return e!=null ? e.getText() : null;
    }
    /** XPath helper */
    protected long getLong(Element elem, String path) 
        throws JDOMException {
        return Long.parseLong(getString(elem, path));
    }
    /** XPath helper */
    protected Date getDate(Element elem, String path) 
        throws ParseException, JDOMException {
        DateFormat format = DateFormat.getDateTimeInstance(
           DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault());
        return format.parse(getString(elem, path));
    }
}
