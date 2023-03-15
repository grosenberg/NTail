package net.certiv.ntail.dialogs;

import net.certiv.ntail.NTailPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class FindDialog extends Dialog {

	private FindDialog dialog;
	private FindForm findForm;
	private Composite dialogArea = null;
	private IFindReplaceTarget target = null;

	private int offset;
	private boolean newSearch = true;
	private OptionChanged optionChanged;

	public FindDialog(Shell parentShell, IFindReplaceTarget target) {
		super(parentShell);
		this.target = target;
		this.dialog = this;

		setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
		setBlockOnOpen(false);
	}

	/**
	 * Set title of the dialog.
	 */
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("NTail Find");
	}

	/**
	 * Create and layout the SWT controls for the dialog
	 */
	protected Control createDialogArea(Composite parent) {
		dialogArea = (Composite) super.createDialogArea(parent);
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.BEGINNING;
		GridLayout layout = new GridLayout();
		dialogArea.setLayout(layout);
		dialogArea.setSize(new Point(250, 270));
		findForm = new FindForm(dialogArea, SWT.NONE);
		return dialogArea;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		return findForm.compositeButtonBar;
	}

	@Override
	public void create() {
		super.create();
		if (optionChanged == null) {
			optionChanged = new OptionChanged();

			findForm.forwardRadio.addSelectionListener(optionChanged);
			findForm.backwardRadio.addSelectionListener(optionChanged);
			findForm.caseCheck.addSelectionListener(optionChanged);
			findForm.wrapCheck.addSelectionListener(optionChanged);
			findForm.wordCheck.addSelectionListener(optionChanged);
			findForm.incrCheck.addSelectionListener(optionChanged);

			findForm.findCombo.addModifyListener(new NewFind());
			findForm.findButton.addSelectionListener(new DoFind());
			findForm.closeButton.addSelectionListener(new CloseFind());

			getShell().addShellListener(new ActivationListener());
		}
	}

	private class CloseFind extends SelectionAdapter {

		public void widgetSelected(SelectionEvent e) {
			FindState state = NTailPlugin.getDefault().getFindState();
			if (state == null) state = new FindState();
			state.load(dialog);
			NTailPlugin.getDefault().setFindState(state);
			close();
		}
	}

	private class OptionChanged extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			newSearch = true;
			super.widgetSelected(e);
		}
	}

	/* Find comboBox contents changed */
	private class NewFind implements ModifyListener {

		public void modifyText(ModifyEvent e) {
			if (findForm.findCombo.getText().length() > 0) {
				findForm.findButton.setEnabled(true);
				newSearch = true;
				if (findForm.incrCheck.getSelection()) {
					offset = find(findForm.findCombo.getText());
					newSearch = false;
				}
			} else {
				newSearch = true;
				findForm.findButton.setEnabled(false);
			}
			findForm.statusText.setText("");
			findForm.statusText.redraw();
		}
	}

	/* Find button pressed */
	private class DoFind extends SelectionAdapter {

		public void widgetSelected(SelectionEvent e) {
			// commit contents of the combo to the combo's list
			if (findForm.findCombo.indexOf(findForm.findCombo.getText()) == -1) {
				findForm.findCombo.add(findForm.findCombo.getText(), 0);
			}
			if (newSearch) {
				offset = find(findForm.findCombo.getText());
				newSearch = false;
			} else {
				offset = findNext(findForm.findCombo.getText());
			}
		}
	}

	protected int findNext(String text) {
		// do not repeat after a not found search
		if (offset == -1) return -1;
		if (findForm.forwardRadio.getSelection()) {
			offset = offset + 1;
		}
		return find(text);
	}

	protected int find(String text) {
		boolean forward = findForm.forwardRadio.getSelection();
		boolean wrap = findForm.wrapCheck.getSelection();
		boolean upCase = findForm.caseCheck.getSelection();
		boolean whWord = findForm.wordCheck.getSelection();
		int loc = -1;

		// try a search
		if (forward) {
			loc = target.findAndSelect(offset, text, forward, upCase, whWord);
		} else {
			loc = target.findAndSelect(offset - 1, text, forward, upCase, whWord);
		}
		// on not found and wrap, try again
		if (wrap && loc == -1) {
			loc = target.findAndSelect(-1, text, forward, upCase, whWord);
		}
		// characterize the results
		if (loc == -1) {
			findForm.statusText.setText("Not found");
			findForm.statusText.redraw();
		}
		return loc;
	}

	public class FindState {
		private boolean forward = true;
		private boolean wrap = false;
		private boolean upCase = true;
		private boolean whWord = true;
		private boolean incr = false;
		private String[] items = new String[0];
		private Point location = null;

		public void load(FindDialog dialog) {
			this.forward = dialog.findForm.forwardRadio.getSelection();
			this.wrap = dialog.findForm.wrapCheck.getSelection();
			this.upCase = dialog.findForm.caseCheck.getSelection();
			this.whWord = dialog.findForm.wordCheck.getSelection();
			this.incr = dialog.findForm.incrCheck.getSelection();
			this.items = dialog.findForm.findCombo.getItems();
			this.location = dialog.getShell().getLocation();
		}

		public void restore(FindDialog dialog) {
			dialog.findForm.forwardRadio.setSelection(this.forward);
			dialog.findForm.backwardRadio.setSelection(!this.forward);
			dialog.findForm.wrapCheck.setSelection(this.wrap);
			dialog.findForm.caseCheck.setSelection(this.upCase);
			dialog.findForm.wordCheck.setSelection(this.whWord);
			dialog.findForm.incrCheck.setSelection(this.incr);
			dialog.findForm.findCombo.setItems(this.items);
			if (location != null) dialog.getShell().setLocation(location);
		}
	}

	private class ActivationListener extends ShellAdapter {

		@Override
		public void shellActivated(ShellEvent e) {
			FindState state = NTailPlugin.getDefault().getFindState();
			if (state == null) state = new FindState();
			state.restore(dialog);

			offset = target.getSelection().x;
			String selection = target.getSelectionText();
			if (selection != null && selection.length() > 0) {
				findForm.findCombo.setText(selection);
			}

			Shell eShell = (Shell) e.widget;
			if (getShell() == eShell) findForm.findCombo.setFocus();
		}
	}
}