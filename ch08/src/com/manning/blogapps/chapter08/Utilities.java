package com.manning.blogapps.chapter08;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * General purpose utilities. 
 */
public class Utilities {
    
    private static final SimpleDateFormat iso8601Format = 
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    
    public String formatIso8601Date(Date date) {
        String str = iso8601Format.format(date);
        StringBuffer sb = new StringBuffer();
        sb.append( str.substring(0,str.length()-2) );
        sb.append( ":" );
        sb.append( str.substring(str.length()-2) );
        return sb.toString();
    }   
    
    public String formatIso8601Time(long time) {
        return formatIso8601Date(new Date(time));
    }   

    private static final SimpleDateFormat rfc822Format = 
        new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss z");   
    
    public String formatRfc822Date(Date date) {
        return rfc822Format.format(date);
    } 
    
    public String formatRfc822Time(long time) {
        return formatRfc822Date(new Date(time));
    } 
        
    public String escapeXml(String s) {
        return StringEscapeUtils.escapeXml(s);
    }
    
    public String escapeHtml(String s) {
        return StringEscapeUtils.escapeHtml(s);
    }
    
    public String textToHtml(String s, boolean xml) {
        if (xml) {
            return escapeXml(s);
        } else {
            return escapeHtml(s);
        }
    }
    
    public static String inputStreamToString(InputStream is) throws IOException {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        StringBuffer sb = new StringBuffer();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }     
        return sb.toString();
    }   
    
}
