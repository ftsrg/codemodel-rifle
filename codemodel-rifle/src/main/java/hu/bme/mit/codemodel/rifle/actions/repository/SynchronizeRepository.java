package hu.bme.mit.codemodel.rifle.actions.repository;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

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

        try (Session session = dbServices.getDriver().session()) {
            try (Transaction tx = session.beginTransaction()) {
                for (File file : files) {
                    String contents = FileUtils.readFileToString(file);
                    handleChange.add(sessionId, file.getAbsolutePath(), contents, branchId, null, tx);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
