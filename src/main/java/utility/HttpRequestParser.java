package utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {
    /*
      1. connection.inputStream으로 주어진 http request header를 parsing
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

    private String parseRequestUri(InputStream requestHeader) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(requestHeader));

        String[] header = br.readLine().split(" ");
        String queryPath = header[1];

        this.methodType = header[0];
        this.httpVersion = header[2];
        String[] queries = queryPath.split("\\?");
        this.path = queries[0];

        if(queries.length > 1)
            parseQueryStringParams(queries[1]);

        return path;
    }

    private void parseQueryStringParams(String queryString) {
        Map<String, String> parsedQueryString = HttpRequestUtils.parseQueryString(queryString);
        if(parsedQueryString != null)
            this.params.putAll(parsedQueryString);
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public HttpRequestParser(InputStream requestHeader) throws IOException {
        this.params = new HashMap<>();
        this.resourcePath = new StringBuilder(DEFAULT_PATH);
        this.path = parseRequestUri(requestHeader);

        this.route.put("html", TEMPLATES_PATH);
        this.route.put("favicon", TEMPLATES_PATH);
    }
}
