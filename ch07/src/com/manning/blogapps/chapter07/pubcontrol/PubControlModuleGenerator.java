/*
 * Copyright 2005 David M Johnson (For RSS and Atom In Action)
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
 *
 */
package com.manning.blogapps.chapter07.pubcontrol;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jdom.Element;
import org.jdom.Namespace;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.io.ModuleGenerator;

public class PubControlModuleGenerator implements ModuleGenerator {

	private static final Namespace NS = 
        Namespace.getNamespace("app", PubControlModule.URI);
    private static final Set NAMESPACES;
 
    static {                                            
        Set nss = new HashSet();
        nss.add(NS);
        NAMESPACES = Collections.unmodifiableSet(nss);
    }
  
    public Set getNamespaces() {                        
        return NAMESPACES;
    }
 
    public String getNamespaceUri() {
        return PubControlModule.URI;
    }

    public void generate(Module module, Element element) {    
        PubControlModule m = (PubControlModule)module;
        String draft = m.getDraft() ? "yes" : "no";
        Element controlElem = new Element("control", NS); 
        Element draftElem = new Element("draft", NS);     
        draftElem.addContent(draft);
        controlElem.addContent(draftElem);
        element.addContent(controlElem);                  
    }
    
}

