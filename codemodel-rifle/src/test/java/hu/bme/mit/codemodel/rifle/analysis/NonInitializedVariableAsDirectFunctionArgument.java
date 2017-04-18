package hu.bme.mit.codemodel.rifle.analysis;

import hu.bme.mit.codemodel.rifle.actions.repository.SynchronizeRepository;
import hu.bme.mit.codemodel.rifle.tasks.ImportExport;
import hu.bme.mit.codemodel.rifle.tasks.RunAnalyses;
import org.junit.Test;

public class NonInitializedVariableAsDirectFunctionArgument extends AnalysisTestCase {
    @Test
    public void test() {
        String path = this.getTestResourcesFolderPath("test");

        SynchronizeRepository synchronizeRepository = new SynchronizeRepository(path, branchId, sessionId);
        synchronizeRepository.sync();

        ImportExport importExport = new ImportExport();
        importExport.importExport(branchId);

        RunAnalyses runAnalyses = new RunAnalyses();
        runAnalyses.runAnalyses(branchId);
    }
}
