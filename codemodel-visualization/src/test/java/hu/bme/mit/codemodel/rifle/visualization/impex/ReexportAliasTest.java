package hu.bme.mit.codemodel.rifle.visualization.impex;

import org.junit.Test;

public class ReexportAliasTest extends ImpexTestCase {
    @Test
    public void test_reexportAlias_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_reexportAlias_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_reexportAlias_importName() {
        String path = this.getTestResourcesFolderPath("test_reexportAlias_importName");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_reexportAlias_importAlias() {
        String path = this.getTestResourcesFolderPath("test_reexportAlias_importAlias");
        this.doImportExportAndVisualization(path);
    }
}
