package q2p.perfomancetests;

public final class AllocationTest {
	public static final int TOTAL_POSTS = 5_000_000;
	public static final int SPLITTER = 5;
	public static final int BYTES_PER_POSTS = 1000;
	
	public static final void test(final boolean split) {
		final byte[][] massive = new byte[SPLITTER][TOTAL_POSTS/SPLITTER*BYTES_PER_POSTS];
		
		final Runtime rt = Runtime.getRuntime();
		System.out.println("MB: " + (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024));
		
		try {
			Thread.sleep(10000);
		} catch(final InterruptedException e) {}
	}
}