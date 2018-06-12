package org.quatrix.api;

import io.swagger.client.api.AuthApi;
import io.swagger.client.model.FileMetadataGetResp;
import io.swagger.client.model.SessionLoginResp;

import java.math.BigDecimal;

//TODO: extend with other API methods
public interface QuatrixApi {

    AuthApi auth();

    SessionLoginResp login() throws QuatrixApiException;

    FileMetadataGetResp getHomeDirMeta(BigDecimal content) throws QuatrixApiException;

    void close() throws QuatrixApiException;
}
