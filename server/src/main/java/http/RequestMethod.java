package http;

public enum RequestMethod {
    GET, POST;

    public boolean isPOST() {
        return this == RequestMethod.POST;
    }

    public boolean isGET() {
        return this == RequestMethod.GET;
    }
}
