package com.twan.xposedbase.hook;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class FilterXpInputStream extends  InputStream {
    private final ByteArrayOutputStreamUtils mBuffer = new ByteArrayOutputStreamUtils();
    private final byte[] mReadBuffer = new byte[6];
    private final InputStream mStream;
    private final byte[] mXposedBytes = "xposed".getBytes();

    public FilterXpInputStream(InputStream stream) {
        this.mStream = stream;
    }

    public int read() throws IOException {
        int read = this.mStream.read();
        if (read != -1) {
            byte b = (byte) read;
            this.mReadBuffer[0] = this.mReadBuffer[1];
            this.mReadBuffer[1] = this.mReadBuffer[2];
            this.mReadBuffer[2] = this.mReadBuffer[3];
            this.mReadBuffer[3] = this.mReadBuffer[4];
            this.mReadBuffer[4] = this.mReadBuffer[5];
            this.mReadBuffer[5] = b;
            if (Arrays.equals(this.mReadBuffer, this.mXposedBytes)) {
                return 97;
            }
        }
        return read;
    }

    public int read( byte[] b, int off, int len) throws IOException {
        int read = this.mStream.read(b, off, len);
        if (read != -1) {
            this.mBuffer.write(this.mReadBuffer);
            this.mBuffer.write(b, 0, read);
            int l = 0;
            while (true) {
                int indexOfBuff = this.mBuffer.indexOfBuff(this.mXposedBytes, l, this.mBuffer.size());
                int index = indexOfBuff;
                if (indexOfBuff <= -1) {
                    break;
                }
                l = index + this.mXposedBytes.length;
                this.mBuffer.getBuff()[index] = 120;
                this.mBuffer.getBuff()[index + 1] = 120;
                this.mBuffer.getBuff()[index + 2] = 120;
                this.mBuffer.getBuff()[index + 3] = 120;
                this.mBuffer.getBuff()[index + 4] = 120;
                this.mBuffer.getBuff()[index + 5] = 120;
            }
            if (read == 1) {
                this.mReadBuffer[0] = this.mReadBuffer[1];
                this.mReadBuffer[1] = this.mReadBuffer[2];
                this.mReadBuffer[2] = this.mReadBuffer[3];
                this.mReadBuffer[3] = this.mReadBuffer[4];
                this.mReadBuffer[4] = this.mReadBuffer[5];
                this.mReadBuffer[5] = b[read - 1];
            } else if (read == 2) {
                this.mReadBuffer[0] = this.mReadBuffer[2];
                this.mReadBuffer[1] = this.mReadBuffer[3];
                this.mReadBuffer[2] = this.mReadBuffer[4];
                this.mReadBuffer[3] = this.mReadBuffer[5];
                this.mReadBuffer[4] = b[read - 2];
                this.mReadBuffer[5] = b[read - 1];
            } else if (read == 3) {
                this.mReadBuffer[0] = this.mReadBuffer[3];
                this.mReadBuffer[1] = this.mReadBuffer[4];
                this.mReadBuffer[2] = this.mReadBuffer[5];
                this.mReadBuffer[3] = b[read - 3];
                this.mReadBuffer[4] = b[read - 2];
                this.mReadBuffer[5] = b[read - 1];
            } else if (read == 4) {
                this.mReadBuffer[0] = this.mReadBuffer[4];
                this.mReadBuffer[1] = this.mReadBuffer[5];
                this.mReadBuffer[2] = b[read - 4];
                this.mReadBuffer[3] = b[read - 3];
                this.mReadBuffer[4] = b[read - 2];
                this.mReadBuffer[5] = b[read - 1];
            } else if (read == 5) {
                this.mReadBuffer[0] = this.mReadBuffer[5];
                this.mReadBuffer[1] = b[read - 5];
                this.mReadBuffer[2] = b[read - 4];
                this.mReadBuffer[3] = b[read - 3];
                this.mReadBuffer[4] = b[read - 2];
                this.mReadBuffer[5] = b[read - 1];
            } else if (read == 6 || read > 6) {
                this.mReadBuffer[0] = b[read - 6];
                this.mReadBuffer[1] = b[read - 5];
                this.mReadBuffer[2] = b[read - 4];
                this.mReadBuffer[3] = b[read - 3];
                this.mReadBuffer[4] = b[read - 2];
                this.mReadBuffer[5] = b[read - 1];
            }
            read = this.mBuffer.size() - 6;
            System.out.println(read);
            if (read > 0) {
                System.arraycopy(this.mBuffer.getBuff(), 6, b, 0, read);
            }
        }
        this.mBuffer.releaseCache();
        return read;
    }

    public long skip(long n) throws IOException {
        return this.mStream.skip(n);
    }

    public int available() throws IOException {
        return this.mStream.available();
    }

    public void close() throws IOException {
        this.mStream.close();
    }

    public synchronized void mark(int readlimit) {
        this.mStream.mark(readlimit);
    }

    public synchronized void reset() throws IOException {
        this.mStream.reset();
    }

    public boolean markSupported() {
        return this.mStream.markSupported();
    }
}
