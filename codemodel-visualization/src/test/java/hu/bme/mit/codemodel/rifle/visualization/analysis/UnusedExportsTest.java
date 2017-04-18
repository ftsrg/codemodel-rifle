package hu.bme.mit.codemodel.rifle.visualization.analysis;

import org.junit.Test;

public class UnusedExportsTest extends AnalysisTestCase {
    @Test
    public void basicTest() {
        String path = this.getTestResourcesFolderPath("basicTest");
        this.synchronizeAndAnalyse(path);
    }
}
