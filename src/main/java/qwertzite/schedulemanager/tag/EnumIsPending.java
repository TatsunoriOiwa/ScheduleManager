package qwertzite.schedulemanager.tag;

public enum EnumIsPending {
	TRUE(1),
	FALSE(0);
	
	private static EnumIsPending[] META_LOOKUP = new EnumIsPending[values().length];
	
	private final int index;

	private EnumIsPending(int index) {
		this.index = index;
	}
	
	public static EnumIsPending fromIndex(int index) {
		return META_LOOKUP[index];
	}
	public int getIndex() { return index; }
	
	static {
		for (EnumIsPending e : values()) {
			META_LOOKUP[e.getIndex()] = e;
		}
	}
}
