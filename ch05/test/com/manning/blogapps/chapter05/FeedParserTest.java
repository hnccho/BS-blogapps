package com.manning.blogapps.chapter05;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import junit.framework.TestCase;

public class FeedParserTest extends TestCase {
    String testdataDir = "../../testdata/";
    private List testFeeds = new ArrayList();
    public static class TestFeed {
        public String path;
        public int items;
        public int enclosures;
        public TestFeed(String path, int items, int enclosures) {
            this.path = path;
            this.items = items;
            this.enclosures = enclosures;
        }
    }
    public void testFeedParser() {
        try {
            IFeedParser parser = new AnyFeedParser();
            Iterator feedIter = testFeeds.iterator();
            while (feedIter.hasNext()) {
                TestFeed testFeed = (TestFeed)feedIter.next();
                int items = 0;
                int enclosures = 0;
                Map feedMap = parser.parseFeed(testdataDir + testFeed.path); 
                Iterator itemIter = ((List)feedMap.get("items")).iterator();
                while (itemIter.hasNext()) {
                    Map itemMap = (Map)itemIter.next();
                    assertTrue(itemMap.get("title") instanceof String);
                    assertTrue(itemMap.get("link") instanceof String);
                    assertTrue(((String)itemMap.get("link")).startsWith("http"));
                    if (itemMap.get("description") != null) {
                        assertTrue(itemMap.get("description") instanceof String);
                    }
                    if (itemMap.get("pubDate") != null) {
                        assertTrue(itemMap.get("pubDate") instanceof Date);
                    }
                    if (itemMap.get("content") != null) {
                        assertTrue(itemMap.get("content") instanceof String);
                    }
                    items++;
                    if (itemMap.get("enclosure") != null) {
                        Map encMap = (Map)itemMap.get("enclosure");
                        assertTrue(encMap.get("url") instanceof String);
                        assertTrue(encMap.get("length") instanceof String);
                        assertTrue(encMap.get("type") instanceof String);
                        enclosures++;
                    }
                }
                assertEquals(testFeed.path, testFeed.items, items);
                assertEquals(testFeed.path, testFeed.enclosures, enclosures);
                System.out.println(
                    "Tested " + testFeed.path + " items:" + items + " enclosures:" + enclosures);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        } 
    }
    public void setUp() {
        try {
            BufferedReader br = 
                new BufferedReader(new FileReader("testdata.csv"));
            String line = null;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("#")) {
                    StringTokenizer toker = new StringTokenizer(line, ",");
                    testFeeds.add(new TestFeed(
                        toker.nextToken(),
                        Integer.parseInt(toker.nextToken().trim()),
                        Integer.parseInt(toker.nextToken().trim())));    
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

