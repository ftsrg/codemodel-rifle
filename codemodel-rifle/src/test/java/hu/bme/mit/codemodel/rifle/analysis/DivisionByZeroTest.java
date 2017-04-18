package hu.bme.mit.codemodel.rifle.analysis;

import org.junit.Test;

public class DivisionByZeroTest extends AnalysisTestCase {
    @Test
    public void simpleConstant() {
        String path = this.getTestResourcesFolderPath("simpleConstant");
        this.syncAndAnalyse(path);
    }
}
