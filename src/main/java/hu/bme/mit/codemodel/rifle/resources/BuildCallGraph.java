package hu.bme.mit.codemodel.rifle.resources;

import hu.bme.mit.codemodel.rifle.utils.DbServices;
import hu.bme.mit.codemodel.rifle.utils.DbServicesManager;
import hu.bme.mit.codemodel.rifle.utils.ResourceReader;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
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
            "statement_IfStatementAlternate",
            "statement_IfStatementNoAlternate",
            "statement_VariableDeclarationStatement"
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

        final DbServices dbServices = DbServicesManager.getDbServices(branchid);
        ExecutorService executorService = Executors.newCachedThreadPool();

        List<Callable<String>> tasks = new ArrayList<>();

        for (int i = 0; i < QUERYNAMES.size(); i++) {
            String name = QUERYNAMES.get(i);
            String query = QUERIES.get(i);

            Callable<String> callable = () -> {
                Transaction tx = dbServices.beginTx();
                StringBuilder builder = new StringBuilder();

                try {
                    builder.append('\n').append(name).append('\n');
                    final Result result = dbServices.graphDb.execute(query);
                    builder.append(result.resultAsString()).append('\n');

                    tx.success();
                } catch (Exception e) {
                    System.err.println(name);
                    e.printStackTrace();

                    builder.append(name).append('\n').append(e.toString()).append('\n');
                    throw new WebApplicationException(e);
                } finally {
                    if (tx != null) {
                        tx.close();
                    }
                }

                return builder.toString();
            };

            tasks.add(callable);
        }

        try {
            final List<Future<String>> futures = executorService.invokeAll(tasks);
            StringBuilder builder = new StringBuilder();

            for (Future<String> future : futures) {
                builder.append(future.get());
                builder.append('\n');
            }

            return Response.ok(builder.toString()).build();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return Response.serverError().build();
    }
}
