package hu.bme.mit.codemodel.rifle.visualization.impex;

import org.junit.Test;

public class ExportNameTest extends ImpexTestCase {
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
}
