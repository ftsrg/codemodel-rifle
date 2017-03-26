package hu.bme.mit.codemodel.rifle.visualization;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
 * - https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/import
 *
 * For a systematic summary and for decoding the testcases, check:
 * https://docs.google.com/spreadsheets/d/1Du9TV8l2FY-eD3j5LumuNgZCYMAJY6z9pSUoQ_p0D50/edit?usp=sharing
 */
public class BasicJSImportExportTest extends TestCase {

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

        try (FileOutputStream fop = new FileOutputStream(beforePng)) {
            eg.setOutputStream(fop);
            eg.png(branchId, -1, true, false);
        } catch (IOException|NullPointerException e) {
            e.printStackTrace();
        }

        ImportExport importExport = new ImportExport();
        importExport.importExport(branchId);

        try {
            afterPng = new File(path + File.separator + "after.png");
            if (!afterPng.exists()) {
                afterPng.createNewFile();
            }
        } catch (IOException|NullPointerException e) {
            e.printStackTrace();
        }

        try (FileOutputStream fop = new FileOutputStream(afterPng)) {
            eg.setOutputStream(fop);
            eg.png(branchId, -1, true, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test12() {
        String path = this.getTestResourcesFolderPath("test12");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test13() {
        String path = this.getTestResourcesFolderPath("test13");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test14() {
        String path = this.getTestResourcesFolderPath("test14");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test15() {
        String path = this.getTestResourcesFolderPath("test15");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test16() {
        String path = this.getTestResourcesFolderPath("test16");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test19() {
        String path = this.getTestResourcesFolderPath("test19");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test22() {
        String path = this.getTestResourcesFolderPath("test22");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test23() {
        String path = this.getTestResourcesFolderPath("test23");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test24() {
        String path = this.getTestResourcesFolderPath("test24");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test25() {
        String path = this.getTestResourcesFolderPath("test25");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test26() {
        String path = this.getTestResourcesFolderPath("test26");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test29() {
        String path = this.getTestResourcesFolderPath("test29");
        this.doImportExportAndVisualization(path);
    }

}
