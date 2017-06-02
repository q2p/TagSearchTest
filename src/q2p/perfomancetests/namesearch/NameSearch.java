package q2p.perfomancetests.namesearch;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import q2p.perfomancetests.Storage;

public final class NameSearch {
	private static final Tag[] sorted = new Tag[Tag.tags.length];
	public static final void test() {
		//fillSorted();
		//fillCor();
		
		//testSortedBinary();
		//testCorIteration();
		//testCorBinary();
		
		//saveSorted();
		//saveCor();
		
		testSortedHard();
		testCorHard();
	}
	private static final int blockSize = 4*1024;
	private static final void testSortedHard() {
		try(RandomAccessFile raf = new RandomAccessFile("str.dat", "r")) {
			long here = System.currentTimeMillis();
			
			for(int i = 1, j = 0; i != 0; i--) {
				for(final Tag tag : Tag.tags) {
					raf.seek(0);
					int size = raf.readInt();
					
					final String key = tag.name;
					int lo = 0;
					int hi = size - 1;
					int mid = -1;
					int[] arr;
					int[] bufferA = null;
					String[] bufferT = null;
					int offf = -1;
					int offf2 = -1;
					while(lo <= hi) {
						if((hi-lo+2)*4 <= blockSize && bufferA == null) {
							offf = lo;
							bufferA = Storage.readInts(raf, (1+lo)*4, hi-lo+2);
						}
						if(bufferA != null && bufferA != null && bufferA[hi - offf + 1] - bufferA[lo - offf] <= blockSize && bufferT == null) {
							//bufferT = bufferize(raf, bufferA, offf, lo, hi);
							bufferT = new String[hi-lo+1];
							final byte[] buffer = new byte[bufferA[hi-offf+1] - bufferA[lo-offf]];
							raf.seek(bufferA[lo-offf]);
							raf.readFully(buffer);
							for(int o = 0; o != hi-lo+1; o++) {
								final int toff = bufferA[lo-offf+o];
								bufferT[o] = new String(buffer, toff - bufferA[lo-offf], bufferA[lo-offf+o+1] - toff, StandardCharsets.UTF_8);
							}
							offf2 = lo;
						}
						mid = lo+(hi-lo)/2;
						final String buffered;
						if(bufferT == null) {
							if(bufferA == null) {
								arr = Storage.readInts(raf, (mid+1)*4, 2);
								buffered = Storage.readIndexed(raf, arr[0], arr[1]);
							} else
								buffered = Storage.readIndexed(raf, bufferA[mid - offf], bufferA[mid - offf + 1]);
						} else {
							buffered = bufferT[mid - offf2];
						}
						if(key.compareTo(buffered) < 0) hi = mid - 1;
						else if(key.compareTo(buffered) > 0) lo = mid + 1;
						else {
							mid = -2;
							break;
						}
					}
					if(++j % 5000 == 0)
						System.out.print(j/5000+".");
				}
			}
			here = System.currentTimeMillis() - here;
			System.out.println("\nsorted hard binary: " + here);
		} catch(final Exception e) {
			e.printStackTrace();
		}
	}
	
	private static final void testCorHard() {
		try(RandomAccessFile raf = new RandomAccessFile("cor.dat", "r")) {
			long here = System.currentTimeMillis();
			
			for(int i = 1, j = 0; i != 0; i--) {
				for(final Tag tag : Tag.tags) {
					searchCorBinary(raf, tag.name);
					if(++j % 5000 == 0)
						System.out.print(j/5000+".");
				}
			}
			here = System.currentTimeMillis() - here;
			System.out.println("\ncor hard binary: " + here);
		} catch(final IOException e) {
			e.printStackTrace();
		}
	}
	private static final int searchCorBinary(final RandomAccessFile raf, final String key) throws IOException {
		final char[] syms = key.toCharArray();
		
		int pointer = 0;
		
		char c, sym;
		int lo, hi, mid, size;
		int[] temp;
		raf.seek(0);
		
		loop:
		while(pointer != syms.length) {
			c = syms[pointer];
			size = Storage.readInts(raf, 2)[1];
			temp = Storage.readCharsInts(raf, size, size);
			//dump(temp);
			
			lo = 0;
			hi = size - 1;
			while (lo <= hi) {
				mid = lo + (hi - lo) / 2;
				sym = (char)temp[mid];
				if(c < sym) hi = mid - 1;
				else if(c > sym) lo = mid + 1;
				else {
					raf.seek(temp[size+mid]);
					pointer++;
					continue loop;
				}
			}
			return -1;
		}
		return raf.readInt();
	}
	private static final void saveSorted() {
		try(RandomAccessFile raf = new RandomAccessFile("str.dat", "rw")) {
			int size = sorted.length;
			raf.writeInt(size);
			int offset = (size+2)*4;
			raf.writeInt(offset);
			for(int i = 0; i != size;) {
				raf.seek(offset);
				offset += Storage.writeIndexed(raf, sorted[i++].name);
				raf.seek((1+i)*4);
				raf.writeInt(offset);
				if(i % 20000 == 0)
					System.out.println(i);
			}
		} catch(final IOException e) {
			e.printStackTrace();
		}
	}
	private static final void saveCor() {
		try(RandomAccessFile raf = new RandomAccessFile("cor.dat", "rw")) {
			CorEntry.root.write(raf, 0);
		} catch(final IOException e) {
			e.printStackTrace();
		}
	}
	private static final void testSortedBinary() {
		long here = System.currentTimeMillis();

		for(int i = 16; i != 0; i--) {
			for(final Tag tag : Tag.tags) {
				String key = tag.name;
				int lo = 0;
				int hi = sorted.length - 1;
				int mid = -1;
				while (lo <= hi) {
					mid = lo + (hi - lo) / 2;
					if(key.compareTo(sorted[mid].name) < 0) hi = mid - 1;
					else if(key.compareTo(sorted[mid].name) > 0) lo = mid + 1;
					else break;
				}
			}
		}
		
		here = System.currentTimeMillis() - here;
		System.out.println("sorted binary: " + here);
	}
	private static final void testCorIteration() {
		long here = System.currentTimeMillis();

		for(int i = 16; i != 0; i--) {
			for(final Tag tag : Tag.tags)
				CorEntry.search(tag.name);
		}
		
		here = System.currentTimeMillis() - here;
		System.out.println("cor iterational: " + here);
	}
	private static final void testCorBinary() {
		long here = System.currentTimeMillis();

		for(int i = 16; i != 0; i--) {
			for(final Tag tag : Tag.tags)
				CorEntry.searchBinary(tag.name);
		}
		
		here = System.currentTimeMillis() - here;
		System.out.println("cor binary: " + here);
	}
	private static final void fillSorted() {
		for(int i = sorted.length-1; i != -1; i--)
			sorted[i] = Tag.tags[i];
		
		Arrays.sort(sorted);
	}
	private static final void fillCor() {
		for(final Tag tag : Tag.tags)
			CorEntry.add(tag.name, tag.id);
	}
}