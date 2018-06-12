package org.quatrix.api;

import org.mule.api.MuleException;
import org.mule.config.i18n.Message;

public class QuatrixApiException extends MuleException {

    public QuatrixApiException(Message message) {
        super(message);
    }

    public QuatrixApiException(Throwable cause) {
        super(cause);
    }
}
