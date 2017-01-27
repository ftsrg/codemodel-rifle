package hu.bme.mit.codemodel.rifle.resources.imports;

import java.util.logging.Logger;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import hu.bme.mit.codemodel.rifle.utils.DbServices;
import hu.bme.mit.codemodel.rifle.utils.DbServicesManager;
import hu.bme.mit.codemodel.rifle.utils.ResourceReader;

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
            Result result = dbServices.execute(IMPORT_EXPORT);
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
