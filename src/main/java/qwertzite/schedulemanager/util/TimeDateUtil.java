package qwertzite.schedulemanager.util;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import qwertzite.schedulemanager.util.datetime.EnumMonth;

public class TimeDateUtil {
	
	/** {@code hh:mm:ss} */
	public static final DateTimeFormatter ISO_TIME_NONFRACTION;
	/** {@code YY/MM/DD} */
	public static final DateTimeFormatter SLASH_DATE_SIMPLE;
	static {
		ISO_TIME_NONFRACTION = new DateTimeFormatterBuilder()
				.appendValue(ChronoField.HOUR_OF_DAY, 2)
				.appendLiteral(':')
				.appendValue(ChronoField.MINUTE_OF_HOUR, 2)
				.optionalStart()
				.appendLiteral(':')
				.appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();
		SLASH_DATE_SIMPLE = new DateTimeFormatterBuilder()
				.appendValue(ChronoField.YEAR, 4)
				.appendLiteral("/")
				.appendValue(ChronoField.MONTH_OF_YEAR, 2)
				.appendLiteral("/")
				.appendValue(ChronoField.DAY_OF_MONTH, 2).toFormatter();
	}
	
	public static int getCurrentYear() {
		return getCurrentDateTime().getYear();
	}
	
	public static EnumMonth getCurrentMonth() {
		return EnumMonth.fromIndex(getCurrentDateTime().getMonthValue());
	}
	
	public static int getCurrentDay() {
		return getCurrentDateTime().getDayOfMonth();
	}
	
	public static LocalDateTime getCurrentDateTime() {
		return LocalDateTime.now();
	}
	
	public static LocalDateTime getFromString(String fullstring) {
		return LocalDateTime.parse(fullstring);
	}
	
	private static final Map<DayOfWeek, String> DOW_2_STR;
	static {
		Map<DayOfWeek, String> tmp = new HashMap<>();
		tmp.put(DayOfWeek.SUNDAY  , "日");
		tmp.put(DayOfWeek.MONDAY  , "月");
		tmp.put(DayOfWeek.TUESDAY , "火");
		tmp.put(DayOfWeek.WEDNESDAY,"水");
		tmp.put(DayOfWeek.THURSDAY, "木");
		tmp.put(DayOfWeek.FRIDAY  , "金");
		tmp.put(DayOfWeek.SATURDAY, "土");
		DOW_2_STR = Collections.unmodifiableMap(tmp);
	}
	
	public static String dayOfWeekString(DayOfWeek day) {
		return DOW_2_STR.get(day);
	}
}
