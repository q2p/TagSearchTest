package q2p.perfomancetests;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

public final class Storage {
	public static final String readString(final DataInputStream dis) throws Exception {
		final byte[] buff = new byte[dis.readInt()];
		dis.read(buff);
		return new String(buff, Assist.UTF_8);
	}
	public static final void writeString(final DataOutputStream dos, final String string) throws IOException {
		final byte[] buff = string.getBytes(Assist.UTF_8);
		dos.writeInt(buff.length);
		dos.write(buff);
	}
	public static final String getFileContents(final String fileName) {
		try {
			final FileInputStream fis = new FileInputStream(fileName);
			Assist.storeCloseable(fis);
			byte[] buff = new byte[fis.available()];
			fis.read(buff);
			Assist.safeClose(fis);
			
			return new String(buff, StandardCharsets.UTF_8);
		} catch(final IOException e) {
			Assist.abort("Failed to read file: \""+fileName+"\"", e);
			return null;
		}
	}
	public static final String getResFileContents(final String fileName) {
		try {
			final InputStream is = Assist.class.getClassLoader().getResourceAsStream("res/"+fileName);
			if(is == null)
				throw new FileNotFoundException();
			Assist.storeCloseable(is);
			byte[] buff = new byte[is.available()];
			is.read(buff);
			Assist.safeClose(is);
			
			return new String(buff, StandardCharsets.UTF_8);
		} catch(final IOException e) {
			Assist.abort("Failed to read resource file: \""+fileName+"\"", e);
			return null;
		}
	}
	public static final int writeIndexed(final RandomAccessFile raf, final String string) throws IOException {
		final byte[] buff = string.getBytes(StandardCharsets.UTF_8);
		raf.write(buff);
		return buff.length;
	}
	public static final String readIndexed(final RandomAccessFile raf, final int offset, final int edge) throws Exception {
		final byte[] buff = new byte[edge-offset];
		raf.seek(offset);
		raf.readFully(buff);
		return new String(buff, StandardCharsets.UTF_8);
	}
	public static final int[] readInts(final RandomAccessFile raf, final int offset, final int amount) throws IOException {
		final int[] ret = new int[amount];
		final byte[] buff = new byte[4*amount];
		raf.seek(offset);
		raf.readFully(buff);
		ByteBuffer.wrap(buff).asIntBuffer().get(ret);
		return ret;
	}
	public static final int[] readInts(final RandomAccessFile raf, final int amount) throws IOException {
		final int[] ret = new int[amount];
		final byte[] buff = new byte[4*amount];
		raf.readFully(buff);
		ByteBuffer.wrap(buff).asIntBuffer().get(ret);
		return ret;
	}
	public static final char[] readChars(final RandomAccessFile raf, final int amount) throws IOException {
		final char[] ret = new char[amount];
		final byte[] buff = new byte[2*amount];
		raf.readFully(buff);
		ByteBuffer.wrap(buff).asCharBuffer().get(ret);
		return ret;
	}
	public static final int[] readCharsInts(final RandomAccessFile raf, final int chars, int ints) throws IOException {
		final byte[] buff = new byte[2*chars+4*ints];
		final int[] ret = new int[chars+ints];
		raf.readFully(buff);
		ByteBuffer.wrap(buff, 2*chars, 4*ints).asIntBuffer().get(ret, chars, ints);
		CharBuffer cb = ByteBuffer.wrap(buff, 0, 2*chars).asCharBuffer();
		for(int i = 0; i != chars; i++)
			ret[i] = cb.get();
		return ret;
	}
	public static final void writeInts(final RandomAccessFile raf, final int ... ints) throws IOException {
		final ByteBuffer byteBuffer = ByteBuffer.allocate(4*ints.length);
		byteBuffer.asIntBuffer().put(ints);
		raf.write(byteBuffer.array());
	}
}