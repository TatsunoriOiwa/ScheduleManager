package qwertzite.schedulemanager.client.screen;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import qwertzite.schedulemanager.client.ScheduleFilter;
import qwertzite.schedulemanager.schedule.ScheduleEntry;

public class MainScreen extends Composite {
	
	private PaneScheduleDetail detailPane;
	private PaneScheduleList listPane;
	@SuppressWarnings("unused")
	private PaneGlobal globalPane;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MainScreen(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashFormLeft = new SashForm(this, SWT.SMOOTH);
		sashFormLeft.setSashWidth(4);
		
		PaneScheduleList compositeList = new PaneScheduleList(sashFormLeft, SWT.BORDER, this);
		this.listPane = compositeList;
		
		SashForm sashFormBottom = new SashForm(sashFormLeft, SWT.SMOOTH | SWT.VERTICAL);
		sashFormBottom.setSashWidth(4);
		
		PaneScheduleDetail compositeCentre = new PaneScheduleDetail(sashFormBottom, SWT.BORDER);
		this.detailPane = compositeCentre;
		
		PaneGlobal compositeViewConf = new PaneGlobal(sashFormBottom, SWT.BORDER);
		this.globalPane = compositeViewConf;
		
		sashFormLeft.setWeights(new int[] {1, 1});
		sashFormBottom.setWeights(new int[] {2, 1});
	}
	
	public void mirrorScheduleContent() {
		this.listPane.mirrorListContent();
	}
	
	public void onSelectionChanged(ScheduleEntry entry, int pos, int len) {
		this.detailPane.setEntry(entry);
		this.listPane.mirrorButtonStateToEntry(entry, pos, len);
	}
	
	public void onDateUpdated(Object origin, ScheduleEntry entry, int newPos) {
//		if (origin != this.detailPane)
			this.detailPane.mirrorDate();
		if (origin != this.listPane) {
			if (newPos >= 0) this.listPane.mirrorEntryMove(entry, newPos);
			else this.listPane.mirrorEntryUpdate();
		}
	}
	
	public void onContentChanged(Object origin) {
		if (origin != this.detailPane) this.detailPane.mirrorContent();
		if (origin != this.listPane)   this.listPane.mirrorEntryUpdate();
	}
	
	public void onEntryInserted(int pos, ScheduleEntry entry) {
		this.detailPane.mirrorContent();
		this.listPane.mirrorEntryInsertion(entry, pos);
	}
	
	public void onEntryDeleted(ScheduleEntry entry) {
		this.listPane.mirrorEntryDeletion(entry);
		this.detailPane.setEntry(null);
	}
	
	public void onEntryUpDown(ScheduleEntry entry, int newPos) {
		this.listPane.mirrorEntryMove(entry, newPos);
	}
	
	public void markScheduleAsDone(ScheduleEntry entry) {
		this.listPane.mirrorEntryDone(entry);
	}
	
	public void onFIlterChanged(ScheduleFilter filter) {
		this.listPane.mirrorFilter(filter);
	}
	
	public void mirrorSynchStatus() {
		this.listPane.mirrorSynchStat();
	}
	
	public void enableSynchBtn(boolean enable) {
		this.listPane.enableSynchBtn(enable);
	}
	
	public void enableBackupBtn() {
		this.listPane.enableBackupBtn();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
