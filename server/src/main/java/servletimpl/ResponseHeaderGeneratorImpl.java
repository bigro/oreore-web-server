package servletimpl;

import servletinterfaces.Cookie;
import servletinterfaces.ResponseHeaderGenerator;
import util.Util;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ResponseHeaderGeneratorImpl implements ResponseHeaderGenerator {
    private List<Cookie> cookies;

    private static String getCookieDateString(Calendar cal) {
        DateFormat df = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss",
                Locale.US);
        df.setTimeZone(cal.getTimeZone());
        Date now = cal.getTime();
        return df.format(cal.getTime()) + " GMT";
    }

    /**
     * 
     * @param output
     * @throws IOException
     * 
     * Cookieに関するレスポンスヘッダーを出力する。
     */
    @Override
    public void generate(OutputStream output) throws IOException {
        for (Cookie cookie : cookies) {
            String header;
            header = "Set-Cookie: "
                    + cookie.getName() + "=" + cookie.getValue();

            if (cookie.getDomain() != null) {
                header += "; Domain=" + cookie.getDomain();
            }
            if (cookie.getMaxAge() > 0) {
                Calendar cal
                        = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                cal.add(Calendar.SECOND, cookie.getMaxAge());
                header += "; Expires=" + getCookieDateString(cal);
            } else if (cookie.getMaxAge() == 0) {
                Calendar cal
                        = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                cal.set(1970, 0, 1, 0, 0, 10);
                header += "; Expires=" + getCookieDateString(cal);
            }
            if (cookie.getPath() != null) {
                header += "; Path=" + cookie.getPath();
            }
            if (cookie.getSecure()) {
                header += "; Secure";
            }
            if (cookie.isHttpOnly()) {
                header += "; HttpOnly";
            }
            Util.writeLine(output, header);
        }
    }

    ResponseHeaderGeneratorImpl(List<Cookie> cookies) {
        this.cookies = cookies;
    }
}
