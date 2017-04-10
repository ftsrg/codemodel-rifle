package hu.bme.mit.codemodel.rifle.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Manages resources reading from the resources directory.
 * <p>
 * Separate resource types have their own getters, see {@link #query(String)}.
 */
public class ResourceReader {
    protected static String readFromResource(String fileName) {
        InputStream resourceAsStream = ResourceReader.class.getClassLoader().getResourceAsStream(fileName);

        try {
            return IOUtils.toString(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String query(String queryName) {
        return readFromResource("queries" + File.separator + queryName + ".cypher");
    }

    public static Collection<String> getImportExportQueries() {
        final String[] extensions = new String[]{ "cypher" };

        File importExportQueriesDirectory = new File(ResourceReader.class.getClassLoader().getResource("queries" + File.separator + "impex").getPath());
        Collection<File> importExportQueryFiles = FileUtils.listFiles(importExportQueriesDirectory, extensions, false);
        Collection<String> importExportQueries = new ArrayList<>();

        for (File file : importExportQueryFiles) {
            importExportQueries.add(readFromResource("queries" + File.separator + "impex" + File.separator + file.getName()));
        }

        return importExportQueries;
    }
}
