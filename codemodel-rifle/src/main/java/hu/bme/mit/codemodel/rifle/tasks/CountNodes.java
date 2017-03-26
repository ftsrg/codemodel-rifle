package hu.bme.mit.codemodel.rifle.tasks;

import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

import hu.bme.mit.codemodel.rifle.database.DbServices;
import hu.bme.mit.codemodel.rifle.database.DbServicesManager;
import hu.bme.mit.codemodel.rifle.utils.ResourceReader;

public class CountNodes {

    private static final String COUNT_NODES = ResourceReader.query("countnodes");
    private static final String COUNT_COMPILATIONUNIT_NODES = ResourceReader.query("countcompilationunitnodes");

    public int countAll(String branchId) {
        final DbServices dbServices = DbServicesManager.getDbServices(branchId);

        try (Transaction tx = dbServices.beginTx()) {
            StatementResult result = dbServices.execute(COUNT_NODES);
            tx.success();

            return result.single().get("count").asInt();
        }
    }

    public int countCompilationUnitNodes(String branchId) {
        final DbServices dbServices = DbServicesManager.getDbServices(branchId);

        try (Transaction tx = dbServices.beginTx()) {
            StatementResult result = dbServices.execute(COUNT_COMPILATIONUNIT_NODES);
            tx.success();

            return result.single().get("count").asInt();
        }
    }

}
