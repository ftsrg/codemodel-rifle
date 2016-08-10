package hu.bme.mit.codemodel.rifle.resources;

import org.apache.commons.io.FileUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by steindani on 07/19/16.
 */
@Path("import")
public class ImportDirectory {

    private static final String[] extensions = new String[]{"js"};

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response handle(
            @QueryParam("sessionid") String sessionid,
            @QueryParam("path") String path,
            String content,

            @DefaultValue("master")
            @QueryParam("branchid") String branchid
    ) {
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

        return Response.ok(builder.toString()).build();
    }

}
