package qwertzite.schedulemanager.client.dialog;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;

public class ConfirmationDialog extends Dialog {

	protected boolean result = false;
	protected Shell shell;
	private String message;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ConfirmationDialog(Shell parent, int style, String title, String message) {
		super(parent, SWT.CLOSE | SWT.RESIZE | SWT.TITLE | SWT.PRIMARY_MODAL);
		setText(title);
		this.message = message;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public boolean open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(800, 200);
		shell.setText(getText());
		shell.setLayout(new GridLayout(2, false));
		
		Label lblMessage = new Label(shell, SWT.NONE);
		lblMessage.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 2, 1));
		lblMessage.setText(this.message);
		
		Button btnYes = new Button(shell, SWT.NONE);
		GridData gd_btnYes = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnYes.widthHint = 100;
		btnYes.setLayoutData(gd_btnYes);
		btnYes.setText("YES");
		btnYes.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> this.onBtnPressed(e, true)));
		
		Button btnNo = new Button(shell, SWT.NONE);
		GridData gd_btnNo = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnNo.widthHint = 100;
		btnNo.setLayoutData(gd_btnNo);
		btnNo.setText("NO");
		btnNo.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> this.onBtnPressed(e, false)));
	}
	
	private void onBtnPressed(SelectionEvent event, boolean flag) {
		this.result = flag;
		this.shell.dispose();
	}

}
