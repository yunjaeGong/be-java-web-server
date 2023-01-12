package utility;

import java.util.Map;

public class HttpRequest {
    private final String methodType;
    private final String httpVersion;

    private final String url;
    private Map<String, String> parameters;
    private Map<String, String> requestBody;

    public HttpRequest(String methodType, String url, String httpVersion) {
        this.methodType = methodType;
        this.url = url;
        this.httpVersion = httpVersion;
    }
}
