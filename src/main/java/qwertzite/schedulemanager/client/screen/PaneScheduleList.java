package qwertzite.schedulemanager.client.screen;

import java.time.LocalDateTime;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.wb.swt.SWTResourceManager;

import qwertzite.schedulemanager.client.ScheduleFilter;
import qwertzite.schedulemanager.client.ScheduleManager;
import qwertzite.schedulemanager.schedule.ScheduleEntry;
import qwertzite.schedulemanager.schedule.Schedules;
import qwertzite.schedulemanager.storage.ScheduleStorage;
import qwertzite.schedulemanager.util.StringUtil;
import qwertzite.schedulemanager.util.TimeDateUtil;

public class PaneScheduleList extends Composite {
	@SuppressWarnings("unused")
	private MainScreen mainScreen;
//	
//	private Table table;
//	
//	private TableEditor editor;
	
	private ListViewer listViewer;
	private Button btnUp;
	private Button btnDown;
	private Button btnDone;
	private Button btnDelete;
	private CTabFolder tabFolder;
	private CTabItem tbtmSchedule;
	private CTabItem tbtmRecord;
	private List list_1;
	private ListViewer listViewer_2;
	private Button btnCopy;
	private Button btnSynch;
	private Label label;
	private Button btnBackUp;
	private Label lblLastSynch;
	private Label lblLastBackup;
	private Button btnNormalise;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PaneScheduleList(Composite parent, int style, MainScreen mainScreen) {
		super(parent, style);
		
		this.mainScreen = mainScreen;
		setLayout(new GridLayout(7, false));
		
		btnSynch = new Button(this, SWT.NONE);
		btnSynch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnSynch.setImage(SWTResourceManager.getImage(PaneScheduleList.class, "/assets/schedulemanager/icons/synch.png"));
		btnSynch.addSelectionListener(SelectionListener.widgetSelectedAdapter(this::onSynch));
		
		lblLastSynch = new Label(this, SWT.NONE);
		lblLastSynch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		lblLastSynch.setText("- -:- -:- -");
		
		lblLastBackup = new Label(this, SWT.NONE);
		lblLastBackup.setAlignment(SWT.RIGHT);
		lblLastBackup.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1));
		lblLastBackup.setText(" - - - -/- -/- -");
		
		btnBackUp = new Button(this, SWT.NONE);
		btnBackUp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnBackUp.setText("BACKUP");
		btnBackUp.addSelectionListener(SelectionListener.widgetSelectedAdapter(this::onBackUp));
		
		label = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 7, 1));
		
		Button btnAdd = new Button(this, SWT.NONE);
		btnAdd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnAdd.setText("  INSERT  ");
		btnAdd.addSelectionListener(SelectionListener.widgetSelectedAdapter(this::onInsert));
		
		btnUp = new Button(this, SWT.NONE);
		btnUp.setText("↑");
		btnUp.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> this.onUpDown(e, true)));
		
		btnNormalise = new Button(this, SWT.NONE);
		btnNormalise.setText("・");
		btnNormalise.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> this.onNormalisePosition(e)));
		
		btnDown = new Button(this, SWT.NONE);
		btnDown.setText("↓");
		btnDown.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> this.onUpDown(e, false)));
		
		btnDone = new Button(this, SWT.NONE);
		btnDone.setText("  DONE  ");
		btnDone.addSelectionListener(SelectionListener.widgetSelectedAdapter(this::onDonePressed));
		new Label(this, SWT.NONE);
		
		btnCopy = new Button(this, SWT.NONE);
		btnCopy.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnCopy.setText("  COPY  ");
		btnCopy.addSelectionListener(SelectionListener.widgetSelectedAdapter(this::onCopyPressed));
		
		tabFolder = new CTabFolder(this, SWT.BORDER);
		tabFolder.addMouseListener(MouseListener.mouseDownAdapter(this::onFolderClicked));
		tabFolder.setTabHeight(30);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 7, 1));
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		tbtmSchedule = new CTabItem(tabFolder, SWT.NONE);
		tbtmSchedule.setText("SCHEDULES");
		
		ListViewer listViewer_1 = new ListViewer(tabFolder);
		listViewer_1.setUseHashlookup(true);
		List list = listViewer_1.getList();
		tbtmSchedule.setControl(list);
		listViewer_1.addSelectionChangedListener(this::onSelectionChanged);
		listViewer_1.setContentProvider(new ScheduleContentProvider());
		listViewer_1.setLabelProvider(new LabelProvider());
//		listViewer_1.setInput(ScheduleManager.INSTANCE.schedules);
		this.listViewer = listViewer_1;
		
		tbtmRecord = new CTabItem(tabFolder, SWT.NONE);
		tbtmRecord.setText("RECORDS");
		
		listViewer_2 = new ListViewer(tabFolder, SWT.BORDER | SWT.V_SCROLL);
		listViewer_2.setUseHashlookup(true);
		list_1 = listViewer_2.getList();
		listViewer_2.setContentProvider(new RecordContentProvider());
		listViewer_2.setLabelProvider(new LabelProviderPast());
//		listViewer_2.setInput(ScheduleManager.INSTANCE.schedules);
		tbtmRecord.setControl(list_1);
		
		tabFolder.setSelection(this.tbtmSchedule);
		
		btnDelete = new Button(this, SWT.NONE);
		btnDelete.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnDelete.setText("  DELETE  ");
		btnDelete.addSelectionListener(SelectionListener.widgetSelectedAdapter(this::onDelete));
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
//		Table table = new Table(this, SWT.BORDER | SWT.MULTI);
//		table.setLinesVisible (true);
//		this.table = table;
//		for (int i=0; i<3; i++) {
//			TableColumn column = new TableColumn (table, SWT.NONE);
//			column.setWidth(100);
//			
//		}
//		for (int i=0; i<30; i++) {
//			TableItem item = new TableItem (table, SWT.NONE);
//			item.setText(new String [] {"" + i, "" + i , "" + i});
//		}
//		
//		final TableEditor editor = new TableEditor (table);
//		editor.horizontalAlignment = SWT.LEFT;
//		editor.grabHorizontal = true;
//		table.addListener(SWT.MouseDown, this::onTableEdit);
//		this.editor = editor;
		
		
		
	}
	
//	private void onTableEdit(Event event) {
//		Rectangle clientArea = table.getClientArea ();
//		Point pt = new Point(event.x, event.y);
//		int index = table.getTopIndex();
//		while (index < table.getItemCount()) {
//			boolean visible = false;
//			final TableItem item = table.getItem(index);
//			for (int i=0; i<table.getColumnCount (); i++) {
//				Rectangle rect = item.getBounds(i);
//				if (rect.contains (pt)) {
//					final int column = i;
//					final Text text = new Text (table, SWT.NONE);
//					Listener textListener = e -> {
//						switch (e.type) {
//							case SWT.FocusOut:
//								item.setText (column, text.getText ());
//								text.dispose ();
//								break;
//							case SWT.Traverse:
//								switch (e.detail) {
//									case SWT.TRAVERSE_RETURN:
//										item.setText (column, text.getText ());
//										//FALL THROUGH
//									case SWT.TRAVERSE_ESCAPE:
//										text.dispose ();
//										e.doit = false;
//								}
//								break;
//							case SWT.KeyDown:
//								break;
//						}
//					};
//					text.addListener (SWT.FocusOut, textListener);
//					text.addListener (SWT.Traverse, textListener);
//					text.addKeyListener(KeyListener.keyPressedAdapter(e -> this.keyPressedListener(e, text, item)));
//					editor.setEditor(text, item, i);
//					text.setText (item.getText (i));
//					text.selectAll();
//					text.setFocus();
//					return;
//				}
//				if (!visible && rect.intersects (clientArea)) {
//					visible = true;
//				}
//			}
//			if (!visible) return;
//			index++;
//		}
//	}
	
//	private void keyPressedListener(KeyEvent keyEvent, Text text, TableItem item) {
//		switch(keyEvent.keyCode) {
//		case SWT.ARROW_UP:
//			System.out.println("up");
//			int index = this.table.getSelectionIndex();
//			System.out.println("selection " + index);
//			if (index > 0) {
//				this.table.select(index -1);
//			}
//			break;
//		case SWT.ARROW_DOWN:
//			System.out.println("down");
//			break;
//		case SWT.ARROW_LEFT:
//			System.out.println("left");
//			break;
//		case SWT.ARROW_RIGHT:
//			System.out.println("right");
//			break;
//		}
//	}
	
	private void onSelectionChanged(SelectionChangedEvent event) {
		ScheduleManager.INSTANCE.onSelectionChanged((ScheduleEntry) event.getStructuredSelection().getFirstElement());
	}
	
	private void onInsert(SelectionEvent event) {
		ScheduleManager.INSTANCE.addSchedule();
	}
	
	private void onDelete(SelectionEvent event) {
		ScheduleManager.INSTANCE.deleteSchedule();
	}
	
	private void onUpDown(SelectionEvent event, boolean up) {
		ScheduleManager.INSTANCE.onEntryUpDown(up);
	}
	
	private void onNormalisePosition(SelectionEvent event) {
		ScheduleManager.INSTANCE.getSelection().setOutOfOrder(false);
		ScheduleManager.INSTANCE.onDateChanged(null);
	}
	
	private void onDonePressed(SelectionEvent event) {
		ScheduleManager.INSTANCE.onMarkDone();
	}
	
	private void onCopyPressed(SelectionEvent event) {
		ScheduleManager.INSTANCE.copySchedule();
	}
	
	private void onSynch(SelectionEvent event) {
		this.btnSynch.setEnabled(false);
		ScheduleManager.INSTANCE.synchData();
	}
	
	private void onBackUp(SelectionEvent event) {
		this.btnBackUp.setEnabled(false);
		ScheduleManager.INSTANCE.createBackUp();
	}
	
	private void onFolderClicked(MouseEvent event) {
		this.listViewer.setSelection(new StructuredSelection());
		this.listViewer_2.setSelection(new StructuredSelection());
	}
	
	public void mirrorListContent() {
		listViewer.setInput(ScheduleManager.INSTANCE.schedules);
		listViewer_2.setInput(ScheduleManager.INSTANCE.schedules);
	}
	
	public void mirrorButtonStateToEntry(ScheduleEntry e, int pos, int len) {
		if (e == null) {
			this.btnUp.setEnabled(false);
			this.btnNormalise.setEnabled(false);
			this.btnDown.setEnabled(false);
			this.btnDelete.setEnabled(false);
			this.btnDone.setEnabled(false);
			this.btnCopy.setEnabled(false);
		} else {
			this.btnUp.setEnabled(pos > 0);
			this.btnNormalise.setEnabled(e.isOutOfOrder());
			this.btnDown.setEnabled(pos < len-1);
			this.btnDelete.setEnabled(true);
			this.btnDone.setEnabled(true);
			this.btnCopy.setEnabled(true);
		}
	}
	
	public void mirrorEntryUpdate() {
		this.listViewer.update(ScheduleManager.INSTANCE.getSelection(), null);
	}
	
	public void mirrorEntryInsertion(ScheduleEntry e, int pos) {
		ViewerFilter[] filters = this.listViewer.getFilters();
		this.listViewer.resetFilters();
		this.listViewer.insert(e, pos);
		this.listViewer.setFilters(filters);
		this.listViewer.setSelection(new StructuredSelection(e));
		this.listViewer.reveal(e);
		
	}
	
	public void mirrorEntryDeletion(ScheduleEntry entry) {
		this.listViewer.remove(entry);
		ScheduleManager.INSTANCE.onSelectionChanged((ScheduleEntry) this.listViewer.getStructuredSelection().getFirstElement());
	}
	
	public void mirrorEntryMove(ScheduleEntry entry, int newPos) {
		ViewerFilter[] filters = this.listViewer.getFilters();
		this.listViewer.resetFilters();
		this.listViewer.remove(entry);
		this.listViewer.insert(entry, newPos);
		this.listViewer.setFilters(filters);
		this.listViewer.setSelection(new StructuredSelection(entry));
		this.listViewer.reveal(entry);
		this.btnNormalise.setEnabled(entry.isOutOfOrder());
	}
	
	public void mirrorEntryDone(ScheduleEntry entry) {
		this.listViewer.remove(entry);
		this.listViewer_2.insert(entry, 0);
	}
	
	public void mirrorFilter(ScheduleFilter filter) {
		this.listViewer.setFilters(filter);
		ScheduleEntry entry = ScheduleManager.INSTANCE.getSelection();
		if (entry != null) {
			this.listViewer.reveal(entry);
		}
	}
	
	public void mirrorSynchStat() {
		switch(ScheduleStorage.getSynchStatus()) {
		case OFFLINE:
			this.btnSynch.setImage(SWTResourceManager.getImage(PaneScheduleList.class, "/assets/schedulemanager/icons/synch.png"));
			break;
		case SYNCH_NG:
			this.btnSynch.setImage(SWTResourceManager.getImage(PaneScheduleList.class, "/assets/schedulemanager/icons/synch_ng.png"));
			break;
		case SYNCH_OK:
			this.btnSynch.setImage(SWTResourceManager.getImage(PaneScheduleList.class, "/assets/schedulemanager/icons/synch_ok.png"));
			break;
		default:
			break;
		}
		LocalDateTime lastsynch = ScheduleStorage.getSynchTimestamp();
		this.lblLastSynch.setText(lastsynch == null ? "- -:- -:- -" : lastsynch.format(TimeDateUtil.ISO_TIME_NONFRACTION));
	}
	
	public void enableSynchBtn(boolean enable) {
		this.btnSynch.setEnabled(true);
	}
	
	public void enableBackupBtn() {
		this.btnBackUp.setEnabled(true);
		LocalDateTime time = ScheduleStorage.getLastBackupTime();
		this.lblLastBackup.setText(time == null ? " - - - -/- -/- -" : time.format(TimeDateUtil.SLASH_DATE_SIMPLE));
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	public static class ScheduleContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {
			Schedules schedules = (Schedules) inputElement;
			return schedules.getEntries().toArray();
		}
		
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
	
	public static class RecordContentProvider implements IStructuredContentProvider {
		@Override
		public Object[] getElements(Object inputElement) {
			Schedules schedules = (Schedules) inputElement;
			return schedules.getRecords().toArray();
		}
	}
	
	public static class LabelProvider implements ILabelProvider {

		@Override
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Image getImage(Object element) {
			return null;
		}

		@Override
		public String getText(Object element) {
			ScheduleEntry entry = (ScheduleEntry) element;
			
			return (!entry.hasMonth()? "__ "   : StringUtil.zeroFill(entry.getMonth().getIndex())) + "/"
				 + (!entry.hasDay()  ? "__  　"  : (StringUtil.zeroFill(entry.getDay()) + " " + entry.getDayOfWeekString())) + " - "
				 + (!entry.hasTime() ? " ____ ": StringUtil.zeroFill4(entry.getTime())) + " : "
				 + entry.getTitle();
		}
	}
	
	public static class LabelProviderPast implements ILabelProvider {

		@Override
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Image getImage(Object element) {
			return null;
		}

		@Override
		public String getText(Object element) {
			ScheduleEntry entry = (ScheduleEntry) element;
			LocalDateTime completed = entry.getCompletedTime();
			return (completed != null ? ("(" + StringUtil.zeroFill(completed.getMonthValue()) + "/" + StringUtil.zeroFill(completed.getDayOfMonth()) + " "+ TimeDateUtil.dayOfWeekString(completed.getDayOfWeek()) + ") ")
					: "(__ /__  　) ")
				 + (!entry.hasMonth()? "__ "   : StringUtil.zeroFill(entry.getMonth().getIndex())) + "/"
				 + (!entry.hasDay()  ? "__  　"  : (StringUtil.zeroFill(entry.getDay()) + " " + entry.getDayOfWeekString()))
//				 + " - "
//				 + (!entry.hasTime() ? " ____ ": StringUtil.zeroFill4(entry.getTime()))
				 + " : "
				 + entry.getTitle();
		}
	}
}
