package hu.bme.mit.codemodel.rifle;

import org.junit.Test;

import hu.bme.mit.codemodel.rifle.resources.imports.ImportDirectory;
import hu.bme.mit.codemodel.rifle.resources.imports.ImportExport;
import hu.bme.mit.codemodel.rifle.resources.utils.CountNodes;
import hu.bme.mit.codemodel.rifle.resources.utils.DeleteGraph;

public class BasicTest {

	@Test
	public void test() {
		String path = "src/test/resources";
		String branchid = "master";
		String sessionid = "dummy";

		DeleteGraph deleteGraph = new DeleteGraph();
		deleteGraph.delete(branchid);

        ImportDirectory importDirectory = new ImportDirectory();
        importDirectory.handle(sessionid, path, branchid);

        ImportExport importExport = new ImportExport();
        importExport.importExport(branchid);

        CountNodes countNodes = new CountNodes();
        countNodes.get(branchid);
	}

}
