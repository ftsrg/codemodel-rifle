package hu.bme.mit.codemodel.rifle.visualization.impex;

import org.junit.Test;

public class ExportDefaultClassWithNameTest extends ImpexTestCase {
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
}
