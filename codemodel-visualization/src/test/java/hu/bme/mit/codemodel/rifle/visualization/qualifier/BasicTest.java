package hu.bme.mit.codemodel.rifle.visualization.qualifier;

import org.junit.Test;

public class BasicTest extends QualifierSystemTestCase {
    @Test
    public void basicTest() {
        String path = this.getTestResourcesFolderPath("basicTest");
        this.doQualificationAndVisualization(path);
    }

    @Test
    public void basicExceptionTest() {
        String path = this.getTestResourcesFolderPath("basicExceptionTest");
        this.doQualificationAndVisualization(path);
    }
}
