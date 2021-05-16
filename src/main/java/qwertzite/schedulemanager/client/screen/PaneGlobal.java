package qwertzite.schedulemanager.client.screen;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import qwertzite.schedulemanager.client.ScheduleFilter;
import qwertzite.schedulemanager.client.ScheduleManager;
import qwertzite.schedulemanager.tag.EnumIsPending;
import qwertzite.schedulemanager.tag.EnumPublicness;
import qwertzite.schedulemanager.tag.EnumUrgent;

public class PaneGlobal extends Composite {
	
	private Button btnPublicness;
	private Button rdbPublic;
	private Button rdbPrivate;
	private Button btnTaskOnly;
	private Button btnHidePlaceholder;
	private Button btnPending;
	private Button rdbHidePending;
	private Button rdbPendingOnly;
	private Composite cmpUrgency;
	private Button btnAllUrgency;
	private Button btnReduced;
	private Button btnImportant;
	private Button btnUrgentOnly;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PaneGlobal(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		CTabFolder tabFolder = new CTabFolder(this, SWT.BORDER);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem tbtmView = new CTabItem(tabFolder, SWT.NONE);
		tbtmView.setText("VIEW");
		tabFolder.setSelection(tbtmView);
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmView.setControl(composite);
		composite.setLayout(new GridLayout(2, false));
		
		btnPublicness = new Button(composite, SWT.CHECK);
		btnPublicness.setText("PUBLICNESS");
		btnPublicness.addSelectionListener(SelectionListener.widgetSelectedAdapter(this::onBtnPublicnessSelected));
		
		Composite cmpPublicness = new Composite(composite, SWT.BORDER);
		GridLayout gl_cmpPublicness = new GridLayout(2, true);
		gl_cmpPublicness.marginHeight = 0;
		cmpPublicness.setLayout(gl_cmpPublicness);
		cmpPublicness.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		rdbPublic = new Button(cmpPublicness, SWT.RADIO);
		rdbPublic.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		rdbPublic.setText("PUBLIC");
		rdbPublic.addSelectionListener(SelectionListener.widgetSelectedAdapter(this::onRdbPublicSelected));
		
		rdbPrivate = new Button(cmpPublicness, SWT.RADIO);
		rdbPrivate.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		rdbPrivate.setText("PRIVATE");
		rdbPrivate.addSelectionListener(SelectionListener.widgetSelectedAdapter(this::onRdbPrivateSelected));
		
		btnTaskOnly = new Button(composite, SWT.CHECK);
		btnTaskOnly.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnTaskOnly.setText("TASK ONLY");
		btnTaskOnly.addSelectionListener(SelectionListener.widgetSelectedAdapter(this::onBtnTaskOnlySelected));
		new Label(composite, SWT.NONE);
		
		btnHidePlaceholder = new Button(composite, SWT.CHECK);
		btnHidePlaceholder.setText("HIDE PLACEHOLDER");
		btnHidePlaceholder.addSelectionListener(SelectionListener.widgetSelectedAdapter(this::onBtnHidePlaceholderSelected));
		new Label(composite, SWT.NONE);
		
		btnPending = new Button(composite, SWT.CHECK);
		btnPending.setText("PENDING");
		btnPending.addSelectionListener(SelectionListener.widgetSelectedAdapter(this::onBtnPendingSelected));
		
		Composite cmpPending = new Composite(composite, SWT.BORDER);
		GridLayout gl_cmpPending = new GridLayout(2, true);
		gl_cmpPending.marginHeight = 0;
		cmpPending.setLayout(gl_cmpPending);
		cmpPending.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		
		rdbHidePending = new Button(cmpPending, SWT.RADIO);
		rdbHidePending.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		rdbHidePending.setText("HIDE");
		rdbHidePending.addSelectionListener(SelectionListener.widgetSelectedAdapter(this::onRdbHidePending));
		
		rdbPendingOnly = new Button(cmpPending, SWT.RADIO);
		rdbPendingOnly.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		rdbPendingOnly.setText("ONLY");
		rdbPendingOnly.addSelectionListener(SelectionListener.widgetSelectedAdapter(this::onRdbPendingOnly));
		
		cmpUrgency = new Composite(composite, SWT.NONE);
		GridLayout gl_cmpUrgency = new GridLayout(2, true);
		gl_cmpUrgency.marginWidth = 0;
		gl_cmpUrgency.marginHeight = 0;
		cmpUrgency.setLayout(gl_cmpUrgency);
		cmpUrgency.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		
		btnAllUrgency = new Button(cmpUrgency, SWT.RADIO);
		btnAllUrgency.setText("ALL");
		btnAllUrgency.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> this.onRdbUrgency(e, EnumUrgent.NON_URGENT, btnAllUrgency)));
		
		btnReduced = new Button(cmpUrgency, SWT.RADIO);
		btnReduced.setText("REDUCED");
		btnReduced.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> this.onRdbUrgency(e, EnumUrgent.NORMAL, btnReduced)));
		
		btnImportant = new Button(cmpUrgency, SWT.RADIO);
		btnImportant.setText("IMPORTANT");
		btnImportant.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> this.onRdbUrgency(e, EnumUrgent.IMPORTANT, btnImportant)));
		
		btnUrgentOnly = new Button(cmpUrgency, SWT.RADIO);
		btnUrgentOnly.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnUrgentOnly.setText("URGENT");
		btnUrgentOnly.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> this.onRdbUrgency(e, EnumUrgent.URGENT, btnUrgentOnly)));
		
		this.mirrorFilter(ScheduleManager.INSTANCE.filter);
	}
	
	private void onBtnPublicnessSelected(SelectionEvent event) {
		boolean selection = this.btnPublicness.getSelection();
		this.rdbPublic.setEnabled(selection);
		this.rdbPrivate.setEnabled(selection);
		ScheduleManager sm = ScheduleManager.INSTANCE;
		sm.filter.setUsePublicness(selection);
		sm.onFilterChanged();
	}
	
	private void onRdbPublicSelected(SelectionEvent event) {
		if (!this.rdbPublic.getSelection()) { return; }
		ScheduleManager sm = ScheduleManager.INSTANCE;
		sm.filter.setVisiblePublicness(EnumPublicness.PUBLIC);
		sm.onFilterChanged();
	}
	
	private void onRdbPrivateSelected(SelectionEvent event) {
		if (!this.rdbPrivate.getSelection()) { return; }
		ScheduleManager sm = ScheduleManager.INSTANCE;
		sm.filter.setVisiblePublicness(EnumPublicness.PRIVATE);
		sm.onFilterChanged();
	}
	
	private void onBtnTaskOnlySelected(SelectionEvent event) {
		ScheduleManager sm = ScheduleManager.INSTANCE;
		sm.filter.setShowTaskOnly(this.btnTaskOnly.getSelection());
		sm.onFilterChanged();
	}
	
	private void onBtnHidePlaceholderSelected(SelectionEvent event) {
		ScheduleManager sm = ScheduleManager.INSTANCE;
		sm.filter.setHidePlaceholder(this.btnHidePlaceholder.getSelection());
		sm.onFilterChanged();
	}
	
	private void onBtnPendingSelected(SelectionEvent event) {
		this.rdbHidePending.setEnabled(this.btnPending.getSelection());
		this.rdbPendingOnly.setEnabled(this.btnPending.getSelection());
		ScheduleManager sm = ScheduleManager.INSTANCE;
		sm.filter.setUsePending(this.btnPending.getSelection());
		sm.onFilterChanged();
	}
	
	private void onRdbHidePending(SelectionEvent event) {
		if (!this.rdbPublic.getSelection()) { return; }
		ScheduleManager sm = ScheduleManager.INSTANCE;
		sm.filter.setVisiblePending(EnumIsPending.FALSE);
		sm.onFilterChanged();
	}
	
	private void onRdbPendingOnly(SelectionEvent event) {
		if (!this.rdbPublic.getSelection()) { return; }
		ScheduleManager sm = ScheduleManager.INSTANCE;
		sm.filter.setVisiblePending(EnumIsPending.TRUE);
		sm.onFilterChanged();
	}
	
	private void onRdbUrgency(SelectionEvent event, EnumUrgent urgency, Button widget) {
		if (widget.getSelection()) {
			ScheduleManager sm = ScheduleManager.INSTANCE;
			sm.filter.setUrgency(urgency);
			sm.onFilterChanged();
		}
	}
	
	public void mirrorFilter(ScheduleFilter filter) {
		boolean flag;
		flag = filter.isUsePublicness();
		this.btnPublicness.setSelection(flag);
		this.rdbPublic.setSelection(filter.getVisiblePublicness() == EnumPublicness.PUBLIC);
		this.rdbPublic.setEnabled(flag);
		this.rdbPrivate.setSelection(filter.getVisiblePublicness() == EnumPublicness.PRIVATE);
		this.rdbPrivate.setEnabled(flag);
		this.btnTaskOnly.setSelection(filter.isShowTaskOnly());
		this.btnHidePlaceholder.setSelection(filter.isHidePlaceholder());
		flag = filter.isUsePending();
		this.btnPending.setSelection(flag);
		this.rdbHidePending.setSelection(filter.getVisiblePending() == EnumIsPending.FALSE);
		this.rdbHidePending.setEnabled(flag);
		this.rdbPendingOnly.setSelection(filter.getVisiblePending() == EnumIsPending.TRUE);
		this.rdbPendingOnly.setEnabled(flag);
		
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
