package utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static utility.HttpRequestUtils.*;

public class HttpRequestParser {
    /*
      1. connection.inputStream으로 주어진 http requestLine parsing
      2. 요청 uri에 QueryString 포함 여부 체크
      3. uri 자원의 파일 타입에 따라 html -> templates / 이외 js, css, pont -> /static으로 라우팅
     */
    private static final String DEFAULT_PATH = "/Users/rentalhub-mac88/Desktop/Softeer/be-java-web-server/src/main/resources";
    private static final String TEMPLATES_PATH = "/templates";
    private static final String STATIC_PATH = "/static";

    private final StringBuilder resourcePath;

    private final Map<String, String> route = new HashMap<>();

    private String methodType;
    public String path;
    public String httpVersion;
    private Map<String, String> params;

    private String parseRequestLine(InputStream requestHeader) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(requestHeader));
        String requestLine = br.readLine();

        return parseRequestLine(requestLine);
    }

    private String parseRequestLine(String requestHeader) {
        Map<String, String> parsedHeader = HttpRequestUtils.parseRequestLine(requestHeader);

        this.path = parsedHeader.get(PATH);
        this.methodType = parsedHeader.get(METHOD_TYPE);
        this.httpVersion = parsedHeader.get(HTTP_VERSION);
        String queryString = parsedHeader.get(QUERY_STRING);

        if(queryString != null && !queryString.isEmpty())
            parseQueryStringParams(queryString);

        return path;
    }

    private void parseQueryStringParams(String queryString) {
        Map<String, String> parsedQueryString = HttpRequestUtils.parseQueryString(queryString);
        if(parsedQueryString != null)
            this.params.putAll(parsedQueryString);
    }

    public Map<String, String> getParams() {
        return new HashMap<>(this.params);
    }

    public boolean hasParams() {
        return this.params.size() > 0;
    }

    public HttpRequestParser(InputStream requestHeader) throws IOException {
        this.params = new HashMap<>();
        this.resourcePath = new StringBuilder(DEFAULT_PATH);
        this.path = this.parseRequestLine(requestHeader);

        this.route.put("html", TEMPLATES_PATH);
        this.route.put("favicon", TEMPLATES_PATH);
    }

    public HttpRequestParser(String requestHeader) {
        this.params = new HashMap<>();
        this.resourcePath = new StringBuilder(DEFAULT_PATH);
        this.path = this.parseRequestLine(requestHeader);

        this.route.put("html", TEMPLATES_PATH);
        this.route.put("favicon", TEMPLATES_PATH);
    }
}
