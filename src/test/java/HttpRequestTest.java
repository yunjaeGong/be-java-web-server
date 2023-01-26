import db.UserDatabase;
import dto.HttpResponse;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.UserService;
import dto.HttpRequest;
import utility.HttpRequestUtils;
import utility.HttpStatusCode;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.*;

public class HttpRequestTest {

    private static final String DEFAULT_PATH = "./src/main/resources";

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestTest.class);

    @BeforeEach
    private void cleanUpDatabase() {
        UserDatabase.clear();
    }

    @Test
    public void Given_RootUrl_When_parseRequestUrl_Then_RootUrl() throws IOException {
        // given
        String request = "GET / HTTP/1.1 \r\n" +
                "Host: localhost:8080 \r\n" +
                "Connection: keep-alive \r\n" +
                "Accept: */* \r\n";

        // when
        HttpRequest parsedObject = new HttpRequest(new ByteArrayInputStream(request.getBytes()));

        // then
        assertNotNull(parsedObject);
        assertEquals("/", parsedObject.getPath());
    }

    @Test
    public void Given_IndexHtml_When_parseRequestUrl_Then_IndexHtml() throws IOException {
        // given
        String request = "GET /index.html HTTP/1.1 \r\n" +
                "Host: localhost:8080 \r\n" +
                "Connection: keep-alive \r\n" +
                "Accept: */* \r\n";

        // when
        HttpRequest parsedObject = new HttpRequest(new ByteArrayInputStream(request.getBytes()));

        // then
        assertNotNull(parsedObject);
        assertEquals("/index.html", parsedObject.getPath());
    }

    // POST
    @Test
    public void Given_PostSignUp_When_SignUp_Then_SignUpUser() throws IOException {
        // given
        String request = "POST /user/create HTTP/1.1 \r\n" +
                "Host: localhost:8080 \r\n" +
                "Connection: keep-alive \r\n" +
                "Content-Length: 74 \r\n" +
                "Content-Type: application/x-www-form-urlencoded \r\n" +
                "Accept: */* \r\n" +
                "\r\n" +
                "userId=javajigi&password=password&name=javajigi&email=javajigi%40slipp.net";

        // when
        HttpRequest parsedRequest = new HttpRequest(new ByteArrayInputStream(request.getBytes()));
        String body = parsedRequest.getBody();

        Map<String, String> userQuery = HttpRequestUtils.parseQueryString(body);

        User user = User.UserBuilder.builder()
                .setUserId(userQuery.get("userId"))
                .setPassword(userQuery.get("password"))
                .setName(userQuery.get("name"))
                .setEmail(userQuery.get("email")).build();

        try {
            UserService.signUpUser(user);
        } catch (IllegalStateException e) {
            logger.error("/user/create - " + e.getMessage());
            entry("Location", "/user/form.html");
        }

        List<String> expected = new ArrayList<>();
        expected.add("userId=javajigi&password=password&name=javajigi&email=javajigi%40slipp.net");

        List<String> result = new ArrayList<>();
        result.add(parsedRequest.getBody());
        // then
        assertLinesMatch(result,expected);

        assertTrue(UserDatabase.findUserById("javajigi").isPresent());
    }

    @Test
    public void Given_PostSignUp_When_SignUp_Then_ResponseWithRedirection() throws IOException {
        // given
        String request = "POST /user/create HTTP/1.1 \r\n" +
                "Host: localhost:8080 \r\n" +
                "Connection: keep-alive \r\n" +
                "Content-Length: 74 \r\n" +
                "Content-Type: application/x-www-form-urlencoded \r\n" +
                "Accept: */* \r\n" +
                "\r\n" +
                "userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net";

        // when
        HttpRequest parsedRequest = new HttpRequest(new ByteArrayInputStream(request.getBytes()));
        String body = parsedRequest.getBody();

        Map<String, String> userQuery = HttpRequestUtils.parseQueryString(body);

        User user = User.UserBuilder.builder()
                .setUserId(userQuery.get("userId"))
                .setPassword(userQuery.get("password"))
                .setName(userQuery.get("name"))
                .setEmail(userQuery.get("email")).build();

        HttpResponse response = new HttpResponse("",
                HttpStatusCode.FOUND,
                "text/html", Map.ofEntries(entry("Location", "/index.html"))
        );

        try {
            UserService.signUpUser(user);
        } catch (IllegalStateException e) {
            logger.error("/user/create - " + e.getMessage());
            entry("Location", "/user/form.html");
        }

        DataOutputStream outputStream = response.of(new DataOutputStream(System.out));

        outputStream.flush();
    }
}
