package servletinterfaces;

import java.io.IOException;
import java.io.PrintWriter;

public interface HttpServletResponse {
    void setContentType(String contentType);

    void setCharacterEncoding(String charset);

    PrintWriter getWriter() throws IOException;

    void addCookie(Cookie cookie);
}
