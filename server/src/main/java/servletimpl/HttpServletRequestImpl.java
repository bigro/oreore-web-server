package servletimpl;

import servletinterfaces.Cookie;
import servletinterfaces.HttpServletRequest;
import servletinterfaces.HttpSession;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Map;

public class HttpServletRequestImpl implements HttpServletRequest {
    private String method;
    private String characterEncoding;
    private Map<String, String> parameterMap;
    private Map<String, String> requestHeader;
    private Cookie[] cookies;
    private HttpSessionImpl session;
    private HttpServletResponseImpl response;
    private final String SESSION_COOKIE_ID = "JSESSIONID";


    @Override
    public String getMethod() {
        return this.method;
    }

    @Override
    public String getParameter(String name) {
        String value = this.parameterMap.get(name);
        String decoded = null;
        try {
            decoded = URLDecoder.decode(value, this.characterEncoding);
        } catch (UnsupportedEncodingException ex) {
            throw new AssertionError(ex);
        }
        return decoded;
    }

    @Override
    public void setCharacterEncoding(String env)
            throws UnsupportedEncodingException {
        if (!Charset.isSupported(env)) {
            throw new UnsupportedEncodingException("encoding.." + env);
        }
        this.characterEncoding = env;
    }

    @Override
    public Cookie[] getCookies() {
        return this.cookies;
    }

    private static Cookie[] parseCookies(String cookieString) {
        if (cookieString == null) {
            return null;
        }
        String[] cookiePairArray = cookieString.split(";");
        Cookie[] ret = new Cookie[cookiePairArray.length];
        int cookieCount = 0;

        for (String cookiePair : cookiePairArray) {
            String[] pair = cookiePair.split("=", 2);

            ret[cookieCount] = new Cookie(pair[0], pair[1]);
            cookieCount++;
        }

        return ret;
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

    @Override
    public HttpSession getSession(boolean create) {
        if (!create) {
            return this.session;
        }
        if (this.session == null) {
            SessionManager manager = SessionManager.getInstance();
            this.session = manager.createSession();
            addSessionCookie();
        }
        return this.session;
    }

    private HttpSessionImpl getSessionInternal() {
        if (this.cookies == null) {
            return null;
        }
        Cookie cookie = null;
        for (Cookie tempCookie : this.cookies) {
            if (tempCookie.getName().equals(SESSION_COOKIE_ID)) {
                cookie = tempCookie;
            }
        }
        SessionManager manager = SessionManager.getInstance();
        HttpSessionImpl ret = null;
        if (cookie != null) {
            ret = manager.getSession(cookie.getValue());
        }
        return ret;
    }

    private void addSessionCookie() {
        this.response.addCookie(new Cookie(SESSION_COOKIE_ID, this.session.getId() + "; HttpOnly"));
    }

    HttpServletRequestImpl(String method, Map<String, String> requestHeader,
                           Map<String, String> parameterMap,
                           HttpServletResponseImpl resp) {
        this.method = method;
        this.requestHeader = requestHeader;
        this.cookies = parseCookies(requestHeader.get("COOKIE"));
        this.parameterMap = parameterMap;
        this.response = resp;
        this.session = getSessionInternal();
        if (this.session != null) {
            addSessionCookie();
        }
    }

    HttpServletRequestImpl(String method, Map<String, String> parameterMap) {
        this.method = method;
        this.parameterMap = parameterMap;
    }
}
