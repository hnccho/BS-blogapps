package com.manning.blogapps.chapter05;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/** Extends AnyFeedParser to add caching. */
public class AnyFeedParserCaching extends AnyFeedParser {

	public String cacheDir = ".";

	public String getCacheDir() {
        return cacheDir;
    }

	public void setCacheDir(String cacheDir) {
        this.cacheDir = cacheDir;
    }

	public static void main(String[] args) throws Exception {
        Map parsedFeed = new AnyFeedParserCaching().parseFeed(args[0]);
        FeedPrinter.displayFeed(parsedFeed, new PrintWriter(System.out));
    }

	public Map parseFeed(String feedFileName) throws Exception {
        if (!feedFileName.startsWith("http://")) {
            return (parseFeed(new FileReader(feedFileName)));
        }
        URL url = new URL(feedFileName);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        CachedFeed cachedFeed = getCache(url);
        if (cachedFeed != null) {
            conn.setIfModifiedSince(cachedFeed.lastModified);
        }
        if (conn.getResponseCode() != HttpURLConnection.HTTP_NOT_MODIFIED) {
            System.out.println("Fetching new copy of feed");
            String feedString = inputStreamToString(conn.getInputStream());
            putCache(url, feedString, conn.getLastModified());
            return parseFeed(new StringReader(feedString));
        }
        System.out.println("Not fetching, parsing cached feed");
        return parseFeed(new StringReader(cachedFeed.feed));
    }

	public static class CachedFeed implements Serializable {
		private static final long serialVersionUID = 1L;
		URL url;
        String feed;
        long lastModified;
        public CachedFeed(URL url, String feed, long lastModified) {
            this.url = url;
            this.feed = feed;
            this.lastModified = lastModified;
        }
    }
    
	private void putCache(URL url, String feed, long lastModified) {
        CachedFeed cachedFeed = new CachedFeed(url, feed, lastModified);
        String fileName = cacheDir + File.separator + "feed_" + url.hashCode();
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(cachedFeed);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	private CachedFeed getCache(URL url) {
        CachedFeed cachedFeed = null;
        String fileName = cacheDir + File.separator + "feed_" + url.hashCode();
        FileInputStream fis;
        try {
            fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            cachedFeed = (CachedFeed)ois.readObject();
            fis.close();
        } catch (Exception ignored) {}
        return cachedFeed;
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
