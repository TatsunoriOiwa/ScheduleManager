package qwertzite.schedulemanager;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public class Log {
	public static final Logger LOGGER;
	
	static {
		// TRACE, DEBUG, INFO, WARN, WRROR, FATAL
		Logger log = (Logger) LogManager.getLogger("SM");
		//log.setLevel(Level.INFO);
		LOGGER = log;
	}
	
	public static void info(String format) {
		Log.LOGGER.info(format);
	}
	
	public static void info(String format, Object... arguments) {
		Log.LOGGER.info(format, arguments);
	}
	
	public static void info(String format, Throwable e) {
		Log.LOGGER.info(format, e);
	}
	
	public static void error(String format) {
		Log.LOGGER.error(format);
	}
	
	public static void error(String format, Object... arguments) {
		Log.LOGGER.error(format, arguments);
	}
	
	public static void error(String format, Throwable e) {
		Log.LOGGER.error(format, e);
	}
	
	public static void warn(String format) {
		Log.LOGGER.warn(format);
	}
	
	public static void warn(String format, Object... arguments) {
		Log.LOGGER.warn(format, arguments);
	}
	
	public static void warn(String format, Throwable e) {
		Log.LOGGER.warn(format, e);
	}
	
	public static void fatal(String message) {
		Log.LOGGER.fatal(message);
	}
	
	public static void fatal(String format, Object... arguments) {
		Log.LOGGER.fatal(format, arguments);
	}
	
	public static void fatal(String format, Throwable e) {
		Log.LOGGER.fatal(format, e);
	}
	
	public static void debug(String message) {
		Log.LOGGER.debug(message);
	}
	
	public static void debug(String format, Object... arguments) {
		Log.LOGGER.debug(format, arguments);
	}
	
	public static void debug(String message, Throwable e) {
		Log.LOGGER.debug(message, e);
	}
	
	/** must be called after {@link LaunchConf} was initialised */
	public static void setLogLevel(Level level) {
		Log.LOGGER.setLevel(level);
	}
}
