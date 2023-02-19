import org.junit.jupiter.api.Test;
import utility.HttpRequestParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpRequestParserTest {

    private static final String DEFAULT_PATH = "/Users/rentalhub-mac88/Desktop/Softeer/be-java-web-server/src/main/resources";

    @Test
    public void Given_RootUrl_When_parseRequestUrl_Then_RootUrl() throws IOException {
        // given
        String request = "GET / HTTP/1.1 \r\n" +
                "Host: localhost:8080 \r\n" +
                "Connection: keep-alive \r\n" +
                "Accept: */* \r\n";

        // when
        HttpRequestParser parser = new HttpRequestParser(new ByteArrayInputStream(request.getBytes()));

        // then
        assertNotNull(parser);
        assertEquals("/", parser.path);
    }

    @Test
    public void Given_IndexHtml_When_parseRequestUrl_Then_IndexHtml() throws IOException {
        // given
        String request = "GET /index.html HTTP/1.1 \r\n" +
                "Host: localhost:8080 \r\n" +
                "Connection: keep-alive \r\n" +
                "Accept: */* \r\n";

        // when
        HttpRequestParser parser = new HttpRequestParser(new ByteArrayInputStream(request.getBytes()));

        // then
        assertNotNull(parser);
        assertEquals("/index.html", parser.path);
    }
}
