package hu.bme.mit.codemodel.rifle.analysis;

import hu.bme.mit.codemodel.rifle.TestCase;
import hu.bme.mit.codemodel.rifle.actions.utils.DeleteGraph;
import org.junit.After;
import org.junit.Before;

public class AnalysisTestCase extends TestCase {
    AnalysisTestCase() {
        this.testFolderPrefix = "analysis";
    }

    @Before
    @After
    public void deleteDb() {
        new DeleteGraph().delete(branchId);
    }
}
