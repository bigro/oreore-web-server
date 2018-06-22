package servletimpl;

import config.ServletManager;
import http.Request;
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
     * 
     * @param request HTTPリクエストを表現したクラス
     * @param input
     * @param output
     * @throws Exception
     */
    public static void doService(Request request, InputStream input, OutputStream output) throws Exception {

        ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
        HttpServletResponseImpl response = new HttpServletResponseImpl(outputBuffer);
        
        HttpServletRequest httpServletRequest = request.toHttpServletRequest(response, input);

        // Servletのサービスを実行する。（doGet()やdoPost()）
        ServletManager.getServlet(request.getUri().getPath()).service(httpServletRequest, response);

        // Servletのサービス側でリダイレクトを設定しているとstatusがSC_FOUNDになっているので、リダイレクトしてない場合がこっち。
        if (response.status == HttpServletResponse.SC_OK) {

            // レスポンスヘッダーを出力する。
            ResponseHeaderGenerator hg = new ResponseHeaderGeneratorImpl(response.cookies);
            SendResponse.sendOkResponseHeader(output, response.contentType, hg);

            // Servlet実行結果のViewを出力する。
            response.printWriter.flush();
            byte[] outputBytes = outputBuffer.toByteArray();
            for (byte b : outputBytes) {
                output.write((int) b);
            }
            // リダイレクトの場合    
        } else if (response.status == HttpServletResponse.SC_FOUND) {
            String redirectLocation;
            // "/"始まりの場合はサーバ名を付与する。
            if (response.redirectLocation.startsWith("/")) {
                String host = request.getHeaderValue("HOST");
                redirectLocation = "http://"
                        + ((host != null) ? host : Constants.SERVER_NAME)
                        + response.redirectLocation;
                // それ以外はそのままのパスにリダイレクト    
            } else {
                redirectLocation = response.redirectLocation;
            }
            SendResponse.sendFoundResponse(output, redirectLocation);
        }

    }
}
