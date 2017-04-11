package hu.bme.mit.codemodel.rifle.visualization.impex;

import org.junit.Test;

public class ExportAsDefaultTest extends ImpexTestCase {
    @Test
    public void test_exportAsDefault_importDefault() {
        String path = this.getTestResourcesFolderPath("test_exportAsDefault_importDefault");
        this.doImportExportAndVisualization(path);
    }
}
