package utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

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

        this.path = queryPath.split("\\?")[0];


        if(path.equals("/"))  // TODO: redirect하기
            path = "/index.html";

        parseQueryStringParams(path);

        if(path.contains("html") || path.contains("favicon")) {
            resourcePath.append(TEMPLATES_PATH);
        } else {
            resourcePath.append(STATIC_PATH);
        }

        resourcePath.append(path);

        return resourcePath.toString();
    }

    private void parseQueryStringParams(String queryString) {
        this.params = HttpRequestUtils.parseQueryString(queryString);
    }

    public Map<String, String> getParams() {
        return new HashMap<>(this.params);
    }

    public HttpRequestParser(InputStream requestHeader) throws IOException {
        this.resourcePath = new StringBuilder(DEFAULT_PATH);
        this.path = parseRequestUri(requestHeader);
    }
}
