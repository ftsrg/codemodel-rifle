package hu.bme.mit.codemodel.rifle.tasks;

import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

import hu.bme.mit.codemodel.rifle.database.DbServices;
import hu.bme.mit.codemodel.rifle.database.DbServicesManager;
import hu.bme.mit.codemodel.rifle.database.ResourceReader;

public class CountNodes {

    private static final String COUNT_NODES = ResourceReader.query("countnodes");

    public String get(String branchId) {
        final DbServices dbServices = DbServicesManager.getDbServices(branchId);

        try (Transaction tx = dbServices.beginTx()) {
            StatementResult result = dbServices.execute(COUNT_NODES);
            System.out.println(result.single());
            tx.success();
            return result.toString();
        }
    }

}
