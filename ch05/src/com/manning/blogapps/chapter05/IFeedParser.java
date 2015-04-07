package com.manning.blogapps.chapter05;

import java.io.Reader;
import java.util.Map;

/** Interface for all FeedParsers */
public interface IFeedParser {
    public Map parseFeed(Reader is) throws Exception;
    public Map parseFeed(String fileName) throws Exception;
}
