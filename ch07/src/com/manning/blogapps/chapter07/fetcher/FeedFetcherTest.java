package com.manning.blogapps.chapter07.fetcher;

import java.io.File;
import java.net.URL;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.FetcherEvent;
import com.sun.syndication.fetcher.FetcherListener;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.HashMapFeedInfoCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;

public class FeedFetcherTest implements FetcherListener {      

	public static void main(String[] args) throws Exception {
        if (args.length < 2) {                                 
            System.out.println(
                "USAGE: FeedFetcherTest [disk|memory] <feed-url>");
            return;
        }
        new FeedFetcherTest(args[0], args[1]);                 
    }
    
	public FeedFetcherTest(String type, String url) throws Exception {
        FeedFetcherCache feedInfoCache = null;
        if ("disk".equals(type)) {                             
            File cache = new File("./cache");
            if (!cache.exists()) cache.mkdirs();
            feedInfoCache = new DiskFeedInfoCache(cache.getAbsolutePath());
        } else { 
            feedInfoCache = new HashMapFeedInfoCache();        
        }       
        FeedFetcher feedFetcher = new HttpURLFeedFetcher(feedInfoCache);
        feedFetcher.addFetcherEventListener(this);             
        SyndFeed feed = feedFetcher.retrieveFeed(new URL(url));             
    }
	
    public void fetcherEvent(FetcherEvent ev) {                
        System.out.println("FetcherEvent received");
        System.out.println("  eventType: " + ev.getEventType());
        System.out.println("  urlString: " + ev.getUrlString());
    }
    
}

