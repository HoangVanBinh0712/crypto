package test.crypto.trade.config;

public class ApiException extends RuntimeException {
    private final String errorCode;

    public ApiException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApiException(String message) {
        super(message);
        this.errorCode = "ERROR";
    }

    public String getErrorCode() {
        return errorCode;
    }
}