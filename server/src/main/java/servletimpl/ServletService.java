package servletimpl;

import servletinterfaces.HttpServlet;
import servletinterfaces.HttpServletRequest;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class ServletService {
    private static HttpServlet createServlet(ServletInfo info) throws Exception {
        Class<?> clazz = Class.forName(info.servletClassName);
        return (HttpServlet)clazz.newInstance();
    }

    private static Map<String, String> stringToMap(String str) {
        Map<String, String> parameterMap = new HashMap<>();
        if (str != null) {
            String[] paramArray = str.split("&");
            for (String param : paramArray) {
                String[] keyValue = param.split("=");
                parameterMap.put(keyValue[0], keyValue[1]);
            }
        }
        return parameterMap;
    }

    private static String readToSize(InputStream input, int size) throws Exception{
        int ch;
        StringBuilder sb = new StringBuilder();
        int readSize = 0;

        while (readSize < size && (ch = input.read()) != -1) {
            sb.append((char)ch);
            readSize++;
        }
        return sb.toString();
    }

    public static void doService(String method, String query, ServletInfo info,
                                 Map<String, String> requestHeader,
                                 InputStream input, OutputStream output) throws Exception {
        if (info.servlet == null) {
            info.servlet = createServlet(info);
        }
        HttpServletRequest req;
        if (method.equals("GET")) {
            Map<String, String> map;
            map = stringToMap(query);
            req = new HttpServletRequestImpl("GET", map);
        } else if (method.equals("POST")) {
            int contentLength = Integer.parseInt(requestHeader.get("CONTENT-LENGTH"));
            Map<String, String> map;
            String line = readToSize(input, contentLength);
            map = stringToMap(line);
            req = new HttpServletRequestImpl("POST", map);
        } else {
            throw new AssertionError("BAD METHOD:" + method);
        }
        HttpServletResponseImpl resp = new HttpServletResponseImpl(output);

        info.servlet.service(req, resp);

        resp.printWriter.flush();
    }
}
