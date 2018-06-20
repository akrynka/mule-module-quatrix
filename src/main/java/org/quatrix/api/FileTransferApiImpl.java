package org.quatrix.api;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.Pair;
import io.swagger.client.api.FileApi;
import io.swagger.client.api.UploadApi;
import io.swagger.client.model.IdsReq;
import io.swagger.client.model.UploadFinalizeResp;
import io.swagger.client.model.UploadLinkReq;
import org.quatrix.model.UploadResult;
import org.quatrix.util.QuatrixThrowable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

final class FileTransferApiImpl implements FileTransferApi {

    private static final String X_AUTH_TOKEN_HEADER = "X-Auth-Token";
    // upload buffer size 4KB
    private static final int MAX_UPLOAD_BUFFER_LEN = 4194304;

    private final ApiClient client;
    private final UploadApi uploadApi;
    private final FileApi fileApi;

    FileTransferApiImpl(ApiClient client) {
        this.client = client;
        this.uploadApi = new UploadApi(client);
        this.fileApi = new FileApi(client);
    }

    /**
     * Upload run in 3 steps: get upload link, upload file, finalize upload.
     * @param dirId Directory id where to upload file
     * @param file File for upload
     * @param name File name on remote filesystem
     * @param resolveConflict if true server should automatically resolve possible name conflict
     * @return {@link UploadResult}
     */
    @Override
    public UploadResult uploadFile(UUID dirId, File file, String name, boolean resolveConflict) {
        final UUID uploadKey = retrieveUploadKey(dirId, file, name, resolveConflict);

        try {
            uploadFile(file, uploadKey);
        } catch (Exception e) {
            QuatrixThrowable.throwNow(e);
        }

        return UploadResult.from(finalizeUpload(uploadKey));
    }

    @Override
    public File downloadFile(List<UUID> fileIds) throws QuatrixApiException {
        return download(retrieveDownloadLink(fileIds));
    }

    private UploadFinalizeResp finalizeUpload(UUID uploadKey) {
        try {
            return uploadApi.uploadFinalizeIdGet(uploadKey, null);
        } catch (ApiException e) {
            throw new QuatrixApiException(e);
        }
    }

    private void uploadFile(File file, UUID uploadKey) throws Exception {
        final String url = String.format("%s/upload/chunked/%s",
                client.getBasePath().replace("/api/1.0", ""), uploadKey
        );
        final long contentLength = file.length();
        final MediaType mediaType = Optional.fromNullable(Files.probeContentType(file.toPath()))
                .transform(new Function<String, MediaType>() {
                    @Override
                    public MediaType apply(String s) {
                        return MediaType.parse(s);
                    }
                }).orNull();

        long readBytes = 0L;
        long totalReadBytes = 0L;

        try (InputStream fileStream = new FileInputStream(file)) {
            while (readBytes >= 0) {
                int buffSize = Math.min(MAX_UPLOAD_BUFFER_LEN, (int) (contentLength - totalReadBytes));
                byte[] buffer = new byte[Math.max(buffSize, 1)];
                readBytes = fileStream.read(buffer);
                if (readBytes > 0) {
                    final String contentRange
                            = String.format("bytes %d-%d/%d", totalReadBytes, (totalReadBytes + readBytes) - 1, contentLength);

                    try {
                        Response response = client.getHttpClient().newCall(
                                new Request.Builder()
                                        .url(url)
                                        .header(X_AUTH_TOKEN_HEADER, getAuthToken())
                                        .header("Content-Range", contentRange)
                                        .post(RequestBody.create(mediaType, buffer))
                                        .build()

                        ).execute();

                        if (!response.isSuccessful()) {
                            throw new ApiException("Unable to upload file", response.code(), response.headers().toMultimap(), response.body().string());
                        }

                    } catch (IOException | ApiException e) {
                        throw new QuatrixApiException(e);
                    }

                    totalReadBytes += readBytes;
                }
            }
        }
    }

    private UUID retrieveUploadKey(UUID dirId, File file, String name, boolean resolveConflict) {
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
                            .header(X_AUTH_TOKEN_HEADER, getAuthToken())
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

    private String getAuthToken() {
        Map<String, String> headers = new HashMap<>();
        client.getAuthentication("api_key").applyToParams(new ArrayList<Pair>(), headers);

        return headers.get(X_AUTH_TOKEN_HEADER);
    }
}
