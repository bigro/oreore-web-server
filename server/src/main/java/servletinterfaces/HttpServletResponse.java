package servletinterfaces;

import java.io.IOException;
import java.io.PrintWriter;

public interface HttpServletResponse {
    int SC_OK = 200;
    int SC_FOUND = 302;
    
    void setContentType(String contentType);

    void setCharacterEncoding(String charset);

    PrintWriter getWriter() throws IOException;

    void addCookie(Cookie cookie);

    void sendRedirect(String location);

    void setStatus(int sc);
}
