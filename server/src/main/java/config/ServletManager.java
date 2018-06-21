package config;

import servletinterfaces.HttpServlet;

import java.util.HashMap;
import java.util.Map;

public class ServletManager {
    
    private static HashMap<String, HttpServlet> urlMapping = new HashMap<>();
    
    // ここでServletのインスタンスを作ってしまうのは微妙かもしれない。
    // 最初にとりあえず作成するより使うときに作成する方がコストが多少抑えられるような。
    public static void init() throws Exception {
        for (Map.Entry<String, String> entry : ServletConfig.urlMapping.entrySet()) {
            urlMapping.put(entry.getKey(), createServlet(entry.getValue()));
        }
    }
    
    public static boolean exists(String url) {
        return urlMapping.containsKey(url);
    }
    
    public static HttpServlet getServlet(String url) {
        return urlMapping.get(url);
    }

    private static HttpServlet createServlet(String servletClassName) throws Exception {
        Class<?> clazz = Class.forName(servletClassName);
        return (HttpServlet) clazz.newInstance();
    }
}
