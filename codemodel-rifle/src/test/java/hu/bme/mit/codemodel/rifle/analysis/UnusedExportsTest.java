package hu.bme.mit.codemodel.rifle.analysis;

import org.junit.Test;

public class UnusedExportsTest extends AnalysisTestCase {
    @Test
    public void exportAlias() {
        String path = this.getTestResourcesFolderPath("exportAlias");
        this.synchronizeAndAnalyse(path);
    }

    @Test
    public void exportName() {
        String path = this.getTestResourcesFolderPath("exportName");
        this.synchronizeAndAnalyse(path);
    }
}
