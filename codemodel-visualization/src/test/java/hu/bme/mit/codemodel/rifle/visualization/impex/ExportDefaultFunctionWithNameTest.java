package hu.bme.mit.codemodel.rifle.visualization.impex;

import org.junit.Test;

public class ExportDefaultFunctionWithNameTest extends ImpexTestCase {
    @Test
    public void test_exportDefaultFunctionWithName_importAlias() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultFunctionWithName_importAlias");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDefaultFunctionWithName_importDefault() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultFunctionWithName_importDefault");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDefaultFunctionWithName_importName() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultFunctionWithName_importName");
        this.doImportExportAndVisualization(path);
    }
}
