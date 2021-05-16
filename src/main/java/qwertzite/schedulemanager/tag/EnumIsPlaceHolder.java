package qwertzite.schedulemanager.tag;

public enum EnumIsPlaceHolder {
	TRUE(1),
	FALSE(0);
	
	private static EnumIsPlaceHolder[] META_LOOKUP = new EnumIsPlaceHolder[values().length];
	
	private final int index;

	private EnumIsPlaceHolder(int index) {
		this.index = index;
	}
	
	public static EnumIsPlaceHolder fromIndex(int index) {
		return META_LOOKUP[index];
	}
	public int getIndex() { return index; }
	
	static {
		for (EnumIsPlaceHolder e : values()) {
			META_LOOKUP[e.getIndex()] = e;
		}
	}
}
