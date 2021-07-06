package qwertzite.schedulemanager.client.screen;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import qwertzite.schedulemanager.client.ScheduleManager;
import qwertzite.schedulemanager.schedule.ScheduleEntry;
import qwertzite.schedulemanager.tag.EnumIsPending;
import qwertzite.schedulemanager.tag.EnumIsPlaceHolder;
import qwertzite.schedulemanager.tag.EnumPublicness;
import qwertzite.schedulemanager.tag.EnumScheduleType;
import qwertzite.schedulemanager.tag.EnumUrgent;
import qwertzite.schedulemanager.util.StringUtil;
import qwertzite.schedulemanager.util.datetime.EnumMonth;
import qwertzite.schedulemanager.util.swt.WidgetUtil;

public class PaneScheduleDetail extends Composite {
	private ScheduleEntry entry;
	
	private Text yearField;
	private Text monthField;
	private Text dayField;
	private Text timeField;
	private Text titleField;
//	private Label labelSlash1;
//	private Label labelSlash2;
	private Label labelDoW;

	private Text textMemo;
	
	private Label lblPublicness;
	private Button[] publicness = new Button[2];
	private Label lblScheduleType;
	private Button[] scheduletype = new Button[2];
	private Button btnPlaceholder;
	private Button btnPending;
	private Button[] urgency = new Button[4];
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PaneScheduleDetail(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(7, false));
		
		yearField = new Text(this, SWT.BORDER);
		GridData gd_yearField = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_yearField.widthHint = 20;
		yearField.setLayoutData(gd_yearField);
		yearField.setTextLimit(2);
		yearField.addFocusListener(FocusListener.focusLostAdapter(this::onYearFocusLost));
		yearField.addFocusListener(FocusListener.focusGainedAdapter(e -> this.onTextFieldSelected(e, yearField)));
		yearField.addKeyListener(KeyListener.keyPressedAdapter(this::onKeyPressedYear));
		yearField.addTraverseListener(this::onTraverseYear);
		
		Label labelSlash1 = new Label(this, SWT.NONE);
		labelSlash1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		labelSlash1.setText("/");

		monthField = new Text(this, SWT.BORDER);
		GridData gd_monthField = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_monthField.widthHint = 20;
		monthField.setLayoutData(gd_monthField);
		monthField.setTextLimit(2);
		monthField.addFocusListener(FocusListener.focusLostAdapter(this::onMonthFocusLost));
		monthField.addFocusListener(FocusListener.focusGainedAdapter(e -> this.onTextFieldSelected(e, monthField)));
		monthField.addKeyListener(KeyListener.keyPressedAdapter(this::onKeyPressedMonth));
		monthField.addTraverseListener(this::onTraverseMonth);
		
		Label labelSlash2 = new Label(this, SWT.NONE);
		labelSlash2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		labelSlash2.setText("/");
		
		dayField = new Text(this, SWT.BORDER);
		GridData gd_dayField = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_dayField.widthHint = 20;
		dayField.setLayoutData(gd_dayField);
		dayField.setTextLimit(2);
		dayField.addFocusListener(FocusListener.focusLostAdapter(this::onDayFocusLost));
		dayField.addFocusListener(FocusListener.focusGainedAdapter(e -> this.onTextFieldSelected(e, dayField)));
		dayField.addKeyListener(KeyListener.keyPressedAdapter(this::onKeyPressedDay));
		dayField.addTraverseListener(this::onTraverseDay);
		
		labelDoW = new Label(this, SWT.NONE);
		labelDoW.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		labelDoW.setText("曜");
		
		timeField = new Text(this, SWT.BORDER);
		GridData gd_timeField = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_timeField.widthHint = 40;
		timeField.setLayoutData(gd_timeField);
		timeField.setTextLimit(4);
		timeField.addFocusListener(FocusListener.focusLostAdapter(this::onTimeFocusLost));
		timeField.addFocusListener(FocusListener.focusGainedAdapter(e -> this.onTextFieldSelected(e, timeField)));
		timeField.addKeyListener(KeyListener.keyPressedAdapter(this::onKeyPressedTime));
		timeField.addTraverseListener(this::onTraverseTime);
		
		titleField = new Text(this, SWT.BORDER);
		titleField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 7, 1));
		titleField.addFocusListener(FocusListener.focusLostAdapter(this::onTitleFocusLost));
		titleField.addKeyListener(KeyListener.keyPressedAdapter(this::onKeyPressedTitle));
		titleField.addTraverseListener(this::onTraverseTitle);
		
		textMemo = new Text(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		WidgetUtil.makeTextAutoScroll(textMemo);
		textMemo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 7, 1));
		textMemo.addFocusListener(FocusListener.focusLostAdapter(this::onNoteFocusLost));
		textMemo.addKeyListener(KeyListener.keyPressedAdapter(this::onKeyPressedNote));
		
		Composite cmpTags = new Composite(this, SWT.BORDER);
		cmpTags.setLayout(new GridLayout(1, false));
		cmpTags.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 7, 1));
		
		Composite cmpPublicness = new Composite(cmpTags, SWT.NONE);
		cmpPublicness.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_cmpPublicness = new GridLayout(3, true);
		gl_cmpPublicness.marginWidth = 0;
		gl_cmpPublicness.marginHeight = 0;
		cmpPublicness.setLayout(gl_cmpPublicness);
		
		lblPublicness = new Label(cmpPublicness, SWT.NONE);
		lblPublicness.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblPublicness.setText("PUBLICNESS : ");
		
		Button btnPublic = new Button(cmpPublicness, SWT.RADIO);
		btnPublic.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnPublic.setSize(85, 21);
		btnPublic.setText("PUBLIC");
		btnPublic.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> this.onPublicnessSelection(e, EnumPublicness.PUBLIC, btnPublic)));
		this.publicness[EnumPublicness.PUBLIC.getIndex()] = btnPublic;
		
		Button btnPrivate = new Button(cmpPublicness, SWT.RADIO);
		btnPrivate.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnPrivate.setSize(82, 21);
		btnPrivate.setText("PRIVATE");
		btnPrivate.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> this.onPublicnessSelection(e, EnumPublicness.PRIVATE, btnPrivate)));
		this.publicness[EnumPublicness.PRIVATE.getIndex()] = btnPrivate;
		
		Composite cmpScheduleType = new Composite(cmpTags, SWT.NONE);
		cmpScheduleType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		GridLayout gl_cmpScheduleType = new GridLayout(3, true);
		gl_cmpScheduleType.marginWidth = 0;
		gl_cmpScheduleType.marginHeight = 0;
		cmpScheduleType.setLayout(gl_cmpScheduleType);
		
		lblScheduleType = new Label(cmpScheduleType, SWT.NONE);
		lblScheduleType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblScheduleType.setBounds(0, 0, 73, 21);
		lblScheduleType.setText("TYPE : ");
		
		Button btnTask = new Button(cmpScheduleType, SWT.RADIO);
		btnTask.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnTask.setText("TASK");
		btnTask.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> this.onScheduleTypeSelection(e, EnumScheduleType.TASK, btnTask)));
		this.scheduletype[EnumScheduleType.TASK.getIndex()] = btnTask;
		
		Button btnSchedule = new Button(cmpScheduleType, SWT.RADIO);
		btnSchedule.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnSchedule.setText("SCHEDULE");
		btnSchedule.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> this.onScheduleTypeSelection(e, EnumScheduleType.SCHEDULE, btnSchedule)));
		this.scheduletype[EnumScheduleType.SCHEDULE.getIndex()] = btnSchedule;
		
		Composite composite_1 = new Composite(cmpTags, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		GridLayout gl_composite_1 = new GridLayout(2, true);
		gl_composite_1.marginWidth = 0;
		gl_composite_1.marginHeight = 0;
		composite_1.setLayout(gl_composite_1);
		
		btnPlaceholder = new Button(composite_1, SWT.CHECK);
		btnPlaceholder.setText("PLACEHOLDER");
		btnPlaceholder.addSelectionListener(SelectionListener.widgetSelectedAdapter(this::onPlaceholderSelection));
		
		btnPending = new Button(composite_1, SWT.CHECK);
		btnPending.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnPending.setText("PENDING");
		btnPending.addSelectionListener(SelectionListener.widgetSelectedAdapter(this::onPendingSelection));
		
		Composite composite = new Composite(cmpTags, SWT.NONE);
		GridLayout gl_composite = new GridLayout(2, true);
		gl_composite.marginHeight = 0;
		gl_composite.marginWidth = 0;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Button btnUrgent = new Button(composite, SWT.RADIO);
		btnUrgent.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnUrgent.setText("URGENT");
		btnUrgent.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> this.onUrgencySelected(e, EnumUrgent.URGENT, btnUrgent)));
		this.urgency[EnumUrgent.URGENT.getIndex()] = btnUrgent;
		
		Button btnImportant = new Button(composite, SWT.RADIO);
		btnImportant.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnImportant.setText("IMPORTANT");
		btnImportant.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> this.onUrgencySelected(e, EnumUrgent.IMPORTANT, btnImportant)));
		this.urgency[EnumUrgent.IMPORTANT.getIndex()] = btnImportant;
		
		Button btnNormal = new Button(composite, SWT.RADIO);
		btnNormal.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnNormal.setText("NORMAL");
		btnNormal.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> this.onUrgencySelected(e, EnumUrgent.NORMAL, btnNormal)));
		this.urgency[EnumUrgent.NORMAL.getIndex()] = btnNormal;
		
		Button btnNonUrgent = new Button(composite, SWT.RADIO);
		btnNonUrgent.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnNonUrgent.setText("NonURGENT");
		btnNonUrgent.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> this.onUrgencySelected(e, EnumUrgent.NON_URGENT, btnNonUrgent)));
		this.urgency[EnumUrgent.NON_URGENT.getIndex()] = btnNonUrgent;
		
		this.setEntry(null);
	}
	
	private void onTextFieldSelected(FocusEvent e, Text text) {
		text.selectAll();
	}
	
	private void onTraverseYear(TraverseEvent event) {
		switch(event.detail) {
		case SWT.TRAVERSE_RETURN:
			this.updateYear(true);
			break;
		case SWT.TRAVERSE_ARROW_PREVIOUS:
			break;
		case SWT.TRAVERSE_ARROW_NEXT:
			if (this.yearField.getCaretPosition() == this.yearField.getText().length() && this.yearField.getSelectionCount() == 0) {
				this.monthField.setFocus();
			}
		}
	}
	
	private void onKeyPressedYear(KeyEvent event) {
		switch(event.keyCode) {
		case SWT.ARROW_UP:
			break;
		case SWT.ARROW_DOWN:
			this.monthField.setFocus();
			break;
		}
	}
	
	private void onYearFocusLost(FocusEvent event) {
		this.updateYear(true);
	}

	private void onTraverseMonth(TraverseEvent event) {
		switch(event.detail) {
		case SWT.TRAVERSE_RETURN:
			this.updateMonth();
			break;
		case SWT.TRAVERSE_ARROW_PREVIOUS:
			if (this.monthField.getCaretPosition() == 0) { this.yearField.setFocus(); }
			break;
		case SWT.TRAVERSE_ARROW_NEXT:
			if (this.monthField.getCaretPosition() == this.monthField.getText().length() && this.monthField.getSelectionCount() == 0) {
				this.dayField.setFocus();
			}
		}
	}
	
	private void onKeyPressedMonth(KeyEvent event) {
		switch(event.keyCode) {
		case SWT.ARROW_UP:
			this.yearField.setFocus();
			break;
		case SWT.ARROW_DOWN:
			this.dayField.setFocus();
			break;
		}
	}
	
	private void onMonthFocusLost(FocusEvent event) {
		this.updateMonth();
	}

	private void onTraverseDay(TraverseEvent event) {
		switch(event.detail) {
		case SWT.TRAVERSE_RETURN:
			this.updateDay();
			break;
		case SWT.TRAVERSE_ARROW_PREVIOUS:
			if (this.dayField.getCaretPosition() == 0) { this.monthField.setFocus(); }
			break;
		case SWT.TRAVERSE_ARROW_NEXT:
			if (this.dayField.getCaretPosition() == this.dayField.getText().length() && this.dayField.getSelectionCount() == 0) {
				this.timeField.setFocus();
			}
		}
	}
	
	private void onKeyPressedDay(KeyEvent event) {
		switch(event.keyCode) {
		case SWT.ARROW_UP:
			this.monthField.setFocus();
			break;
		case SWT.ARROW_DOWN:
			this.timeField.setFocus();
			break;
		}
	}
	
	private void onDayFocusLost(FocusEvent event) {
		this.updateDay();
	}

	private void onTraverseTime(TraverseEvent event) {
		switch(event.detail) {
		case SWT.TRAVERSE_RETURN:
			this.updateTime();
			break;
		case SWT.TRAVERSE_ARROW_PREVIOUS:
			if (this.timeField.getCaretPosition() == 0) { this.dayField.setFocus(); }
			break;
		case SWT.TRAVERSE_ARROW_NEXT:
//			if (this.timeField.getCaretPosition() == this.timeField.getText().length() && this.timeField.getSelectionCount() == 0) {
//				this.titleField.setFocus();
//			}
		}
	}
	
	private void onKeyPressedTime(KeyEvent event) {
		switch(event.keyCode) {
		case SWT.ARROW_UP:
			this.dayField.setFocus();
			break;
		case SWT.ARROW_DOWN:
			if (this.timeField.getCaretPosition() >= this.timeField.getCharCount()) {
				this.titleField.setFocus();
			} else {
				this.timeField.setSelection(this.timeField.getCharCount());
			}
			break;
		}
	}
	
	private void onTimeFocusLost(FocusEvent event) {
		this.updateTime();
	}

	private void onTraverseTitle(TraverseEvent event) {
		switch(event.detail) {
		case SWT.TRAVERSE_RETURN:
			this.updateTitle();
			break;
//		case SWT.TRAVERSE_ARROW_PREVIOUS:
//			if (this.titleField.getCaretPosition() == 0) { this.timeField.setFocus(); }
//			break;
//		case SWT.TRAVERSE_ARROW_NEXT:
//			if (this.titleField.getCaretPosition() == this.titleField.getText().length()) {
//				this.textMemo.setFocus();
//			}
		}
	}
	
	private void onKeyPressedTitle(KeyEvent event) {
		switch(event.keyCode) {
		case SWT.ARROW_UP:
			if (this.titleField.getCaretPosition() <= 0) {
				this.timeField.setFocus();
			} else {
				this.titleField.setSelection(0);
			}
			break;
		case SWT.ARROW_DOWN:
			this.titleField.setSelection(this.titleField.getCharCount());
			break;
		}
	}
	
	private void onTitleFocusLost(FocusEvent e) {
		this.updateTitle();
	}
	
	private void onKeyPressedNote(KeyEvent event) {
		switch(event.keyCode) {
		case SWT.ARROW_UP:
			if (this.textMemo.getCaretLineNumber() <= 0 && (event.stateMask & SWT.ALT) == 0) {
				Point sel = this.textMemo.getSelection();
				sel.x = 0;
				if ((event.stateMask & SWT.SHIFT) == 0) {
					sel.y = 0;
				}
				this.textMemo.setSelection(sel);
			}
			break;
		case SWT.ARROW_DOWN:
			if (this.textMemo.getCaretLineNumber() == this.textMemo.getLineCount()-1 && (event.stateMask & SWT.ALT) == 0) {
				Point sel = this.textMemo.getSelection();
				if ((event.stateMask & SWT.SHIFT) == 0) {
					sel.x = this.textMemo.getCharCount();
				}
				sel.y = this.textMemo.getCharCount();
				this.textMemo.setSelection(sel);
			}
			break;
		case 's':
			if ((event.stateMask & SWT.CTRL) == SWT.CTRL) {
				this.updateNote();
			}
		}
	}

	private void onNoteFocusLost(FocusEvent e) {
		this.updateNote();
	}
	
	private void updateYear(boolean notify) {
		try {
			String str = this.yearField.getText();
			if (str.isEmpty()) {
				if (!this.entry.hasYear()) return;
				this.entry.clearYear();
				this.yearField.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
				ScheduleManager.INSTANCE.onDateChanged(this);
				return;
			} else {
				int year = Integer.valueOf(str) + 2000;
				if (year == this.entry.getYear()) { return; }
				if (this.entry.setYear(year)) {
					this.yearField.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
					ScheduleManager.INSTANCE.onDateChanged(this);
					return;
				}
			}
		} catch (NumberFormatException e) { /* do nothing */ }
		this.yearField.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
	}
	
	private void updateMonth() {
		try {
			String str = this.monthField.getText().replaceAll(" ", "");
			if (str.isEmpty()) {
				if (!this.entry.hasMonth()) { return; }
				this.entry.clearMonth();
				this.monthField.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
				ScheduleManager.INSTANCE.onDateChanged(this);
				return;
			} else {
				int m = Integer.valueOf(str);
				if (m == this.entry.getMonth().getIndex()) { return; }
				if (this.entry.setMonth(EnumMonth.fromIndex(m))) {
					this.monthField.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
					ScheduleManager.INSTANCE.onDateChanged(this);
					return;
				}
			}
		} catch (NumberFormatException e) { /* do nothing */ }
		this.monthField.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
	}

	private void updateDay() {
		try {
			String str = this.dayField.getText();
			if (str.isEmpty()) {
				if (!this.entry.hasDay()) { return; }
				this.entry.clearDay();
				this.dayField.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
				ScheduleManager.INSTANCE.onDateChanged(this);
				return;
			} else {
				int m = Integer.valueOf(str);
				if (m == this.entry.getDay()) { return; }
				if (this.entry.setDay(m)) {
					this.dayField.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
					ScheduleManager.INSTANCE.onDateChanged(this);
					return;
				}
			}
		} catch (NumberFormatException e) { /* do nothing */ }
		this.dayField.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
	}

	private void updateTime() {
		try {
			String str = this.timeField.getText();
			if (str.isEmpty()) {
				if (!this.entry.hasTime()) { return; }
				this.entry.clearTime();
				this.timeField.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
				ScheduleManager.INSTANCE.onDateChanged(this);
				return;
			} else {
				int m = Integer.valueOf(str);
				if (m == this.entry.getTime()) { return; }
				if (this.entry.setTime(m)) {
					this.timeField.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
					ScheduleManager.INSTANCE.onDateChanged(this);
					return;
				}
			}
		} catch (NumberFormatException e) { /* do nothing */ }
		this.timeField.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
	}

	private void updateTitle() {
		if (this.entry.getTitle().equals(this.titleField.getText())) { return; }
		this.entry.setTitle(this.titleField.getText());
		ScheduleManager.INSTANCE.onSummaryChanged(this);
	}

	private void updateNote() {
		if (this.entry.getNote().equals(this.textMemo.getText())) { return; }
		this.entry.setNote(this.textMemo.getText());
		ScheduleManager.INSTANCE.onContentSave();
	}

	private void onPublicnessSelection(SelectionEvent event, EnumPublicness selected, Button widget) {
		if (widget.getSelection()) {
			if (this.entry.getTagPublicness() == selected) { return; }
			this.entry.setTagPublicness(selected);
			ScheduleManager.INSTANCE.onContentSave();
		}
	}
	
	private void onScheduleTypeSelection(SelectionEvent event, EnumScheduleType selected, Button widget) {
		if (widget.getSelection()) {
			if (this.entry.getTagScheduleType() == selected) { return; }
			this.entry.setTagScheduleType(selected);
			ScheduleManager.INSTANCE.onContentSave();
		}
	}
	
	private void onPlaceholderSelection(SelectionEvent event) {
		EnumIsPlaceHolder newstate = this.btnPlaceholder.getSelection() ? EnumIsPlaceHolder.TRUE : EnumIsPlaceHolder.FALSE;
		if (this.entry.getTagPlaceHolder() == newstate) { return; }
		this.entry.setTagPlaceHolder(newstate);
		ScheduleManager.INSTANCE.onContentSave();
	}
	
	private void onPendingSelection(SelectionEvent event) {
		EnumIsPending newstate = this.btnPending.getSelection() ? EnumIsPending.TRUE : EnumIsPending.FALSE;
		if (this.entry.getTagPending() == newstate) { return; }
		this.entry.setTagPending(newstate);
		ScheduleManager.INSTANCE.onContentSave();
	}
	
	private void onUrgencySelected(SelectionEvent event, EnumUrgent selected, Button widget) {
		if (widget.getSelection()) {
			if (this.entry.getTagUrgent() == selected) { return; }
			this.entry.setTagUrgent(selected);
			ScheduleManager.INSTANCE.onContentSave();
		}
	}
	
	public void setEntry(ScheduleEntry entry) {
		if (this.yearField.isFocusControl() || this.monthField.isFocusControl() || this.dayField.isFocusControl()
				|| this.timeField.isFocusControl() || this.titleField.isFocusControl()
				|| this.textMemo.isFocusControl()) { this.forceFocus(); }
		this.entry = entry;
		this.mirrorDate();
		this.mirrorContent();
		this.mirrorTags();
	}
	
	public void mirrorDate() {
		if (entry != null) {
			String str;
			str = entry.hasYear() ? StringUtil.zeroFill(entry.getYear() % 100) : "";
			if (!this.yearField.getText().equals(str)) this.yearField.setText(str);
			this.yearField.setEnabled(true);
			str = entry.getMonth().getDisplayString();
			if (!this.monthField.getText().equals(str)) this.monthField.setText(str);
			this.monthField.setEnabled(true);
			str = entry.hasDay() ? StringUtil.zeroFill(entry.getDay()) : "";
			if (!this.dayField.getText().equals(str)) this.dayField.setText(str);
			this.dayField.setEnabled(true);
			this.labelDoW.setText(entry.hasDay() ? entry.getDayOfWeekString() : "　");
			str = entry.hasTime() ? StringUtil.zeroFill4(entry.getTime()) : "";
			if (!this.timeField.getText().equals(str)) this.timeField.setText(str);
			this.timeField.setEnabled(true);
		} else {
			this.yearField.setText("");
			this.yearField.setEnabled(false);
			this.monthField.setText("");
			this.monthField.setEnabled(false);
			this.dayField.setText("");
			this.dayField.setEnabled(false);
			this.labelDoW.setText("　");
			this.timeField.setText("");
			this.timeField.setEnabled(false);
		}
	}
	
	public void mirrorContent() {
		if (entry != null) {
			this.titleField.setText(entry.getTitle());
			this.titleField.setEnabled(true);
			this.textMemo.setText(entry.getNote());
			this.textMemo.setEnabled(true);
		} else {
			this.titleField.setText("");
			this.titleField.setEnabled(false);
			this.textMemo.setText("");
			this.textMemo.setEnabled(false);
		}
	}
	
	public void mirrorTags() {
		if (this.entry != null) {
			this.lblPublicness.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
			this.publicness[EnumPublicness.PUBLIC.getIndex()].setSelection(this.entry.getTagPublicness() == EnumPublicness.PUBLIC);
			this.publicness[EnumPublicness.PUBLIC.getIndex()].setEnabled(true);
			this.publicness[EnumPublicness.PRIVATE.getIndex()].setSelection(this.entry.getTagPublicness() == EnumPublicness.PRIVATE);
			this.publicness[EnumPublicness.PRIVATE.getIndex()].setEnabled(true);
			this.lblScheduleType.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
			this.scheduletype[EnumScheduleType.TASK.getIndex()].setSelection(this.entry.getTagScheduleType() == EnumScheduleType.TASK);
			this.scheduletype[EnumScheduleType.TASK.getIndex()].setEnabled(true);
			this.scheduletype[EnumScheduleType.SCHEDULE.getIndex()].setSelection(this.entry.getTagScheduleType() == EnumScheduleType.SCHEDULE);
			this.scheduletype[EnumScheduleType.SCHEDULE.getIndex()].setEnabled(true);
			this.btnPlaceholder.setSelection(this.entry.getTagPlaceHolder() == EnumIsPlaceHolder.TRUE);
			this.btnPlaceholder.setEnabled(true);
			this.btnPending.setSelection(this.entry.getTagPending() == EnumIsPending.TRUE);
			this.btnPending.setEnabled(true);
			this.urgency[EnumUrgent.URGENT.getIndex()].setSelection(this.entry.getTagUrgent() == EnumUrgent.URGENT);
			this.urgency[EnumUrgent.URGENT.getIndex()].setEnabled(true);
			this.urgency[EnumUrgent.IMPORTANT.getIndex()].setSelection(this.entry.getTagUrgent() == EnumUrgent.IMPORTANT);
			this.urgency[EnumUrgent.IMPORTANT.getIndex()].setEnabled(true);
			this.urgency[EnumUrgent.NORMAL.getIndex()].setSelection(this.entry.getTagUrgent() == EnumUrgent.NORMAL);
			this.urgency[EnumUrgent.NORMAL.getIndex()].setEnabled(true);
			this.urgency[EnumUrgent.NON_URGENT.getIndex()].setSelection(this.entry.getTagUrgent() == EnumUrgent.NON_URGENT);
			this.urgency[EnumUrgent.NON_URGENT.getIndex()].setEnabled(true);
		} else {
			this.lblPublicness.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_DISABLED_FOREGROUND));
			this.publicness[EnumPublicness.PUBLIC.getIndex()].setSelection(false);
			this.publicness[EnumPublicness.PUBLIC.getIndex()].setEnabled(false);
			this.publicness[EnumPublicness.PRIVATE.getIndex()].setSelection(false);
			this.publicness[EnumPublicness.PRIVATE.getIndex()].setEnabled(false);
			this.lblScheduleType.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_DISABLED_FOREGROUND));
			this.scheduletype[EnumScheduleType.TASK.getIndex()].setSelection(false);
			this.scheduletype[EnumScheduleType.TASK.getIndex()].setEnabled(false);
			this.scheduletype[EnumScheduleType.SCHEDULE.getIndex()].setSelection(false);
			this.scheduletype[EnumScheduleType.SCHEDULE.getIndex()].setEnabled(false);
			this.btnPlaceholder.setSelection(false);
			this.btnPlaceholder.setEnabled(false);
			this.btnPending.setSelection(false);
			this.btnPending.setEnabled(false);
			this.urgency[EnumUrgent.URGENT.getIndex()].setSelection(false);
			this.urgency[EnumUrgent.URGENT.getIndex()].setEnabled(false);
			this.urgency[EnumUrgent.IMPORTANT.getIndex()].setSelection(false);
			this.urgency[EnumUrgent.IMPORTANT.getIndex()].setEnabled(false);
			this.urgency[EnumUrgent.NORMAL.getIndex()].setSelection(false);
			this.urgency[EnumUrgent.NORMAL.getIndex()].setEnabled(false);
			this.urgency[EnumUrgent.NON_URGENT.getIndex()].setSelection(false);
			this.urgency[EnumUrgent.NON_URGENT.getIndex()].setEnabled(false);
		}
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
