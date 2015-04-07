/*
 * Copyright 2005, Dave Johnson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.manning.blogapps.chapter10.atomclient;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.WireFeedInput;
import com.sun.syndication.io.WireFeedOutput;

/**
 * Convenience methods for Rome/JDOM.
 * @author Dave Johnson
 */
public class RomeUtilities {
    public static final String FEED_TYPE = "atom_1.0"; 

    /** 
     * Serialize a ROME Atom Entry, including foreign markup.
     * @param entry         ROME Atom entry to be serialzied
     */
    public static void serializeEntry(Entry entry, Writer writer) 
        throws IllegalArgumentException, FeedException, IOException {
        
        // Create feed to wrap the <entry>, write it to a DOM
        Feed feed = new Feed();
        feed.setFeedType(FEED_TYPE);
        List entries = new ArrayList();
        entries.add(entry);            
        feed.setEntries(entries);
        WireFeedOutput wireFeedOutput = new WireFeedOutput();
        Document feedDoc = wireFeedOutput.outputJDom(feed);
        
        // Pull <entry> element out of the DOM
        Element entryElement= (Element)feedDoc.getRootElement().getChildren().get(0);
         
        // And write it out
        XMLOutputter outputter = new XMLOutputter();
        outputter.output(entryElement, writer);
    }
        
    /**
     * Parse Atom entry XML document, extracting foreign markup along the way,
     * and return ROME Atom Entry.
     * @param reader        Reader from which to read XML
     * @returns ROME Atom Entry
     */
    public static Entry parseEntry(Reader reader) 
        throws JDOMException, IOException, IllegalArgumentException, FeedException {
        
        // Parse XML into DOM and detach the <entry> element
        SAXBuilder builder = new SAXBuilder();
        Document entryDoc = builder.build(reader);
        Element fetchedEntryElement = entryDoc.getRootElement();
        fetchedEntryElement.detach();
        
        // Create Rome feed, output it as a DOM, and add <entry> element
        Feed feed = new Feed();
        feed.setFeedType(FEED_TYPE);
        WireFeedOutput wireFeedOutput = new WireFeedOutput();
        Document feedDoc = wireFeedOutput.outputJDom(feed); 
        feedDoc.getRootElement().addContent(fetchedEntryElement);
        
        // Build a Rome object model based on that DOM, get entry from it
        WireFeedInput input = new WireFeedInput();
        Feed parsedFeed = (Feed)input.build(feedDoc);
        Entry entry = (Entry)parsedFeed.getEntries().get(0);        
        
        return entry;
    }
    
}
