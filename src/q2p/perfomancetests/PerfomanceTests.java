package q2p.perfomancetests;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import q2p.perfomancetests.stresssize.StressBuilder;

public final class PerfomanceTests {
	public static final void main(final String[] args) {
		//NameSearch.test();
		//TaggingSearch.test();
		//BytesConversions.test();
		//createFile();
		
		//StressBuilder.test();
		
		AllocationTest.test(true);
	}

	private static final void createFile() {
		try(final DataOutputStream dos = new DataOutputStream(new FileOutputStream("empty_posts"))) {
			dos.writeInt(0);
			dos.flush();
		} catch(final Exception e) {
			e.printStackTrace();
		}
	}
}