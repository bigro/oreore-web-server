package http;

import servletimpl.HttpServletRequestImpl;
import servletimpl.HttpServletResponseImpl;
import servletinterfaces.HttpServletRequest;
import util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTPのリクエストを表現したクラス
 */
public class Request {
    private RequestMethod method;
    private String requestLine;
    private RequestHeader requestHeader;

    public Request(InputStream input) throws IOException {
        this.requestHeader = new RequestHeader();

        String line;
        while ((line = Util.readLine(input)) != null) {
            if (line.isEmpty()) break;
            // GETリクエストの場合は GET /bbs/TestBBS HTTP/1.1 の様な形で来るので"GET"始まりかで判断する。
            if (line.startsWith(RequestMethod.GET.name())) {
                method = RequestMethod.GET;
                requestLine = line; // "GET /bbs/TestBBS HTTP/1.1" みたいな文字列が入る
            } else if (line.startsWith(RequestMethod.POST.name())) {
                method = RequestMethod.POST;
                requestLine = line;
            } else {
                requestHeader.add(line);
            }
        }
    }

    public boolean isIllegality() {
        return requestLine == null;
    }
    
    public RequestURI getUri() throws UnsupportedEncodingException {
        // リクエストURLは"GET /bbs/TestBBS HTTP/1.1"の様に半角スペース区切りの2つ目に記載されているので、
        // 取得してUTF-8でデコードする。
        String uri = URLDecoder.decode(requestLine.split(" ")[1], "UTF-8");
        return new RequestURI(uri);
    }

    public HttpServletRequest toHttpServletRequest(HttpServletResponseImpl response, InputStream input) throws Exception {
        if (method.isGET()) {
            return new HttpServletRequestImpl("GET", requestHeader.fields, getParameters(getUri().getQuery()), response);
        }
        
        if (method.isPOST()) {
            String query = readToSize(input);
            return new HttpServletRequestImpl("POST", requestHeader.fields, getParameters(query), response);
        }
        
        throw new AssertionError("BAD METHOD:" + method);
    }

    public String getHeaderValue(String key) {
        return requestHeader.get(key);
    }

    private String readToSize(InputStream input) throws Exception {
        // POSTの場合はリクエストヘッダーの後に改行を挟んだ後のリクエストボディにクエリが記載されている為、リクエストボディからクエリを取得する。
        int size = Integer.parseInt(requestHeader.get("CONTENT-LENGTH"));
        int ch;
        StringBuilder sb = new StringBuilder();
        int readSize = 0;

        while (readSize < size && (ch = input.read()) != -1) {
            sb.append((char) ch);
            readSize++;
        }
        return sb.toString();
    }

    private Map<String, String> getParameters(String query) {
        // リクエストクエリをMapに変換する。
        Map<String, String> parameters = new HashMap<>();
        if (query != null) {
            // 複数のクエリは"&"で区切られている。
            String[] paramArray = query.split("&");
            // 名前と値は"="で区切られている。
            for (String param : paramArray) {
                String[] keyValue = param.split("=");
                parameters.put(keyValue[0], keyValue[1]);
            }
        }
        return parameters;
    }
}
