package hu.bme.mit.codemodel.rifle.visualization.impex;

import com.google.common.base.Stopwatch;
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
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public abstract class ImpexTestCase extends TestCase {
    private static final Logger logger = Logger.getLogger("codemodel");

    ImpexTestCase() {
        this.testFolderPrefix = "impex";
    }

    @Before
    @After
    public void deleteDb() {
        new DeleteGraph().delete(branchId);
    }

    protected void doImportExportAndVisualization(String path) {
        DbServices dbServices = DbServicesManager.getDbServices(branchId);
        DbServiceDecorator dbServicesDecorator = new DbServiceDecorator(dbServices.getUnderlyingDatabaseService());

        SynchronizeRepository synchronizeRepository = new SynchronizeRepository(path, branchId, sessionId);
        synchronizeRepository.sync();

        File beforePng = null;
        File afterPng = null;

        ExportGraph eg = new ExportGraph(dbServicesDecorator);

        String[] testNameSplit = path.split(File.separator);
        String testName = testNameSplit[testNameSplit.length - 1];

        try {
            beforePng = new File(path + File.separator + testName + "_before.png");
            if (!beforePng.exists()) {
                beforePng.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stopwatch stopwatch = Stopwatch.createUnstarted();

        try (FileOutputStream fop = new FileOutputStream(beforePng)) {
            eg.setOutputStream(fop);
            stopwatch.start();
            eg.png(branchId, -1, true, false);
            long exportedBefore = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            logger.info(String.format("%s %dms", "EXPORTBEFORE", exportedBefore));
            stopwatch.reset();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        ImportExport importExport = new ImportExport();
        stopwatch.start();
        importExport.importExport(branchId);
        long impex = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        stopwatch.reset();
        logger.info(String.format("%s %dms", "IMPEX", impex));


        try {
            afterPng = new File(path + File.separator + testName + "_after.png");
            if (!afterPng.exists()) {
                afterPng.createNewFile();
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        try (FileOutputStream fop = new FileOutputStream(afterPng)) {
            eg.setOutputStream(fop);
            stopwatch.start();
            eg.png(branchId, -1, true, false);
            long exportedAfter = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            stopwatch.reset();
            logger.info(String.format("%s %dms", "EXPORTAFTER", exportedAfter));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
