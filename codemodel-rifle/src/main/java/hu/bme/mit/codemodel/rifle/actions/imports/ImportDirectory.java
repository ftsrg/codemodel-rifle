package hu.bme.mit.codemodel.rifle.actions.imports;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

public class ImportDirectory {

    private static final String[] extensions = new String[]{ "js" };

    public String handle(String sessionId, String path, String branchId) {
        HandleChange handleChange = new HandleChange();
        Collection<File> files = FileUtils.listFiles(new File(path), extensions, true);

        StringBuilder builder = new StringBuilder();

        for (File file : files) {
            builder.append(file.getAbsolutePath());

            try {
                String c = FileUtils.readFileToString(file);
                handleChange.modify(sessionId, file.getAbsolutePath(), c, branchId, null);

                builder.append(" SUCCESS\n");
            } catch (IOException e) {
                e.printStackTrace();

                builder.append("\nERROR\n");
                builder.append(e.toString());
            }
        }

        return builder.toString();
    }

}
