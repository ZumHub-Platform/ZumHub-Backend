package com.application.client.response;

import com.application.client.ClientManager;
import com.application.client.data.ClientToken;
import com.google.common.base.Charsets;
import com.google.common.base.Supplier;
import com.server.mapping.Mapping;
import com.server.request.Request;
import com.server.request.RequestType;
import com.server.response.Content;
import com.server.response.ContentType;
import com.server.response.Response;
import com.server.response.StringResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.Base64;
import java.util.concurrent.CompletableFuture;

public class ClientAuthorizationMapping extends Mapping<String> {

    @Override
    public Response<String> handle(Request request) {
        if (request.getRequestType().equals(RequestType.HEAD)) {
            String credentials = new String(Base64.getDecoder().decode(request.getHeaders().get("Authorization").split("Basic ")[1]), Charsets.UTF_8);
            String mail = credentials.split(":")[0];
            String password = credentials.split(":")[1];

            StringResponse response = new StringResponse();
            response.setStatus(HttpResponseStatus.REQUEST_TIMEOUT);

            response.setAsyncContent(CompletableFuture.supplyAsync(((Supplier<Content<String>>) () -> {
                if (!ClientManager.getInstance().getClientCredentialsAuthority().isClient(mail)) {
                    response.setStatus(HttpResponseStatus.UNAUTHORIZED);

                    return new Content<>("", ContentType.APPLICATION_JSON);
                }

                try {
                    response.setStatus(HttpResponseStatus.OK);

                    ClientToken token = ClientManager.getInstance().getClientCredentialsAuthority().authorizeClientCredentials(mail, password);
                    Content<String> content = new Content<>(null, ContentType.APPLICATION_JSON);
                    content.setHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
                    content.setHeader(HttpHeaderNames.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString((token.getToken()).getBytes(Charsets.UTF_8)));

                    return content;
                } catch (RuntimeException e) {
                    response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);

                    return new Content<>(e.getMessage(), ContentType.APPLICATION_JSON);
                }
            })));

            return response;
        }

        return Response.EMPTY_RESPONSE;
    }
}
