package hu.bme.mit.codemodel.rifle;

import java.io.File;

public abstract class TestCase {

    protected static final String branchId = "dummyTest";
    protected static final String sessionId = "dummyTest";

    protected String getTestResourcesFolderPath(String testMethodName) {
        try {
            String testResourceFolderWithinResources = this.getClass().getSimpleName() + File.separator +
                testMethodName;
            String path = this.getClass().getClassLoader().getResource(testResourceFolderWithinResources).getPath();
            return path;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

}
