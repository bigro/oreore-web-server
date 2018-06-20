package util;

import java.io.*;

public class SendResponse {

    public static void sendOkResponseHeader(PrintWriter writer,
                                            String contentType)
            throws IOException {
        writer.println("HTTP/1.1 200 OK");
        writer.println("Date: " + Util.getDateStringUtc());
        writer.println("Server: Henacat");
        writer.println("Connection: close");
        writer.println("Content-type: " + contentType);
        writer.println("");
    }

    public static void sendOkResponse(OutputStream output, InputStream fis,
                               String ext) throws Exception {
        Util.writeLine(output, "HTTP/1.1 200 OK");
        Util.writeLine(output, "Date: " + Util.getDateStringUtc());
        Util.writeLine(output, "Server: Server04.java");
        Util.writeLine(output, "Connection: close");
        Util.writeLine(output, "Content-type: "
                + Util.getContentType(ext));
        Util.writeLine(output, "");

        int ch;
        while ((ch = fis.read()) != -1) {
            output.write(ch);
        }
    }

    public static void sendNotFoundResponse(OutputStream output,
                                     String errorDocumentRoot)
            throws Exception {

        Util.writeLine(output, "HTTP/1.1 404 Not Found");
        Util.writeLine(output, "Date: " + Util.getDateStringUtc());
        Util.writeLine(output, "Server: Server04.java");
        Util.writeLine(output, "Connection: close");
        Util.writeLine(output, "Content-type: text/html");
        Util.writeLine(output, "");

        try (FileInputStream fis
                     = new FileInputStream(errorDocumentRoot + "/404.html");) {
            int ch;
            while ((ch = fis.read()) != -1) {
                output.write(ch);
            }
        }
    }

    public static void sendMovePermanentlyResponse(OutputStream output, String location) throws IOException {
        Util.writeLine(output, "HTTP/1.1 301 Moved Permanently");
        Util.writeLine(output, "Date: " + Util.getDateStringUtc());
        Util.writeLine(output, "Server: Server05.java");
        Util.writeLine(output, "Location: " + location);
        Util.writeLine(output, "Connection: close");
        Util.writeLine(output, "");
    }
}
