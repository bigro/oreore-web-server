package servletimpl;

import servletinterfaces.HttpServlet;

import java.util.HashMap;

public class ServletInfo {
    private static HashMap<String, ServletInfo> servletCollection = new HashMap<>();

    private String urlPattern;
    String servletDirectory;
    String servletClassName;
    HttpServlet servlet;

    public ServletInfo(String urlPattern, String servletDirectory, String servletClassName) {
        this.urlPattern = urlPattern;
        this.servletDirectory = servletDirectory;
        this.servletClassName = servletClassName;
    }

    public static void addServlet(String urlPattern, String servletDirectory,
                                  String servletClassName) {
        ServletInfo servletInfo = new ServletInfo(urlPattern, servletDirectory, servletClassName);
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
