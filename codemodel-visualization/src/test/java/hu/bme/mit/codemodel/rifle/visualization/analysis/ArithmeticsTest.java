package hu.bme.mit.codemodel.rifle.visualization.analysis;

import org.junit.Test;

public class ArithmeticsTest extends AnalysisTestCase {
    @Test
    public void test_logarithmArgument() {
        String path = this.getTestResourcesFolderPath("test_logarithmArgument");
        this.synchronizeAndAnalyse(path);
    }

    @Test
    public void test_squareRootArgument() {
        String path = this.getTestResourcesFolderPath("test_squareRootArgument");
        this.synchronizeAndAnalyse(path);
    }
}
