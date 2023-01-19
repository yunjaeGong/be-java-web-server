package controller;

import dto.HttpRequest;
import dto.HttpResponse;
import dto.Session;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.SessionService;
import service.UserService;
import utility.HttpMethodType;
import utility.HttpStatusCode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ViewController {
    // 동적 html을 반환해야 하는 html의 경우
    public static final String DEFAULT_PATH = "./src/main/resources/templates";
    private static final Logger logger = LoggerFactory.getLogger(ViewController.class);

    @ControllerMapping(method= HttpMethodType.GET, path="/index.html")
    public HttpResponse index(HttpRequest request) throws IOException {
        StringBuilder generatedPage = new StringBuilder();

        String path = DEFAULT_PATH + request.getPath();
        String contentType = Files.probeContentType(Path.of(path));

        String sid = SessionService.extractSidFromCookie(request);
        logger.debug("/index.html, with SessionId: {}", sid);

        if(sid != null && !sid.isBlank()) {
            logger.debug(sid);
            User user = null;

            try {
                Session s = SessionService.findSessionBySID(sid);
                user = UserService.findUserById(s.getUserId());
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage());
            }
        }

        logger.debug("contentType: " +contentType);

        return new HttpResponse(path, HttpStatusCode.OK, contentType, Map.of(), generatedPage);
    }

    @ControllerMapping(method = HttpMethodType.GET, path = "/user/list.html")
    public HttpResponse userList(HttpRequest request) throws IOException {
        Map<String, String> header = new HashMap<>();
        HttpStatusCode status = HttpStatusCode.INTERNAL_ERROR;

        logger.debug("user - userList");


        String path = request.getPath();

        String sid = SessionService.extractSidFromCookie(request);

        List<User> users = null;
        if(sid != null && sid.isBlank()) {
            try {
                Session s = SessionService.findSessionBySID(sid);
                users = UserService.findAllUsers();

                status = HttpStatusCode.OK;
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage());
            }
        }
        // TODO: user 리스트가 들어간 페이지 생성
        // generatePageWithGivenString(DEFAULT_PATH + path, "", String.format("<li><a role=\"button\">%s</a></li>", ""));

        logger.debug("contentType: " +"text/html");

        return new HttpResponse("", status, "text/html", header);
    }

    private StringBuilder generatePageWithGivenString(String templatePath, String toReplace, String given) throws IOException {
        StringBuilder generatedPage = new StringBuilder();
        String curLine = "";
        BufferedReader br = new BufferedReader(new FileReader(templatePath));

        while ((curLine = br.readLine()) != null) {
            if(curLine.contains(toReplace)) {
                generatedPage.append(given);
                continue;
            }
            generatedPage.append(curLine);
        }
        return generatedPage;
    }
}
