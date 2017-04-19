package hu.bme.mit.codemodel.rifle.visualization.analysis;

import org.junit.Test;

public class UnusedExportsTest extends AnalysisTestCase {
    @Test
    public void exportDeclaration() {
        String path = this.getTestResourcesFolderPath("exportDeclaration");
        this.synchronizeAndAnalyse(path);
    }

    @Test
    public void exportDefault() {
        String path = this.getTestResourcesFolderPath("exportDefault");
        this.synchronizeAndAnalyse(path);
    }

    @Test
    public void exportName() {
        String path = this.getTestResourcesFolderPath("exportName");
        this.synchronizeAndAnalyse(path);
    }
}
