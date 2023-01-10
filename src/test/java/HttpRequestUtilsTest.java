import org.junit.jupiter.api.Test;
import utility.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HttpRequestUtilsTest {

    @Test
    public void Given_ValidQueryString_When_parseQueryString_Then_AsExpected() {
        // given
        String query = "/user/create?userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net";

        Map<String, String> expect = new HashMap<>();

        expect.put("userId", "javajigi");
        expect.put("password", "password");
        expect.put("name", "%EB%B0%95%EC%9E%AC%EC%84%B1");
        expect.put("email", "javajigi%40slipp.net");

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
    public void Given_EmptyQuery_When_parseQueryString_Then_Null() {
        // given
        String query = "/user/create";

        // when
        Map<String, String> queryParams = HttpRequestUtils.parseQueryString(query);

        // then
        assertNull(queryParams);
    }

    @Test
    public void Given_NotValidQuery_When_parseQueryString_Then_Null() {
        // given
        String query = "/user/create?";

        // when
        Map<String, String> queryParams = HttpRequestUtils.parseQueryString(query);

        // then
        assertNull(queryParams);
    }
}
