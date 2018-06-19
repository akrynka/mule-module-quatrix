package org.quatrix.model;

import io.swagger.client.model.SessionLoginResp;

import java.util.UUID;

//@Data
//@AllArgsConstructor
public class Session {

    private final UUID id;

    public Session(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public static Session from(SessionLoginResp sessionLoginResp) {
        return new Session(sessionLoginResp.getSessionId());
    }
}
