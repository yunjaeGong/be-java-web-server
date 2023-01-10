import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.HttpRequestParser;
import utility.HttpRequestUtils;
import webserver.RequestController;
import webserver.RequestHandler;

import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static webserver.RequestController.DEFAULT_PATH;
import static webserver.RequestController.TEMPLATES_PATH;

public class RequestControllerTest {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    @Test
    public void Given_StaticResourceString_When_RequestController_Then_PathToResource() {
        String resource = "GET /index.html HTTP/1.1";
        String result = null;

        try {
            result = RequestController.requestController(resource);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        assertNotNull(result);
        assertEquals(DEFAULT_PATH + TEMPLATES_PATH + "/index.html", result);
    }

    @Test
    public void Given_DynamicResourceString_When_RequestController_Then_PathToResource() {
        String resource = "GET /user/create?userId=asdf&email=asdf@naver.com&password=1234&name=asdf HTTP/1.1 \r\n";
        String result = null;

        try {
            result = RequestController.requestController(resource);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        assertNotNull(result);
        assertEquals("dynamic", result);
    }
}
