package hu.bme.mit.codemodel.rifle.visualization.impex;

import org.junit.Test;

public class ExportDefaultFunctionTest extends ImpexTestCase {
    @Test
    public void test_exportDefaultFunction_importDefault() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultFunction_importDefault");
        this.doImportExportAndVisualization(path);
    }
}
