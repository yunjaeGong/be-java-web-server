import org.junit.jupiter.api.Test;
import utility.HttpRequestParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpRequestParserTest {

    private static final String DEFAULT_PATH = "/Users/rentalhub-mac88/Desktop/Softeer/be-java-web-server/src/main/resources";

    @Test
    public void Given_EmptyUrl_When_parseRequestUri_Then_IndexHtml() throws IOException {
        // given
        String request = "GET / HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "Accept: */*";

        // when
        HttpRequestParser parser = new HttpRequestParser(new ByteArrayInputStream(request.getBytes()));

        // then
        assertNotNull(parser);
        assertEquals(DEFAULT_PATH + "/templates/index.html", parser.path);
    }
}
