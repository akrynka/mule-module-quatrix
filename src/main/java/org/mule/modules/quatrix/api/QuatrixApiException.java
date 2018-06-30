package org.mule.modules.quatrix.api;

import io.swagger.client.ApiException;

public class QuatrixApiException extends Exception {

    private int code = 0;
    private String responseBody = null;

    public QuatrixApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public QuatrixApiException(String message, int code) {
        this(message, null);
        this.code = code;
    }

    public QuatrixApiException(ApiException ex) {
        this(ex.getMessage(), ex);
        this.code = ex.getCode();
        this.responseBody = ex.getResponseBody();
    }

    public QuatrixApiException(Exception cause, int code) {
        this(cause.getMessage(), cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
