package hu.bme.mit.codemodel.rifle.visualization;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.google.common.base.Stopwatch;
import hu.bme.mit.codemodel.rifle.actions.repository.SynchronizeRepository;
import hu.bme.mit.codemodel.rifle.actions.utils.DeleteGraph;
import hu.bme.mit.codemodel.rifle.database.DbServices;
import hu.bme.mit.codemodel.rifle.database.DbServicesManager;
import hu.bme.mit.codemodel.rifle.tasks.ImportExport;
import hu.bme.mit.codemodel.rifle.visualization.actions.ExportGraph;
import hu.bme.mit.codemodel.rifle.visualization.database.DbServiceDecorator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * There are many cases regarding ECMA6 imports and exports.
 * For the complete reference, check:
 * - https://developer.mozilla.org/hu/docs/Web/JavaScript/Reference/Statements/export
 * - https://developer.mozilla.org/hu/docs/Web/JavaScript/Reference/Statements/import
 * <p>
 * For a systematic summary and for decoding the testcases, check:
 * https://docs.google.com/spreadsheets/d/1Du9TV8l2FY-eD3j5LumuNgZCYMAJY6z9pSUoQ_p0D50/edit?usp=sharing
 */
public class BasicJSImportExportTest extends TestCase {
    private static final Logger logger = Logger.getLogger("codemodel");

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

        try {
            beforePng = new File(path + File.separator + "before.png");
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
            afterPng = new File(path + File.separator + "after.png");
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

    @Test
    public void test_exportName_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_exportName_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportName_importName() {
        String path = this.getTestResourcesFolderPath("test_exportName_importName");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportName_importAlias() {
        String path = this.getTestResourcesFolderPath("test_exportName_importAlias");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportAlias_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_exportAlias_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportAlias_importName() {
        String path = this.getTestResourcesFolderPath("test_exportAlias_importName");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportAlias_importAlias() {
        String path = this.getTestResourcesFolderPath("test_exportAlias_importAlias");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDeclaration_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_exportDeclaration_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDeclaration_importName() {
        String path = this.getTestResourcesFolderPath("test_exportDeclaration_importName");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDeclaration_importAlias() {
        String path = this.getTestResourcesFolderPath("test_exportDeclaration_importAlias");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_9_importDefault() {
        String path = this.getTestResourcesFolderPath("test_9_importDefault");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_9_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_9_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_10_importDefault() {
        String path = this.getTestResourcesFolderPath("test_10_importDefault");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_10_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_10_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_11_importDefault() {
        String path = this.getTestResourcesFolderPath("test_11_importDefault");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_11_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_11_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_12_importDefault() {
        String path = this.getTestResourcesFolderPath("test_12_importDefault");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_12_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_12_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_13_importDefault() {
        String path = this.getTestResourcesFolderPath("test_13_importDefault");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_13_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_13_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_14_importDefault() {
        String path = this.getTestResourcesFolderPath("test_14_importDefault");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_14_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_14_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_15_importDefault() {
        String path = this.getTestResourcesFolderPath("test_15_importDefault");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_15_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_15_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_16_importDefault() {
        String path = this.getTestResourcesFolderPath("test_16_importDefault");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_16_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_16_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_17_importDefault() {
        String path = this.getTestResourcesFolderPath("test_17_importDefault");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_17_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_17_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_17_importName() {
        String path = this.getTestResourcesFolderPath("test_17_importName");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_17_importAlias() {
        String path = this.getTestResourcesFolderPath("test_17_importAlias");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_18_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_18_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_18_importName() {
        String path = this.getTestResourcesFolderPath("test_18_importName");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_18_importAlias() {
        String path = this.getTestResourcesFolderPath("test_18_importAlias");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_19_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_19_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_19_importName() {
        String path = this.getTestResourcesFolderPath("test_19_importName");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_19_importAlias() {
        String path = this.getTestResourcesFolderPath("test_19_importAlias");
        this.doImportExportAndVisualization(path);
    }
}
