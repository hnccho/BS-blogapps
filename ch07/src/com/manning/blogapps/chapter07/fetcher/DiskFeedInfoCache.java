/*
 * Copyright 2005 Sun Microsystems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.manning.blogapps.chapter07.fetcher;
import java.io.*;
import java.net.URL;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.SyndFeedInfo;

public class DiskFeedInfoCache implements FeedFetcherCache {

	protected String cachePath = null;

	public DiskFeedInfoCache(String cachePath) {
        this.cachePath = cachePath;
    }
    
	public SyndFeedInfo getFeedInfo(URL url) {
        SyndFeedInfo info = null;
        String fileName = cachePath + File.separator + "feed_" + url.hashCode();
        FileInputStream fis;
        try {
            fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            info = (SyndFeedInfo)ois.readObject();
            fis.close();
        } catch (FileNotFoundException ignored) {
            // File not fouund is a cache miss
        } catch (ClassNotFoundException cnfe) {
            throw new RuntimeException("Attempting to read from cache", cnfe);
        } catch (IOException ioe) {
            throw new RuntimeException("Attempting to read from cache", ioe);
        }
        return info;
    }
    
	public void setFeedInfo(URL url, SyndFeedInfo feedInfo) {
        String fileName = cachePath + File.separator + "feed_" + url.hashCode();
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(feedInfo);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException("Attempting to write to cache", e);
        }
    }
	
}
