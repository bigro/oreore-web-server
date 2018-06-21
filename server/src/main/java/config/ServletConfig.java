package config;

import java.util.HashMap;

public class ServletConfig {
    static HashMap<String, String> urlMapping = new HashMap<>();
    static {
        urlMapping.put("/bbs/TestBBS","bbs.TestBBS");
    }
}
