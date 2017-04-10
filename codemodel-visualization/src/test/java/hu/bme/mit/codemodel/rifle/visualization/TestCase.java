package hu.bme.mit.codemodel.rifle.visualization;

import java.io.File;

public abstract class TestCase {
    protected static final String branchId = "dummyTestExport";
    protected static final String sessionId = "dummyTestExport";
    protected String testFolderPrefix = "";

    protected String getTestResourcesFolderPath(String testMethodName) {
        try {
            String testResourceFolderWithinResources = this.testFolderPrefix + File.separator + this.getClass().getSimpleName() +
                File.separator + testMethodName;
            String path = this.getClass().getClassLoader().getResource(testResourceFolderWithinResources).getPath();
            return path;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }
}
