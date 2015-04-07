package com.manning.blogapps.chapter12;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Iterator;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class Tapi2opml {
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {  //|#1
            System.out.println(
                    "USAGE: tapi2opml [blogcosmos|linkcosmos|outbound] [search] [filename]");
            System.exit(-1);
        }
        String option = args[0];
        String search = args[1];
        String filename = args[2];
        Technorati tapi = new Technorati();  //|#2
        Technorati.Result result = null;
        if (option.equals("blogcosmos")) {         
            result = tapi.getWeblogCosmos(search);    
            System.out.println("Writing blogcosmos to OPML");
        } else if (option.equals("linkcosmos")) {
            result = tapi.getLinkCosmos(search);        
            System.out.println("Writing linkcosmos links to OPML");
        } else if (option.equals("outbound")) {
            result = tapi.getOutbound(search);        
            System.out.println("Writing outbound links to OPML");
        } 
        FileWriter fw = new FileWriter(filename);
        PrintWriter pw = new PrintWriter(fw);
        pw.println(tapi2opml(result));   //#4
        pw.close();
        checkXml(filename);
        System.out.println("Wrote file ["+filename+"]");
    }
    public static String tapi2opml(Technorati.Result result) {  //|#5
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding='utf-8'?>\n"); //|#6
        sb.append("<opml version=\"1.1\">\n<body>\n");
        Iterator blogs = result.getWeblogs().iterator();         //|#7
        while (blogs.hasNext()) {
            Technorati.Weblog blog = (Technorati.Weblog) blogs.next();
            if (blog.getRssurl() != null && blog.getRssurl().length() > 0) { //|#8
                sb.append("<outline text=\"");
                sb.append(blog.getName());
                sb.append("\" xmlUrl=\"");
                sb.append(blog.getRssurl());
                sb.append("\" htmlUrl=\"");
                sb.append(blog.getUrl());
                sb.append("\" />\n");
            }
        }
        sb.append("</body></opml>"); 
        return sb.toString();
    }
    public static void checkXml(String filename) throws Exception {
        try {
            SAXBuilder builder = new SAXBuilder();
            builder.build(new FileInputStream(filename));
        }
        catch (JDOMException e) {
            System.err.println("ERROR: OPML file produced is not valid XML");
            e.printStackTrace();
        }
    }
}

