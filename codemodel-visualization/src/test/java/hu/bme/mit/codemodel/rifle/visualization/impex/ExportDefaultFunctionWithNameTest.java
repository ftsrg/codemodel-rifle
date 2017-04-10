package hu.bme.mit.codemodel.rifle.visualization.impex;

import org.junit.Test;

public class ExportDefaultFunctionWithNameTest extends ImpexTestCase {
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
}
