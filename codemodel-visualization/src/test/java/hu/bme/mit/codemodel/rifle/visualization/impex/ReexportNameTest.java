package hu.bme.mit.codemodel.rifle.visualization.impex;

import org.junit.Test;

public class ReexportNameTest extends ImpexTestCase {
    @Test
    public void test_reexportName_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_reexportName_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_reexportName_importName() {
        String path = this.getTestResourcesFolderPath("test_reexportName_importName");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_reexportName_importAlias() {
        String path = this.getTestResourcesFolderPath("test_reexportName_importAlias");
        this.doImportExportAndVisualization(path);
    }
}
