package hu.bme.mit.codemodel.rifle.visualization;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import hu.bme.mit.codemodel.rifle.actions.repository.SynchronizeRepository;
import hu.bme.mit.codemodel.rifle.actions.utils.DeleteGraph;
import hu.bme.mit.codemodel.rifle.database.DbServices;
import hu.bme.mit.codemodel.rifle.database.DbServicesManager;
import hu.bme.mit.codemodel.rifle.visualization.actions.ExportGraph;
import hu.bme.mit.codemodel.rifle.visualization.database.DbServiceDecorator;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class BasicExportTest extends TestCase {

    @Before
    @After
    public void deleteDb() {
        new DeleteGraph().delete(branchId);
    }

    @Test
    @Ignore
    public void test1() throws IOException {
        DbServices dbServices = DbServicesManager.getDbServices(branchId);
        DbServiceDecorator dbServicesDecorator = new DbServiceDecorator(dbServices.getUnderlyingDatabaseService());
        String path = this.getTestResourcesFolderPath("test1");

        SynchronizeRepository synchronizeRepository = new SynchronizeRepository(path, branchId, sessionId);
        synchronizeRepository.sync();

        ExportGraph eg = new ExportGraph(dbServicesDecorator);

        File exportedFile = new File(path + File.separator + "export.png");
        if (! exportedFile.exists()) {
            exportedFile.createNewFile();
        }

        FileOutputStream fop = new FileOutputStream(exportedFile);

        eg.setOutputStream(fop);
        eg.png(branchId, -1, true, false);
    }

}
