package hu.bme.mit.codemodel.rifle.actions.queries;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

import hu.bme.mit.codemodel.rifle.database.DbServices;
import hu.bme.mit.codemodel.rifle.database.DbServicesManager;
import hu.bme.mit.codemodel.rifle.database.ResourceReader;

public class UnusedFunctions {
    protected final String UNUSED_QUERY = ResourceReader.query("unusedfunctions");
    protected final String GENERATE_CALLS = ResourceReader.query("generatecalls");
    protected final String REMOVE_CFG = ResourceReader.query("removecfg");

    private static final Logger logger = Logger.getLogger("codemodel");

    public String getUnusedFunctions(String sessionId, String branchId) {
        final DbServices dbServices = DbServicesManager.getDbServices(branchId);

        long startMillis = System.currentTimeMillis();

        dbServices.execute(REMOVE_CFG);

        long cfgDone = System.currentTimeMillis();

        dbServices.execute(GENERATE_CALLS);

        long callsDone = System.currentTimeMillis();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("sessionid", sessionId);
        StatementResult result = dbServices.execute(UNUSED_QUERY, parameters);

        long queryDone = System.currentTimeMillis();

        logger.info(" NOCFG " + (cfgDone - startMillis));
        logger.info(" CALLS " + (callsDone - cfgDone));
        logger.info(" QUERY " + (queryDone - callsDone));

        try (Transaction tx = dbServices.beginTx()) {
            JSONObject response = new JSONObject();
            JSONArray functions = new JSONArray();

            processRows:
            while (result.hasNext()) {
                Record next = result.next();

                Object id = next.get("id");

                Object startLine = next.get("start.line");
                Object startColumn = next.get("start.column");

                Object endLine = next.get("end.line");
                Object endColumn = next.get("end.column");

                Object deadSessionid = next.get("dead.session");

                try {
                    final String s = (String)deadSessionid;
                    if (s != null && !s.equals(sessionId)) {
                        continue processRows;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                JSONObject row = new JSONObject();
                JSONObject start = new JSONObject();
                start.put("line", startLine);
                start.put("column", startColumn);

                JSONObject end = new JSONObject();
                end.put("line", endLine);
                end.put("column", endColumn);

                row.put("id", id);
                row.put("start", start);
                row.put("end", end);
                row.put("sessionid", deadSessionid);
                functions.put(row);
            }
            response.put("unusedfunctions", functions);

            return response.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }
}
