package webserver;

import config.ServletManager;
import http.Request;
import http.RequestURI;
import servletimpl.ServletService;
import util.Constants;
import util.SendResponse;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class ServerThread implements Runnable {

    private static final String DOCUMENT_ROOT = "server/src/main/resources/web";
    private static final String ERROR_DOCUMENT = "server/src/main/resources";

    private Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        OutputStream output = null;
        try {
            /*
                以下の様なリクエスト情報が取得される。
                GET /bbs/TestBBS HTTP/1.1
                Host: localhost:8001
                Connection: keep-alive
                Cache-Control: max-age=0
                Upgrade-Insecure-Requests: 1
                User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.79 Safari/537.36
                Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,* / *;q=0.8
                Accept-Encoding: gzip, deflate, br
                Accept-Language: ja-JP,ja;q=0.9,en-US;q=0.8,en;q=0.7
                Cookie: Idea-82e9b1cf=6fc9cf77-d596-48b4-8d35-cc124454ced6; searchVal=%7B%22fb261cf6-21d8-4ef1-9bae-1497251c498c%22%3A%22NAME%22%7D; _ga=GA1.1.985216103.1508769118; JSESSIONID=B255DB42914B5165CEF2FBD14D481CFD
             */
            InputStream input = socket.getInputStream();

            Request request = new Request(input);
            if (request.isIllegality()) {
                return;
            }
            
            RequestURI requestUri = request.getUri();
            
            String path = requestUri.getPath();

            output = new BufferedOutputStream(socket.getOutputStream());

            // Servletのサービスを実行する。(doGetとかdoPost)
            if (ServletManager.exists(path)) {
                ServletService.doService(request, input, output);
                return;
            }
            
            // ここからはServletに設定されてないパスがリクエストされた場合(htmlファイルなど)

            // リクエストファイルの拡張子を取得する。
            String ext = requestUri.getExtension();

            Path pathObj = Paths.get(DOCUMENT_ROOT + path);
            Path realPath;
            try {
                realPath = pathObj.toRealPath();
            } catch (NoSuchFileException ex) { // リクエストされたファイルが存在しない場合はエラーページを出力する
                SendResponse.sendNotFoundResponse(output, ERROR_DOCUMENT);
                return;
            }
            
            // "../"などの相対パスを指定された時（ディレクトリトラサーバル）の対策
            if (!realPath.startsWith(Paths.get(DOCUMENT_ROOT).toRealPath().toString())) {
                SendResponse.sendNotFoundResponse(output, ERROR_DOCUMENT);
                return;
            // リクエストパスがディレクトリだった場合は末尾に"/"をつけたパスにリダイレクトする。    
            } else if (Files.isDirectory(realPath)) {
                String host = request.getHeaderValue("HOST");
                String location = "http://"
                        + ((host != null) ? host : Constants.SERVER_NAME)
                        + path + "/";
                SendResponse.sendMovePermanentlyResponse(output, location);
                return;
            }

            try (InputStream inputStream = new BufferedInputStream(Files.newInputStream(realPath))) {
                SendResponse.sendOkResponse(output, inputStream, ext);
            } catch (FileNotFoundException ex) {
                SendResponse.sendNotFoundResponse(output, ERROR_DOCUMENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
