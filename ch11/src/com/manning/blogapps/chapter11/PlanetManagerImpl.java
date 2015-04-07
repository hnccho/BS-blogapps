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
package com.manning.blogapps.chapter11;

import com.manning.blogapps.chapter11.pojos.PlanetConfigData;
import com.manning.blogapps.chapter11.pojos.PlanetEntryData;
import com.manning.blogapps.chapter11.pojos.PlanetSubscriptionData;
import com.manning.blogapps.chapter11.rome.DiskFeedInfoCache;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.sun.syndication.fetcher.impl.SyndFeedInfo;

/**
 * Base class for PlanetManager implementations.
 * @author Dave Johnson
 */
public abstract class PlanetManagerImpl {
    protected String localURL = null;
    
    private static Log logger =
            LogFactory.getFactory().getInstance(PlanetManagerImpl.class);
    
    public PlanetManagerImpl() {
    }
    
    public abstract PlanetConfigData getConfiguration() throws Exception;
    public abstract Iterator getAllSubscriptions() throws Exception;
    public abstract void saveEntry(PlanetEntryData entry) throws Exception;
    public abstract void saveSubscription(PlanetSubscriptionData entry) throws Exception;
    public abstract void deleteEntry(PlanetEntryData entry) throws Exception;
    public abstract void clearCachedAggregations() throws Exception;
    
    public void refreshEntries() throws Exception {
        Date now = new Date();
        long startTime = System.currentTimeMillis();
        PlanetConfigData config = getConfiguration();
        if (config == null || config.getCacheDir() == null) {
            logger.warn("Planet cache directory not set, aborting refresh");
            return;
        }
        FeedFetcherCache feedInfoCache =
                new DiskFeedInfoCache(config.getCacheDir());
        
        if (config.getProxyHost()!=null && config.getProxyPort() > 0) {
            System.setProperty("proxySet", "true");
            System.setProperty("http.proxyHost", config.getProxyHost());
            System.setProperty("http.proxyPort",
                    Integer.toString(config.getProxyPort()));
        }
        /** a hack to set 15 sec timeouts for java.net.HttpURLConnection */
        System.setProperty("sun.net.client.defaultConnectTimeout", "15000");
        System.setProperty("sun.net.client.defaultReadTimeout", "15000");
        
        FeedFetcher feedFetcher = new HttpURLFeedFetcher(feedInfoCache);
        //FeedFetcher feedFetcher = new HttpClientFeedFetcher(feedInfoCache);
        feedFetcher.setUsingDeltaEncoding(false);
        feedFetcher.setUserAgent("RollerPlanetAggregator");
                
        // Loop through all subscriptions in the system
        Iterator subs = getAllSubscriptions();
        while (subs.hasNext()) {
            long subStartTime = System.currentTimeMillis();
            
            // Fetch latest entries for each subscription
            Set newEntries = null;
            int count = 0;
            PlanetSubscriptionData sub = (PlanetSubscriptionData)subs.next();
            if (!StringUtils.isEmpty(localURL) && sub.getFeedUrl().startsWith(localURL)) {
                newEntries = getNewEntriesLocal(sub, feedFetcher, feedInfoCache);
            } else {
                newEntries = getNewEntriesRemote(sub, feedFetcher, feedInfoCache);
            }
            count = newEntries.size();
            
            logger.debug("   Entry count: " + count);
            if (count > 0) {
                Iterator entryIter = sub.getEntries().iterator();
                while (entryIter.hasNext()) {
                    deleteEntry((PlanetEntryData)entryIter.next());
                }
                sub.purgeEntries();
                sub.addEntries(newEntries);
            }
            long subEndTime = System.currentTimeMillis();
            logger.info("   " + count + " - "
                    + ((subEndTime-subStartTime)/1000.0)
                    + " seconds to process (" + count + ") entries of "
                    + sub.getFeedUrl());
        }
        // Clear the aggregation cache
        clearCachedAggregations();
        
        long endTime = System.currentTimeMillis();
        logger.info("--- DONE --- Refreshed entries in "
                + ((endTime-startTime)/1000.0) + " seconds");
    }
    
    /**
     * Override this if you have local feeds (i.e. feeds that you don't
     * have to fetch via HTTP and parse with ROME).
     */
    protected Set getNewEntriesLocal(PlanetSubscriptionData sub,
            FeedFetcher feedFetcher, FeedFetcherCache feedInfoCache)
            throws Exception {
        
        // If you don't override, local feeds will be treated as remote feeds
        return getNewEntriesRemote(sub, feedFetcher, feedInfoCache);
    }
    
    protected Set getNewEntriesRemote(PlanetSubscriptionData sub,
            FeedFetcher feedFetcher, FeedFetcherCache feedInfoCache)
            throws Exception {
        
        Set newEntries = new TreeSet();
        SyndFeed feed = null;
        URL feedUrl = null;
        Date lastUpdated = new Date();
        try {
            feedUrl = new URL(sub.getFeedUrl());
            logger.debug("Get feed from cache "+sub.getFeedUrl());
            feed = feedFetcher.retrieveFeed(feedUrl);
            SyndFeedInfo feedInfo = feedInfoCache.getFeedInfo(feedUrl);
            if (feedInfo.getLastModified() != null) {
                long lastUpdatedLong =
                        ((Long)feedInfo.getLastModified()).longValue();
                if (lastUpdatedLong != 0) {
                    lastUpdated = new Date(lastUpdatedLong);
                }
            }
            Thread.sleep(100); // be nice
        } catch (Exception e) {
            logger.warn("ERROR parsing " + sub.getFeedUrl()
            + " : " + e.getClass().getName() + " : " + e.getMessage());
            logger.debug(e);
            return newEntries; // bail out
        }
        if (lastUpdated!=null && sub.getLastUpdated()!=null) {
            Calendar feedCal = Calendar.getInstance();
            feedCal.setTime(lastUpdated);
            
            Calendar subCal = Calendar.getInstance();
            subCal.setTime(sub.getLastUpdated());
            
            if (!feedCal.after(subCal)) {
                if (logger.isDebugEnabled()) {
                    String msg = MessageFormat.format(
                            "   Skipping ({0} / {1})",
                            new Object[] {
                        lastUpdated, sub.getLastUpdated()});
                        logger.debug(msg);
                }
                return newEntries; // bail out
            }
        }
        if (feed.getPublishedDate() != null) {
            sub.setLastUpdated(feed.getPublishedDate());
            saveSubscription(sub);
        }
        
        // Kludge for Feeds without entry dates: most recent entry is given
        // feed's last publish date (or yesterday if none exists) and earler
        // entries are placed at once day intervals before that.
        Calendar cal = Calendar.getInstance();
        if (sub.getLastUpdated() != null) {
            cal.setTime(sub.getLastUpdated());
        } else {
            cal.setTime(new Date());
            cal.add(Calendar.DATE, -1);
        }
        
        // Populate subscription object with new entries
        Iterator entries = feed.getEntries().iterator();
        while (entries.hasNext()) {
            try {
                SyndEntry romeEntry = (SyndEntry) entries.next();
                PlanetEntryData entry =
                        new PlanetEntryData(feed, romeEntry, sub);
                if (entry.getPublished() == null) {
                    logger.debug(
                            "No published date, assigning fake date for "+feedUrl);
                    entry.setPublished(cal.getTime());
                }
                if (entry.getPermalink() == null) {
                    logger.warn("No permalink, rejecting entry from "+feedUrl);
                } else {
                    saveEntry(entry);
                    newEntries.add(entry);
                }
                cal.add(Calendar.DATE, -1);
            } catch (Exception e) {
                logger.error("ERROR processing subscription entry", e);
            }
        }
        return newEntries;
    }
}
