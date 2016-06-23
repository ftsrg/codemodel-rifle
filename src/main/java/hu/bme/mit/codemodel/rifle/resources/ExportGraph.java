package hu.bme.mit.codemodel.rifle.resources;

import hu.bme.mit.codemodel.rifle.utils.DbServices;
import hu.bme.mit.codemodel.rifle.utils.DbServicesManager;
import org.neo4j.graphdb.*;
import org.neo4j.visualization.graphviz.GraphvizWriter;
import org.neo4j.walk.Visitor;
import org.neo4j.walk.Walker;

import javax.ws.rs.*;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;

/**
 * Created by steindani on 3/2/16.
 */
@Path("export")
public class ExportGraph {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("full")
    public Response full(
            @DefaultValue("master")
            @QueryParam("branchid") String branchid
    ) {
        try {
            final DbServices dbServices = DbServicesManager.getDbServices(branchid);
            Transaction transaction = dbServices.beginTx();

            StreamingOutput stream = output -> {

                GraphvizWriter writer = new GraphvizWriter();
                writer.emit(output, Walker.fullGraph(dbServices.graphDb));

            };

            return Response.ok(stream).build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Response.serverError().build();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("simple")
    public Response simple(
            @DefaultValue("master")
            @QueryParam("branchid") String branchid
    ) {
        try {
            final DbServices dbServices = DbServicesManager.getDbServices(branchid);
            Transaction transaction = dbServices.beginTx();

            StreamingOutput stream = output -> {
                new GraphvizWriter().emit(output, new SimpleWalker(dbServices));
            };

            return Response.ok(stream).build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Response.serverError().build();
    }


    @GET
    @Produces(MediaType.APPLICATION_SVG_XML)
    @Path("svg")
    public Response svg(
            @DefaultValue("master")
            @QueryParam("branchid") String branchid
    ) {
        try {
            final DbServices dbServices = DbServicesManager.getDbServices(branchid);
            Transaction transaction = dbServices.beginTx();

            final File dot = File.createTempFile("dot", null);
            dot.deleteOnExit();

            NewlineFilterStream fileOutputStream = new NewlineFilterStream(new FileOutputStream(dot));
            new GraphvizWriter().emit(fileOutputStream, new SimpleWalker(dbServices));
            fileOutputStream.close();


            ProcessBuilder builder = new ProcessBuilder("dot", "-Tsvg", dot.getAbsolutePath());
            builder.redirectErrorStream(true);
            Process process = builder.start();

            final InputStream inputStream = process.getInputStream();
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            StreamingOutput stream = output -> {
                int read = bufferedInputStream.read();
                while (read != -1) {
                    output.write(read);
                    read = bufferedInputStream.read();
                }
                output.flush();
                bufferedInputStream.close();
            };

            return Response.ok(stream).build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Response.serverError().build();
    }

    @GET
    @Produces("image/png")
    @Path("png")
    public Response png(
            @DefaultValue("master")
            @QueryParam("branchid") String branchid
    ) {
        try {
            final DbServices dbServices = DbServicesManager.getDbServices(branchid);
            Transaction transaction = dbServices.beginTx();

            final File dot = File.createTempFile("dot", null);
            dot.deleteOnExit();

            NewlineFilterStream fileOutputStream = new NewlineFilterStream(new FileOutputStream(dot));
            new GraphvizWriter().emit(fileOutputStream, new SimpleWalker(dbServices));
            fileOutputStream.close();


            ProcessBuilder builder = new ProcessBuilder("dot", "-Tpng", dot.getAbsolutePath());
            builder.redirectErrorStream(true);
            Process process = builder.start();

            final InputStream inputStream = process.getInputStream();
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            StreamingOutput stream = output -> {
                int read = bufferedInputStream.read();
                while (read != -1) {
                    output.write(read);
                    read = bufferedInputStream.read();
                }
                output.flush();
                bufferedInputStream.close();
            };

            return Response.ok(stream).build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Response.serverError().build();
    }

    protected class SimpleWalker extends Walker {
        private final DbServices dbServices;

        public SimpleWalker(DbServices dbServices) {
            this.dbServices = dbServices;
        }

        @Override
        public <R, E extends Throwable> R accept(Visitor<R, E> visitor) throws E {
            for (Node node : dbServices.graphDb.getAllNodes()) {
                if (node.hasLabel(Label.label("CompilationUnit"))) {
                    continue;
                }
                if (node.hasLabel(Label.label("SourceSpan"))) {
                    continue;
                }
                if (node.hasLabel(Label.label("SourceLocation"))) {
                    continue;
                }

                visitor.visitNode(node);
                for (Relationship edge : node.getRelationships(Direction.OUTGOING)) {
                    if (edge.isType(RelationshipType.withName("location"))) {
                        continue;
                    }
                    visitor.visitRelationship(edge);
                }
            }
            return visitor.done();
        }
    }

    class NewlineFilterStream extends FilterOutputStream {
        int buffer = -1;

        /**
         * Creates an output stream filter built on top of the specified
         * underlying output stream.
         *
         * @param out the underlying output stream to be assigned to
         *            the field <tt>this.out</tt> for later use, or
         *            <code>null</code> if this instance is to be
         *            created without an underlying stream.
         */
        public NewlineFilterStream(OutputStream out) {
            super(out);
        }

        @Override
        public void write(int b) throws IOException {
            if (buffer == ',' && b == ' ') {
                out.write('\\');
                out.write('n');
//                out.write('\n');
                buffer = -1;
            } else {
                if (buffer != -1) {
                    out.write(buffer);
                }

                buffer = b;
            }
        }

        @Override
        public void flush() throws IOException {
            if (buffer != -1) {
                out.write(buffer);
            }
            buffer = -1;
            super.flush();
        }
    }
}
