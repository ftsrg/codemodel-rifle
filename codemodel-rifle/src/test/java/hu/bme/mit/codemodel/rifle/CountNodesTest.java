package hu.bme.mit.codemodel.rifle;

import com.google.common.base.Stopwatch;
import hu.bme.mit.codemodel.rifle.TestCase;
import hu.bme.mit.codemodel.rifle.actions.repository.SynchronizeRepository;
import hu.bme.mit.codemodel.rifle.actions.utils.DeleteGraph;
import hu.bme.mit.codemodel.rifle.tasks.CountNodes;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CountNodesTest extends TestCase {

    @Before
    @After
    public void deleteDb() {
        new DeleteGraph().delete(branchId);
    }

    @Test
    public void test1() {
        String path = this.getTestResourcesFolderPath("test1");

        Stopwatch stopwatch = Stopwatch.createStarted();

        SynchronizeRepository synchronizeRepository = new SynchronizeRepository(path, branchId, sessionId);
        synchronizeRepository.sync();

        long syncDone = stopwatch.elapsed(TimeUnit.MILLISECONDS);

        System.out.println(path);
        System.out.println("SynchronizeRepository");
        System.out.println(syncDone);

        stopwatch.reset();

        CountNodes countNodes = new CountNodes();
        int nodesCount = countNodes.countAll(branchId);

        long countDone = stopwatch.elapsed(TimeUnit.MILLISECONDS);

        System.out.println(path);
        System.out.println("CountNodes");
        System.out.println(countDone);

        assertTrue(nodesCount > 0);
    }

    @Test
    public void test2() {
        String path = this.getTestResourcesFolderPath("test2");
        File testFolder = new File(path);
        int filesNumber = FileUtils.listFiles(testFolder, new String[]{ "js" }, true).size();

        SynchronizeRepository synchronizeRepository = new SynchronizeRepository(path, branchId, sessionId);

        Stopwatch stopwatch = Stopwatch.createStarted();
        synchronizeRepository.sync();
        long syncDone = stopwatch.elapsed(TimeUnit.MILLISECONDS);

        System.out.println(path);
        System.out.println("SynchronizeRepository");
        System.out.println(syncDone);

        stopwatch.reset();

        CountNodes countNodes = new CountNodes();
        int compilationUnitNodesCount = countNodes.countCompilationUnitNodes(branchId);

        long countDone = stopwatch.elapsed(TimeUnit.MILLISECONDS);

        System.out.println(path);
        System.out.println("CountNodes");
        System.out.println(countDone);

        assertEquals(filesNumber, compilationUnitNodesCount);
    }

    @Test
    public void test3() {
        String path = this.getTestResourcesFolderPath("test3");
        File testFolder = new File(path);
        int filesNumber = FileUtils.listFiles(testFolder, new String[]{ "js" }, true).size();

        Stopwatch stopwatch = Stopwatch.createStarted();

        SynchronizeRepository synchronizeRepository = new SynchronizeRepository(path, branchId, sessionId);
        synchronizeRepository.sync();

        long syncDone = stopwatch.elapsed(TimeUnit.MILLISECONDS);

        System.out.println(path);
        System.out.println("SynchronizeRepository");
        System.out.println(syncDone);

        stopwatch.reset();

        CountNodes countNodes = new CountNodes();
        int compilationUnitNodesCount = countNodes.countCompilationUnitNodes(branchId);

        long countDone = stopwatch.elapsed(TimeUnit.MILLISECONDS);

        System.out.println(path);
        System.out.println("CountNodes");
        System.out.println(countDone);

        assertEquals(filesNumber, compilationUnitNodesCount);
    }

}
