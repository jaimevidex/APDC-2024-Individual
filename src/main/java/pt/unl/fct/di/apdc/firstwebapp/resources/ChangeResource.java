package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import org.apache.commons.codec.digest.DigestUtils;
import pt.unl.fct.di.apdc.firstwebapp.util.ChangePasswordData;
import pt.unl.fct.di.apdc.firstwebapp.util.ChangeRoleData;
import pt.unl.fct.di.apdc.firstwebapp.util.ChangeStateData;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/change")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ChangeResource {
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
    public ChangeResource() {}

    @POST
    @Path("/role")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeRole(ChangeRoleData data) {
        LOG.fine("Attempt to change role from user: " + data.usernameDestiny);

        Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
        Key userDestinyKey = datastore.newKeyFactory().setKind("User").newKey(data.usernameDestiny);

        Entity userDestiny = datastore.get(userDestinyKey);
        if (userDestiny==null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Problem with the user he may not exist!").build();
        } else {
            Entity user = datastore.get(userKey);
            String roleUser = user.getString("user_role");
            String roleUserDestiny = userDestiny.getString("user_role");

            if(System.currentTimeMillis() > data.expirationDate){
                return Response.status(Response.Status.BAD_REQUEST).entity("Do login again, your session expired!").build();
            }
            if (roleUser.equals("USER")
                    || roleUser.equals("GBO")
                    || (roleUser.equals("GA")
                    && (roleUserDestiny.equals("GA") || roleUserDestiny.equals("SU")))){
                return Response.status(Response.Status.BAD_REQUEST).entity("You don't have the permissions to do that!").build();
            } else if ((roleUser.equals("GA") && roleUserDestiny.equals("USER") && !data.role.equals("GBO"))
                    || (roleUser.equals("GA") && roleUserDestiny.equals("GBO") && !data.role.equals("USER"))) {
                return Response.status(Response.Status.BAD_REQUEST).entity("You don't have the permissions to do that!").build();
            } else {
                Entity newUser = Entity.newBuilder(userDestinyKey)
                        .set("user_email", userDestiny.getString("user_email"))
                        .set("user_name", userDestiny.getString("user_name"))
                        .set("user_phone", userDestiny.getString("user_phone"))
                        .set("user_pwd", userDestiny.getString("user_pwd"))
                        .set("user_role", data.role)
                        .set("user_active", userDestiny.getBoolean("user_active"))
                        .build();
                datastore.put(newUser);
                return Response.ok("Alterado com sucesso user com username.: " + data.usernameDestiny).build();
            }
        }
    }

    @POST
    @Path("/state")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeState(ChangeStateData data){
        LOG.fine("Attempt to change state from user: " + data.usernameDestiny);

        Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
        Key userDestinyKey = datastore.newKeyFactory().setKind("User").newKey(data.usernameDestiny);

        Entity userDestiny = datastore.get(userDestinyKey);
        if (userDestiny == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Problem with the user he may not exist!").build();
        } else {
            if (System.currentTimeMillis() > data.expirationDate) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Do login again, your session expired!").build();
            }
            Entity user = datastore.get(userKey);
            String roleUser = user.getString("user_role");
            String roleUserDestiny = userDestiny.getString("user_role");
            if (roleUser.equals("USER")) {
                return Response.status(Response.Status.BAD_REQUEST).entity("You don't have the permissions to do that!").build();
            } else if (roleUser.equals("GBO") && !roleUserDestiny.equals("USER")) {
                return Response.status(Response.Status.BAD_REQUEST).entity("You don't have the permissions to do that!").build();
            } else if (roleUser.equals("GA") && (!roleUserDestiny.equals("USER") || !roleUserDestiny.equals("GBO"))) {
                return Response.status(Response.Status.BAD_REQUEST).entity("You don't have the permissions to do that!").build();
            } else {
                Entity newUser = Entity.newBuilder(userDestinyKey)
                        .set("user_email", userDestiny.getString("user_email"))
                        .set("user_name", userDestiny.getString("user_name"))
                        .set("user_phone", userDestiny.getString("user_phone"))
                        .set("user_pwd", userDestiny.getString("user_pwd"))
                        .set("user_role", userDestiny.getString("user_role"))
                        .set("user_active", !userDestiny.getBoolean("user_active"))
                        .build();
                datastore.put(newUser);
                return Response.ok("Alterado com sucesso user com username.: " + data.usernameDestiny).build();
            }
        }
    }

    @POST
    @Path("/password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changePassword(ChangePasswordData data){
        LOG.fine("Attempt to change password from user: " + data.username);

        Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
        Entity user = datastore.get(userKey);
        if (user == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Problem with the user he may not exist!").build();
        } else {
            if (System.currentTimeMillis() > data.expirationDate) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Do login again, your session expired!").build();
            }
            String hashedPWD = user.getString("user_pwd");
            if (!hashedPWD.equals(DigestUtils.sha512Hex(data.oldPassword)) || !data.isDataValid()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Try again make sure every parameter is filled correctly!").build();
            } else {
                Entity newUser = Entity.newBuilder(userKey)
                        .set("user_email", user.getString("user_email"))
                        .set("user_name", user.getString("user_name"))
                        .set("user_phone", user.getString("user_phone"))
                        .set("user_pwd", DigestUtils.sha512Hex(data.password))
                        .set("user_role", user.getString("user_role"))
                        .set("user_active", user.getBoolean("user_active"))
                        .build();
                datastore.put(newUser);
                return Response.ok("Alterado com sucesso user com username.: " + data.username).build();
            }
        }
    }
}
