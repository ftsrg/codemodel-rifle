package hu.bme.mit.codemodel.rifle.analysis;

import org.junit.Test;

public class NonInitializedVariableTest extends AnalysisTestCase {
    @Test
    public void asDirectFunctionArgument() {
        String path = this.getTestResourcesFolderPath("asDirectFunctionArgument");
        this.syncAndAnalyse(path);
    }
}
