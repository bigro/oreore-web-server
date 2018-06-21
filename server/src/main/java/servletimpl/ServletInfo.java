package servletimpl;

import servletinterfaces.HttpServlet;

import java.util.HashMap;

public class ServletInfo {
    private static HashMap<String, ServletInfo> servletCollection = new HashMap<>();

    private String urlPattern;
    String servletClassName;
    HttpServlet servlet;

    public ServletInfo(String urlPattern, String servletClassName) {
        this.urlPattern = urlPattern;
        this.servletClassName = servletClassName;
    }

    public static void addServlet(String urlPattern, String servletClassName) {
        ServletInfo servletInfo = new ServletInfo(urlPattern, servletClassName);
        servletCollection.put(urlPattern, servletInfo);
    }

    /**
     * 
     * @param urlPattern
     * @return 引数で渡されたURLにマッピングされたServletInfo
     */
    public static ServletInfo searchServlet(String urlPattern) {
        return servletCollection.get(urlPattern);
    }
}
