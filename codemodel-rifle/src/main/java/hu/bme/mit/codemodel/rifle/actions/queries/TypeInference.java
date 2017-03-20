package hu.bme.mit.codemodel.rifle.actions.queries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

import hu.bme.mit.codemodel.rifle.database.DbServices;
import hu.bme.mit.codemodel.rifle.database.DbServicesManager;
import hu.bme.mit.codemodel.rifle.database.ResourceReader;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.FailsafeFuture;
import net.jodah.failsafe.RetryPolicy;

public class TypeInference {

    protected static RetryPolicy retryPolicy = new RetryPolicy()
//            .retryOn(DeadlockDetectedException.class)
        .withBackoff(10, 10000, TimeUnit.MILLISECONDS);

    protected final static List<String> QUERYNAMES = Arrays.asList("type/TypeSystem", "type/literal/Boolean", "type/literal/RegExp", "type/literal/Infinity", "type/literal/Numeric", "type/literal/Null", "type/literal/String", "type/operator/binary/LogicalOr", "type/variable/VariableDeclarator", "type/variable/Read", "type/variable/Write");
    protected final static List<String> QUERIES = QUERYNAMES.stream().map(ResourceReader::query).collect(Collectors.toList());

    public String infer(String branchId) {

        // 1. initialize TypeSystem
        // 2. tag literals (parallel)
        // 3. repeat the following until there is no modification (parallel, 2x):
        //    - tag unary expressions
        //    - tag binary expressions
        //    - handle and tag new expressions
        //    - handle and tag call expressions
        // 4. ???
        // 5. profit

        final DbServices dbServices = DbServicesManager.getDbServices(branchId);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

        List<FailsafeFuture<String>> futures = new ArrayList<>();

        for (int i = 0; i < QUERYNAMES.size(); i++) {
            String name = QUERYNAMES.get(i);
            String query = QUERIES.get(i);

            futures.add(Failsafe.with(retryPolicy).with(executorService).onRetryAsync(throwable -> System.err.println("Retrying " + name + "\n" + throwable.toString())).get(() -> {
                System.err.println("Starting " + name);
                StringBuilder builder = new StringBuilder();

                try (Transaction tx = dbServices.beginTx()) {
                    builder.append('\n').append(name).append('\n');
                    final StatementResult result = dbServices.execute(query);
                    builder.append(result.toString()).append('\n');

                    tx.success();
                    return builder.toString();
                }
            }));
        }

        try {
            StringBuilder builder = new StringBuilder();

            for (FailsafeFuture<String> future : futures) {
                String result = future.get();
                System.err.println(result + "\n");

                builder.append(result);
                builder.append('\n');
            }

            return builder.toString();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return "ERROR";
    }
}
