package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.cloud.datastore.*;
import pt.unl.fct.di.apdc.firstwebapp.util.RemoveUserData;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/remove")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RemoveUserResource {
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

    public RemoveUserResource() {
    }

    @DELETE
    @Path("/{username}")
    public Response removeUser(@PathParam("username") String username, RemoveUserData data) {
        LOG.fine("Attempt to remove user: " + username);

        Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
        Key userDestinyKey = datastore.newKeyFactory().setKind("User").newKey(username);

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
            if (roleUser.equals("GBO")) {
                return Response.status(Response.Status.BAD_REQUEST).entity("You don't have the permissions to do that!").build();
            } else if (roleUser.equals("USER") && !username.equals(data.username)) {
                return Response.status(Response.Status.BAD_REQUEST).entity("You don't have the permissions to do that!").build();
            } else if (roleUser.equals("GA") && (!roleUserDestiny.equals("GBO") || !roleUserDestiny.equals("USER"))) {
                return Response.status(Response.Status.BAD_REQUEST).entity("You don't have the permissions to do that!").build();
            } else {
                Query<Entity> childQuery = Query.newEntityQueryBuilder()
                        .setKind("Token") // Replace with your child entity kind
                        .setFilter(StructuredQuery.PropertyFilter.hasAncestor(userKey))
                        .build();
                QueryResults<Entity> childResults = datastore.run(childQuery);
                while (childResults.hasNext()) {
                   Entity child = childResults.next();
                   datastore.delete(child.getKey());
                }
                datastore.delete(userDestinyKey);
                return Response.ok("User with username '" + username + "' deleted successfully!").build();
            }
        }
    }
}
