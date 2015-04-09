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
import org.jdom.Element;
import org.jdom.Namespace;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.io.ModuleParser;

public class PubControlModuleParser implements ModuleParser {  
 
	public String getNamespaceUri() {  
        return PubControlModule.URI; 
    }
	
    public Module parse(Element elem) {                         
        Namespace ns = Namespace.getNamespace(PubControlModule.URI);
        PubControlModule module = new PubControlModuleImpl();   
        Element control = elem.getChild("control", ns);         
        if (control != null) {
            Element draft = control.getChild("draft", ns);      
            if (draft != null && "yes".equals(draft.getText()))
                module.setDraft(true);
            else if (draft != null && "no".equals(draft.getText()))
                module.setDraft(false);
            else 
                module = null; 
        }
        return module;
    }
    
}



