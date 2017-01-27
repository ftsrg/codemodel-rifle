package hu.bme.mit.codemodel.rifle.resources.utils;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.neo4j.driver.v1.Transaction;

import hu.bme.mit.codemodel.rifle.database.DbServices;
import hu.bme.mit.codemodel.rifle.database.DbServicesManager;
import hu.bme.mit.codemodel.rifle.database.ResourceReader;

@Path("deletegraph")
public class DeleteGraph {

    private static final String DELETE_GRAPH = ResourceReader.query("deletegraph");

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response delete(@DefaultValue("master") @QueryParam("branchid") String branchid) {
        final DbServices dbServices = DbServicesManager.getDbServices(branchid);

        try (Transaction tx = dbServices.beginTx()) {
            dbServices.execute(DELETE_GRAPH);
            tx.success();
            return Response.ok().build();
        }
    }

}
