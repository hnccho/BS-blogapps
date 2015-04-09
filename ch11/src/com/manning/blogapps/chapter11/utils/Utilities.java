package com.manning.blogapps.chapter11.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

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
    
    public String escapeXML(String s) {
        return StringEscapeUtils.escapeXml(s);
    }
    
    public String escapeHTML(String s) {
        return StringEscapeUtils.escapeHtml(s);
    }
    
    public static String unescapeHTML(String str) {
        return StringEscapeUtils.unescapeHtml(str);
    }
    
    public String textToHTML(String s, boolean xml) {
        if (xml) {
            return escapeXML(s);
        } else {
            return escapeHTML(s);
        }
    }
    
    public String textToHTML(String s) {
        return escapeHTML(s);
    }
    
    public static String[] stringToStringArray(String instr, String delim)
    throws NoSuchElementException, NumberFormatException {
        StringTokenizer toker = new StringTokenizer(instr, delim);
        String stringArray[] = new String[toker.countTokens()];
        int i = 0;
        
        while (toker.hasMoreTokens()) {
            stringArray[i++] = toker.nextToken();
        }
        return stringArray;
    }
    
    /**
     * Strips HTML and truncates.
     */
    public static String truncate(
            String str, int lower, int upper, String appendToEnd) {
    	
        // strip markup from the string
        String str2 = removeHTML(str, false);
        
        // quickly adjust the upper if it is set lower than 'lower'
        if (upper < lower) {
            upper = lower;
        }
        
        // now determine if the string fits within the upper limit
        // if it does, go straight to return, do not pass 'go' and collect $200
        if(str2.length() > upper) {
            // the magic location int
            int loc;
            
            // first we determine where the next space appears after lower
            loc = str2.lastIndexOf(' ', upper);
            
            // now we'll see if the location is greater than the lower limit
            if(loc >= lower) {
                // yes it was, so we'll cut it off here
                str2 = str2.substring(0, loc);
            } else {
                // no it wasnt, so we'll cut it off at the upper limit
                str2 = str2.substring(0, upper);
                loc = upper;
            }
            
            // the string was truncated, so we append the appendToEnd String
            str2 = str2 + appendToEnd;
        }
        
        return str2;
    }
    
    /**
     * This method based on code from the String taglib at Apache Jakarta.
     * http://cvs.apache.org/viewcvs/jakarta-taglibs/string/src/org/apache/taglibs/string/util/StringW.java?rev=1.16&content-type=text/vnd.viewcvs-markup
     * Copyright (c) 1999 The Apache Software Foundation.
     * Author: Henri Yandell bayard@generationjava.com
     */
    public static String truncateNicely(String str, int lower, int upper, String appendToEnd) {
    	
        // strip markup from the string
        String str2 = removeHTML(str, false);
        boolean diff = (str2.length() < str.length());
        
        // quickly adjust the upper if it is set lower than 'lower'
        if(upper < lower) {
            upper = lower;
        }
        
        // now determine if the string fits within the upper limit
        // if it does, go straight to return, do not pass 'go' and collect $200
        if(str2.length() > upper) {
            // the magic location int
            int loc;
            
            // first we determine where the next space appears after lower
            loc = str2.lastIndexOf(' ', upper);
            
            // now we'll see if the location is greater than the lower limit
            if(loc >= lower) {
                // yes it was, so we'll cut it off here
                str2 = str2.substring(0, loc);
            } else {
                // no it wasnt, so we'll cut it off at the upper limit
                str2 = str2.substring(0, upper);
                loc = upper;
            }
            
            // HTML was removed from original str
            if (diff) {
                
                // location of last space in truncated string
                loc = str2.lastIndexOf(' ', loc);
                
                // get last "word" in truncated string (add 1 to loc to eliminate space
                String str3 = str2.substring(loc+1);
                
                // find this fragment in original str, from 'loc' position
                loc = str.indexOf(str3, loc) + str3.length();
                
                // get truncated string from original str, given new 'loc'
                str2 = str.substring(0, loc);
                
                // get all the HTML from original str after loc
                str3 = extractHTML(str.substring(loc));
                
                // remove any tags which generate visible HTML
                // This call is unecessary, all HTML has already been stripped
                //str3 = removeVisibleHTMLTags(str3);
                
                // append the appendToEnd String and
                // add extracted HTML back onto truncated string
                str = str2 + appendToEnd + str3;
            } else {
                // the string was truncated, so we append the appendToEnd String
                str = str2 + appendToEnd;
            }
            
        }
        
        return str;
    }
    
    public static String truncateText(String str, int lower, int upper, String appendToEnd) {
        // strip markup from the string
        String str2 = removeHTML(str, false);
        boolean diff = (str2.length() < str.length());
        
        // quickly adjust the upper if it is set lower than 'lower'
        if(upper < lower) {
            upper = lower;
        }
        
        // now determine if the string fits within the upper limit
        // if it does, go straight to return, do not pass 'go' and collect $200
        if(str2.length() > upper) {
            // the magic location int
            int loc;
            
            // first we determine where the next space appears after lower
            loc = str2.lastIndexOf(' ', upper);
            
            // now we'll see if the location is greater than the lower limit
            if(loc >= lower) {
                // yes it was, so we'll cut it off here
                str2 = str2.substring(0, loc);
            } else {
                // no it wasnt, so we'll cut it off at the upper limit
                str2 = str2.substring(0, upper);
                loc = upper;
            }
            // the string was truncated, so we append the appendToEnd String
            str = str2 + appendToEnd;
        }
        return str;
    }
    
    /**
     * Remove occurences of html, defined as any text
     * between the characters "&lt;" and "&gt;".
     * Optionally replace HTML tags with a space.
     */
    public static String removeHTML(String str, boolean addSpace) {
 
    	if (str == null) return "";
        StringBuffer ret = new StringBuffer(str.length());
        int start = 0;
        int beginTag = str.indexOf("<");
        int endTag = 0;
        if (beginTag == -1)
            return str;
        
        while (beginTag >= start) {
            if (beginTag > 0) {
                ret.append(str.substring(start, beginTag));
                
                // replace each tag with a space (looks better)
                if (addSpace) ret.append(" ");
            }
            endTag = str.indexOf(">", beginTag);
            
            // if endTag found move "cursor" forward
            if (endTag > -1) {
                start = endTag + 1;
                beginTag = str.indexOf("<", start);
            }
            // if no endTag found, get rest of str and break
            else {
                ret.append(str.substring(beginTag));
                break;
            }
        }
        // append everything after the last endTag
        if (endTag > -1 && endTag + 1 < str.length()) {
            ret.append(str.substring(endTag + 1));
        }
        return ret.toString().trim();
    }
    
    /**
     * Remove occurences of html, defined as any text
     * between the characters "&lt;" and "&gt;".  Replace
     * any HTML tags with a space.
     */
    public static String removeHTML(String str) {
        return removeHTML(str, true);
    }
    
   /**
     * Extract (keep) JUST the HTML from the String.
     * @param str
     * @return
     */
    public static String extractHTML(String str) {
        if (str == null) return "";
        StringBuffer ret = new StringBuffer(str.length());
        int start = 0;
        int beginTag = str.indexOf("<");
        int endTag = 0;
        if (beginTag == -1)
            return str;

        while (beginTag >= start)
        {
            endTag = str.indexOf(">", beginTag);
            
            // if endTag found, keep tag
            if (endTag > -1)
            {
                ret.append( str.substring(beginTag, endTag+1) );
                
                // move start forward and find another tag
                start = endTag + 1;
                beginTag = str.indexOf("<", start);
            }
            // if no endTag found, break
            else
            {
                break;
            }
        }
        return ret.toString();
    }
    
}
