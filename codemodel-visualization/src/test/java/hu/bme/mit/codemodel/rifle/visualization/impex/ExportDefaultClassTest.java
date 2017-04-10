package hu.bme.mit.codemodel.rifle.visualization.impex;

import org.junit.Test;

public class ExportDefaultClassTest extends ImpexTestCase {
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
}
