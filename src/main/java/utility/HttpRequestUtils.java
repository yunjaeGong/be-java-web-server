package utility;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpRequestUtils {
    public static final String METHOD_TYPE = "methodType";
    public static final String HTTP_VERSION = "httpVersion";
    public static final String PATH = "path";
    public static final String QUERY_STRING = "queryString";

    public static Map<String, String> parseQueryString(String queryString)  {
        String[] params = queryString.split("&");

        if(params.length == 0)
            return null;

        return Stream.of(params).map((param) -> param.split("=")).collect(Collectors.toMap(a->a[0], a->a[1]));
    }

    public static Map<String, String> parseRequestLine(String requestHeader) {
        Map<String, String> requestResources = new HashMap<>();
        String[] header = requestHeader.split(" ");
        String queryPath = header[1];

        requestResources.put(METHOD_TYPE, header[0]);
        requestResources.put(HTTP_VERSION, header[2]);

        String[] queries = queryPath.split("\\?");
        requestResources.put(PATH, queries[0]);
        if(queries.length == 2)
            requestResources.put(QUERY_STRING, queries[1]);

        return requestResources;
    }
}
