package hu.bme.mit.codemodel.rifle.analysis;

import hu.bme.mit.codemodel.rifle.actions.repository.SynchronizeRepository;
import hu.bme.mit.codemodel.rifle.tasks.ImportExport;
import hu.bme.mit.codemodel.rifle.tasks.RunAnalyses;
import org.junit.Test;

public class NonInitializedVariable extends AnalysisTestCase {
    @Test
    public void test1() {
        String path = this.getTestResourcesFolderPath("test1");
        this.syncAndAnalyse(path);
    }
}
