package controller;

import db.Database;
import dto.Session;
import dto.SessionCookie;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.SessionService;
import service.UserService;
import dto.HttpRequest;
import dto.HttpResponse;
import utility.HttpRequestUtils;
import utility.HttpStatusCode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class RequestController {
    /*
    경로에 따라 정적 리소스, 동적 리소스 구분
    정적 리소스는 단순 서빙
    동적 리소스의 경우 Redirect, 비즈니스 로직 처리 등 수행
    */
    public static final String DEFAULT_PATH = "./src/main/resources";
    public static final String TEMPLATES_PATH = "/templates";
    public static final String STATIC_PATH = "/static";
    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);

    private static final int DYNAMIC = 1;
    private static final int STATIC = 2;

    private static final List<String> staticResource = new ArrayList<>(Arrays.asList("js", "html", "css", "font", "woff"));


    public static HttpResponse requestController(InputStream in) throws IOException {
        HttpRequest request = new HttpRequest(in);
        String path = request.getPath();

        logger.debug("Request Path: " + path);

        String[] args = path.split("\\.");

        if(args.length >= STATIC)  // .min.js, .js, .html , .ico
            return staticResourceController(request);
        else
            return dynamicResourceController(request);

    }

    public static HttpResponse staticResourceController(HttpRequest request) throws IOException {
        StringBuilder resourcePath = new StringBuilder(DEFAULT_PATH);
        String path = request.getPath();
        String contentType = Files.probeContentType(Path.of(path));

        if(path.contains("html") || path.contains(".ico")) {
            resourcePath.append(TEMPLATES_PATH);

            // index.html
            // session cookie 유무 검사
            if(request.getRequestHeader().containsKey("Cookie")) {
                String sid = request.getRequestHeader().get("Cookie");
                sid = Stream.of(sid.split(" ")).map(String::trim).filter((str) -> str.contains("sid=")).findFirst().orElse("");
                if(sid.charAt(sid.length()-1) == ';')
                    sid = sid.substring(0, sid.length()-1);

                if(!sid.isBlank()) {
                    sid = sid.split("=")[1];
                    logger.debug(sid);
                    User user = null;
                    resourcePath.append(path);

                    try {
                        Session s = SessionService.findSessionBySID(sid);
                        user = UserService.findUserById(s.getUserId());

                    } catch (IllegalArgumentException e) {
                        logger.error(e.getMessage());
                    }

                    String curLine = "";
                    BufferedReader br = new BufferedReader(new FileReader(resourcePath.toString()));
                    StringBuilder sb = new StringBuilder();

                    while ((curLine = br.readLine()) != null) {
                        if(curLine.contains("로그인"))
                            sb.append(String.format("<li><a role=\"button\">%s</a></li>", user.getName()));
                        else
                            sb.append(curLine);
                    }

                    return new HttpResponse.HttpResponseBuilder()
                            .setResourcePath("")
                            .setStatusCode(HttpStatusCode.OK)
                            .setContentType(contentType)
                            .setGeneratedPage(sb).build();
                }
            }

        } else {
            resourcePath.append(STATIC_PATH);
        }
        resourcePath.append(path);

        logger.debug("contentType: " +contentType);

        return new HttpResponse(resourcePath.toString(), HttpStatusCode.OK, contentType);
    }

    public static HttpResponse dynamicResourceController(HttpRequest request) throws IOException {
        Map<String, String> header = new HashMap<>();
        HttpStatusCode status = HttpStatusCode.NOT_FOUND;
        String contentType = "text/html";
        String path = request.getPath();

        if(path.equals("/")) {
            path = "/index.html";
            status = HttpStatusCode.OK;
        }

        if(path.equals("/user/create") && (request.hasParams() || !request.getBody().isEmpty())) {
            Map<String, String> params = null;

            if(!request.getBody().isBlank())
                params = HttpRequestUtils.parseQueryString(request.getBody());

            if(request.hasParams())
                params = request.getQueryString();

            Objects.requireNonNull(params);

            User user = User.UserBuilder.builder()
                    .setUserId(params.get("userId"))
                    .setPassword(params.get("password"))
                    .setName(params.get("name"))
                    .setEmail(params.get("email")).build();

            path = "";
            status = HttpStatusCode.FOUND;
            header.put("Location", "/index.html");

            try {
                UserService.signUpUser(user);
            } catch (IllegalStateException e) {
                logger.error("/user/create - " + e.getMessage());
                header.put("Location", "/user/form.html");
            }

            logger.debug("created User: {}", Database.findUserById(params.get("userId")));
        }

        if(path.equals("/user/login")) {
            boolean loginSuccess = false;
            Map<String, String> params = null;

            if(!request.getBody().isBlank())
                params = HttpRequestUtils.parseQueryString(request.getBody());

            if(request.hasParams())
                params = request.getQueryString();

            Objects.requireNonNull(params);

            path = "";
            status = HttpStatusCode.FOUND;

            try {
                loginSuccess = UserService.loginUser(params.get("userId"), params.get("password"));
                header.put("Location", "/index.html");

            } catch (IllegalStateException | IllegalArgumentException e) {
                logger.error("/user/login - " + e.getMessage());
                header.put("Location", "/user/login_failed.html");
            }

            if(loginSuccess) {
                // Session을 Cookie로 저장
                SessionCookie session = SessionService.createSession(params.get("userId"), Map.of());
                header.put("Set-Cookie", session.toString());
                logger.debug("/user/login - " + session);
            }

            logger.debug("Login User: {}" + params.get("userId"));
        }

        if(!path.isBlank())
            contentType = Files.probeContentType(Path.of(path));
        logger.debug("contentType: " +contentType);

        return new HttpResponse(path, status, contentType, header);
    }
}
