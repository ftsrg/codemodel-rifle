package hu.bme.mit.codemodel.rifle.actions.repository;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.google.common.base.Stopwatch;
import hu.bme.mit.codemodel.rifle.database.DbServices;
import hu.bme.mit.codemodel.rifle.database.DbServicesManager;
import org.apache.commons.io.FileUtils;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;

/**
 * Synchronizes the database to the repository on a given path, branch and session.
 * <p>
 * Temporarily, this means only a recursive directory import without any incrementality.
 * Logic will be replaced with a filelist from a git diff.
 */
public class SynchronizeRepository {
    protected static final String[] extensions = new String[]{ "js" };

    protected final DbServices dbServices;

    private static final Logger logger = Logger.getLogger("codemodel");

    protected final String path;
    protected final String branchId;
    protected final String sessionId;

    public SynchronizeRepository(String path, String branchId, String sessionId) {
        this.path = path;
        this.branchId = branchId;
        this.sessionId = sessionId;

        this.dbServices = DbServicesManager.getDbServices(this.branchId);
    }

    public void sync() {
        HandleChange handleChange = new HandleChange();
        Collection<File> files = FileUtils.listFiles(new File(path), extensions, true);

        Stopwatch stopwatch = Stopwatch.createUnstarted();

        try (Session session = dbServices.getDriver().session()) {
            for (File file : files) {
                Transaction tx = null;

//                Using try-finally instead of try-with-resources to measure commit time
                try {
                    tx = session.beginTransaction();

                    String contents = FileUtils.readFileToString(file);
                    logger.info(String.format("%s (%d bytes)", file.getAbsolutePath(), file.length()));
                    handleChange.add(sessionId, file.getAbsolutePath(), contents, branchId, null, tx);

                    tx.success();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (tx != null) {
                        stopwatch.start();
                        tx.close();
                        long commitDone = stopwatch.elapsed(TimeUnit.MILLISECONDS);
                        logger.info(String.format("COMMIT %dms", commitDone));
                        stopwatch.reset();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
