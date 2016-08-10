package hu.bme.mit.codemodel.rifle.resources;

import hu.bme.mit.codemodel.rifle.utils.DbServices;
import hu.bme.mit.codemodel.rifle.utils.DbServicesManager;
import hu.bme.mit.codemodel.rifle.utils.ResourceReader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * Created by steindani on 6/29/16.
 */
@Path("lastcommit")
public class GetLastCommitHash {
    private static final String GET_LAST_COMMIT_HASH = ResourceReader.query("getlastcommithash");

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response run(
            @DefaultValue("master")
            @QueryParam("branchid") String branchid
    ) {
        final DbServices dbServices = DbServicesManager.getDbServices(branchid);
        try (Transaction tx = dbServices.beginTx()) {
            final Result result = dbServices.graphDb.execute(GET_LAST_COMMIT_HASH);

            JSONObject response = new JSONObject();
            while (result.hasNext()) {
                Map<String, Object> next = result.next();

                Object commitHash = next.get("commitHash");
                response.put("commitHash", commitHash);
            }

            return Response.ok(response.toString()).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.serverError().build();
    }
}
