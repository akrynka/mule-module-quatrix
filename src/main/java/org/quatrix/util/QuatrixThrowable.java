package org.quatrix.util;

import io.swagger.client.ApiException;
import org.quatrix.api.QuatrixApiException;

public class QuatrixThrowable {

    public static <R> R throwNow(ApiException ex) throws QuatrixApiException {
        throw new QuatrixApiException(ex);
    }

    public static <R> R throwNow(Throwable ex) throws QuatrixApiException {
        throw new QuatrixApiException(ex);
    }

}
