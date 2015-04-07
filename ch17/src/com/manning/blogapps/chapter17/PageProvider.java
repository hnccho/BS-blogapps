package com.manning.blogapps.chapter17;

import com.ecyrd.jspwiki.providers.FileSystemProvider;

public class PageProvider extends FileSystemProvider {
    public boolean pageExists(String pageName) {
        return true;
    }
}
