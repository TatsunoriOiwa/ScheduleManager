package qwertzite.schedulemanager.util.datetime;

import java.time.Month;
import java.time.Year;

public enum EnumMonth {
	NONE(0, false, ""),
	JAN( 1, false,"01"),
	FEB( 2, false,"02"),
	MAR( 3, false,"03"),
	APR( 4, false,"04"),
	MAY( 5, false,"05"),
	JUN( 6, true, "06"),
	JUL( 7, true, "07"),
	AUG( 8, true, "08"),
	SEP( 9, true, "09"),
	OCT(10, true, "10"),
	NOV(11, true, "11"),
	DEC(12, true, "12");
	
	private final static EnumMonth[] META_LOOKUP = new EnumMonth[values().length];
	
	private final int index;
	private final boolean incrementYear;
	private final String displayString;
	private final Month month;
	
	private EnumMonth(int index, boolean incrementYear, String displayString) {
		this.index = index;
		this.incrementYear = incrementYear;
		this.displayString = displayString;
		this.month = index == 0 ? null : Month.of(index);
	}
	
	public static EnumMonth fromIndex(int index) {
		if (index < 0) { index = 0; }
		else if (index > 12) { index = 0; }
		return META_LOOKUP[index];
	}
	
	/**
	 * 
	 * @param month
	 * @return NONE if month == null
	 */
	public static EnumMonth fromMonth(Month month) {
		return EnumMonth.fromIndex(month == null ? 0 : month.getValue());
	}
	
	/**
	 * from 1 to 12, 0 if not set
	 */
	public int getIndex() { return this.index; }
	public int getNewYear(int year) { return this.incrementYear ? year + 1 : year; }
	public String getDisplayString() { return this.displayString; }
	public EnumMonth getNext() {
		int next = this.getIndex() + 1;
		if (next == 13) { next = 1; }
		return EnumMonth.fromIndex(next);
	}
	
	public Month getMonth() { return this.month; }
	public int length(int year) {
		return this.month.length(Year.isLeap(year));
	}
	
	static {
		for (EnumMonth e : values()) {
			META_LOOKUP[e.getIndex()] = e;
		}
	}
}
