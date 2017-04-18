package hu.bme.mit.codemodel.rifle;

import java.io.File;

public abstract class TestCase {
    protected static final String branchId = "dummyTestExport";
    protected static final String sessionId = "dummyTestExport";
    protected String testFolderPrefix = "";

    protected String getTestResourcesFolderPath(String testMethodName) {
        try {
            String testResourceFolderWithinResources = this.getClass().getSimpleName() + File.separator +
                testMethodName;

            if (!this.testFolderPrefix.equals("")) {
                testResourceFolderWithinResources = this.testFolderPrefix + File.separator +
                    testResourceFolderWithinResources;
            }

            String path = this.getClass().getClassLoader().getResource(testResourceFolderWithinResources).getPath();
            return path;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }
}
