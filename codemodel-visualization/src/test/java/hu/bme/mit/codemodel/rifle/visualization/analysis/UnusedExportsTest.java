package hu.bme.mit.codemodel.rifle.visualization.analysis;

import org.junit.Test;

public class UnusedExportsTest extends AnalysisTestCase {
    @Test
    public void exportName() {
        String path = this.getTestResourcesFolderPath("exportName");
        this.synchronizeAndAnalyse(path);
    }
}
