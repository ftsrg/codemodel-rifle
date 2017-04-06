package hu.bme.mit.codemodel.rifle.tasks;

import java.util.Collection;
import java.util.logging.Logger;

import org.neo4j.driver.v1.Transaction;

import hu.bme.mit.codemodel.rifle.database.DbServices;
import hu.bme.mit.codemodel.rifle.database.DbServicesManager;
import hu.bme.mit.codemodel.rifle.utils.ResourceReader;

public class ImportExport {
    private static final Logger logger = Logger.getLogger("codemodel");

    public boolean importExport(String branchId) {
        final DbServices dbServices = DbServicesManager.getDbServices(branchId);
        final Collection<String> importExportQueries = ResourceReader.getImportExportQueries();

        try (Transaction tx = dbServices.beginTx()) {
            for (String query : importExportQueries) {
                dbServices.execute(query);
            }

            tx.success();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
