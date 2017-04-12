package hu.bme.mit.codemodel.rifle.visualization.impex;

import org.junit.Test;

public class ExportDefaultExpressionTest extends ImpexTestCase {
    @Test
    public void test_exportDefaultExpression_importDefault() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultExpression_importDefault");
        this.doImportExportAndVisualization(path);
    }
}
