package qwertzite.schedulemanager;

import qwertzite.schedulemanager.client.ScheduleManager;

/**
 * 
 * Launch the application.
 * @author ellip
 * @date 2020/10/25
 */
public class Main {

	public static void main(String[] args) {
		try {
			Log.info("Java name:{}, version:{}", System.getProperty("java.vm.name"), System.getProperty("java.version"));
			Log.info("OS name:{}, arch:{}, version:{}", System.getProperty("os.name"), System.getProperty("os.arch"), System.getProperty("os.version"));
			Log.info("JAVA_HOME:{}", System.getProperty("java.home"));
			new ScheduleManager().run();
		} catch (Exception e) {
			Log.error("", e);
		}
	}
}
