package hu.bme.mit.codemodel.rifle.visualization.analysis;

import org.junit.Test;

public class NonInitializedVariableTest extends AnalysisTestCase {
    @Test
    public void test_nonInitializedVariable() {
        String path = this.getTestResourcesFolderPath("test_nonInitializedVariable");
        this.synchronizeAndAnalyse(path);
}
}
