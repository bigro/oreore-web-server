package webserver;

import servletimpl.ServletInfo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        ServletInfo.addServlet("/bbs/TestBBS",
                "/Users/ooguro/Documents/git/oreore-web-server/server/out/production/classes/bbs",
                "bbs.TestBBS");

        try (ServerSocket server = new ServerSocket(8001)) {
            for (; ; ) {
                Socket socket = server.accept();
                ServerThread serverThread = new ServerThread(socket);
                Thread thread = new Thread(serverThread);
                thread.start();
            }
        }
    }
}
