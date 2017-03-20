package hu.bme.mit.codemodel.rifle.actions.queries;

import java.util.logging.Logger;

import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

import hu.bme.mit.codemodel.rifle.database.DbServices;
import hu.bme.mit.codemodel.rifle.database.DbServicesManager;

public class RunQuery {

    private static final Logger logger = Logger.getLogger("codemodel");

    public String run(String content, String branchId) {
        final DbServices dbServices = DbServicesManager.getDbServices(branchId);
        try (Transaction tx = dbServices.beginTx()) {
            long start = System.currentTimeMillis();

            final StatementResult result = dbServices.execute(content);
            tx.success();

            logger.info(" RUN " + (System.currentTimeMillis() - start));
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "ERROR";
    }
}
