package hu.bme.mit.codemodel.rifle.visualization.impex;

import org.junit.Test;

public class ExportDefaultGeneratorWithNameTest extends ImpexTestCase {
    @Test
    public void test_exportDefaultGeneratorWithName_importDefault() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultGeneratorWithName_importDefault");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDefaultGeneratorWithName_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultGeneratorWithName_importNamespace");
        this.doImportExportAndVisualization(path);
    }
}
