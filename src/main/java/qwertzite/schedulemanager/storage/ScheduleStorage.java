package qwertzite.schedulemanager.storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;

import org.eclipse.swt.SWT;

import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import qwertzite.schedulemanager.Log;
import qwertzite.schedulemanager.client.ScheduleManager;
import qwertzite.schedulemanager.client.dialog.ConfirmationDialog;
import qwertzite.schedulemanager.schedule.Schedules;
import qwertzite.schedulemanager.util.TimeDateUtil;
import qwertzite.schedulemanager.util.datetime.YearMonth;

public class ScheduleStorage {
	
	public static final String FILE_LOCATION = "schedule.json";
	public static final String DONE_LOCATION = "records";
	public static final String BACKUP_LOCATION = "backup";
	public static final File ROOT_DIR = new File(".");
	public static final File DONE_DIR = new File(ROOT_DIR, DONE_LOCATION);
//	public static final File BACKUP_DIR = new File(ROOT_DIR, BACKUP_LOCATION);
	public static final File SCHEDULE_FILE = new File(".", FILE_LOCATION);
	
	public static final String TIMESTAMP = "timestamp";
	
	private static LocalDateTime lastSynch = null;
	private static EnumDriveStatus driveStat = EnumDriveStatus.OFFLINE;
	private static LocalDateTime timestampSchedule;
	private static LocalDateTime timestampRecord;
	
	private static LocalDateTime lastBackup;
	
//	private static Thread updateChecker;
	
	public static void onInit() {
		if (SmGoogleDrive.authoriseAndConnect(false)) {
			driveStat = EnumDriveStatus.SYNCH_NG;
		} else {
			driveStat = EnumDriveStatus.OFFLINE;
		}
		
		Schedules schedules = ScheduleManager.INSTANCE.schedules;
		YearMonth ymNow = YearMonth.now();
		try {
			loadSchedule(schedules);
			loadDoneTask(schedules, ymNow);
		} catch (IOException e) {
			Log.warn("Failed to load local file!", e);
			throw new RuntimeException(e);
		}
		
		if (SmGoogleDrive.isAuthorised()) {
			synchBody(false);
//			updateChecker = new Thread(() -> {
//				autoSynchCheck();
//			});
//			updateChecker.start();
//			Log.info("Started auto synch check.");
		}
	}

	public static void onShutdown() {
		Log.info("Stopping auto sync status check...");
//		updateChecker.interrupt();
	}
	
	/**
	 * 
	 * @return null if there is no schedule.json.
	 * @throws IOException
	 */
	private static void loadSchedule(Schedules schedules) throws IOException {
		Log.info("Loading local schedule file.");
		JsonObject local = loadLocal(ROOT_DIR, SCHEDULE_FILE);
		if (local != null) {
			schedules.loadSchedule(local);
		}
		timestampSchedule = LocalDateTime.parse(getTimestampOf(local));
	}
	
	private static void loadDoneTask(Schedules schedules, YearMonth now) throws IOException {
		Log.info("Loading local done tasks. {}", now);
		JsonObject local = loadLocal(DONE_DIR, new File(DONE_DIR, now.toString() + ".json"));
		if (local != null) {
			schedules.loadDoneTask(local, now, true);
		}
		timestampRecord = LocalDateTime.parse(getTimestampOf(local));
	}
	
	private static JsonObject loadLocal(File dir, File f) throws IOException {
		if (!dir.exists()) { dir.mkdirs(); }
		if (!f.exists()) { return null; }
		FileInputStream fis = new FileInputStream(f);
		return parseToJson(fis);
	}
	
	private static JsonObject loadDrive(String name, Folder folder) throws IOException{
		try {
			return parseToJson(SmGoogleDrive.loadFile(name, folder));
		} catch (IOException e) {
			Log.warn("Failed to load from drive!", e);
			return null;
		}
		
	}
	
	public static void save(boolean record) {
		LocalDateTime timestamp = TimeDateUtil.getCurrentDateTime();
		saveSchedules(timestamp);
		if (record) saveDoneTask(YearMonth.now(), timestamp);
		if (driveStat == EnumDriveStatus.SYNCH_OK && ScheduleManager.INSTANCE.isRunning()) {
			driveStat = EnumDriveStatus.SYNCH_NG;
			ScheduleManager.INSTANCE.mirrorSynchStatus();
		}
	}
	
	public static void saveSchedules(LocalDateTime timestamp) {
		try {
			JsonObject json = new JsonObject();
			timestampSchedule = timestamp;
			json.addProperty(TIMESTAMP, timestamp.toString());
			ScheduleManager.INSTANCE.schedules.saveSchedules(json);
			File ff = new File(".", FILE_LOCATION);
			if (!ff.exists()) { ff.createNewFile(); }
			FileOutputStream fos = new FileOutputStream(ff);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
			JsonWriter jw = new JsonWriter(bw);
			jw.setIndent("\t");
			Streams.write(json, jw);
			jw.close();
			Log.info("Saved schedules.");
		} catch (IOException e) {
			Log.warn("Caught an exception while saving schedules");
		}
	}
	
	public static void saveDoneTask(YearMonth now, LocalDateTime timestamp) {
		try {
			JsonObject json = new JsonObject();
			timestampRecord = timestamp;
			json.addProperty(TIMESTAMP, timestamp.toString());
			ScheduleManager.INSTANCE.schedules.saveRecord(json);
			
			if (!DONE_DIR.exists()) {
				DONE_DIR.mkdirs();
			}
			File ff = new File(DONE_DIR, YearMonth.now().toString() + ".json");
			if (!ff.exists()) { ff.createNewFile(); }
			
			FileOutputStream fos = new FileOutputStream(ff);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
			JsonWriter jw = new JsonWriter(bw);
			Streams.write(json, jw);
			jw.close();
			Log.info("Saved completed tasks.");
		} catch (IOException e) {
			Log.warn("Caught an exception while saving completed tasks.");
		}
	}
	
	private static JsonObject parseToJson(InputStream is) throws IOException {
		if (is == null) { return null; }
		InputStreamReader isr = new InputStreamReader(is, "UTF-8");
		JsonReader jr = new JsonReader(isr);
		jr.setLenient(true);
		JsonObject json = Streams.parse(jr).getAsJsonObject();
		jr.close();
		return json;
	}
	
	public static void synch(boolean threading) {
		synch(threading, true);
	}
	
	public static void synch(boolean threading, boolean authriseDrive) {
		if (!SmGoogleDrive.isAuthorised()) {
			if (!authriseDrive) {
				Log.info("Drive not authorised. Aborting synch process.");
			}
			Log.info("Authorising google drive.");
			SmGoogleDrive.authoriseAndConnect(true);
		}
		
		Runnable runnable = () -> {
			boolean changed = synchBody(true);
			if (ScheduleManager.INSTANCE.isRunning()) {
				ScheduleManager.INSTANCE.getDisplay().syncExec(() -> {
					ScheduleManager.INSTANCE.mirrorSynchStatus();
					ScheduleManager.INSTANCE.enableSynchBtn(true);
					if (changed) {
						ScheduleManager.INSTANCE.mirrorScheduleContent();
					}
				});
			}
		};
		if (threading) { new Thread(runnable).start(); }
		else { runnable.run(); }
	}
	
	private static boolean synchBody(boolean warn) {
		Log.info("Synch status check...");
		LocalDateTime current = TimeDateUtil.getCurrentDateTime();
		/*
		 * ドライブから読み込む
		 * 
		 */
		boolean changed = false;
		boolean result = true;
		boolean overwrite = false;
		try {
			JsonObject driveSchedule = loadDrive(FILE_LOCATION, null);
			LocalDateTime timestampDriveSchedule = LocalDateTime.parse(getTimestampOf(driveSchedule));
			Log.info("Local: {}, Drive: {}", timestampSchedule, timestampDriveSchedule);
			if (timestampDriveSchedule.isAfter(timestampSchedule)) {
				ScheduleManager.INSTANCE.schedules.loadSchedule(driveSchedule);
				changed = true;
				Log.info("Applied drive schedule data.");
				saveSchedules(timestampDriveSchedule);
			} else if (lastSynch != null && lastSynch.isBefore(timestampDriveSchedule)) {
				if (!warn || (overwrite=confirmToOverwrite())) {
					ScheduleManager.INSTANCE.schedules.loadSchedule(driveSchedule);
					changed = true;
					Log.info("Detected drive data modification after previous synchronisation. Applied drive schedule data.");
					saveSchedules(timestampDriveSchedule);
				} else {
					Log.info("Detected drive data modification after previous synchronisation. Applied drive schedule data.");
				}
			} else if (timestampDriveSchedule.isBefore(timestampSchedule)) {
//				save(false, timestampSchedule);
				SmGoogleDrive.uploadFile(FILE_LOCATION, null, SCHEDULE_FILE);
				Log.info("Uploaded schedule file to drive.");
			} else {
				Log.info("Schedule file synch status OK.");
			}
		} catch (IOException e) {
			Log.warn("Failed to synchronise schedule file!", e);
			result = false;
		}
		
		try {
			YearMonth ymNow = YearMonth.now();
			JsonObject driveRecord = loadDrive(ymNow.toString() + ".json", Folder.RECORD);
			LocalDateTime timestampDriveRecord = LocalDateTime.parse(getTimestampOf(driveRecord));
			Log.info("Local: {}, Drive: {}", timestampRecord, timestampDriveRecord);
			
			if (timestampDriveRecord.isAfter(timestampRecord)) {
				ScheduleManager.INSTANCE.schedules.loadDoneTask(driveRecord, ymNow, true);
				changed = true;
				Log.info("Applied drive records data.");
				saveDoneTask(ymNow, timestampDriveRecord);
			} else if (lastSynch != null && lastSynch.isBefore(timestampDriveRecord) && (!warn || overwrite)) {
				ScheduleManager.INSTANCE.schedules.loadDoneTask(driveRecord, ymNow, true);
				changed = true;
				Log.info("Detected drive data modification after previous synchronisation. Applied drive records data.");
				saveDoneTask(ymNow, timestampDriveRecord);
			} else if (timestampDriveRecord.isBefore(timestampRecord)) {
//				saveDoneTask(ymNow);
				String name = YearMonth.now().toString() + ".json";
				SmGoogleDrive.uploadFile(name, Folder.RECORD, new File(DONE_DIR, name));
				Log.info("Uploaded records file to drive.");
			} else {
				Log.info("Records file synch status OK.");
			}
		} catch(IOException e) {
			Log.warn("Failed to synchronise records file!", e);
			result = false;
		}
		
		if (result) lastSynch = current;
		driveStat = result ? EnumDriveStatus.SYNCH_OK : EnumDriveStatus.SYNCH_NG;
		return changed;
	}
	
	/** DO NOT USE THIS FIELD */
	@Deprecated
	private static boolean tmpConfirmation;
	private static boolean confirmToOverwrite() {
		ScheduleManager.INSTANCE.getDisplay().syncExec(() -> { tmpConfirmation = new ConfirmationDialog(ScheduleManager.INSTANCE.getShell(), SWT.NONE, "Synch",
				"Drive data was modified after last synchronization.\n"
				+ "Do you want to overwrite local data?\n"
				+ "If not, drive data will be overwritten.").open();});
		return tmpConfirmation;
	}
	
	public static void createBackup() {
		try {
			Log.info("Creating backup...");
			LocalDateTime now = TimeDateUtil.getCurrentDateTime();
			SmGoogleDrive.uploadFile(now + "-schedule" + ".json", Folder.BACKUP, SCHEDULE_FILE);
			String rname = YearMonth.now().toString() + ".json";
			SmGoogleDrive.uploadFile(now + "-" + rname, Folder.BACKUP, new File(DONE_DIR, rname));
			lastBackup = now;
			Log.info("Created new backup file.");
		} catch (IOException e) {
			Log.warn("Failed to create backup file!", e);
		}
		ScheduleManager.INSTANCE.enableBackup();
	}
	
	private static String getTimestampOf(JsonObject json) {
		if (json == null || !json.has(TIMESTAMP)) {
			return LocalDateTime.MIN.toString();
		}
		return json.get(TIMESTAMP).getAsString();
	}
	
	public static EnumDriveStatus getSynchStatus() { return driveStat; }
	public static LocalDateTime getSynchTimestamp() { return lastSynch; }
	public static LocalDateTime getLastBackupTime() { return lastBackup; }

//	private static void autoSynchCheck() {
//		while (ScheduleManager.INSTANCE.isRunning()) {
//			try {
//				Log.info("Waiting {} msec.", 5*1000);
//				Thread.sleep(5*1000);
//				System.out.println("check!");
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//				Log.info("Auto Synch stopped.", e);
//				break;
//			}
//		}
//	}
	
	public static enum Folder {
		RECORD	("records"),
		BACKUP	("backup");
		
		private final String name;
		
		private Folder(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}
}
