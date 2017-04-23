package hu.bme.mit.codemodel.rifle.visualization.qualifier;

import org.junit.Test;

public class ComplexTests extends QualifierSystemTestCase {
    @Test
    public void complexTestOne() {
        String path = this.getTestResourcesFolderPath("complexTestOne");
        this.doQualificationAndVisualization(path);
    }

    @Test
    public void complexTestTwo() {
        String path = this.getTestResourcesFolderPath("complexTestTwo");
        this.doQualificationAndVisualization(path);
    }
}
