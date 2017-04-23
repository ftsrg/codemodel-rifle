package hu.bme.mit.codemodel.rifle.analysis;

import org.junit.Test;

public class DivisionByZeroTest extends AnalysisTestCase {
    @Test
    public void simpleConstant() {
        String path = this.getTestResourcesFolderPath("simpleConstant");
        this.synchronizeAndAnalyse(path);
    }

    @Test
    public void nestedFunction() {
        String path = this.getTestResourcesFolderPath("nestedFunction");
        this.synchronizeAndAnalyse(path);
    }

    @Test
    public void importedFunction() {
        String path = this.getTestResourcesFolderPath("importedFunction");
        this.synchronizeAndAnalyse(path);
    }
}
