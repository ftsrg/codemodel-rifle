package hu.bme.mit.codemodel.rifle.resources;

import java.util.logging.Logger;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import hu.bme.mit.codemodel.rifle.utils.DbServices;
import hu.bme.mit.codemodel.rifle.utils.DbServicesManager;

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

            final Result execute = dbServices.execute(content);
            tx.success();

            logger.info(" RUN " + (System.currentTimeMillis() - start));
            return Response.ok(execute.resultAsString()).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.serverError().build();
    }
}
