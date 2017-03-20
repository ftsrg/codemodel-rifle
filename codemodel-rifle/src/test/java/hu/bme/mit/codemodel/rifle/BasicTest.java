package hu.bme.mit.codemodel.rifle;

import org.junit.Test;

import hu.bme.mit.codemodel.rifle.actions.repository.SynchronizeRepository;
import hu.bme.mit.codemodel.rifle.tasks.ImportExport;
import hu.bme.mit.codemodel.rifle.tasks.CountNodes;
import hu.bme.mit.codemodel.rifle.actions.utils.DeleteGraph;

public class BasicTest {

	@Test
	public void test() {
		String path = "src/test/resources";
		String branchId = "master";
		String sessionId = "dummy";

		DeleteGraph deleteGraph = new DeleteGraph();
		deleteGraph.delete(branchId);

        SynchronizeRepository synchronizeRepository = new SynchronizeRepository(path, branchId, sessionId);
        synchronizeRepository.sync();

        ImportExport importExport = new ImportExport();
        importExport.importExport(branchId);

        CountNodes countNodes = new CountNodes();
        countNodes.get(branchId);
	}

}
