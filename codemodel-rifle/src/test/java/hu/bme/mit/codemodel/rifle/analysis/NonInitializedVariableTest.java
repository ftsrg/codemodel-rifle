package hu.bme.mit.codemodel.rifle.analysis;

import org.junit.Test;

public class NonInitializedVariableTest extends AnalysisTestCase {
    @Test
    public void asDefaultImport() {
        String path = this.getTestResourcesFolderPath("asDefaultImport");
        this.syncAndAnalyse(path);
    }

    @Test
    public void asDirectFunctionArgument() {
        String path = this.getTestResourcesFolderPath("asDirectFunctionArgument");
        this.syncAndAnalyse(path);
    }

    @Test
    public void asImportedVariable() {
        String path = this.getTestResourcesFolderPath("asImportedVariable");
        this.syncAndAnalyse(path);
    }
}
