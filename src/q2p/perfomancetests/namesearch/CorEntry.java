package q2p.perfomancetests.namesearch;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;

public final class CorEntry {
	public static final CorEntry root = new CorEntry('\n');
	
	final char sym;
	CorEntry[] arms = new CorEntry[0];
	int link = -1;

	private static int st = 0;
	public final int write(final RandomAccessFile raf, int offset) throws IOException {
		if(link != -1) {
			st++;
			if(st % 5000 == 0)
				System.out.println(st);
		}
		final int size = arms.length;
		byte[] arr = new byte[8+size*2];
		ByteBuffer.wrap(arr).asIntBuffer().put(link).put(size);
		CharBuffer cb = ByteBuffer.wrap(arr, 8, 2*size).asCharBuffer();
		for(int i = 0; i != size; i++)
			cb.put(arms[i].sym);
		
		raf.write(arr);
		
		int cp = offset+8+2*size; // offset + link + size + syms[]
		offset = cp+4*size; // cp + pointers[]
		for(int i = 0; i != size; i++) {
			raf.writeInt(offset);
			raf.seek(offset);
			offset = arms[i].write(raf, offset);
			cp += 4;
			raf.seek(cp);
		}
		return offset;
	}
	
	private CorEntry(final char sym) {
		this.sym = sym;
	}
	
	public static final void add(final String key, final int link) {
		final char[] syms = key.toCharArray();
		int pointer = 0;
		
		CorEntry entry = root;
		char c;
		loop: do {
			if(pointer == syms.length) {
				if(pointer != 0 && entry.link == -1)
					entry.link = link;
				
				return;
			}
			
			c = syms[pointer];
			
			for(int i = entry.arms.length-1; i != -1; i--) {
				if(c == entry.arms[i].sym) {
					entry = entry.arms[i];
					pointer++;
					continue loop;
				}
			}
			break;
		} while(true);
		
		int i = entry.arms.length - 1;
		while(i != -1 && c < entry.arms[i].sym)
			i--;
		i++;
		
		entry.arms = Arrays.copyOf(entry.arms, entry.arms.length+1);
		for(int j = entry.arms.length - 1; j != i; j--)
			entry.arms[j] = entry.arms[j-1];
		
		entry = entry.arms[i] = new CorEntry(c);
		
		pointer++;
		for(i = syms.length - pointer; i != 0; i--) {
			entry.arms = new CorEntry[] {new CorEntry(syms[pointer++])};
			entry = entry.arms[0];
		}
		entry.link = link;
	}
	
	public static final int search(final String key) {
		final char[] syms = key.toCharArray();
		int pointer = 0;
		
		CorEntry entry = root;
		char c;
		loop:
		while(pointer != syms.length) {
			c = syms[pointer];

//		for(int i = entry.arms.length-1; i != -1; i--) { // TODO: slower
			for(int j = entry.arms.length, i = 0; i != j; i++) {
				if(c == entry.arms[i].sym) {
					entry = entry.arms[i];
					pointer++;
					continue loop;
				}
			}
			return -1;
		}
		return entry.link;
	}
	
	public static final int searchBinary(final String key) {
		final char[] syms = key.toCharArray();
		int pointer = 0;
		
		CorEntry te, entry = root;
		char c;
		int lo, hi, mid;
		loop:
		while(pointer != syms.length) {
			c = syms[pointer];

			lo = 0;
			hi = entry.arms.length - 1;
			while (lo <= hi) {
				mid = lo + (hi - lo) / 2;
				te = entry.arms[mid];
				if(c < te.sym) hi = mid - 1;
				else if(c > te.sym) lo = mid + 1;
				else {
					entry = te;
					pointer++;
					continue loop;
				}
			}
			return -1;
		}
		return entry.link;
	}
}