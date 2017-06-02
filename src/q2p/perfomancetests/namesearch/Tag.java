package q2p.perfomancetests.namesearch;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import q2p.perfomancetests.Assist;
import q2p.perfomancetests.Storage;

public final class Tag implements Comparable<Tag> {
	public static Tag[] tags;
	
	static {
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream("tags.dat"));
			byte[] buff = new byte[dis.available()];
			dis.readFully(buff);
			dis.close();
			dis = new DataInputStream(new ByteArrayInputStream(buff));
			
			tags = new Tag[dis.readInt()];
			for(int i = 0, j = tags.length; i != j; i++) {
				tags[i] = new Tag(dis, i);
			}
			
			dis.close();
		} catch(final Exception e) {
			Assist.abort(e.getMessage());
		}
		System.out.println("loaded");
	}
	
	public static final void initilize() {}
	
	public final int id;
	public final String name;
	
	public Tag(final DataInputStream dis, final int id) throws Exception {
		this.id = id;
		
		name = Storage.readString(dis);
		dis.skipBytes(6);
		dis.skipBytes(dis.readInt());
		
		for(int i = dis.readInt(); i != 0; i--) {
			dis.skipBytes(5);
			dis.skipBytes(dis.readInt());
		}
		
		for(int i = dis.readInt(); i != 0; i--)
			dis.skipBytes(5);
	}
	
	public final int compareTo(final Tag tag) {
		return name.compareTo(tag.name);
	}
}