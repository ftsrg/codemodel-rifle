package hu.bme.mit.codemodel.rifle.visualization.impex;

import org.junit.Test;

public class ReexportNamespaceTest extends ImpexTestCase {
    @Test
    public void test_reexportNamespace_importDefault() {
        String path = this.getTestResourcesFolderPath("test_reexportNamespace_importDefault");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_reexportNamespace_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_reexportNamespace_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_reexportNamespace_importName() {
        String path = this.getTestResourcesFolderPath("test_reexportNamespace_importName");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_reexportNamespace_importAlias() {
        String path = this.getTestResourcesFolderPath("test_reexportNamespace_importAlias");
        this.doImportExportAndVisualization(path);
    }
}
