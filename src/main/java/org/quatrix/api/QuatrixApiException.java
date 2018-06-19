package org.quatrix.api;

import io.swagger.client.ApiException;

import java.util.List;
import java.util.Map;

//@Getter
public class QuatrixApiException extends RuntimeException {

    private int code = 0;
    private Map<String, List<String>> responseHeaders = null;
    private String responseBody = null;

    public QuatrixApiException(ApiException ex) {
        super(ex);
        this.code = ex.getCode();
        this.responseHeaders = ex.getResponseHeaders();
        this.responseBody = ex.getResponseBody();
    }

    public QuatrixApiException(Throwable cause) {
        super(cause);
    }

    public QuatrixApiException(String message) {
        super(message);
    }

    public int getCode() {
        return code;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
