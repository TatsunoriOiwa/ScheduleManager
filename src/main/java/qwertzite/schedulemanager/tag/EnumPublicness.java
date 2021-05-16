package qwertzite.schedulemanager.tag;

public enum EnumPublicness {
	PUBLIC(0),
	PRIVATE(1);
	
	private static EnumPublicness[] META_LOOKUP = new EnumPublicness[values().length];
	
	private final int index;

	private EnumPublicness(int index) {
		this.index = index;
	}
	
	public static EnumPublicness fromIndex(int index) {
		return META_LOOKUP[index];
	}
	public int getIndex() { return index; }
	
	static {
		for (EnumPublicness e : values()) {
			META_LOOKUP[e.getIndex()] = e;
		}
	}
}
