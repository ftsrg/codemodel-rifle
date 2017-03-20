package hu.bme.mit.codemodel.rifle.tasks;

import java.util.logging.Logger;

import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

import hu.bme.mit.codemodel.rifle.database.DbServices;
import hu.bme.mit.codemodel.rifle.database.DbServicesManager;
import hu.bme.mit.codemodel.rifle.database.ResourceReader;

public class ImportExport {
    private static final String IMPORT_EXPORT = ResourceReader.query("basicimport");

    private static final Logger logger = Logger.getLogger("codemodel");

    public String importExport(String branchId) {

        final DbServices dbServices = DbServicesManager.getDbServices(branchId);
        long start = System.currentTimeMillis();

        try (Transaction tx = dbServices.beginTx()) {
            StatementResult result = dbServices.execute(IMPORT_EXPORT);
            tx.success();
            logger.info(" TIME " + (System.currentTimeMillis() - start));
            logger.info(result.toString());
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }
}
