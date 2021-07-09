package qwertzite.schedulemanager.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;

import com.google.gson.JsonObject;

import qwertzite.schedulemanager.util.datetime.EnumMonth;

public class FuzzyDate implements Cloneable {
	public static final LocalDateTime EARLIEST;
	public static final LocalDateTime LATEST;
	
	static {
		EARLIEST = LocalDateTime.of(LocalDate.MIN, LocalTime.of(0, 0));
		LATEST = LocalDateTime.of(LocalDate.MAX, LocalTime.of(23, 59));
	}
	
	private int year = -1;
	private EnumMonth month = EnumMonth.NONE;
	private int day = -1;
	private DayOfWeek dayofweek = null;
	private int time = -1;
	
	
	public FuzzyDate loadFromJsonObj(JsonObject eobj) {
		if (eobj.has("year")) { 
			int y= eobj.get("year").getAsInt();
			if (y >= 0) y = (y % 100) + 2000;
			this.setYear(y);
		if (eobj.has("month")) { this.setMonth(EnumMonth.fromIndex(eobj.get("month").getAsInt()));
		if (eobj.has("day")) { this.setDay(eobj.get("day").getAsInt());
		if (eobj.has("time")) { this.setTime(eobj.get("time").getAsInt());
		} } } }
		return this;
	}
	
	public JsonObject writeToJsonObj(JsonObject obj) {
		obj.addProperty("year", this.getYear());
		obj.addProperty("month", this.getMonth().getIndex());
		obj.addProperty("day", this.getDay());
		obj.addProperty("time", this.getTime());
		return obj;
	}
	
	/**
	 * 
	 * @param other
	 * @return -1 if this is earlier than the other, 1 if this > other.
	 */
	public int compare(FuzzyDate other) {
		if (!this.hasYear() || !other.hasYear()) { return 0; }
		int flag = Integer.compare(this.getYear(), other.getYear());
		if (flag != 0) { return flag; }
		
		if (!this.hasMonth() || !other.hasMonth()) { return 0; }
		flag = Integer.compare(this.getMonth().getIndex(), other.getMonth().getIndex());
		if(flag != 0) { return flag; }
		
		if (!this.hasDay() || !other.hasDay()) { return 0; }
		flag = Integer.compare(this.getDay(), other.getDay());
		if (flag != 0) { return flag; }
		
		if (!this.hasTime() || !other.hasTime()) { return 0; }
		return Integer.compare(this.getTime(), other.getTime());
	}
	
	/**
	 * 
	 * @param other
	 * @return 0: same, -1: fuzzier, 1: more strict.
	 */
	public int isStrictThan(FuzzyDate other) {
		if (this.hasTime()) return other.hasTime() ? 0 : 1;
		if (other.hasTime()) return -1;
		if (this.hasDay()) return other.hasDay() ? 0 : 1;
		if (other.hasDay()) return -1;
		if (this.hasMonth()) return other.hasMonth() ? 0 : 1;
		if (other.hasMonth()) return -1;
		if (this.hasYear()) return other.hasYear() ? 0 : 1;
		if (other.hasYear()) return -1;
		return 0;
	}
	
	public boolean hasYear() { return this.year >= 0; }
	public int getYear() { return year; }
	public boolean setYear(int year) {
		if (year >= 0 ) {
			this.year = year;
			this.updateDayOfWeek();
			return true;
		} else {
			return false;
		}
	}
	/** Cascadingly delete month data. */
	public void clearYear() {
		this.year = -1;
		this.clearMonth();
	}

	public boolean hasMonth() { return this.month != EnumMonth.NONE; }
	public EnumMonth getMonth() { return month; }
	public boolean setMonth(EnumMonth month) {
		if (month == EnumMonth.NONE) { return false; }
		this.month = month;
		if (!this.hasYear()) {
			LocalDateTime now = TimeDateUtil.getCurrentDateTime();
			int year = now.getYear();
			// EnumMonth month
			LocalDateTime nnow = LocalDateTime.of(year, now.getMonth(), 1, 0, 0);
			LocalDateTime cand = LocalDateTime.of(year, month.getIndex(), 1, 0, 0);
			if (cand.isBefore(nnow)) { year++; }
			this.year = year;
		}
		this.updateDayOfWeek();
		return true;
	}
	public void clearMonth() {
		this.month = EnumMonth.NONE;
		this.clearDay();
	}
	
	public boolean hasDay() { return this.getDay() > 0; }
	public int getDay() { return day; }
	public boolean setDay(int day) {
		if (day <= 0 || day > 31) {
			return false;
		}
		LocalDateTime now = TimeDateUtil.getCurrentDateTime();
		if (this.hasMonth()) {
			if (day > Month.of(this.getMonth().getIndex()).length(Year.isLeap(2000 + this.getYear()))) {
				return false;
			}
			this.day = day;
		} else {
			int year = this.hasYear() ? this.getYear() : now.getYear(); // 仮？
			Month month = now.getMonth(); // 仮
			if (day > month.length(Year.isLeap(year))) { // Never be true in December.
				month = month.plus(1);
			}
			LocalDateTime nnow = LocalDateTime.of(year, month, now.getDayOfMonth(), 0, 0);
			LocalDateTime cand = LocalDateTime.of(year, month, day, 0, 0);
			if (cand.isBefore(nnow)) {
				month = month.plus(1);
				if (month == Month.JANUARY) {
					if (year != now.getYear()) return false;
					year++;
				}
				if (day > month.length(Year.isLeap(year))) {
					month = month.plus(1);
				}
			}
			this.year = year;
			this.month = EnumMonth.fromMonth(month);
			this.day = day;
		}
		this.updateDayOfWeek();
		return true;
	}
	public void clearDay() {
		this.day = -1;
		this.dayofweek = null;
		this.clearTime();
	}
	
	private void updateDayOfWeek() {
		if (!this.hasDay()) return;
		this.dayofweek = TimeDateUtil.getCurrentDateTime().withYear(this.getYear()).withMonth(this.getMonth().getIndex()).withDayOfMonth(this.getDay()).getDayOfWeek();
	}
	
	public String getDayOfWeekString() {
		return TimeDateUtil.dayOfWeekString(this.dayofweek);
	}
	
	public boolean hasTime() { return this.getTime() >= 0; }
	public int getTime() { return this.time; }
	public boolean setTime(int time) {
		if (time < 0) { return false; }
		int hour = time / 100;
		int min = time % 100;
		if (hour < 0 || hour >= 24 || min < 0 || min >= 60) { return false; }
		if (!this.hasDay()) {
			LocalDateTime now = TimeDateUtil.getCurrentDateTime();
			int year = this.hasYear() ? this.getYear() : now.getYear();
			EnumMonth month = this.hasMonth() ? this.getMonth() : EnumMonth.fromIndex(now.getMonthValue());
			int day = this.hasDay() ? this.getDay() : now.getDayOfMonth();
			LocalDateTime nnow = LocalDateTime.of(year, month.getMonth(), now.getDayOfMonth(), now.getHour(), now.getMinute());
			LocalDateTime cand = LocalDateTime.of(year, month.getMonth(), day, hour, min);
			if (cand.isBefore(nnow)) {
				day++;
				if (day > month.length(year)) {
					day = 1;
					if (month.getMonth() != now.getMonth()) { return false; }
					month = month.getNext();
					if (month == EnumMonth.JAN) {
						if (year != now.getYear()) { return false; }
						year++;
					}
				}
			}
			this.year = year;
			this.month = month;
			this.day = day;
		}
		this.time = time;
		this.updateDayOfWeek();
		return true;
	}
	public void clearTime() { this.time = -1; }
	
	public LocalDateTime earliestPossible() {
		LocalDateTime ret = EARLIEST;
		if (this.hasYear()) ret = ret.withYear(this.getYear());
		if (this.hasMonth()) ret = ret.withMonth(this.getMonth().getIndex());
		if (this.hasDay()) ret = ret.withDayOfMonth(this.getDay());
		if (this.hasTime()) ret = ret.withHour(this.time / 100).withMinute(this.time % 100);
		return ret;
	}
	
	public LocalDateTime latestPossible() {
		LocalDateTime ret = LATEST;
		if (this.hasYear()) ret = ret.withYear(this.getYear());
		if (this.hasMonth()) ret = ret.withMonth(this.getMonth().getIndex());
		if (this.hasDay()) ret = ret.withDayOfMonth(this.getDay());
		if (this.hasTime()) ret = ret.withHour(this.time / 100).withMinute(this.time % 100);
		return ret;
	}
	
	@Override
	public FuzzyDate clone() {
		try {
//			ScheduleEntry clone = (ScheduleEntry) super.clone();
			return (FuzzyDate) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}
}
