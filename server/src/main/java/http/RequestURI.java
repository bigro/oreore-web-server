package http;

public class RequestURI {
    private String path;
    private String query;

    public RequestURI(String value) {
        // リクエストURLにクエリが含まれている場合はリソースパスとクエリが"?"で区切られてるので別々に取得する。
        String[] pathAndQuery = value.split("\\?");
        this.path = pathAndQuery[0];
        if (pathAndQuery.length > 1) {
            this.query = pathAndQuery[1];
        }

    }

    public String getPath() {
        // リクエストパスが"/"で終わっていた場合はindex.htmlを表示する。
        if (path.endsWith("/")) {
            return path + "index.html";
        }
        return path;
    }

    public String getQuery() {
        return query;
    }
    
    public String getExtension() {
        String[] tmp = getPath().split("\\.");
        return tmp[tmp.length - 1];
    }
}
