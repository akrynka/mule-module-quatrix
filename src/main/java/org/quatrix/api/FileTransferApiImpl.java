package org.quatrix.api;

import com.squareup.okhttp.*;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.Pair;
import io.swagger.client.api.FileApi;
import io.swagger.client.api.UploadApi;
import io.swagger.client.model.IdsReq;
import io.swagger.client.model.UploadLinkReq;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public final class FileTransferApiImpl implements FileTransferApi {

    private final ApiClient client;
    private final UploadApi uploadApi;
    private final FileApi fileApi;

    public FileTransferApiImpl(ApiClient client) {
        this.client = client;
        this.uploadApi = new UploadApi(client);
        this.fileApi = new FileApi(client);
    }

    @Override
    public void uploadFile(UUID dirId, File file, String name, boolean resolveConflict) throws QuatrixApiException {
        final UUID uploadKey = retrieveUploadKey(dirId, file, name, resolveConflict);

        uploadFile(file, uploadKey);
        finalizeUpload(uploadKey);
    }

    @Override
    public File downloadFile(List<UUID> fileIds) throws QuatrixApiException {
        return download(retrieveDownloadLink(fileIds));
    }

    private void finalizeUpload(UUID uploadKey) {
        try {
            uploadApi.uploadFinalizeIdGet(uploadKey, null);
        } catch (ApiException e) {
            throw new QuatrixApiException(e);
        }
    }

    private void uploadFile(File file, UUID uploadKey) throws QuatrixApiException {
        final String url = String.format("%s/upload/chunked/%s", client.getBasePath(), uploadKey);

        try {
            Response response = client.getHttpClient().newCall(
                    new Request.Builder()
                            .url(url)
                            .header("Authorization", getBasicAuthHeaderValue())
                            .header("Transfer-Encoding", "chunked")
                            .header("Content-Range", "") //TODO: check if required
                            .post(RequestBody.create(MediaType.parse(file.getName()), file))
                            .build()

            ).execute();

            if (!response.isSuccessful()) {
                throw new ApiException("Unable to upload file", response.code(), response.headers().toMultimap(), response.body().string());
            }

        } catch (IOException | ApiException e) {
            throw new QuatrixApiException(e);
        }
    }

    private UUID retrieveUploadKey(UUID dirId, File file, String name, boolean resolveConflict) throws QuatrixApiException {
        final UploadLinkReq req = new UploadLinkReq()
                .parentId(dirId)
                .name(name)
                .fileSize(BigDecimal.valueOf(file.length()))
                .resolve(resolveConflict);

        try {
            return uploadApi.uploadLinkPost(req).getUploadKey();
        } catch (ApiException e) {
            throw new QuatrixApiException(e);
        }
    }

    private UUID retrieveDownloadLink(List<UUID> fileIds) {
        try {
            return fileApi.fileDownloadLinkPost(new IdsReq().ids(fileIds)).getId();
        } catch (ApiException e) {
            throw new QuatrixApiException(e);
        }
    }

    private File download(UUID downloadLink) {
        final String url = String.format("%s/file/download/%s", client.getBasePath(), downloadLink);
        final OkHttpClient httpClient = client.getHttpClient();

        try {
            Response response = httpClient.newCall(
                    new Request.Builder()
                            .url(url)
                            .header("Authorization", getBasicAuthHeaderValue())
                            .header("Accept", "application/octet-stream")
                            .get()
                            .build()
            ).execute();
            if (!response.isSuccessful()) {
                throw new ApiException("Unable to download file", response.code(), response.headers().toMultimap(), response.body().string());
            }

            return client.downloadFileFromResponse(response);
        } catch (IOException | ApiException e) {
            throw new QuatrixApiException(e);
        }
    }

    private String getBasicAuthHeaderValue() {
        Map<String, String> headers = new HashMap<>();
        client.getAuthentication("basicAuth").applyToParams(new ArrayList<Pair>(), headers);

        return headers.get("Authorization");
    }
}
