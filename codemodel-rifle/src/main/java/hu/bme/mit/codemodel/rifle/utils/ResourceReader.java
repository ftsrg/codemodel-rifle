package hu.bme.mit.codemodel.rifle.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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
}
