package hu.bme.mit.codemodel.rifle.actions.imports;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.neo4j.driver.v1.Transaction;

import com.google.common.base.Stopwatch;
import com.shapesecurity.shift.ast.Module;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.ParserWithLocation;
import com.shapesecurity.shift.scope.GlobalScope;
import com.shapesecurity.shift.scope.ScopeAnalyzer;

import hu.bme.mit.codemodel.rifle.database.DbServices;
import hu.bme.mit.codemodel.rifle.database.DbServicesManager;
import hu.bme.mit.codemodel.rifle.database.GraphIterator;
import hu.bme.mit.codemodel.rifle.database.ResourceReader;

public class HandleChange {

    private static final String SET_COMMIT_HASH = ResourceReader.query("setcommithash");
    private static final String REMOVE_FILE = ResourceReader.query("removefile");

    private static final Logger logger = Logger.getLogger("codemodel");

    public boolean add(String sessionId, String path, String content, String branchId, String commitHash) {
        setCommitHashInNewTransaction(branchId, commitHash);

        try {
            parseFile(sessionId, path, content, branchId);

            return true;
        } catch (JsError jsError) {
            System.err.println(path);
            jsError.printStackTrace();

            return false;
        }
    }

    public boolean modify(String sessionId, String path, String content, String branchId, String commitHash) {
        remove(sessionId, path, branchId, commitHash);
        return add(sessionId, path, content, branchId, commitHash);
    }

    public boolean remove(String sessionId, String path, String branchId, String commitHash) {
        return removeFile(sessionId, path, branchId, commitHash);
    }

    protected void parseFile(String sessionId, String path, String content, String branchId) throws JsError {
        Stopwatch stopwatch = Stopwatch.createStarted();

        ParserWithLocation parser = new ParserWithLocation();
        Module module = parser.parseModule(content);

        long parseDone = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        stopwatch.reset();

        GlobalScope scope = ScopeAnalyzer.analyze(module);

        long scopeDone = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        stopwatch.reset();

        GraphIterator iterator = new GraphIterator(DbServicesManager.getDbServices(branchId), path, parser);
        iterator.iterate(scope, sessionId);

        long graphDone = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        stopwatch.reset();

        logger.info(path + " PARSE " + parseDone);
        logger.info(path + " SCOPE " + scopeDone);
        logger.info(path + " GRAPH " + graphDone);
    }

    protected boolean removeFile(String sessionId, String path, String branchId, String commitHash) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("path", path);
        parameters.put("sessionid", sessionId);

        final DbServices dbServices = DbServicesManager.getDbServices(branchId);
        try (Transaction tx = dbServices.beginTx()) {
            setCommitHash(dbServices, tx, branchId, commitHash);
            dbServices.execute(REMOVE_FILE, parameters);
            tx.success();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void setCommitHash(DbServices dbServices, Transaction tx, String branchId, String commitHash) {
        if (commitHash == null) {
            return;
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("commithash", commitHash);

        dbServices.execute(SET_COMMIT_HASH, parameters);
    }

    private void setCommitHashInNewTransaction(String branchId, String commitHash) {
        final DbServices dbServices = DbServicesManager.getDbServices(branchId);

        try (Transaction tx = dbServices.beginTx()) {
            setCommitHash(dbServices, tx, branchId, commitHash);
            tx.success();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
