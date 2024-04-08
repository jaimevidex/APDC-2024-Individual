package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import pt.unl.fct.di.apdc.firstwebapp.util.UserData;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;

@Path("/list")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ListUsersResource {
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
    private final Gson g = new Gson();
    public ListUsersResource() {
    }

    @GET
    @Path("/")
    public Response listUsers(@HeaderParam("username") String username, @HeaderParam("expirationDate") long expirationDate) {

        if(System.currentTimeMillis() > expirationDate){
            return Response.status(Response.Status.BAD_REQUEST).entity("Do login again, your session expired!").build();
        } else {
            Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
            Entity user = datastore.get(userKey);
            if (user == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("UPS! You may not exist!").build();
            } else {
                String roleUser = user.getString("user_role");

                QueryResults<Entity> filteredResults;
                filteredResults = datastore.run(queryByRole(roleUser));

                List<UserData> listUsers = new ArrayList<>();
                while(filteredResults.hasNext()){
                    Entity userEntity = filteredResults.next();
                    if (user.getString("user_role").equals("USER")){
                        listUsers.add(new UserData(
                                userEntity.getKey().getName(),
                                userEntity.getString("user_email"),
                                userEntity.getString("user_name")
                        ));
                    } else {
                        listUsers.add(new UserData(
                                userEntity.getKey().getName(),
                                userEntity.getString("user_email"),
                                userEntity.getString("user_name"),
                                userEntity.getString("user_phone"),
                                userEntity.getBoolean("user_public"),
                                userEntity.getString("user_occupation"),
                                userEntity.getString("user_workspace"),
                                userEntity.getString("user_address"),
                                userEntity.getString("user_zip"),
                                userEntity.getString("user_nif"),
                                userEntity.getString("user_role"),
                                userEntity.getBoolean("user_active"),
                                userEntity.getString("user_photo")
                        ));
                    }
                }
                return Response.ok().entity(g.toJson(listUsers)).build();
            }
        }
    }

    private Query<Entity> queryByRole(String role) {
        switch (role) {
            case ("USER"):
                return Query.newEntityQueryBuilder()
                        .setKind("User")
                        .setFilter(
                                StructuredQuery.CompositeFilter.and(
                                        StructuredQuery.PropertyFilter.eq("user_role", "USER"),
                                        StructuredQuery.PropertyFilter.eq("user_public", true),
                                        StructuredQuery.PropertyFilter.eq("user_active", true)
                                )
                        )
                        .build();
            case ("GBO"):
                return Query.newEntityQueryBuilder()
                        .setKind("User")
                        .setFilter(
                                StructuredQuery.PropertyFilter.eq("user_role", "USER")
                        )
                        .build();
            case("GA"):
                List<String> rolesAsList = Arrays.asList("USER","GBO","GA");
                List<Value<String>> roleValues = rolesAsList.stream().map(StringValue::of).collect(Collectors.toList());
                ListValue roleListValues = ListValue.of(roleValues);

                return Query.newEntityQueryBuilder()
                        .setKind("User")
                        .setFilter(
                                StructuredQuery.PropertyFilter.in("user_role",roleListValues)
                        )
                        .build();
            default:
                return Query.newEntityQueryBuilder().setKind("User").build();
        }
    }
}
