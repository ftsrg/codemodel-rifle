package hu.bme.mit.codemodel.rifle.visualization.impex;

import org.junit.Test;

public class ExportDefaultDeclarationTest extends ImpexTestCase {
    @Test
    public void test_exportDefaultDeclaration_importAlias() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultDeclaration_importAlias");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDefaultDeclaration_importDefault() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultDeclaration_importDefault");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDefaultDeclaration_importName() {
        String path = this.getTestResourcesFolderPath("test_exportDefaultDeclaration_importName");
        this.doImportExportAndVisualization(path);
    }
}
