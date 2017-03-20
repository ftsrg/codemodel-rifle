package hu.bme.mit.codemodel.rifle.actions;

import org.json.JSONObject;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

import hu.bme.mit.codemodel.rifle.database.DbServices;
import hu.bme.mit.codemodel.rifle.database.DbServicesManager;
import hu.bme.mit.codemodel.rifle.database.ResourceReader;

public class GetLastCommitHash {
    private static final String GET_LAST_COMMIT_HASH = ResourceReader.query("getlastcommithash");

    public String run(String branchId) {
        final DbServices dbServices = DbServicesManager.getDbServices(branchId);
        try (Transaction tx = dbServices.beginTx()) {
            final StatementResult result = dbServices.execute(GET_LAST_COMMIT_HASH);

            JSONObject response = new JSONObject();
            while (result.hasNext()) {
                Record next = result.next();

                Object commitHash = next.get("commitHash");
                response.put("commitHash", commitHash);
            }

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "ERROR";
    }
}
