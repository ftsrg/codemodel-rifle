package hu.bme.mit.codemodel.rifle.resources;

import hu.bme.mit.codemodel.rifle.utils.DbServices;
import hu.bme.mit.codemodel.rifle.utils.DbServicesManager;
import hu.bme.mit.codemodel.rifle.utils.ResourceReader;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by steindani on 3/2/16.
 */
@Path("buildcfg")
public class BuildCallGraph {

    protected final static List<String> QUERYNAMES = Arrays.asList(
            "ListNoItem",
            "ListWithItem",
            "expression_CallExpressionNoParam",
            "expression_CallExpressionParam",
            "expression_LiteralX",
            "statement_BlockStatement",
            "statement_ExpressionStatement",
            "statement_FunctionDeclaration",
            "statement_IfStatement"
    );
    protected final static List<String> QUERIES = QUERYNAMES.stream()
            .map(ResourceReader::query)
            .collect(Collectors.toList());

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response buildCfg(
            @DefaultValue("master")
            @QueryParam("branchid") String branchid
    ) {
        Transaction tx = null;
        StringBuilder builder = new StringBuilder();

        try {
            final DbServices dbServices = DbServicesManager.getDbServices(branchid);
            tx = dbServices.beginTx();

            for (int i = 0; i < QUERYNAMES.size(); i++) {
                String name = QUERYNAMES.get(i);
                String query = QUERIES.get(i);

                builder.append('\n').append(name).append('\n');

                try {
                    final Result result = dbServices.graphDb.execute(query);
                    builder.append(result.resultAsString()).append('\n');
                } catch (Exception e) {
                    e.printStackTrace();
                    builder.append(e.toString()).append('\n');
                }
            }

            tx.success();

            return Response.ok(builder.toString()).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException(e);
        } finally {
            if (tx != null) {
                tx.close();
            }
        }
    }
}
