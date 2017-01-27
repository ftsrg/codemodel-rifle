package hu.bme.mit.codemodel.rifle;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import hu.bme.mit.codemodel.rifle.resources.imports.HandleChange;
import hu.bme.mit.codemodel.rifle.utils.DbServices;
import hu.bme.mit.codemodel.rifle.utils.DbServicesManager;
import hu.bme.mit.codemodel.rifle.utils.ResourceReader;

public class BasicTest {

	private static final String[] extensions = new String[]{"js"};
	private static final String IMPORT_EXPORT = ResourceReader.query("basicimport");
    private static final String COUNT_NODES = ResourceReader.query("countnodes");

	@Test
	public void test() {
		String path = "src/test/resources";
		String branchid = "master";
		String sessionid = "dummy";

        HandleChange handleChange = new HandleChange();
        Collection<File> files = FileUtils.listFiles(new File(path), extensions, true);

        StringBuilder builder = new StringBuilder();

        for (File file : files) {
            builder.append(file.getAbsolutePath());

            try {
                String c = FileUtils.readFileToString(file);
                handleChange.modify(sessionid, file.getAbsolutePath(), c, branchid, null);

                builder.append(" SUCCESS\n");
            } catch (IOException e) {
                e.printStackTrace();

                builder.append("\nERROR\n");
                builder.append(e.toString());
            }
        }
        System.out.println(builder.toString());

        //

		final DbServices dbServices = DbServicesManager.getDbServices(branchid);

        try (Transaction tx = dbServices.beginTx()) {
            Result result = dbServices.execute(IMPORT_EXPORT);
            tx.success();
            System.out.println(result.resultAsString());
        }

        //

        try (Transaction tx = dbServices.beginTx()) {
            Result result = dbServices.execute(COUNT_NODES);
            tx.success();
            System.out.println(result.resultAsString());
        }
	}

}
