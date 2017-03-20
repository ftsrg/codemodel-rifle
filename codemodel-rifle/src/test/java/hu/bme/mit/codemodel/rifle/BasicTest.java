package hu.bme.mit.codemodel.rifle;

import org.junit.Test;

import hu.bme.mit.codemodel.rifle.actions.imports.ImportDirectory;
import hu.bme.mit.codemodel.rifle.actions.imports.ImportExport;
import hu.bme.mit.codemodel.rifle.actions.utils.CountNodes;
import hu.bme.mit.codemodel.rifle.actions.utils.DeleteGraph;

public class BasicTest {

	@Test
	public void test() {
		String path = "src/test/resources";
		String branchId = "master";
		String sessionId = "dummy";

		DeleteGraph deleteGraph = new DeleteGraph();
		deleteGraph.delete(branchId);

        ImportDirectory importDirectory = new ImportDirectory();
        importDirectory.handle(sessionId, path, branchId);

        ImportExport importExport = new ImportExport();
        importExport.importExport(branchId);

        CountNodes countNodes = new CountNodes();
        countNodes.get(branchId);
	}

}
