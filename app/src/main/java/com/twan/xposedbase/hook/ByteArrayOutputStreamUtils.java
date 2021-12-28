package com.twan.xposedbase.hook;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class ByteArrayOutputStreamUtils extends OutputStream {
    private static final byte[] mNullByteArray = new byte[0];
    private byte[] mBuffer;
    private int mCount;

    public ByteArrayOutputStreamUtils() {
        this(32);
    }

    public ByteArrayOutputStreamUtils(int size) {
        if (size >= 0) {
            this.mBuffer = new byte[size];
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Negative initial size: ");
        sb.append(size);
        throw new IllegalArgumentException(sb.toString());
    }

    public static int indexOf(byte[] array, byte b, int start, int indexRange) {
        if (array == null || array.length == 0 || start >= indexRange) {
            return -1;
        }
        if (start < 0) {
            start = 0;
        }
        if (indexRange > array.length) {
            indexRange = array.length;
        }
        while (start < indexRange) {
            if (array[start] == b) {
                return start;
            }
            start++;
        }
        return -1;
    }

    public static int lastIndexOf(byte[] array, byte b, int startIndex, int indexRange) {
        if (array == null || array.length == 0 || indexRange > startIndex) {
            return -1;
        }
        if (indexRange < 0) {
            indexRange = 0;
        }
        if (startIndex > array.length - 1) {
            startIndex = array.length - 1;
        }
        while (startIndex >= indexRange) {
            if (array[startIndex] == b) {
                return startIndex;
            }
            startIndex--;
        }
        return -1;
    }

    public static int indexOf(byte[] array, byte[] b, int start, int indexRange) {
        if (array == null || array.length == 0 || start > indexRange || b == null || b.length > array.length || b.length == 0 || (indexRange - start) + 1 < b.length) {
            return -1;
        }
        if (start < 0) {
            start = 0;
        }
        if (indexRange > array.length) {
            indexRange = array.length;
        }
        int i = start;
        while (i < indexRange) {
            if (array[i] == b[0]) {
                if (indexRange - i < b.length) {
                    break;
                }
                int i2 = 1;
                while (i2 < b.length && array[i + i2] == b[i2]) {
                    i2++;
                }
                if (i2 == b.length) {
                    return i;
                }
            }
            i++;
        }
        return -1;
    }

    public static int lastIndexOf(byte[] array, byte[] b, int startIndex, int indexRange) {
        if (array == null || array.length == 0 || indexRange > startIndex || b == null || b.length > array.length || b.length == 0 || (startIndex - indexRange) + 1 < b.length) {
            return -1;
        }
        if (indexRange < 0) {
            indexRange = 0;
        }
        if (startIndex > array.length) {
            startIndex = array.length;
        }
        int i = startIndex == array.length ? array.length - 1 : startIndex;
        while (i >= indexRange) {
            if (array[i] == b[0] && b.length + i <= startIndex) {
                int i2 = 1;
                while (i2 < b.length && array[i + i2] == b[i2]) {
                    i2++;
                }
                if (i2 == b.length) {
                    return i;
                }
            }
            i--;
        }
        return -1;
    }

    public int getSize() {
        return this.mCount;
    }

    public void setSize(int size) {
        if (size > this.mBuffer.length) {
            size = this.mBuffer.length;
        }
        this.mCount = size;
    }

    public int getBuffSize() {
        return this.mBuffer.length;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity - this.mBuffer.length > 0) {
            grow(minCapacity);
        }
    }

    private void grow(int minCapacity) {
        int newCapacity = this.mBuffer.length << 1;
        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }
        if (newCapacity < 0) {
            if (minCapacity >= 0) {
                newCapacity = Integer.MAX_VALUE;
            } else {
                throw new OutOfMemoryError();
            }
        }
        this.mBuffer = Arrays.copyOf(this.mBuffer, newCapacity);
    }

    public void write(int b) {
        ensureCapacity(this.mCount + 1);
        this.mBuffer[this.mCount] = (byte) b;
        this.mCount++;
    }

    public void write(byte[] b, int off, int len) {
        if (off < 0 || off > b.length || len < 0 || (off + len) - b.length > 0) {
            throw new IndexOutOfBoundsException();
        }
        ensureCapacity(this.mCount + len);
        System.arraycopy(b, off, this.mBuffer, this.mCount, len);
        this.mCount += len;
    }

    public void writeTo(OutputStream out) throws IOException {
        out.write(this.mBuffer, 0, this.mCount);
    }

    public void reset() {
        this.mCount = 0;
    }

    public byte[] toByteArray() {
        if (this.mCount == 0) {
            return mNullByteArray;
        }
        return Arrays.copyOf(this.mBuffer, this.mCount);
    }

    public int size() {
        return this.mCount;
    }

    public String toString() {
        return new String(this.mBuffer, 0, this.mCount);
    }

    public String toString(String charsetName) throws UnsupportedEncodingException {
        return new String(this.mBuffer, 0, this.mCount, charsetName);
    }

    @Deprecated
    public String toString(int hibyte) {
        return new String(this.mBuffer, hibyte, 0, this.mCount);
    }

    public void close() {
    }

    public void releaseCache() {
        this.mBuffer = mNullByteArray;
        this.mCount = 0;
    }

    public byte[] getBuff() {
        return this.mBuffer;
    }

    public void seekIndex(int index) {
        setSize(index);
    }

    public int getIndex() {
        return this.mCount;
    }

    public int indexOfBuff(byte b, int start) {
        return indexOf(this.mBuffer, b, start, this.mBuffer.length);
    }

    public int indexOfBuff(byte[] b, int start) {
        return indexOf(this.mBuffer, b, start, this.mBuffer.length);
    }

    public int indexOfBuff(byte b, int start, int end) {
        return indexOf(this.mBuffer, b, start, end);
    }

    public int indexOfBuff(byte[] b, int start, int end) {
        return indexOf(this.mBuffer, b, start, end);
    }

    public int lastIndexOfBuff(byte b, int start) {
        return lastIndexOf(this.mBuffer, b, 0, start);
    }

    public int lastIndexOfBuff(byte[] b, int start) {
        return lastIndexOf(this.mBuffer, b, 0, start);
    }

    public int lastIndexOfBuff(byte b, int start, int end) {
        return lastIndexOf(this.mBuffer, b, start, end);
    }

    public int lastIndexOfBuff(byte[] b, int start, int end) {
        return lastIndexOf(this.mBuffer, b, start, end);
    }
}
