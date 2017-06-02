package q2p.perfomancetests.bytesconfversions;

import java.nio.ByteBuffer;
import java.util.Random;

public final class BytesConversions {
	private static final byte[] toBytesB(int value) {
		return ByteBuffer.allocate(4).putInt(value).array();
	}
	
	private static final byte[] toBytesR(int value) {
		return new byte[] { 
			(byte)(value >> 24),
			(byte)(value >> 16),
			(byte)(value >> 8),
			(byte)value };
	}
	
	private static final int fromBytesB(byte[] bytes, final int offset) {
		return ByteBuffer.wrap(bytes, offset, 4).getInt();
	}
	
	private static final int fromBytesR(byte[] bytes, final int offset) {
		return bytes[offset] << 24 | (bytes[offset+1] & 0xFF) << 16 | (bytes[offset+2] & 0xFF) << 8 | (bytes[offset+3] & 0xFF);
	}
	
	private static final ByteBuffer stb = ByteBuffer.allocateDirect(4);
	private static final void toBytesS(final int value) {
		stb.putInt(value);
		stb.position(0);
	}
	private static final void fromBytesS(final byte[] bytes, final int offset) {
		stb.getInt();
		stb.position(0);
	}

	public static void test() {
		final int[] arr = new int[4*1024*1024];
		final byte[] brr = new byte[4*arr.length];
		final Random r = new Random();
		for(int i = arr.length-1; i != -1; i--) {
			arr[i] = r.nextInt();
			final byte[] bs = toBytesB(arr[i]);
			for(int j = 0; j != 4; j++)
				brr[4*i+j] = bs[j]; 
		}
		
		long start = System.currentTimeMillis();
		for(int i = arr.length-1; i != -1; i--)
			toBytesR(arr[i]);
		start = System.currentTimeMillis() - start;
		System.out.println("raw to: " + start);
		
		start = System.currentTimeMillis();
		for(int i = arr.length-1; i != -1; i--)
			fromBytesR(brr, 4*i);
		start = System.currentTimeMillis() - start;
		System.out.println("raw from: " + start);
		
		start = System.currentTimeMillis();
		for(int i = arr.length-1; i != -1; i--)
			toBytesB(arr[i]);
		start = System.currentTimeMillis() - start;
		System.out.println("buff to: " + start);
		
		start = System.currentTimeMillis();
		for(int i = arr.length-1; i != -1; i--)
			fromBytesB(brr, 4*i);
		start = System.currentTimeMillis() - start;
		System.out.println("buff from: " + start);
		
		start = System.currentTimeMillis();
		for(int i = arr.length-1; i != -1; i--)
			toBytesS(arr[i]);
		start = System.currentTimeMillis() - start;
		System.out.println("stat to: " + start);
		
		start = System.currentTimeMillis();
		for(int i = arr.length-1; i != -1; i--)
			fromBytesS(brr, 4*i);
		start = System.currentTimeMillis() - start;
		System.out.println("stat from: " + start);
	}
}