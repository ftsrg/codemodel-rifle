package hu.bme.mit.codemodel.rifle.visualization.impex;

import org.junit.Test;

public class ExportDefaultClassWithNameTest extends ImpexTestCase {
    @Test
    public void test_exportDefaultClassWithName_importAlias() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultClassWithName_importAlias");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDefaultClassWithName_importDefault() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultClassWithName_importDefault");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDefaultClassWithName_importName() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultClassWithName_importName");
        this.doImportExportAndVisualization(path);
    }
}
