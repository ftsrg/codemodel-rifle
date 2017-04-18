package hu.bme.mit.codemodel.rifle.visualization.analysis;

import org.junit.Test;

public class UnreachableCodeTest extends AnalysisTestCase {
    @Test
    public void test_basic() {
        String path = this.getTestResourcesFolderPath("test_basic");
        this.synchronizeAndAnalyse(path);
    }
}
