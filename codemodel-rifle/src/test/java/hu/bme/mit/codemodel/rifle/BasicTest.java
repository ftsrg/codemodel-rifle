package hu.bme.mit.codemodel.rifle;

import org.junit.*;

import hu.bme.mit.codemodel.rifle.actions.repository.SynchronizeRepository;
import hu.bme.mit.codemodel.rifle.tasks.ImportExport;
import hu.bme.mit.codemodel.rifle.tasks.CountNodes;
import hu.bme.mit.codemodel.rifle.actions.utils.DeleteGraph;

import static org.junit.Assert.assertTrue;

public class BasicTest extends TestCase {

    @Before
    @After
    public void deleteDb() {
        new DeleteGraph().delete(branchId);
    }

	@Test
	public void test() {
        String path = this.getTestResourcesFolderPath("test");

        SynchronizeRepository synchronizeRepository = new SynchronizeRepository(path, branchId, sessionId);
        synchronizeRepository.sync();

        ImportExport importExport = new ImportExport();
        importExport.importExport(branchId);

        CountNodes countNodes = new CountNodes();
        int nodesCount = countNodes.countAll(branchId);

        assertTrue(nodesCount > 0);
	}

}
