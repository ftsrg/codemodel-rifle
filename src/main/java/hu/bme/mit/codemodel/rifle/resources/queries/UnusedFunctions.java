package hu.bme.mit.codemodel.rifle.resources.queries;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

import hu.bme.mit.codemodel.rifle.database.DbServices;
import hu.bme.mit.codemodel.rifle.database.DbServicesManager;
import hu.bme.mit.codemodel.rifle.database.ResourceReader;

/**
 * Created by steindani on 3/2/16.
 */
@Path("unusedfunctions")
public class UnusedFunctions {
    protected final String UNUSED_QUERY = ResourceReader.query("unusedfunctions");
    protected final String GENERATE_CALLS = ResourceReader.query("generatecalls");
    protected final String REMOVE_CFG = ResourceReader.query("removecfg");

    private static final Logger logger = Logger.getLogger("codemodel");

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUnusedFunctions(
            @QueryParam("sessionid") String sessionid,

            @DefaultValue("master")
            @QueryParam("branchid") String branchid
    ) {
        final DbServices dbServices = DbServicesManager.getDbServices(branchid);
        Transaction tx = dbServices.beginTx();

        long startMillis = System.currentTimeMillis();

        dbServices.execute(REMOVE_CFG);

        long cfgDone = System.currentTimeMillis();

        dbServices.execute(GENERATE_CALLS);

        long callsDone = System.currentTimeMillis();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("sessionid", sessionid);
        StatementResult result = dbServices.execute(UNUSED_QUERY, parameters);

        long queryDone = System.currentTimeMillis();

        logger.info(" NOCFG " + (cfgDone - startMillis));
        logger.info(" CALLS " + (callsDone - cfgDone));
        logger.info(" QUERY " + (queryDone - callsDone));

        try {
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
                    final String s = (String) deadSessionid;
                    if (s != null && !s.equals(sessionid)) {
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

            return Response.ok(response.toString()).build();
        } catch (JSONException e) {
            e.printStackTrace();
            throw new WebApplicationException(e);
        } finally {
            tx.failure();
            tx.close();
        }
    }
}
