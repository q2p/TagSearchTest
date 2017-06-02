package q2p.perfomancetests.taggingsearch;

import java.io.IOException;
import java.io.RandomAccessFile;
import q2p.perfomancetests.Storage;

public final class TaggingSearch {
	private static int max = 0;
	public static final void test() {
		for(int i = 16; i != 0; i--)
			fill();
		try {
			Thread.sleep(10000);
		} catch(final InterruptedException e) {}
	}
	private static final short stack = 640;
	private static void fill() {
		final long l = System.currentTimeMillis();
		try(final RandomAccessFile raf = new RandomAccessFile("crushed.dat", "r")) {
			final int amount = raf.readInt();
			for(int a = 0; a != amount;) {
				final int block = Math.min(amount-a, stack);
				final int[] bi = Storage.readInts(raf, 2*block);

				int length = 0;
				for(int b = 0; b != block; b++)
					length += bi[2*b+1];
				
				for(int t : Storage.readInts(raf, bi[0], length))
					if(t > max)
						max = t;
				
				a += block;
				raf.seek(4*(1+2*a));
			}
		} catch(final IOException e) {
			e.printStackTrace();
		}
		System.out.println(System.currentTimeMillis() - l);
	}
}