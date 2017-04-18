package hu.bme.mit.codemodel.rifle.analysis;

import hu.bme.mit.codemodel.rifle.TestCase;
import hu.bme.mit.codemodel.rifle.actions.repository.SynchronizeRepository;
import hu.bme.mit.codemodel.rifle.actions.utils.DeleteGraph;
import hu.bme.mit.codemodel.rifle.tasks.ImportExport;
import hu.bme.mit.codemodel.rifle.tasks.RunAnalyses;
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

    protected void syncAndAnalyse(String path) {
        SynchronizeRepository synchronizeRepository = new SynchronizeRepository(path, branchId, sessionId);
        synchronizeRepository.sync();

        ImportExport importExport = new ImportExport();
        importExport.importExport(branchId);

        RunAnalyses runAnalyses = new RunAnalyses();
        runAnalyses.runAnalyses(branchId);
    }
}
