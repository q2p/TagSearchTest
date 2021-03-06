package q2p.perfomancetests.stresssize;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Random;

public final class StressBuilder {
	private static final int TOTAL_POSTS = 5_000_000;

	private static final int TEXT_SYMS_PER_POST = 10; // or most often about 5_000
	
	private static final int MISC_PER_POST = 4*32;
	
	private static final int SRC_SYMS = 80;
	
	private static final int CHECK_SUM = 16 + 64 + 2;
	
	private static final int NOTES_PER_POST = 0;
	
	private static final int DATA_PER_NOTE = 30;
	
	private static final int COMMENTS_PER_POST = 0;
	
	private static final int DATA_PER_COMMENT = 100; // 20 - 1000
	
	private static final int SPECS = 6;
	
	private static final int[][] posts = new int[TOTAL_POSTS][0];
	
	private static final int[] points = new int[] {
		      0, 1_330_553,
		      4,   967_816,
		     16,   490_740,
		     32,   296_443,
		     43,   230_754,
		     94,   126_600,
		    128,   100_153,
		    250,    55_100,
		    500,    22_000,
		  1_000,     8_600,
		  1_500,     4_833,
		  5_000,       876,
		 15_000,       211,
		 25_000,       110,
		 50_000,        43,
		100_000,        15,
		354_800,         1,
		595_100,         0
	};
	private static final byte nodes = (byte)(points.length/2);
	
	private static final int[] tags = new int[points[nodes*2-2]];
	static {
		/*for(int i = tags.length - 1; i != -1; i--)
			tags[i] = get(i);

		final Random r = new Random(906090);
		final int[] check = new int[tags[0]+1];
		
		int ls = tags.length;
		for(int tag : tags) {
			System.out.println(tag+":"+(ls--));
			check[0] = posts.length;
			int ci = 1, left = posts.length;
			int p, mid, lo, hi;
			do {
				if(tag % 10000 == 0)
					System.out.println(tag);
				
				p = r.nextInt(left--);
				
				lo = 0;
				hi = ci - 1;
				mid = 0;
				while(lo < hi) {
					mid = lo + (hi - lo) / 2;
					
					if(p + mid < check[mid]) hi = mid;
					else lo = ++mid;
				}
				
				p += mid;
								
				System.arraycopy(check, mid, check, mid+1, ci-mid);
				
				ci++;
				check[mid] = p;
			} while(--tag != 0);

			for(int i = ci-2, k, id = 0, sz; i != -1; i--, id++) {
				k = check[i];
				sz = posts[k].length;
				posts[k] = Arrays.copyOf(posts[k], sz+1);
				posts[k][sz] = id;
			}
		}*/
		try(final RandomAccessFile raf = new RandomAccessFile("crushed.dat", "rw")) {
			/*final int amount = posts.length;
			raf.writeInt(amount);
			int offset = 4*(1+2*amount);
			for(int a = 0; a != amount; a++) {
				final int tags = posts[a].length;
				final byte[] buff = new byte[4 * (1 + tags)];
				final IntBuffer ib = ByteBuffer.wrap(buff).asIntBuffer();
				for(int i = 0; i != tags; i++)
					ib.put(posts[a][i]);
				
				raf.seek(4*(1+2*a));
				raf.writeInt(offset);
				raf.writeInt(tags);
				raf.seek(offset);
				raf.write(buff);
				offset += buff.length;
			}*/
			
			System.out.println
			((406_000_000+
				(TOTAL_POSTS *
					(TEXT_SYMS_PER_POST
						+MISC_PER_POST
						+SRC_SYMS
						+CHECK_SUM
						+(long)SPECS*(
							NOTES_PER_POST * DATA_PER_NOTE+
							COMMENTS_PER_POST * DATA_PER_COMMENT
						)
					)
				))/1024/1024/1024
			);
			
			/*raf.seek
			(406_000_000+
				(TOTAL_POSTS *
					(TEXT_SYMS_PER_POST
						+MISC_PER_POST
						+SRC_SYMS
						+CHECK_SUM
						+(long)SPECS*(
							NOTES_PER_POST * DATA_PER_NOTE+
							COMMENTS_PER_POST * DATA_PER_COMMENT
						)
					)
				)
			);
			raf.write(1);*/
		} catch(final IOException e) {
			e.printStackTrace();
		}
	}
	
	public static final void test() {}
	
	private static final int get(final int id) {
		for(byte i = (byte)(nodes-2); i != -1; i--) {
			final int p1 = points[i*2], p2 = points[(i+1)*2];
			if(p1 <= id && p2 > id) {
				final int x1 = points[i*2+1], x2 = points[(i+1)*2+1];
				return x1 + (id-p1)*(x2-x1)/(p2-p1);
			}
		}
		return 0;
	}
}