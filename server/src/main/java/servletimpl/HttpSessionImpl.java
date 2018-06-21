package servletimpl;

import servletinterfaces.HttpSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSessionImpl implements HttpSession {
    private String id;
    private Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();
    private volatile long lastAccessedTime;
    
    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        Set<String> names = new HashSet<String>();
        names.addAll(attributes.keySet());

        return Collections.enumeration(names);
    }

    @Override
    public void removeAttribute(String name) {
        this.attributes.remove(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        if (value == null){
            removeAttribute(name);
            return;
        }
        this.attributes.put(name, value);
    }

    synchronized void access() {
        this.lastAccessedTime = System.currentTimeMillis();
    }

    long getLastAccessedTime() {
        return this.lastAccessedTime;
    }

    public HttpSessionImpl(String id) {
        this.id = id;
        this.access();
    }
}
