package com.application.client;

import com.application.client.model.ClientProfile;
import com.google.common.base.Charsets;
import com.server.mapping.annotation.Controller;
import com.server.mapping.annotation.Mapping;
import com.server.request.Request;
import com.server.request.RequestType;
import com.server.response.Response;
import org.bson.types.ObjectId;

import java.util.Base64;

@Controller
public class ClientProfileController {

    @Mapping(value = "/profile", method = RequestType.GET, parameters = {"identifier"})
    public Response<String> getProfile(Request request) {
        String identifier = request.getParameter("identifier");
        boolean authenticated = false;

        if (request.hasHeader("Authorization")) {
            String token = new String(Base64.getDecoder().decode(request.getHeaders().get("Authorization").split(
                    "Bearer ")[1]), Charsets.UTF_8);
            ObjectId authorizedId = ClientManager.getInstance().getOnlineClients().get(token);

            if (authorizedId != null) {
                ClientProfile profile =
                        ClientManager.getInstance().getClientProfileAuthority().searchClientProfile(authorizedId);
                if (ClientManager.getInstance().getOnlineClients().get(token).toHexString().equals(identifier)) {
                    authenticated = true;
                }
            }
            if (ClientManager.getInstance().getOnlineClients().containsKey(token) && ClientManager.getInstance().getOnlineClients().get(token).toHexString().equals(identifier)) {
                authenticated = true;
            }
        }

        ObjectId id = new ObjectId(identifier);
        ClientProfile profile = ClientManager.getInstance().getClientProfileAuthority().searchClientProfile(id);

        if (profile == null) {
            return Response.NOT_FOUND;
        }

        return Response.INTERNAL_SERVER_ERROR;
    }
}
