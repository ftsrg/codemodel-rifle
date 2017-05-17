package hu.bme.mit.codemodel.rifle.visualization.analysis;

import org.junit.Test;

public class DivisionByZeroTest extends AnalysisTestCase {
    @Test
    public void test_simpleConstant() {
        String path = this.getTestResourcesFolderPath("test_simpleConstant");
        this.synchronizeAndAnalyse(path);
    }

    @Test
    public void test_function() {
        String path = this.getTestResourcesFolderPath("test_function");
        this.synchronizeAndAnalyse(path);
    }

    @Test
    public void test_runningexample() {
        String path = this.getTestResourcesFolderPath("test_runningexample");
        this.synchronizeAndAnalyse(path);
    }
}
