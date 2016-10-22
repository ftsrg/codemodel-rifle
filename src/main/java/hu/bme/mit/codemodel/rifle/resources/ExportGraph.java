package hu.bme.mit.codemodel.rifle.resources;

import hu.bme.mit.codemodel.rifle.resources.utils.CFGWalker;
import hu.bme.mit.codemodel.rifle.resources.utils.NewlineFilterStream;
import hu.bme.mit.codemodel.rifle.resources.utils.SimpleWalker;
import hu.bme.mit.codemodel.rifle.resources.utils.SubgraphWalker;
import hu.bme.mit.codemodel.rifle.utils.DbServices;
import hu.bme.mit.codemodel.rifle.utils.DbServicesManager;
import org.neo4j.graphdb.Transaction;
import org.neo4j.visualization.graphviz.GraphvizWriter;
import org.neo4j.walk.Walker;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

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
            @QueryParam("branchid") String branchid,
            @DefaultValue("-1")
            @QueryParam("nodeid") long nodeid,

            @DefaultValue("true")
            @QueryParam("simple") boolean simple,
            @DefaultValue("true")
            @QueryParam("cfg") boolean cfg
    ) {
        try {
            final DbServices dbServices = DbServicesManager.getDbServices(branchid);
            Transaction transaction = dbServices.beginTx();

            final File dot = File.createTempFile("dot", null);
            dot.deleteOnExit();

            Walker walker;

            if (nodeid != -1) {
                walker = new SubgraphWalker(dbServices, nodeid, simple, cfg);
            } else {
                walker = new SimpleWalker(dbServices);
            }

            NewlineFilterStream fileOutputStream = new NewlineFilterStream(new FileOutputStream(dot));
            new GraphvizWriter().emit(fileOutputStream, walker);
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
    @Produces(MediaType.TEXT_PLAIN)
    @Path("dot")
    public Response dot(
            @DefaultValue("master")
            @QueryParam("branchid") String branchid,
            @DefaultValue("-1")
            @QueryParam("nodeid") long nodeid,

            @DefaultValue("true")
            @QueryParam("simple") boolean simple,
            @DefaultValue("true")
            @QueryParam("cfg") boolean cfg
    ) {
        try {
            final DbServices dbServices = DbServicesManager.getDbServices(branchid);
            Transaction transaction = dbServices.beginTx();

            final File dot = File.createTempFile("dot", null);
            dot.deleteOnExit();

            Walker walker;

            if (nodeid != -1) {
                walker = new SubgraphWalker(dbServices, nodeid, simple, cfg);
            } else {
                walker = new SimpleWalker(dbServices);
            }

            StreamingOutput stream = output -> {
                GraphvizWriter writer = new GraphvizWriter();
                writer.emit(output, walker);
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
            @QueryParam("branchid") String branchid,
            @DefaultValue("-1")
            @QueryParam("nodeid") long nodeid,

            @DefaultValue("true")
            @QueryParam("simple") boolean simple,
            @DefaultValue("true")
            @QueryParam("cfg") boolean cfg
    ) {
        try {
            final DbServices dbServices = DbServicesManager.getDbServices(branchid);
            Transaction transaction = dbServices.beginTx();

            final File dot = File.createTempFile("dot", null);
            dot.deleteOnExit();

            Walker walker;

            if (nodeid != -1) {
                walker = new SubgraphWalker(dbServices, nodeid, simple, cfg);
            } else {
                walker = new SimpleWalker(dbServices);
            }

            NewlineFilterStream fileOutputStream = new NewlineFilterStream(new FileOutputStream(dot));
            new GraphvizWriter().emit(fileOutputStream, walker);
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

    @GET
    @Produces("image/png")
    @Path("cfg")
    public Response cfg(
            @DefaultValue("master")
            @QueryParam("branchid") String branchid
    ) {
        try {
            final DbServices dbServices = DbServicesManager.getDbServices(branchid);
            Transaction transaction = dbServices.beginTx();

            final File dot = File.createTempFile("dot", null);
            dot.deleteOnExit();

            Walker walker = new CFGWalker(dbServices);

            NewlineFilterStream fileOutputStream = new NewlineFilterStream(new FileOutputStream(dot));
            new GraphvizWriter().emit(fileOutputStream, walker);
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

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("cfgdot")
    public Response cfgdot(
            @DefaultValue("master")
            @QueryParam("branchid") String branchid
    ) {
        try {
            final DbServices dbServices = DbServicesManager.getDbServices(branchid);
            Transaction transaction = dbServices.beginTx();

            final File dot = File.createTempFile("dot", null);
            dot.deleteOnExit();

            StreamingOutput stream = output -> {

                Walker walker = new CFGWalker(dbServices);
                NewlineFilterStream fileOutputStream = new NewlineFilterStream(output);

                GraphvizWriter writer = new GraphvizWriter();
                writer.emit(fileOutputStream, walker);

            };

            return Response.ok(stream).build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Response.serverError().build();
    }
}