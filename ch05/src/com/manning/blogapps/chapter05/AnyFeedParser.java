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
    
    public static void main(String[] args) throws Exception {   //|#1
        Map parsedFeed = new AnyFeedParser().parseFeed(args[0]);
        FeedPrinter.displayFeed(parsedFeed, new PrintWriter(System.out));
    }
    
    public Map parseFeed(String feedFileName) throws Exception {  //|#2
        return (parseFeed(new FileReader(feedFileName)));
    }
    
    public Map parseFeed(Reader reader) throws Exception {   //|#3
        SAXBuilder builder = new SAXBuilder();
        Document feedDoc = builder.build(reader);
        Element root = feedDoc.getRootElement();
        
        if (root.getName().equals("feed")) return parseAtom(root);  //|#4
        return parseRSS(root);                                   //|#5
    }
    
    public Map parseAtom(Element root) throws Exception {   //|#6
        URL baseURI = findBaseURI(root);  //|#7
        
        Map feedMap = new HashMap();
        put(feedMap,"title", parseAtomContent("title", root, ns));   //|#8
        put(feedMap,"link", parseAtomLink(baseURI, root, ns));       //|#9
        put(feedMap, new String[] {"subtitle","description"},  //|#10
                parseAtomContent("subtitle", root, ns));
        
        List itemList = new ArrayList();
        put(feedMap, new String[] {"entries","items"}, itemList);  //|#11
        
        Iterator items = root.getChildren("entry",ns).iterator();  //|#12
        while (items.hasNext()) {
            Element item = (Element) items.next();
            Map itemMap = new HashMap();
            itemList.add(itemMap);
            put(itemMap, new String[] {"id","guid"},               //|#13
                    item.getChildText("id", ns));
            put(itemMap, "title", parseAtomContent("title", item, ns));
            put(itemMap, new String[] {"summary", "description"},    //|#14
                    parseAtomContent("summary", item, ns));
            put(itemMap, "link",    parseAtomLink(baseURI, item, ns));  //|#15
            put(itemMap, "content", parseAtomContent("content", item, ns));
            String dt = item.getChildText("updated", ns);
            if (dt != null) {
                put(itemMap, new String[] {"updated","pubDate"},     //|#16
                        ISO8601DateParser.parse(dt));
            }
        }
        return feedMap;   //|#17
    }
    
    Map parseRSS(Element root) throws Exception {       //|#18
        Namespace contentNS = Namespace.getNamespace(               //|#19
                "content","http://purl.org/rss/1.0/modules/content/");
        Namespace dcNS = Namespace.getNamespace(                    //|#20
                "dc","http://purl.org/dc/elements/1.1/");
        Namespace ns = null;
        if (root.getName().equals("rss")) {  //|#21
            ns = Namespace.NO_NAMESPACE;
        } else {
            ns = Namespace.getNamespace("http://purl.org/rss/1.0/");
        }
        Element channel = root.getChild("channel",ns);  //|#22
        Map feedMap = new HashMap();
        put(feedMap, "title", channel.getChildText("title",ns));
        put(feedMap, "link", channel.getChildText("link",ns));
        put(feedMap, new String[] {"description","subtitle"},
                channel.getChildText("description",ns));
        
        Iterator items = null;
        if (root.getName().equals("rss")) {                    //|#23
            items = channel.getChildren("item",ns).iterator();  //|#24
        } else {
            items = root.getChildren("item",ns).iterator();   //|#25
        }
        List itemList = new ArrayList();
        put(feedMap, new String[] {"entries", "items"}, itemList);
        SimpleDateFormat rfc822_format =
                new SimpleDateFormat( "EEE, dd MMM yyyy hh:mm:ss z" );  //|#26
        while (items.hasNext()) {                               //|#27
            Element item = (Element) items.next();
            Map itemMap = new HashMap();
            itemList.add(itemMap);
            
            Element link = item.getChild("link", ns);   //|#28
            Element guid = item.getChild("guid", ns);
            put(itemMap, new String[] {"id", "guid"}, guid);
            if (link != null) {
                put(itemMap, "link", link.getText());
            } else if (guid != null                     //|#29
                    && "true".equals(guid.getAttributeValue("isPermaLink"))) {
                put(itemMap, new String[] {"link","guid"}, guid.getText());
            }
            put(itemMap,"title", item.getChildText("title", ns));
            put(itemMap,"content", item.getChildText("encoded", contentNS));  //|#30
            put(itemMap,new String[] {"description","summary"},
                    item.getChildText("description", ns));
            
            if (item.getChild("pubDate", ns) != null) {           //|#31
                put(itemMap, new String[] {"pubDate","updated"},
                        rfc822_format.parse(item.getChildText("pubDate", ns)));
            } else if (item.getChild("date", dcNS) != null) {       //|#32
                put(itemMap, new String[] {"pubDate","updated"},
                        ISO8601DateParser.parse(item.getChildText("date", dcNS)));
            }
            Element enc = item.getChild("enclosure", ns);   //|#33
            if (enc != null) {
                Map encMap = new HashMap();
                encMap.put("url",    enc.getAttributeValue("url"));
                encMap.put("length", enc.getAttributeValue("length"));
                encMap.put("type",   enc.getAttributeValue("type"));
                itemMap.put("enclosure", encMap);
            }
        }
        return feedMap; //|#34
    }
    
    void put(Map map, String key, Object value) {   //|#35
        if (value != null) map.put(key, value);
    }
    
    void put(Map map, String[] keys, Object value) {    //|#36
        if (value != null) for (int i=0; i<keys.length; i++) {
            map.put(keys[i], value);
        }
    }
    
    String parseAtomLink(URL baseURI, Element elem, Namespace ns) {   //|#37
        String link = null;
        Iterator links = elem.getChildren("link", ns).iterator();
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
    
    String parseAtomContent(String name, Element elem, Namespace ns) { //|#38
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
        List linksList = root.getChildren("link", ns);
        if (linksList != null) {
            for (Iterator links = linksList.iterator(); links.hasNext(); ) {
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
