package servletinterfaces;

import javax.servlet.ServletException;

public abstract class HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, java.io.IOException {
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, java.io.IOException {
    }

    /**
     * 
     * @param req
     * @param resp
     * @throws ServletException
     * @throws java.io.IOException
     * 
     * リクエストを元にメソッドを呼び出す。
     */
    public void service(HttpServletRequest req,
                        HttpServletResponse resp)
            throws ServletException, java.io.IOException {
        if (req.getMethod().equals("GET")) {
            doGet(req, resp);
        } else if (req.getMethod().equals("POST")) {
            doPost(req, resp);
        }
    }
}
