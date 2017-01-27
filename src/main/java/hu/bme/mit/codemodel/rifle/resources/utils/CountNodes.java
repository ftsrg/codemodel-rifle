package hu.bme.mit.codemodel.rifle.resources.utils;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

import hu.bme.mit.codemodel.rifle.database.DbServices;
import hu.bme.mit.codemodel.rifle.database.DbServicesManager;
import hu.bme.mit.codemodel.rifle.database.ResourceReader;

@Path("countnodes")
public class CountNodes {

    private static final String COUNT_NODES = ResourceReader.query("countnodes");

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response get(@DefaultValue("master") @QueryParam("branchid") String branchid) {
        final DbServices dbServices = DbServicesManager.getDbServices(branchid);

        try (Transaction tx = dbServices.beginTx()) {
            StatementResult result = dbServices.execute(COUNT_NODES);
            System.out.println(result.single());
            tx.success();
            return Response.ok(result.toString()).build();
        }
    }

}
