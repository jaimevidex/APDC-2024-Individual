package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import org.apache.commons.codec.digest.DigestUtils;
import pt.unl.fct.di.apdc.firstwebapp.util.ChangeAttributeData;
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

    @PUT
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
                        .set("user_public", userDestiny.getBoolean("user_public"))
                        .set("user_occupation", userDestiny.getString("user_occupation"))
                        .set("user_workspace", userDestiny.getString("user_workspace"))
                        .set("user_address", userDestiny.getString("user_address"))
                        .set("user_zip", userDestiny.getString("user_zip"))
                        .set("user_nif", userDestiny.getString("user_nif"))
                        .set("user_role", data.role)
                        .set("user_active", userDestiny.getBoolean("user_active"))
                        .set("user_photo", userDestiny.getString("user_photo"))
                        .build();
                datastore.put(newUser);
                return Response.ok("Alterado com sucesso user com username.: " + data.usernameDestiny).build();
            }
        }
    }

    @PUT
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
                        .set("user_public", userDestiny.getBoolean("user_public"))
                        .set("user_occupation", userDestiny.getString("user_occupation"))
                        .set("user_workspace", userDestiny.getString("user_workspace"))
                        .set("user_address", userDestiny.getString("user_address"))
                        .set("user_zip", userDestiny.getString("user_zip"))
                        .set("user_nif", userDestiny.getString("user_nif"))
                        .set("user_role", userDestiny.getString("user_role"))
                        .set("user_active", !userDestiny.getBoolean("user_active"))
                        .set("user_photo", userDestiny.getString("user_photo"))
                        .build();
                datastore.put(newUser);
                return Response.ok("Alterado com sucesso user com username.: " + data.usernameDestiny).build();
            }
        }
    }

    @PUT
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
                        .set("user_public", user.getBoolean("user_public"))
                        .set("user_occupation", user.getString("user_occupation"))
                        .set("user_workspace", user.getString("user_workspace"))
                        .set("user_address", user.getString("user_address"))
                        .set("user_zip", user.getString("user_zip"))
                        .set("user_nif", user.getString("user_nif"))
                        .set("user_role", user.getString("user_role"))
                        .set("user_active", user.getBoolean("user_active"))
                        .set("user_photo", user.getString("user_photo"))
                        .build();
                datastore.put(newUser);

                return Response.ok("Alterado com sucesso user com username.: " + data.username).build();
            }
        }
    }

    @PUT
    @Path("/attributes")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeAttributes(ChangeAttributeData data){

        if(System.currentTimeMillis() > data.expirationDate){
            return Response.status(Response.Status.BAD_REQUEST).entity("Do login again, your session expired!").build();
        } else {
            Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
            Entity user = datastore.get(userKey);
            Key userDestinyKey = datastore.newKeyFactory().setKind("User").newKey(data.usernameDestiny);
            Entity userDestiny = datastore.get(userDestinyKey);
            if (user == null || userDestiny == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("UPS! One of the users may not exist!").build();
            } else {
                String roleUser = user.getString("user_role");
                String roleUserDestiny = userDestiny.getString("user_role");

                String user_email = fillValue(data.user_email,userDestiny.getString("user_email"));
                String user_name = fillValue(data.user_name, userDestiny.getString("user_name"));
                String user_phone = fillValue(data.user_phone, userDestiny.getString("user_phone"));
                Boolean user_public = fillValueBoolean(data.user_public, userDestiny.getBoolean("user_public"));
                String user_occupation = fillValue(data.user_occupation, userDestiny.getString("user_occupation"));
                String user_workspace = fillValue(data.user_workspace, userDestiny.getString("user_workspace"));
                String user_address = fillValue(data.user_address, userDestiny.getString("user_address"));
                String user_zip = fillValue(data.user_zip, userDestiny.getString("user_zip"));
                String user_nif = fillValue(data.user_nif, userDestiny.getString("user_nif"));
                String user_role = fillValue(data.user_role, userDestiny.getString("user_role"));
                Boolean user_active = fillValueBoolean(data.user_active, userDestiny.getBoolean("user_active"));
                String user_photo = fillValue(data.user_photo, userDestiny.getString("user_photo"));

                if (roleUser.equals("USER") && !data.username.equals(data.usernameDestiny)) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("You don't have the permissions to do that!").build();
                } else if (roleUser.equals("USER") && (!data.user_email.isEmpty() || !data.user_name.isEmpty() || !data.user_active.isEmpty() || !data.user_role.isEmpty())) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("You don't have the permissions to do that!").build();
                } else if (roleUser.equals("GBO") && !roleUserDestiny.equals("USER")) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("You don't have the permissions to do that!").build();
                } else if (roleUser.equals("GA") && (!roleUserDestiny.equals("GBO") || !roleUserDestiny.equals("USER"))) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("You don't have the permissions to do that!").build();
                } else if (roleUser.equals("SU") && roleUserDestiny.equals("SU")){
                    return Response.status(Response.Status.BAD_REQUEST).entity("You don't have the permissions to do that!").build();
                } else {
                    Entity newUser = Entity.newBuilder(userDestinyKey)
                            .set("user_email", user_email)
                            .set("user_name", user_name)
                            .set("user_phone", user_phone)
                            .set("user_pwd", userDestiny.getString("user_pwd"))
                            .set("user_public", user_public)
                            .set("user_occupation", user_occupation)
                            .set("user_workspace", user_workspace)
                            .set("user_address", user_address)
                            .set("user_zip", user_zip)
                            .set("user_nif", user_nif)
                            .set("user_role", user_role)
                            .set("user_active", user_active)
                            .set("user_photo", user_photo)
                            .build();
                    datastore.put(newUser);
                    return Response.ok("Alterado com sucesso user com username.: " + data.username).build();
                }
            }
        }
    }

    private String fillValue(String userEmail, String userEmail1) {
        String ans;
        if (userEmail.isEmpty()) {
            ans=userEmail1;
        } else {
            ans=userEmail;
        }
        return ans;
    }

    private Boolean fillValueBoolean(String userPublic, boolean userPublic1) {
        boolean ans;
        if (userPublic.isEmpty()) {
            ans=userPublic1;
        } else {
            ans=userPublic.equals("true");
        }
        return ans;
    }
}
