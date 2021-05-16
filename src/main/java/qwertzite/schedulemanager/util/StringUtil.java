package qwertzite.schedulemanager.util;

public class StringUtil {
	
	
	public static String zeroFill(int val) {
		return String.format("%2s", String.valueOf(val)).replace(" ", "0");
	}
	
	public static String zeroFill4(int val) {
		return String.format("%4s", String.valueOf(val)).replace(" ", "0");
	}
}
