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
 * For a systematic summary and for decoding the testcases, check the documentation.
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

        String[] testNameSplit = path.split(File.separator);
        String testName = testNameSplit[testNameSplit.length - 1];

        try {
            beforePng = new File(path + File.separator + testName + ".png");
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
            afterPng = new File(path + File.separator + testName + ".png");
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
    public void test_exportDefaultExpression_importDefault() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultExpression_importDefault");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDefaultExpression_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultExpression_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDefaultFunction_importDefault() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultFunction_importDefault");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDefaultFunction_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultFunction_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDefaultClass_importDefault() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultClass_importDefault");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDefaultClass_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultClass_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDefaultGenerator_importDefault() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultGenerator_importDefault");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDefaultGenerator_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultGenerator_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDefaultFunctionWithName_importDefault() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultFunctionWithName_importDefault");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDefaultFunctionWithName_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultFunctionWithName_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDefaultClassWithName_importDefault() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultClassWithName_importDefault");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDefaultClassWithName_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultClassWithName_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDefaultGeneratorWithName_importDefault() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultGeneratorWithName_importDefault");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDefaultGeneratorWithName_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultGeneratorWithName_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportAsDefault_importDefault() {
        String path = this.getTestResourcesFolderPath("test_exportAsDefault_importDefault");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportAsDefault_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_exportAsDefault_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_reexportNamespace_importDefault() {
        String path = this.getTestResourcesFolderPath("test_reexportNamespace_importDefault");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_reexportNamespace_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_reexportNamespace_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_reexportNamespace_importName() {
        String path = this.getTestResourcesFolderPath("test_reexportNamespace_importName");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_reexportNamespace_importAlias() {
        String path = this.getTestResourcesFolderPath("test_reexportNamespace_importAlias");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_reexportName_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_reexportName_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_reexportName_importName() {
        String path = this.getTestResourcesFolderPath("test_reexportName_importName");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_reexportName_importAlias() {
        String path = this.getTestResourcesFolderPath("test_reexportName_importAlias");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_reexportAlias_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_reexportAlias_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_reexportAlias_importName() {
        String path = this.getTestResourcesFolderPath("test_reexportAlias_importName");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_reexportAlias_importAlias() {
        String path = this.getTestResourcesFolderPath("test_reexportAlias_importAlias");
        this.doImportExportAndVisualization(path);
    }
}
