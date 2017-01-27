package hu.bme.mit.codemodel.rifle.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.DeadlockDetectedException;

import hu.bme.mit.codemodel.rifle.utils.DbServices;
import hu.bme.mit.codemodel.rifle.utils.DbServicesManager;
import hu.bme.mit.codemodel.rifle.utils.ResourceReader;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.FailsafeFuture;
import net.jodah.failsafe.RetryPolicy;

/**
 * Created by steindani on 3/2/16.
 */
@Path("buildcfg")
public class BuildCallGraph {

    private static RetryPolicy retryPolicy = new RetryPolicy()
            .retryOn(DeadlockDetectedException.class)
            .withBackoff(10, 10000, TimeUnit.MILLISECONDS);

    private final static List<String> QUERYNAMES = Arrays.asList(
            "cfg/ListNoItem",
            "cfg/ListWithItem",
            "cfg/expression/CallExpressionNoParam",
            "cfg/expression/LiteralX",
            "cfg/expression/CallExpressionParam",
            "cfg/statement/VariableDeclarationStatement",
            "cfg/statement/ExpressionStatement",
            "cfg/statement/IfStatementNoAlternate",
            "cfg/statement/BlockStatement",
            "cfg/statement/FunctionDeclaration",
            "cfg/statement/IfStatementAlternate"
    );
    private final static List<String> QUERIES = QUERYNAMES.stream()
            .map(ResourceReader::query)
            .collect(Collectors.toList());

    private static final Logger logger = Logger.getLogger("codemodel");

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response buildCfg(
            @DefaultValue("master")
            @QueryParam("branchid") String branchid
    ) {

        final DbServices dbServices = DbServicesManager.getDbServices(branchid);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

        List<FailsafeFuture<String>> futures = new ArrayList<>();

        long start = System.currentTimeMillis();

        for (int i = 0; i < QUERYNAMES.size(); i++) {
            String name = QUERYNAMES.get(i);
            String query = QUERIES.get(i);

            futures.add(
                    Failsafe.with(retryPolicy)
                            .with(executorService)
                            .onRetryAsync(throwable -> System.err.println("Retrying " + name + "\n" + throwable.toString()))
                            .get(() -> {
                                System.err.println("Starting " + name);
                                StringBuilder builder = new StringBuilder();

                                try (Transaction tx = dbServices.beginTx()) {
                                    builder.append('\n').append(name).append('\n');
                                    final Result result = dbServices.graphDb.execute(query);
                                    builder.append(result.resultAsString()).append('\n');

                                    tx.success();
                                    return builder.toString();
                                }
                            })
            );
        }

        try {
            StringBuilder builder = new StringBuilder();

            for (FailsafeFuture<String> future : futures) {
                String result = future.get();
                System.err.println(result + "\n");

                builder.append(result);
                builder.append('\n');
            }

            logger.info(" CFG " + (System.currentTimeMillis() - start));

            return Response.ok(builder.toString()).build();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return Response.serverError().build();
    }
}
