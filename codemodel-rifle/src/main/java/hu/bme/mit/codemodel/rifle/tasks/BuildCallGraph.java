package hu.bme.mit.codemodel.rifle.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

import hu.bme.mit.codemodel.rifle.database.DbServices;
import hu.bme.mit.codemodel.rifle.database.DbServicesManager;
import hu.bme.mit.codemodel.rifle.database.ResourceReader;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.FailsafeFuture;
import net.jodah.failsafe.RetryPolicy;

public class BuildCallGraph {

    private static RetryPolicy retryPolicy = new RetryPolicy()
//            .retryOn(DeadlockDetectedException.class)
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
    private final static Map<String, String> QUERIES = QUERYNAMES.stream().collect(
            		Collectors.toMap(queryName -> queryName, ResourceReader::query));

    private static final Logger logger = Logger.getLogger("codemodel");

    public String buildCfg(String branchId) {

        final DbServices dbServices = DbServicesManager.getDbServices(branchId);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

        List<FailsafeFuture<String>> futures = new ArrayList<>();

        long start = System.currentTimeMillis();

        for (Entry<String, String> entry : QUERIES.entrySet()) {
            String name = entry.getKey();
            String query = entry.getValue();

            futures.add(
                    Failsafe.with(retryPolicy)
                            .with(executorService)
                            .onRetryAsync(throwable -> System.err.println("Retrying " + name + "\n" + throwable.toString()))
                            .get(() -> {
                                System.err.println("Starting " + name);
                                StringBuilder builder = new StringBuilder();

                                try (Transaction tx = dbServices.beginTx()) {
                                    builder.append('\n').append(name).append('\n');
                                    final StatementResult result = dbServices.execute(query);
                                    builder.append(result.toString()).append('\n');

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

            return builder.toString();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return "ERROR";
    }
}
