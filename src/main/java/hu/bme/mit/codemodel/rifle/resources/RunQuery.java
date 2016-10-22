package hu.bme.mit.codemodel.rifle.resources;

import hu.bme.mit.codemodel.rifle.utils.DbServices;
import hu.bme.mit.codemodel.rifle.utils.DbServicesManager;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * Created by steindani on 6/29/16.
 */
@Path("run")
public class RunQuery {

    private static final Logger logger = Logger.getLogger("codemodel");

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response run(
            String content,
            @DefaultValue("master")
            @QueryParam("branchid") String branchid
    ) {
        final DbServices dbServices = DbServicesManager.getDbServices(branchid);
        try (Transaction tx = dbServices.beginTx()) {
            long start = System.currentTimeMillis();

            final Result execute = dbServices.graphDb.execute(content);
            tx.success();

            logger.info(" RUN " + (System.currentTimeMillis() - start));
            return Response.ok(execute.resultAsString()).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.serverError().build();
    }
}
