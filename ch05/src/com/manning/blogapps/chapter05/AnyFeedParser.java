/**
 * Simple parser that can handle RSS 1.0, RSS 2.0 and Atom, but only
 * reads the fundamental elements of title, pubDate, description, and link.
 * Returns results as a dictionary, keyed by RSS and Atom element names.
 * The dictionary key 'items' (also available as 'entries') holds collection 
 * of item dictionaries.
 *
 * <pre>
 * feed
 *    title
 *    description/subtitle
 * item/entry
 *    guid/id
 *    title
 *    pubDate/updated
 *    description
 *    content
 *    summary
 *    enclosure
 *       url
 *       type
 *       length
 * </pre>
 *
 * <p>Doesn't handle: author, creator, categories or feed-level pubDates</p>
 */
package com.manning.blogapps.chapter05;
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class AnyFeedParser implements IFeedParser {

	Namespace ns = Namespace.getNamespace("http://www.w3.org/2005/Atom");
    
    public static void main(String[] args) throws Exception {   
        Map<String, Object> parsedFeed = new AnyFeedParser().parseFeed(args[0]);
        FeedPrinter.displayFeed(parsedFeed, new PrintWriter(System.out));
    }
    
    public Map<String, Object> parseFeed(String feedFileName) throws Exception {  
        return (parseFeed(new FileReader(feedFileName)));
    }
    
    public Map<String, Object> parseFeed(Reader reader) throws Exception {   
        SAXBuilder builder = new SAXBuilder();
        Document feedDoc = builder.build(reader);
        Element root = feedDoc.getRootElement();
        
        if (root.getName().equals("feed")) return parseAtom(root);  
        return parseRSS(root);                                   
    }
    
    public Map<String, Object> parseAtom(Element root) throws Exception {   
        URL baseURI = findBaseURI(root);  
        
        Map<String, Object> feedMap = new HashMap<String, Object>();
        put(feedMap,"title", parseAtomContent("title", root, ns));   
        put(feedMap,"link", parseAtomLink(baseURI, root, ns));      
        put(feedMap, new String[] {"subtitle","description"},  
                parseAtomContent("subtitle", root, ns));
        
        List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
        put(feedMap, new String[] {"entries","items"}, itemList);  
        
        Iterator<?> items = root.getChildren("entry",ns).iterator();  
        while (items.hasNext()) {
            Element item = (Element) items.next();
            Map<String, Object> itemMap = new HashMap<String, Object>();
            itemList.add(itemMap);
            put(itemMap, new String[] {"id","guid"},               
                    item.getChildText("id", ns));
            put(itemMap, "title", parseAtomContent("title", item, ns));
            put(itemMap, new String[] {"summary", "description"},    
                    parseAtomContent("summary", item, ns));
            put(itemMap, "link",    parseAtomLink(baseURI, item, ns));  
            put(itemMap, "content", parseAtomContent("content", item, ns));
            String dt = item.getChildText("updated", ns);
            if (dt != null) {
                put(itemMap, new String[] {"updated","pubDate"},     
                        ISO8601DateParser.parse(dt));
            }
        }
        return feedMap;   
    }
    
    Map<String, Object> parseRSS(Element root) throws Exception {       
        Namespace contentNS = Namespace.getNamespace(               
                "content","http://purl.org/rss/1.0/modules/content/");
        Namespace dcNS = Namespace.getNamespace(                    
                "dc","http://purl.org/dc/elements/1.1/");
        Namespace ns = null;
        if (root.getName().equals("rss")) {  
            ns = Namespace.NO_NAMESPACE;
        } else {
            ns = Namespace.getNamespace("http://purl.org/rss/1.0/");
        }
        Element channel = root.getChild("channel",ns);  
        Map<String, Object> feedMap = new HashMap<String, Object>();
        put(feedMap, "title", channel.getChildText("title",ns));
        put(feedMap, "link", channel.getChildText("link",ns));
        put(feedMap, new String[] {"description","subtitle"},
                channel.getChildText("description",ns));
        
        Iterator<?> items = null;
        if (root.getName().equals("rss")) {                    
            items = channel.getChildren("item",ns).iterator();  
        } else {
            items = root.getChildren("item",ns).iterator();  
        }
        List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
        put(feedMap, new String[] {"entries", "items"}, itemList);
        SimpleDateFormat rfc822_format =
                new SimpleDateFormat( "EEE, dd MMM yyyy hh:mm:ss z" );  
        while (items.hasNext()) {                               
            Element item = (Element) items.next();
            Map<String, Object> itemMap = new HashMap<String, Object>();
            itemList.add(itemMap);
            
            Element link = item.getChild("link", ns);   
            Element guid = item.getChild("guid", ns);
            put(itemMap, new String[] {"id", "guid"}, guid);
            if (link != null) {
                put(itemMap, "link", link.getText());
            } else if (guid != null                     
                    && "true".equals(guid.getAttributeValue("isPermaLink"))) {
                put(itemMap, new String[] {"link","guid"}, guid.getText());
            }
            put(itemMap,"title", item.getChildText("title", ns));
            put(itemMap,"content", item.getChildText("encoded", contentNS)); 
            put(itemMap,new String[] {"description","summary"},
                    item.getChildText("description", ns));
            
            if (item.getChild("pubDate", ns) != null) {           
                put(itemMap, new String[] {"pubDate","updated"},
                        rfc822_format.parse(item.getChildText("pubDate", ns)));
            } else if (item.getChild("date", dcNS) != null) {      
                put(itemMap, new String[] {"pubDate","updated"},
                        ISO8601DateParser.parse(item.getChildText("date", dcNS)));
            }
            Element enc = item.getChild("enclosure", ns);   
            if (enc != null) {
                Map<String, Object> encMap = new HashMap<String, Object>();
                encMap.put("url",    enc.getAttributeValue("url"));
                encMap.put("length", enc.getAttributeValue("length"));
                encMap.put("type",   enc.getAttributeValue("type"));
                itemMap.put("enclosure", encMap);
            }
        }
        return feedMap; 
    }
    
    void put(Map<String, Object> map, String key, Object value) {  
        if (value != null) map.put(key, value);
    }
    
    void put(Map<String, Object> map, String[] keys, Object value) {   
        if (value != null) for (int i=0; i<keys.length; i++) {
            map.put(keys[i], value);
        }
    }
    
    String parseAtomLink(URL baseURI, Element elem, Namespace ns) {   
        String link = null;
        Iterator<?> links = elem.getChildren("link", ns).iterator();
        while (links.hasNext()) {
            Element linkElem = (Element) links.next();
            String rel = linkElem.getAttributeValue("rel");
            if (rel == null || rel.equals("alternate")) {
                link = resolveURI(baseURI, linkElem, linkElem.getAttributeValue("href"));
                break;
            }
        }
        return link;
    }
    
    String parseAtomContent(String name, Element elem, Namespace ns) {
        String value = null;
        Element contentElem = elem.getChild(name, ns);
        if (contentElem != null) {
            String type = contentElem.getAttributeValue("type");
            if ("text".equals(type) || "html".equals(type)) {
                value = contentElem.getText();
            } else if ("xhtml".equals(type)) {
                XMLOutputter outputter = new XMLOutputter();
                value = outputter.outputString(contentElem.getChildren());
            } else {
                value = elem.getChildText("title", ns);
            }
        }
        return value;
    }
    
    private String resolveURI(URL baseURI, Parent parent, String url) {
        url = (url.equals(".") || url.equals("./")) ? "" : url;
        if (isRelativeURI(url) && parent != null && parent instanceof Element) {
            Attribute baseAtt = ((Element)parent).getAttribute("base", Namespace.XML_NAMESPACE);
            String xmlBase = (baseAtt == null) ? "" : baseAtt.getValue();
            if (!isRelativeURI(xmlBase) && !xmlBase.endsWith("/")) {
                xmlBase = xmlBase.substring(0, xmlBase.lastIndexOf("/")+1);
            }
            return resolveURI(baseURI, parent.getParent(), xmlBase + url);
        } else if (isRelativeURI(url) && parent == null) {
            return baseURI + url;
        } else if (baseURI != null && url.startsWith("/")) {
            String hostURI = baseURI.getProtocol() + "://" + baseURI.getHost();
            if (baseURI.getPort() != baseURI.getDefaultPort()) {
                hostURI = hostURI + ":" + baseURI.getPort();
            }
            return hostURI + url;
        }
        return url;
    }
    
    private boolean isRelativeURI(String uri) {
        if (  uri.startsWith("http://")
        || uri.startsWith("https://")
        || uri.startsWith("/")) {
            return false;
        }
        return true;
    }
    
    private URL findBaseURI(Element root) {
        URL baseURI = null;
        List<?> linksList = root.getChildren("link", ns);
        if (linksList != null) {
            for (Iterator<?> links = linksList.iterator(); links.hasNext(); ) {
                Element link = (Element)links.next();
                if (!root.equals(link.getParent())) break;
                String href = link.getAttribute("href").getValue();
                if (   link.getAttribute("rel", ns) == null
                        || link.getAttribute("rel", ns).getValue().equals("alternate")) {
                    href = resolveURI(null, link, href);
                    try {
                        baseURI = new URL(href);
                        break;
                    } catch (MalformedURLException e) {
                        System.err.println("Base URI is malformed: " + href);
                    }
                }
            }
        }
        return baseURI;
    }
    
}
