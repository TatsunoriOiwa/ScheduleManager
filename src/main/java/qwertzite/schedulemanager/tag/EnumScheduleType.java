package qwertzite.schedulemanager.tag;

public enum EnumScheduleType {
	TASK(0),
	SCHEDULE(1);
	
	private static EnumScheduleType[] META_LOOKUP = new EnumScheduleType[values().length];
	
	private final int index;

	private EnumScheduleType(int index) {
		this.index = index;
	}
	
	public static EnumScheduleType fromIndex(int index) {
		return META_LOOKUP[index];
	}
	public int getIndex() { return index; }
	
	static {
		for (EnumScheduleType e : values()) {
			META_LOOKUP[e.getIndex()] = e;
		}
	}
}
