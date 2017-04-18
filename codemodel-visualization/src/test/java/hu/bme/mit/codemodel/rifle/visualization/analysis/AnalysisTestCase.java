package hu.bme.mit.codemodel.rifle.visualization.analysis;

import hu.bme.mit.codemodel.rifle.actions.repository.SynchronizeRepository;
import hu.bme.mit.codemodel.rifle.actions.utils.DeleteGraph;
import hu.bme.mit.codemodel.rifle.database.DbServices;
import hu.bme.mit.codemodel.rifle.database.DbServicesManager;
import hu.bme.mit.codemodel.rifle.tasks.ImportExport;
import hu.bme.mit.codemodel.rifle.visualization.TestCase;
import hu.bme.mit.codemodel.rifle.visualization.actions.ExportGraph;
import hu.bme.mit.codemodel.rifle.visualization.database.DbServiceDecorator;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class AnalysisTestCase extends TestCase {
    private static final Logger logger = Logger.getLogger("codemodel");

    AnalysisTestCase() {
        this.testFolderPrefix = "analysis";
    }

    @Before
    @After
    public void deleteDb() {
        new DeleteGraph().delete(branchId);
    }

    protected void synchronizeAndAnalyse(String path) {
        DbServices dbServices = DbServicesManager.getDbServices(branchId);
        DbServiceDecorator dbServiceDecorator = new DbServiceDecorator(dbServices.getUnderlyingDatabaseService());

        SynchronizeRepository synchronizeRepository = new SynchronizeRepository(path, branchId, sessionId);
        synchronizeRepository.sync();

        ImportExport importExport = new ImportExport();
        importExport.importExport(branchId);

        File exportedPng = null;

        String[] testNameSplit = path.split(File.separator);
        String testName = testNameSplit[testNameSplit.length - 1];

        try {
            exportedPng = new File(path + File.separator + testName + ".png");
            if (!exportedPng.exists()) {
                exportedPng.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ExportGraph eg = new ExportGraph(dbServiceDecorator);

        try (FileOutputStream fop = new FileOutputStream(exportedPng)) {
            eg.setOutputStream(fop);
            eg.png(branchId, -1, true, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
