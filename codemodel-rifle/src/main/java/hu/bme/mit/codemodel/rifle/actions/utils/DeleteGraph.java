package hu.bme.mit.codemodel.rifle.actions.utils;

import org.neo4j.driver.v1.Transaction;

import hu.bme.mit.codemodel.rifle.database.DbServices;
import hu.bme.mit.codemodel.rifle.database.DbServicesManager;
import hu.bme.mit.codemodel.rifle.database.ResourceReader;

public class DeleteGraph {

    private static final String DELETE_GRAPH = ResourceReader.query("deletegraph");

    public boolean delete(String branchId) {
        final DbServices dbServices = DbServicesManager.getDbServices(branchId);

        try (Transaction tx = dbServices.beginTx()) {
            dbServices.execute(DELETE_GRAPH);
            tx.success();
            return true;
        }
    }

}
