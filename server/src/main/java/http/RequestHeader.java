package http;

import java.util.HashMap;
import java.util.Map;

public class RequestHeader {
    Map<String, String> fields = new HashMap<>();

    /**
     * 
     * @param headerField
     *
     * GETでもPOSTでも始まらない以下の様なリクエスフィールドを属性名と値に分けてMAPに格納する。
     * <ul>
     *     <li>Host: localhost:8001</li>
     *     <li>Connection: keep-alive</li>
     *     <li>Cache-Control: max-age=0</li>
     * </ul>
     */
    public void add(String headerField) {
        int colonPosition = headerField.indexOf(':');

        // コロンが含まれない行はなんかおかしいので追加しない。
        if (colonPosition == -1) return;

        // コロンで名前と値が区切られてるのでsubstringする。
        String headerName = headerField.substring(0, colonPosition).toUpperCase();
        // コロンの後に半角スペース入ってるのでtrimする。
        String headerValue = headerField.substring(colonPosition + 1).trim();
        fields.put(headerName, headerValue);
    }
    
    public String get(String key) {
        return fields.get(key);
    }
}
