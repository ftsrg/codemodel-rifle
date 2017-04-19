package hu.bme.mit.codemodel.rifle.visualization.impex;

import org.junit.Test;

public class ExportDefaultNameTest extends ImpexTestCase {
    @Test
    public void test_exportDefaultName_importAlias() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultName_importAlias");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDefaultName_importDefault() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultName_importDefault");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDefaultName_importName() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultName_importName");
        this.doImportExportAndVisualization(path);
    }
}
