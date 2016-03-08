package hu.bme.mit.codemodel.rifle.resources;

import hu.bme.mit.codemodel.rifle.WebApplication;
import hu.bme.mit.codemodel.rifle.utils.ResourceReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * Created by steindani on 3/2/16.
 */
@Path("unusedfunctions")
public class UnusedFunctions {
    protected static final String NAME = "unusedfunctions";
    protected final String query = ResourceReader.query(NAME);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUnusedFunctions() {

        Transaction tx = WebApplication.graphDatabaseService.beginTx();
        new GenerateCalls().generateCalls();

        Result result = WebApplication.graphDatabaseService.execute(query);

        try {
            JSONObject response = new JSONObject();
            JSONArray functions = new JSONArray();
            while (result.hasNext()) {
                Map<String, Object> next = result.next();

                Object id = next.get("id");

                Object startLine = next.get("start.line");
                Object startColumn = next.get("start.column");

                Object endLine = next.get("end.line");
                Object endColumn = next.get("end.column");

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
                functions.put(row);
            }
            response.append("unusedfunctions", functions);

            return Response.ok(response.toString()).build();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            tx.failure();
            tx.close();
        }

        return Response.serverError().build();
    }
}
