package hu.bme.mit.codemodel.rifle.resources.utils;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by steindani on 7/5/16.
 */
public class NewlineFilterStream extends FilterOutputStream {
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
            out.write('\n');
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
