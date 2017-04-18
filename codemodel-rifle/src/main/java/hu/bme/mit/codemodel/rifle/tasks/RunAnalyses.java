package hu.bme.mit.codemodel.rifle.tasks;

import hu.bme.mit.codemodel.rifle.database.DbServices;
import hu.bme.mit.codemodel.rifle.database.DbServicesManager;
import hu.bme.mit.codemodel.rifle.utils.ResourceReader;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

import java.util.Collection;
import java.util.logging.Logger;

public class RunAnalyses {
    public static final Logger logger = Logger.getLogger("codemodel");

    public void runAnalyses(String branchId) {
        final DbServices dbServices = DbServicesManager.getDbServices(branchId);
        final Collection<String> analysisQueries = ResourceReader.getAnalysisQueries();

        for (String query : analysisQueries) {
            try (Transaction tx = dbServices.beginTx()) {
                StatementResult result = dbServices.execute(query);
                while (result.hasNext()) {
                    Record record = result.next();

                    String message = record.get("message").asString();
                    String entityName = record.get("entityName").asString();
                    String compilationUnitPath = record.get("compilationUnitPath").asString();
                    int line = Integer.parseInt(record.get("line").asString());
                    int column = Integer.parseInt(record.get("column").asString());

                    logger.info(
                        String.format("%s %s at %d:%d in %s", message, entityName, line, column, compilationUnitPath)
                    );
                }

                tx.success();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
