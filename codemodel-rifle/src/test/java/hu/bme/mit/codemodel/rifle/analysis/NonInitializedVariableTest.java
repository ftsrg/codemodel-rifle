package hu.bme.mit.codemodel.rifle.analysis;

import org.junit.Test;

public class NonInitializedVariableTest extends AnalysisTestCase {
    @Test
    public void asDefaultImport() {
        String path = this.getTestResourcesFolderPath("asDefaultImport");
        this.synchronizeAndAnalyse(path);
    }

    @Test
    public void asDirectFunctionArgument() {
        String path = this.getTestResourcesFolderPath("asDirectFunctionArgument");
        this.synchronizeAndAnalyse(path);
    }

    @Test
    public void asImportedVariable() {
        String path = this.getTestResourcesFolderPath("asImportedVariable");
        this.synchronizeAndAnalyse(path);
    }
}
