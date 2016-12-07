package hu.bme.mit.codemodel.rifle.resources;

import hu.bme.mit.codemodel.rifle.utils.DbServices;
import hu.bme.mit.codemodel.rifle.utils.DbServicesManager;
import hu.bme.mit.codemodel.rifle.utils.ResourceReader;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("importexport")
public class ImportExport {
    private static final String IMPORT_EXPORT = ResourceReader.query("basicimport");

    private static final Logger logger = Logger.getLogger("codemodel");

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response importExport(
            @DefaultValue("master")
            @QueryParam("branchid") String branchid
    ) {

        final DbServices dbServices = DbServicesManager.getDbServices(branchid);
        long start = System.currentTimeMillis();

        try (Transaction tx = dbServices.beginTx()) {
            Result result = dbServices.graphDb.execute(IMPORT_EXPORT);
            tx.success();
            logger.info(" IME " + (System.currentTimeMillis() - start));
            logger.info(result.resultAsString());
            return Response.ok(result.resultAsString()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }
}
