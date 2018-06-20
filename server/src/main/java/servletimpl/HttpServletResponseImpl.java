package servletimpl;

import servletinterfaces.HttpServletResponse;
import util.SendResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class HttpServletResponseImpl implements HttpServletResponse {
    private String contentType = "application/octet-stream";
    private String characterEncoding = "ISO-8859-1";
    private OutputStream outputStream;
    PrintWriter printWriter;

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
        String[] temp = contentType.split(" *;");
        if (temp.length > 1) {
            String[] keyValue = temp[1].split("=");
            if (keyValue.length == 2 && keyValue[0].equals("charset")) {
                setCharacterEncoding(keyValue[1]);
            }
        }
    }

    @Override
    public void setCharacterEncoding(String charset) {
        this.characterEncoding = charset;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        this.printWriter
                = new PrintWriter(new OutputStreamWriter(this.outputStream,
                this.characterEncoding));
        SendResponse.sendOkResponseHeader(this.printWriter, this.contentType);
        return this.printWriter;
    }

    HttpServletResponseImpl(OutputStream output) {
        this.outputStream = output;
    }
}
