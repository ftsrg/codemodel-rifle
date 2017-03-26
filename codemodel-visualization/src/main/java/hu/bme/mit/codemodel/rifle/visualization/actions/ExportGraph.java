package hu.bme.mit.codemodel.rifle.visualization.actions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import hu.bme.mit.codemodel.rifle.visualization.database.CfgWalker;
import hu.bme.mit.codemodel.rifle.visualization.database.SimpleWalker;
import hu.bme.mit.codemodel.rifle.visualization.database.SubgraphWalker;
import hu.bme.mit.codemodel.rifle.visualization.utils.NewlineFilterStream;
import org.neo4j.graphdb.Transaction;
import org.neo4j.visualization.graphviz.GraphvizWriter;
import org.neo4j.walk.Walker;

import com.google.common.io.ByteStreams;

import hu.bme.mit.codemodel.rifle.visualization.database.DbServiceDecorator;

public class ExportGraph {

    private DbServiceDecorator dbServices;

    public ExportGraph(DbServiceDecorator dbServices) {
        this.dbServices = dbServices;
    }

    OutputStream out;
    final GraphvizWriter writer = new GraphvizWriter();

    public void setOutputStream(OutputStream f) {
        this.out = f;
    }

    public void full(String branchid) throws IOException {
        Transaction transaction = dbServices.beginTx();
        writer.emit(out, Walker.fullGraph(dbServices.getGraphDb()));
    }

    public void simple(String branchid) throws IOException {
        Transaction transaction = dbServices.beginTx();
        writer.emit(out, new SimpleWalker(dbServices));
    }

    public void svg(String branchid, long nodeid, boolean simple, boolean cfg) throws IOException {
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

        ByteStreams.copy(bufferedInputStream, out);
    }

    public void dot(String branchid, long nodeid, boolean simple, boolean cfg) throws IOException {
        Transaction transaction = dbServices.beginTx();

        final File dot = File.createTempFile("dot", null);
        dot.deleteOnExit();

        Walker walker;

        if (nodeid != -1) {
            walker = new SubgraphWalker(dbServices, nodeid, simple, cfg);
        } else {
            walker = new SimpleWalker(dbServices);
        }

        writer.emit(out, walker);
    }

    public void png(String branchid, long nodeid, boolean simple, boolean cfg) throws IOException {
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

        ByteStreams.copy(bufferedInputStream, out);
    }

    public void cfg(String branchid) throws IOException {
        Transaction transaction = dbServices.beginTx();

        final File dot = File.createTempFile("dot", null);
        dot.deleteOnExit();

        Walker walker = new CfgWalker(dbServices);

        NewlineFilterStream fileOutputStream = new NewlineFilterStream(new FileOutputStream(dot));
        new GraphvizWriter().emit(fileOutputStream, walker);
        fileOutputStream.close();

        ProcessBuilder builder = new ProcessBuilder("dot", "-Tpng", dot.getAbsolutePath());
        builder.redirectErrorStream(true);
        Process process = builder.start();

        final InputStream inputStream = process.getInputStream();
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        ByteStreams.copy(bufferedInputStream, out);
    }

    public void cfgdot(String branchid) throws IOException {
        Transaction transaction = dbServices.beginTx();

        final File dot = File.createTempFile("dot", null);
        dot.deleteOnExit();

        Walker walker = new CfgWalker(dbServices);

        GraphvizWriter writer = new GraphvizWriter();
        writer.emit(out, walker);
    }

}
