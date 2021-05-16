package qwertzite.schedulemanager.util.datetime;

import java.time.LocalDateTime;

import qwertzite.schedulemanager.util.StringUtil;
import qwertzite.schedulemanager.util.TimeDateUtil;

public class YearMonth {
	private final int year;
	private final EnumMonth month;
	
	
	public YearMonth(int year, EnumMonth month) {
		this.year = year;
		this.month = month;
	}
	
	public static YearMonth now() {
		LocalDateTime now = TimeDateUtil.getCurrentDateTime();
		return new YearMonth(now.getYear(), EnumMonth.fromIndex(now.getMonthValue()));
	}
	
	public int getYear() { return year; }
	public EnumMonth getMonth() { return month; }

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof YearMonth) {
			YearMonth other = (YearMonth) obj;
			return this.getYear() == other.getYear() && this.getMonth() == other.getMonth();
		}
		return false;
	}
	
	@Override
	public String toString() {
		return this.getYear() + "-" + StringUtil.zeroFill(this.getMonth().getIndex());
	}
}
