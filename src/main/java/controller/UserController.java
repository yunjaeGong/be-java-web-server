package controller;

import dto.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.HttpMethodType;

public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @ControllerMapping(method = HttpMethodType.POST, path = "/user/create")
    public void createUser() {
        logger.debug("user - createUser");
    }

    @ControllerMapping(method = HttpMethodType.POST, path = "/user/login")
    public void loginUser() {
        logger.debug("user - loginUser");
    }
}
