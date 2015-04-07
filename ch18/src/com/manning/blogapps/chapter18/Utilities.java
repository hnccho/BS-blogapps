package com.manning.blogapps.chapter18;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    
    //------------------------------------------------------------------------
    public static void copyInputToOutput(
        InputStream input, OutputStream output) throws IOException {
        BufferedInputStream in = new BufferedInputStream(input);
        BufferedOutputStream out = new BufferedOutputStream(output);
        byte buffer[] = new byte[8192];
        for (int count = 0; count != -1;) {
            count = in.read(buffer, 0, 8192);
            if (count != -1)
                out.write(buffer, 0, count);
        }
        try {
            in.close();
            out.close();
        }
        catch (IOException ex) {
            throw new IOException("Closing file streams, " + ex.getMessage());
        }
    }
}
