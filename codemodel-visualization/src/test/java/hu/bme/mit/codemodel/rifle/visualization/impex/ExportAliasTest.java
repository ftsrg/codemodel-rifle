package hu.bme.mit.codemodel.rifle.visualization.impex;

import org.junit.Test;

public class ExportAliasTest extends ImpexTestCase {
    @Test
    public void test_exportAlias_importAlias() {
        String path = this.getTestResourcesFolderPath("test_exportAlias_importAlias");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportAlias_importName() {
        String path = this.getTestResourcesFolderPath("test_exportAlias_importName");
        this.doImportExportAndVisualization(path);
    }
}
