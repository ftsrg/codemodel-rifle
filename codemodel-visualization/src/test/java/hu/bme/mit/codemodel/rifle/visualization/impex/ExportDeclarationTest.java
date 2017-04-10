package hu.bme.mit.codemodel.rifle.visualization.impex;

import org.junit.Test;

public class ExportDeclarationTest extends ImpexTestCase {
    @Test
    public void test_exportDeclaration_importNamespace() {
        String path = this.getTestResourcesFolderPath("test_exportDeclaration_importNamespace");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDeclaration_importName() {
        String path = this.getTestResourcesFolderPath("test_exportDeclaration_importName");
        this.doImportExportAndVisualization(path);
    }

    @Test
    public void test_exportDeclaration_importAlias() {
        String path = this.getTestResourcesFolderPath("test_exportDeclaration_importAlias");
        this.doImportExportAndVisualization(path);
    }
}
