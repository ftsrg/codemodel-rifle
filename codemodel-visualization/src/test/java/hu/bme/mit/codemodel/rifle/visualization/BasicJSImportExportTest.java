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
    public void test1_2() {
        String path = this.getTestResourcesFolderPath("test1_2");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test1_3() {
        String path = this.getTestResourcesFolderPath("test1_3");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test1_4() {
        String path = this.getTestResourcesFolderPath("test1_4");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test1_5() {
        String path = this.getTestResourcesFolderPath("test1_5");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test1_6() {
        String path = this.getTestResourcesFolderPath("test1_6");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test1_9() {
        String path = this.getTestResourcesFolderPath("test1_9");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test2_2() {
        String path = this.getTestResourcesFolderPath("test2_2");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test2_3() {
        String path = this.getTestResourcesFolderPath("test2_3");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test2_4() {
        String path = this.getTestResourcesFolderPath("test2_4");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test2_5() {
        String path = this.getTestResourcesFolderPath("test2_5");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test2_6() {
        String path = this.getTestResourcesFolderPath("test2_6");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test2_9() {
        String path = this.getTestResourcesFolderPath("test2_9");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test3_2() {
        String path = this.getTestResourcesFolderPath("test3_2");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test3_3() {
        String path = this.getTestResourcesFolderPath("test3_3");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test3_4() {
        String path = this.getTestResourcesFolderPath("test3_4");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test3_5() {
        String path = this.getTestResourcesFolderPath("test3_5");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test3_6() {
        String path = this.getTestResourcesFolderPath("test3_6");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test3_9() {
        String path = this.getTestResourcesFolderPath("test3_9");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test4_2() {
        String path = this.getTestResourcesFolderPath("test4_2");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test4_3() {
        String path = this.getTestResourcesFolderPath("test4_3");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test4_4() {
        String path = this.getTestResourcesFolderPath("test4_4");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test4_5() {
        String path = this.getTestResourcesFolderPath("test4_5");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test4_6() {
        String path = this.getTestResourcesFolderPath("test4_6");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test4_9() {
        String path = this.getTestResourcesFolderPath("test4_9");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test5_2() {
        String path = this.getTestResourcesFolderPath("test5_2");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test5_3() {
        String path = this.getTestResourcesFolderPath("test5_3");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test5_4() {
        String path = this.getTestResourcesFolderPath("test5_4");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test5_5() {
        String path = this.getTestResourcesFolderPath("test5_5");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test5_6() {
        String path = this.getTestResourcesFolderPath("test5_6");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test5_9() {
        String path = this.getTestResourcesFolderPath("test5_9");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test6_2() {
        String path = this.getTestResourcesFolderPath("test6_2");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test6_3() {
        String path = this.getTestResourcesFolderPath("test6_3");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test6_4() {
        String path = this.getTestResourcesFolderPath("test6_4");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test6_5() {
        String path = this.getTestResourcesFolderPath("test6_5");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test6_6() {
        String path = this.getTestResourcesFolderPath("test6_6");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test6_9() {
        String path = this.getTestResourcesFolderPath("test6_9");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test7_2() {
        String path = this.getTestResourcesFolderPath("test7_2");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test7_3() {
        String path = this.getTestResourcesFolderPath("test7_3");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test7_4() {
        String path = this.getTestResourcesFolderPath("test7_4");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test7_5() {
        String path = this.getTestResourcesFolderPath("test7_5");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test7_6() {
        String path = this.getTestResourcesFolderPath("test7_6");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test7_9() {
        String path = this.getTestResourcesFolderPath("test7_9");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test9_1() {
        String path = this.getTestResourcesFolderPath("test9_1");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test9_2() {
        String path = this.getTestResourcesFolderPath("test9_2");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test9_9() {
        String path = this.getTestResourcesFolderPath("test9_9");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test10_1() {
        String path = this.getTestResourcesFolderPath("test10_1");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test10_2() {
        String path = this.getTestResourcesFolderPath("test10_2");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test10_9() {
        String path = this.getTestResourcesFolderPath("test10_9");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test11_1() {
        String path = this.getTestResourcesFolderPath("test11_1");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test11_2() {
        String path = this.getTestResourcesFolderPath("test11_2");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test11_9() {
        String path = this.getTestResourcesFolderPath("test11_9");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test12_1() {
        String path = this.getTestResourcesFolderPath("test12_1");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test12_2() {
        String path = this.getTestResourcesFolderPath("test12_2");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test12_9() {
        String path = this.getTestResourcesFolderPath("test12_9");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test13_1() {
        String path = this.getTestResourcesFolderPath("test13_1");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test13_2() {
        String path = this.getTestResourcesFolderPath("test13_2");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test13_9() {
        String path = this.getTestResourcesFolderPath("test13_9");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test14_1() {
        String path = this.getTestResourcesFolderPath("test14_1");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test14_2() {
        String path = this.getTestResourcesFolderPath("test14_2");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test14_9() {
        String path = this.getTestResourcesFolderPath("test14_9");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test15_1() {
        String path = this.getTestResourcesFolderPath("test15_1");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test15_2() {
        String path = this.getTestResourcesFolderPath("test15_2");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test15_9() {
        String path = this.getTestResourcesFolderPath("test15_9");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test16_1() {
        String path = this.getTestResourcesFolderPath("test16_1");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test16_2() {
        String path = this.getTestResourcesFolderPath("test16_2");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test16_7() {
        String path = this.getTestResourcesFolderPath("test16_7");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test16_8() {
        String path = this.getTestResourcesFolderPath("test16_8");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test16_9() {
        String path = this.getTestResourcesFolderPath("test16_9");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test17_1() {
        String path = this.getTestResourcesFolderPath("test17_1");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test17_2() {
        String path = this.getTestResourcesFolderPath("test17_2");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test17_3() {
        String path = this.getTestResourcesFolderPath("test17_3");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test17_4() {
        String path = this.getTestResourcesFolderPath("test17_4");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test17_5() {
        String path = this.getTestResourcesFolderPath("test17_5");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test17_6() {
        String path = this.getTestResourcesFolderPath("test17_6");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test17_7() {
        String path = this.getTestResourcesFolderPath("test17_7");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test17_8() {
        String path = this.getTestResourcesFolderPath("test17_8");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test17_9() {
        String path = this.getTestResourcesFolderPath("test17_9");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test18_2() {
        String path = this.getTestResourcesFolderPath("test18_2");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test18_3() {
        String path = this.getTestResourcesFolderPath("test18_3");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test18_4() {
        String path = this.getTestResourcesFolderPath("test18_4");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test18_5() {
        String path = this.getTestResourcesFolderPath("test18_5");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test18_6() {
        String path = this.getTestResourcesFolderPath("test18_6");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test18_9() {
        String path = this.getTestResourcesFolderPath("test18_9");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test19_2() {
        String path = this.getTestResourcesFolderPath("test19_2");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test19_3() {
        String path = this.getTestResourcesFolderPath("test19_3");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test19_4() {
        String path = this.getTestResourcesFolderPath("test19_4");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test19_5() {
        String path = this.getTestResourcesFolderPath("test19_5");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test19_6() {
        String path = this.getTestResourcesFolderPath("test19_6");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test19_9() {
        String path = this.getTestResourcesFolderPath("test19_9");
        this.doImportExportAndVisualization(path);
    }

}
