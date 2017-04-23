package hu.bme.mit.codemodel.rifle.visualization.qualifier;

import org.junit.Test;

public class ImpexTests extends QualifierSystemTestCase {
    @Test
    public void impexTestOne() {
        String path = this.getTestResourcesFolderPath("impexTestOne");
        this.doQualificationAndVisualization(path);
    }
}
