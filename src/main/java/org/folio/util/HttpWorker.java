package org.folio.util;

import lombok.Setter;
import lombok.SneakyThrows;
import org.folio.model.Configuration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.String.format;
import static org.folio.FolioUpdateBibMappingRulesApplication.exitWithError;


@Setter
public class HttpWorker {
    private final Configuration configuration;
    private String cookie;

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String AUTH_PATH = "/authn/login-with-expiry";
    private static final String APPLICATION_JSON = "application/json";

    public HttpWorker(Configuration configuration) {
        this.configuration = configuration;
    }

    public HttpRequest constructGETRequest(String uri) {
        return constructRequest(uri).GET().build();
    }

    public HttpRequest constructDELETERequest(String uri) {
        return constructRequest(uri).DELETE().build();
    }

    @SneakyThrows
    public HttpRequest constructPOSTRequest(String uri, String body) {
        return constructRequest(uri)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    @SneakyThrows
    public HttpRequest constructAuthRequest(String body) {
        var loginTenantId = configuration.getCentralTenant() == null ?
                configuration.getTenant() : configuration.getCentralTenant();

        var builder = HttpRequest.newBuilder()
                .uri(URI.create(configuration.getOkapiUrl() + AUTH_PATH))
                .header("x-okapi-tenant", loginTenantId);

        if (cookie != null) {
            builder.header("Cookie", cookie);
        }
        return builder
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    @SneakyThrows
    public HttpRequest constructPOSTRequest(String uri, Path filePath) {
        return constructRequest(uri)
                .header(CONTENT_TYPE, "application/octet-stream")
                .POST(HttpRequest.BodyPublishers.ofByteArray(Files.readAllBytes(filePath)))
                .build();
    }

    @SneakyThrows
    public HttpRequest constructPUTRequest(String uri, String body) {
        return constructRequest(uri)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    public HttpRequest.Builder constructRequest(String uri) {
        var builder = HttpRequest.newBuilder()
                .uri(URI.create(configuration.getOkapiUrl() + uri))
                .header("x-okapi-tenant", configuration.getTenant());

        if (cookie != null) {
            builder.header("Cookie", cookie);
        }
        return builder;
    }

    @SneakyThrows
    public HttpResponse<String> sendRequest(HttpRequest request) {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    public void verifyStatus(HttpResponse<?> response, int expectedStatus, String errorMessage) {
        if (response.statusCode() != expectedStatus) {
            exitWithError(format("%s, Status code: %s Response: %s", errorMessage, response.statusCode(), response.body()));
        }
    }
}
