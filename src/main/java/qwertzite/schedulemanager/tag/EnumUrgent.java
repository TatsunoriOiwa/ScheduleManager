package qwertzite.schedulemanager.tag;

public enum EnumUrgent {
	URGENT(0),
	IMPORTANT(1),
	NORMAL(2),
	NON_URGENT(3);
	
	private static EnumUrgent[] META_LOOKUP = new EnumUrgent[values().length];
	
	private final int index;

	private EnumUrgent(int index) {
		this.index = index;
	}
	
	public static EnumUrgent fromIndex(int index) {
		return META_LOOKUP[index];
	}
	public int getIndex() { return index; }
	
	static {
		for (EnumUrgent e : values()) {
			META_LOOKUP[e.getIndex()] = e;
		}
	}
}
