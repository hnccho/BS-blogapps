package com.manning.blogapps.chapter05;

import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Parent;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/** Parse-and-print example that parses Atom */
public class ParseAtom {
    Namespace ns = Namespace.getNamespace("http://www.w3.org/2005/Atom");
    
    public static void main(String[] args) throws Exception {
        new ParseAtom(args);
    }
    
    public ParseAtom(String[] args) throws Exception {
    	
        FileInputStream inputStream = new FileInputStream(args[0]);
        
        SAXBuilder builder = new SAXBuilder();
        Document feedDoc = builder.build(inputStream);
        Element root = feedDoc.getRootElement();
        
        URL baseURI = findBaseURI(root);
        
        String feedBase = root.getAttributeValue("base", Namespace.XML_NAMESPACE);
        Iterator entries = root.getChildren("entry", ns).iterator();
        while (entries.hasNext()) {
            
            Element entry = (Element)entries.next();
            String entryBase = entry.getAttributeValue("base", Namespace.XML_NAMESPACE);
            System.out.println("Title: " + entry.getChildText("title", ns));
            
            Element link = entry.getChild("link", ns);
            if (link != null) {
                String href = link.getAttributeValue("href");
                System.out.println("Link: " + resolveURI(baseURI, link, href));
            }
            
            Date date = ISO8601DateParser.parse(entry.getChildText("updated", ns));
            System.out.println("Date: " + date.toString());
            
            Element desc = entry.getChild("content", ns);
            if (desc == null) desc = entry.getChild("summary", ns);
            if (desc == null) break;
            String type = desc.getAttributeValue("type");
            if ("text".equals(type) || "html".equals(type)) {
                System.out.println("Description: " + desc.getText());
            } else if ("xhtml".equals(type)) {
                XMLOutputter outputter = new XMLOutputter();
                System.out.println("Description: " + outputter.outputString(desc.getChildren()));
            }
        }
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
