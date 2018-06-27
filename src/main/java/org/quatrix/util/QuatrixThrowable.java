package org.quatrix.util;

import io.swagger.client.ApiException;
import org.quatrix.api.QuatrixApiException;

public class QuatrixThrowable {

    private QuatrixThrowable() {}

    public static <R> R throwNow(ApiException ex) {
        throw new QuatrixApiException(ex);
    }

    public static <R> R throwNow(Throwable ex) {
        throw new QuatrixApiException(ex);
    }
}
