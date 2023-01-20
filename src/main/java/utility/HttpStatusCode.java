package utility;

public enum HttpStatusCode {
    OK(200, "OK"),
    FOUND(302, "FOUND"),
    NOT_FOUND(404, "NOT FOUND"),
    INTERNAL_ERROR(500, "Internal Server Error");

    private final int responseCode;
    private final String responseMessage;

    HttpStatusCode(int responseCode, String message) {
        this.responseCode = responseCode;
        this.responseMessage = message;
    }

    @Override
    public String toString() {
        return this.responseCode + " " + this.responseMessage;
    }
}
