package com.manning.blogapps.chapter08.filedepot;
import java.util.Collection;
import java.util.Date;

public interface Depot {
    public abstract Collection<?> getFiles();
    public abstract Date getLastUpdateDate();
    public abstract void update(); 
}