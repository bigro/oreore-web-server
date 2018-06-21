package servletimpl;

import servletinterfaces.HttpServlet;
import servletinterfaces.HttpServletRequest;
import servletinterfaces.HttpServletResponse;
import servletinterfaces.ResponseHeaderGenerator;
import util.Constants;
import util.SendResponse;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class ServletService {

    /**
     * @param info
     * @return HttpServletインスタンス
     * @throws Exception 引数に渡されたServletInfoのservletClassNameからHttpServletインスタンスを生成する。
     */
    private static HttpServlet createServlet(ServletInfo info) throws Exception {
        Class<?> clazz = Class.forName(info.servletClassName);
        return (HttpServlet) clazz.newInstance();
    }

    /**
     * @param query リクエストクエリの文字列
     * @return クエリの名前と値のMap
     * <p>
     * リクエストクエリの文字列をMapに変換する。
     */
    private static Map<String, String> stringToMap(String query) {
        Map<String, String> parameterMap = new HashMap<>();
        if (query != null) {
            // 複数のクエリは"&"で区切られている。
            String[] paramArray = query.split("&");
            // 名前と値は"="で区切られている。
            for (String param : paramArray) {
                String[] keyValue = param.split("=");
                parameterMap.put(keyValue[0], keyValue[1]);
            }
        }
        return parameterMap;
    }

    private static String readToSize(InputStream input, int size) throws Exception {
        int ch;
        StringBuilder sb = new StringBuilder();
        int readSize = 0;

        while (readSize < size && (ch = input.read()) != -1) {
            sb.append((char) ch);
            readSize++;
        }
        return sb.toString();
    }

    /**
     * @param method        GET/POST
     * @param query         リクエストURLに含まれるクエリ
     * @param info
     * @param requestHeader "Host: localhost:8001"などのリクエストヘッダーMap
     * @param input
     * @param output
     * @throws Exception
     */
    public static void doService(String method, String query, ServletInfo info, Map<String, String> requestHeader,
                                 InputStream input, OutputStream output) throws Exception {

        // サーブレットインスタンスが生成されてない場合に生成する。
        if (info.servlet == null) {
            info.servlet = createServlet(info);
        }

        ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
        HttpServletResponseImpl resp = new HttpServletResponseImpl(outputBuffer);

        // リクエストクエリをMapに変換してHttpServletRequestを生成する。
        HttpServletRequest req;
        if (method.equals("GET")) {
            Map<String, String> parameterMap;
            parameterMap = stringToMap(query);
            req = new HttpServletRequestImpl("GET", requestHeader, parameterMap, resp);
        } else if (method.equals("POST")) {
            // POSTの場合はリクエストヘッダーの後に改行を挟んでその後にクエリが記載されている。
            int contentLength = Integer.parseInt(requestHeader.get("CONTENT-LENGTH"));
            Map<String, String> parameterMap;
            String line = readToSize(input, contentLength);
            parameterMap = stringToMap(line);
            req = new HttpServletRequestImpl("POST", requestHeader, parameterMap, resp);
        } else {
            throw new AssertionError("BAD METHOD:" + method);
        }

        // Servletのサービスを実行する。（doGet()やdoPost()）
        info.servlet.service(req, resp);

        // Servletのサービス側でリダイレクトを設定しているとstatusがSC_FOUNDになっているので、リダイレクトしてない場合がこっち。
        if (resp.status == HttpServletResponse.SC_OK) {
            
            // レスポンスヘッダーを出力する。
            ResponseHeaderGenerator hg = new ResponseHeaderGeneratorImpl(resp.cookies);
            SendResponse.sendOkResponseHeader(output, resp.contentType, hg);
            
            // Servlet実行結果のViewを出力する。
            resp.printWriter.flush();
            byte[] outputBytes = outputBuffer.toByteArray();
            for (byte b: outputBytes) {
                output.write((int)b);
            }
        // リダイレクトの場合    
        } else if (resp.status == HttpServletResponse.SC_FOUND) {
            String redirectLocation;
            // "/"始まりの場合はサーバ名を付与する。
            if (resp.redirectLocation.startsWith("/")) {
                String host = requestHeader.get("HOST");
                redirectLocation = "http://"
                        + ((host != null) ? host : Constants.SERVER_NAME)
                        + resp.redirectLocation;
            // それ以外はそのままのパスにリダイレクト    
            } else {
                redirectLocation = resp.redirectLocation;
            }
            SendResponse.sendFoundResponse(output, redirectLocation);
        }
        
    }
}
