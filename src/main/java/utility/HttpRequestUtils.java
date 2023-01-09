package utility;

import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpRequestUtils {
    /*
    QueryStringParam 파싱 지원하는 클래스
     */

    public static Map<String, String> parseQueryString(String queryString)  {
        String[] queries = queryString.split("\\?");
        String path = queries[0];

        if(queries.length < 2)
            return null;

        String[] params = queries[1].split("&");

        if(params.length == 0)
            return null;

        return Stream.of(params).map((param) -> param.split("=")).collect(Collectors.toMap(a->a[0], a->a[1]));
    }
}
