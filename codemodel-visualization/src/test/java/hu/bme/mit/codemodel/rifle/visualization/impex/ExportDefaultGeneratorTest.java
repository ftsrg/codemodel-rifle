package hu.bme.mit.codemodel.rifle.visualization.impex;

import org.junit.Test;

public class ExportDefaultGeneratorTest extends ImpexTestCase {
    @Test
    public void test_exportDefaultGenerator_importDefault() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultGenerator_importDefault");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDefaultGenerator_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultGenerator_importNamespace");
        this.doImportExportAndVisualization(path);
    }
}
