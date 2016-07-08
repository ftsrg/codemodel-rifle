package hu.bme.mit.codemodel.rifle.resources;

import hu.bme.mit.codemodel.rifle.utils.DbServices;
import hu.bme.mit.codemodel.rifle.utils.DbServicesManager;
import hu.bme.mit.codemodel.rifle.utils.ResourceReader;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.FailsafeFuture;
import net.jodah.failsafe.RetryPolicy;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.DeadlockDetectedException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by steindani on 3/2/16.
 */
@Path("buildcfg")
public class BuildCallGraph {

    protected static RetryPolicy retryPolicy = new RetryPolicy()
            .retryOn(DeadlockDetectedException.class)
            .withDelay(1, TimeUnit.SECONDS);

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
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

        List<FailsafeFuture<String>> futures = new ArrayList<>();

        for (int i = 0; i < QUERYNAMES.size(); i++) {
            String name = QUERYNAMES.get(i);
            String query = QUERIES.get(i);

            futures.add(
                    Failsafe.with(retryPolicy)
                            .with(executorService)
                            .get(() -> {
                                StringBuilder builder = new StringBuilder();

                                try (Transaction tx = dbServices.beginTx()) {
                                    builder.append('\n').append(name).append('\n');
                                    final Result result = dbServices.graphDb.execute(query);
                                    builder.append(result.resultAsString()).append('\n');

                                    tx.success();
                                } catch (Exception e) {
                                    System.err.println(name);
                                    e.printStackTrace();

                                    builder.append(name).append('\n').append(e.toString()).append('\n');
                                    throw new WebApplicationException(e);
                                }

                                return builder.toString();
                            })
            );
        }

        try {
            StringBuilder builder = new StringBuilder();

            for (FailsafeFuture<String> future : futures) {
                builder.append(future.get());
                builder.append('\n');
            }

            return Response.ok(builder.toString()).build();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return Response.serverError().build();
    }
}
