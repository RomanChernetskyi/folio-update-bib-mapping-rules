package org.folio.client;

import org.folio.model.Configuration;
import org.folio.util.HttpWorker;

import java.net.http.HttpResponse;

public class AuthClient {
    private static final String BODY_FORMAT = "{\"username\": \"%s\",\"password\": \"%s\"}";
    private final Configuration configuration;
    private final HttpWorker httpWorker;

    public AuthClient(Configuration configuration, HttpWorker httpWorker) {
        this.configuration = configuration;
        this.httpWorker = httpWorker;
    }

    public String authorize() {
        String body = String.format(BODY_FORMAT, configuration.getUsername(), configuration.getPassword());

        var request = httpWorker.constructAuthRequest(body);
        var response = httpWorker.sendRequest(request);

        httpWorker.verifyStatus(response, 201, "Failed to authorize user");

        return getCookie(response);
    }

    private String getCookie(HttpResponse<String> response) {
        var result = new StringBuilder();
        for (String token : response.headers().allValues("set-cookie")) {
            var tokenPart = token.split(";")[0];
            result.append(tokenPart).append("; ");
        }
        if (!result.isEmpty()) {
            result.setLength(result.length() - 2);
        }
        return result.toString();
    }
}
