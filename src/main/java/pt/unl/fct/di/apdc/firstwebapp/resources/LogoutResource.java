package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.cloud.datastore.*;
import pt.unl.fct.di.apdc.firstwebapp.util.LogoutData;
import pt.unl.fct.di.apdc.firstwebapp.util.RegisterData;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/logout")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LogoutResource {

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

    public LogoutResource() {}

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doLogout(LogoutData data) {
        LOG.fine("Attempt to logout user: " + data.username);

        Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);

        Entity user = datastore.get(userKey);
        if (user == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Ups! You dont have an account!").build();
        } else {
            if (System.currentTimeMillis() > data.expirationDate) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Do login again, your session expired!").build();
            } else {
                Query<Entity> childQuery = Query.newEntityQueryBuilder()
                        .setKind("Token")
                        .setFilter(StructuredQuery.PropertyFilter.hasAncestor(userKey))
                        .build();
                QueryResults<Entity> childResults = datastore.run(childQuery);
                while (childResults.hasNext()) {
                    Entity child = childResults.next();
                    datastore.delete(child.getKey());
                }
                return Response.ok("User with username '" + data.username + "' logged out successfully!").build();
            }
        }
    }
}
