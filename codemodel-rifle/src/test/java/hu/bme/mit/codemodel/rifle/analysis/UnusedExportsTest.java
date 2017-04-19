package hu.bme.mit.codemodel.rifle.analysis;

import org.junit.Test;

public class UnusedExportsTest extends AnalysisTestCase {
    @Test
    public void namedExports() {
        String path = this.getTestResourcesFolderPath("namedExports");
        this.synchronizeAndAnalyse(path);
    }
}
