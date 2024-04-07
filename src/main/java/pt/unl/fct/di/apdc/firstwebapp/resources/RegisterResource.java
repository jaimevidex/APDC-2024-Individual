package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.cloud.datastore.*;
import org.apache.commons.codec.digest.DigestUtils;
import pt.unl.fct.di.apdc.firstwebapp.util.RegisterData;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterResource {

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

    public RegisterResource() {}
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doRegister(RegisterData data) {

        if (!data.isDataValid()) {
            return badRequestMessage("Por favor preencha os campos corretamente!");
        }

        Transaction txn = datastore.newTransaction();
        Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);

        try {

            Entity user = txn.get(userKey);

            if (user != null) {
                txn.rollback();
                return badRequestMessage("Essa conta já existe, por favor faça login!");
            }
            else {
                String hashedString = DigestUtils.sha512Hex(data.password);
                boolean active = false;
                String role = "USER";
                if (data.username.equals("root")) {
                    active = true;
                    role = "SU";
                }
                user = Entity.newBuilder(userKey)
                        .set("user_email", data.email)
                        .set("user_name", data.name)
                        .set("user_phone", data.phone)
                        .set("user_pwd", hashedString)
                        .set("user_role", role)
                        .set("user_active", active)
                        .build();

                txn.add(user);
                LOG.info("User registered " + data.username);
                txn.commit();
                return Response.ok("Resgistado com sucesso user com username.: " + data.username ).build();
            }
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    private Response badRequestMessage(String message) {
        return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
    }
}
