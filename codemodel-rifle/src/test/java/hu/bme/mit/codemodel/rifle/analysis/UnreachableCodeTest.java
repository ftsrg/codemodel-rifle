package hu.bme.mit.codemodel.rifle.analysis;

import org.junit.Test;

public class UnreachableCodeTest extends AnalysisTestCase {
    @Test
    public void test_basic() {
        String path = this.getTestResourcesFolderPath("test_basic");
        this.synchronizeAndAnalyse(path);
    }

    @Test
    public void testNestedException() {
        String path = this.getTestResourcesFolderPath("testNestedException");
        this.synchronizeAndAnalyse(path);
    }
}
