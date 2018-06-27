package org.quatrix.api;

import io.swagger.client.ApiException;

import java.util.List;
import java.util.Map;

public class QuatrixApiException extends RuntimeException {

    private final int code;
    private final Map<String, List<String>> responseHeaders;
    private final String responseBody;

    public QuatrixApiException(ApiException ex) {
        super(ex);
        this.code = ex.getCode();
        this.responseHeaders = ex.getResponseHeaders();
        this.responseBody = ex.getResponseBody();
    }

    public QuatrixApiException(Throwable cause) {
        super(cause);
        this.code = 0;
        this.responseHeaders = null;
        this.responseBody = null;
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
