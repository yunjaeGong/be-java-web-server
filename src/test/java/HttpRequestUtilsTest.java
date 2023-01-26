import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utility.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static utility.HttpRequestUtils.HTTP_VERSION;
import static utility.HttpRequestUtils.PATH;

public class HttpRequestUtilsTest {

    @Test
    @DisplayName("queryString이 주어졌을 때 queryString 파싱 결과 expect map과 동일한 결과")
    public void Given_ValidQueryString_When_parseQueryString_Then_AsExpected() {
        // given
        String query = "userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net";

        Map<String, String> expect = new HashMap<>();

        expect.put("userId", "javajigi");
        expect.put("password", "password");
        expect.put("name", "박재성");
        expect.put("email", "javajigi@slipp.net");

        // when
        Map<String, String> queryParams = HttpRequestUtils.parseQueryString(query);

        // then
        assertNotNull(queryParams);

        for(Map.Entry<String, String> expected : expect.entrySet()) {
            String key = expected.getKey();
            String value = expected.getValue();

            assertEquals(value, queryParams.get(key));
        }
    }

    @Test
    @DisplayName("빈 queryString이 주어졌을 때 queryString 파싱 결과는 Null")
    public void Given_EmptyQuery_When_parseQueryString_Then_Null() {
        // given
        String query = "";

        // when
        Map<String, String> queryParams = HttpRequestUtils.parseQueryString(query);

        // then
        assertNull(queryParams);
    }

    @Test
    @DisplayName("queryString 포함한 RequestLine이 주어졌을 때 requestLine 및 queryString 파싱")
    public void Given_RequestLineWithQueryString_When_ParseRequestLine_Then_AsExpected() {
        String requestLine = "GET /user/create?userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net HTTP/1.1";

        Map<String, String> expectQuery = new HashMap<>();

        expectQuery.put("userId", "javajigi");
        expectQuery.put("password", "password");
        expectQuery.put("name", "박재성");
        expectQuery.put("email", "javajigi@slipp.net");

        // when
        Map<String, String> request = HttpRequestUtils.parseRequestLine(requestLine);

        // then
        assertNotNull(request);

        // QueryString Parsing 테스트
        for(Map.Entry<String, String> expected : expectQuery.entrySet()) {
            String key = expected.getKey();
            String value = expected.getValue();

            assertEquals(value, expectQuery.get(key));
        }

        // Path Test
        assertEquals("/user/create", request.get(PATH));

        // Http Version Test
        assertEquals("HTTP/1.1", request.get(HTTP_VERSION));

    }
}
