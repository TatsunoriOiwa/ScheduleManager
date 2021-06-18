package qwertzite.schedulemanager.client;

import org.apache.logging.log4j.Level;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import qwertzite.schedulemanager.Log;
import qwertzite.schedulemanager.client.screen.MainScreen;
import qwertzite.schedulemanager.schedule.ScheduleEntry;
import qwertzite.schedulemanager.schedule.Schedules;
import qwertzite.schedulemanager.storage.ScheduleStorage;

public class ScheduleManager {

	public static final String GAMENAME = "ScheduleManager";
	public static final String GAME_ID = "schedulemanag";
	public static final String VERSION = "2.2.2";
	public static final String VERSION_SERIAL = "0018";
	
	public static ScheduleManager INSTANCE = null;

	private Display display;
	private Shell shell;
	private StackLayout shellLayout;
	private MainScreen currentScren;
//	private Runnable renderDispatcher;
//	private Consumer<RenderEvent> mainRenderListener = e -> {};
	
	
	private boolean isRunning;
	
	public Schedules schedules;
	private ScheduleEntry selectedEntry;
	public ScheduleFilter filter;
	

	public ScheduleManager() {
		if (INSTANCE != null) throw new IllegalStateException("ScheduleManager is already running!");
		INSTANCE = this;
	}
	
	public void init() {
		Log.setLogLevel(Level.DEBUG);
		Log.info("Initialising application.");
		// configs may come here
		
		this.schedules = new Schedules();
		this.filter = new ScheduleFilter();

		this.display = Display.getDefault();
		this.shell = new Shell(this.display);
		this.shellLayout = new StackLayout();
		this.shell.setLayout(this.shellLayout);
//		this.shell.setSize(LaunchConf.windowWidth, LaunchConf.windowHeight);
		this.shell.setSize(880, 720);
//		this.shell.setMaximized(LaunchConf.isMaximised);
		this.shell.setText(GAMENAME + " " + VERSION);
//		this.shell.addDisposeListener(EventFactory::mainShellDisposedEvent);
		this.shell.setModified(true);
		MainScreen splashScreen = new MainScreen(shell);
		this.shellLayout.topControl = splashScreen;
		shell.layout();
		shell.open();
		this.currentScren = splashScreen;
		
		Log.info("Initialising storage system and loading schedule data...");
		ScheduleStorage.onInit();
		
		this.mirrorScheduleContent();
		this.mirrorSynchStatus();
		
//		Runnable runnable = new Runnable() {
////			private int cnt = 0;
////			private boolean hasRan = false;
////			private long lastTime = System.currentTimeMillis();
//			public void run() {
////				long currentTime = System.currentTimeMillis();
//////				RenderEvent event = new RenderEvent(this.cnt++, hasRan ? (int)(currentTime - lastTime) : 0);
//////				mainRenderListener.accept(event);
//////				EventFactory.renderEvent(event);
////				this.hasRan = true;
////				if (this.cnt < 0) cnt = 0;
////				this.lastTime = currentTime;
////				display.timerExec(ClientSettings.getRenderInterval(), this);
//				display.timerExec(1000, this);
//			}
//		};
//		display.timerExec(1000, runnable);
//		display.timerExec(ClientSettings.getRenderInterval(), runnable);
//		this.renderDispatcher = runnable;
//		shell.layout();
//		shell.redraw();
		Log.info("Initiated");
	}
	
	public void run() {
		this.setRunning(true);
		this.init();
		
		while (!shell.isDisposed()) {
			if (!this.isRunning) {
				shell.dispose();
				break;
			}
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		this.shutDown();
	}
	
	public void shutDown() {
		Log.info("Shutting down...");
		this.isRunning = false;
		
		// configurations
		
		ScheduleStorage.onShutdown();

//		ScheduleStorage.save(true);
		ScheduleStorage.synch(false, false);
		
		// client
//		display.timerExec(-1, this.renderDispatcher); // prevent runnable from being called
		display.timerExec(-1, () -> {}); // prevent runnable from being called
		display.dispose();
		
		Log.info("Shutdown completed.");
	}
	
	public void setRunning(boolean flag) { this.isRunning = flag; }
	public boolean isRunning() { return this.isRunning; }
	
	public void mirrorScheduleContent() {
		this.currentScren.mirrorScheduleContent();
		this.onSelectionChanged(null);
	}
	
	public void onSelectionChanged(ScheduleEntry entry) {
		this.setSelection(entry);
		this.currentScren.onSelectionChanged(entry, this.schedules.getIndexOf(this.selectedEntry), this.schedules.getEntryNum());
	}
	
	/**
	 * Update schedule date and move to correct position.
	 * @param origin
	 */
	public void onDateChanged(Object origin) {
		int pos = this.schedules.updatePosition(this.selectedEntry, true);
		this.currentScren.onDateUpdated(origin, this.selectedEntry, pos);
		this.save(false);
	}
	
	/** call this to notify other panes. */
	public void onSummaryChanged(Object origin) {
		this.currentScren.onContentChanged(origin);
		this.save(false);
	}
	
	public void onContentSave() {
		this.save(false);
	}
	
	public void onEntryUpDown(boolean up) {
		int newPos = this.schedules.moveEntryUpDown(this.selectedEntry, up);
		this.currentScren.onEntryUpDown(this.selectedEntry, newPos);
		this.save(false);
	}
	
	public void addSchedule() {
		ScheduleEntry n = new ScheduleEntry();
		int pos = this.schedules.insertNewSchedule(this.getSelection(), n);
//		this.selectedEntry = n;
		this.currentScren.onEntryInserted(pos, n);
		this.save(false);
	}
	
	public void deleteSchedule() {
		ScheduleEntry old = this.getSelection();
		this.setSelection(null);
		this.schedules.deleteSchedule(old);
		this.currentScren.onEntryDeleted(old);
		this.save(false);
	}
	
	public void onMarkDone() {
		this.schedules.markScheduleAsDone(this.selectedEntry);
		this.currentScren.markScheduleAsDone(this.selectedEntry);
		this.save(true);
	}
	
	public void copySchedule() {
		ScheduleEntry copy = this.selectedEntry.clone();
		int pos = this.schedules.insertNewSchedule(this.selectedEntry, copy);
		this.selectedEntry = copy;
		this.currentScren.onEntryInserted(pos, copy);
		this.save(false);
	}
	
	public void onFilterChanged() {
		this.currentScren.onFIlterChanged(this.filter);
	}
	
	public void synchData() {
		ScheduleStorage.synch(true);
	}
	
	public void mirrorSynchStatus() {
		this.currentScren.mirrorSynchStatus();
	}
	
	public void enableSynchBtn(boolean enable) {
		this.currentScren.enableSynchBtn(enable);
	}
	
	public void createBackUp() {
		ScheduleStorage.createBackup();
	}
	
	public void enableBackup() {
		this.currentScren.enableBackupBtn();
	}
	
	private void save(boolean record) {
		ScheduleStorage.save(record);
	}
	
	// ==== getters and setters ====
	
	public Display getDisplay() {
		return this.display; 
	}
	
	public Shell getShell() {
		return this.shell;
	}
	
	private void setSelection(ScheduleEntry entry) {
		if (selectedEntry == entry) return;
		this.selectedEntry = entry;
	}
	
	public ScheduleEntry getSelection() {
		return this.selectedEntry;
	}
}
