package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;
import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.LoginData;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
    private final Gson g = new Gson();
    public LoginResource() {}

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doLogin(LoginData data) {
        LOG.fine("Attempt to login user: " + data.username);

        KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");
        Key userKey = userKeyFactory.newKey(data.username);


        Transaction txn = datastore.newTransaction();
        try {
            Entity user = txn.get(userKey);
            if (user != null) {
                String hashedPWD = user.getString("user_pwd");

                if (hashedPWD.equals(DigestUtils.sha512Hex(data.password)) && user.getBoolean("user_active")) {
                    LOG.info("User '" + data.username + "' logged in sucessfully.");

                    AuthToken at = new AuthToken(data.username);
                    Key tokenKey = datastore.newKeyFactory()
                            .addAncestor(PathElement.of("User", data.username))
                            .setKind("Token").newKey(at.tokenID);
                    Entity token = Entity.newBuilder(tokenKey)
                            .set("username", at.username)
                            .set("creationDate", at.creationDate)
                            .set("expirationDate", at.expirationDate)
                            .build();
                    txn.add(token);
                    txn.commit();
                    return Response.ok(g.toJson(at)).build();
                } else {
                    LOG.warning("Wrong password for username: " + data.username);
                    return Response.status(Response.Status.FORBIDDEN).build();
                }

            } else {
                LOG.warning("Failed login attempt for user: " + data.username);
                return Response.status(Response.Status.FORBIDDEN).build();
            }
        } catch (Exception e) {
            txn.rollback();
            LOG.severe(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }
}
