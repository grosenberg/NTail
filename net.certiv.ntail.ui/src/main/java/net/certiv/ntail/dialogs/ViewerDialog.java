package net.certiv.ntail.dialogs;

import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import net.certiv.ntail.Key;
import net.certiv.ntail.NTailPlugin;
import net.certiv.ntail.viewers.Viewer;
import net.certiv.ntail.viewers.ViewerSet;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

public class ViewerDialog extends Dialog {

	protected boolean testMode = false; // just for testing as a standalone dialog

	private boolean editMode = false;
	private Viewer viewer;
	private String errMsg;

	private Group viewGroup = null;
	private Group controlGroup = null;
	private Group filterGroup = null;
	private Group lengthGroup = null;
	private Combo tailFileCombo = null;
	private Combo viewNameCombo = null;
	private Button browseButton = null;
	private Composite composite = null;
	private Button fullRadioButton = null;
	private Button tailRadioButton = null;
	private Label label2 = null;
	private Label countLabel = null;
	private Spinner countSpinner = null;
	private Composite composite1 = null;
	private Label label5 = null;
	private Spinner intervalSpinner = null;
	private Label label6 = null;
	private Button releaseCheckBox = null;
	private Label releaseCountLabel;
	private Spinner releaseCountSpinner = null;
	private Button filterCheckBox = null;
	private Button truncCheckBox = null;
	private Button hyperlinkCheckBox = null;
	private Button ansiCodesCheckBox = null;
	private Button filterLinesCheckBox = null;
	private Button replaceCheckBox = null;
	private Label withLabel = null;
	private Button highlightCheckBox = null;
	private Combo highlightCombo = null;
	private Combo withCombo = null;
	private Combo replaceCombo = null;
	private Combo filterLinesCombo = null;
	private Label label4 = null;
	private Label label7 = null;
	private Label label8 = null;
	private Combo encodingCombo;
	private Label label9;
	private Label dividerLabel;
	private Button dividerCheckBox;
	private Spinner dividerSpinner;

	private Label dividerLabel1;

	public ViewerDialog(Shell parent) {
		super(parent);
	}

	/**
	 * Create and layout the SWT controls for the dialog
	 */
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.horizontalSpacing = 10;
		composite.setLayout(layout);
		initialize(composite);
		return composite;
	}

	/**
	 * Override to set the title of the dialog.
	 */
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (editMode) {
			shell.setText("Edit Viewer");
		} else {
			shell.setText("Add Viewer");
		}
	}

	private void initialize(Composite parent) {
		createViewGroup(parent);
		createControlGroup(parent);
		createLengthGroup(parent);
		createFilterGroup(parent);

		if (!testMode) {
			initViewGroup();
			initControlGroup();
			initFilterGroup();
			adjustForMode();
		}
	}

	/**
	 * This method initializes viewGroup
	 */
	private void createViewGroup(Composite parent) {
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.END;
		gridData3.widthHint = 60;
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		viewGroup = new Group(parent, SWT.NONE);
		label7 = new Label(viewGroup, SWT.NONE);
		label7.setText("Log view name");
		createViewNameCombo();
		viewGroup.setLayoutData(gridData);
		label8 = new Label(viewGroup, SWT.NONE);
		label8.setText("Log file name");
		createTailFileCombo();
		viewGroup.setLayout(gridLayout);
		viewGroup.setText("Define Log View");
		new Label(viewGroup, SWT.NONE);
		browseButton = new Button(viewGroup, SWT.NONE);
		browseButton.setText("Browse");
		browseButton.setLayoutData(gridData3);
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.SINGLE);
				dialog.open();
				if (dialog.getFileName().length() > 0) {
					tailFileCombo.setText(dialog.getFilterPath() + File.separator + dialog.getFileName());
				}
			}
		});
	}

	/**
	 * This method initializes controlGroup
	 */
	private void createControlGroup(Composite parent) {
		GridData gridData4 = new GridData();
		gridData4.verticalAlignment = GridData.FILL;
		controlGroup = new Group(parent, SWT.NONE);
		controlGroup.setText("Log Tail Controls");
		controlGroup.setLayout(new GridLayout());
		controlGroup.setLayoutData(gridData4);
		createCountGroup();
	}

	private void createLengthGroup(Composite parent) {
		GridData gridData23 = new GridData();
		gridData23.horizontalAlignment = GridData.FILL;
		gridData23.verticalAlignment = GridData.FILL;
		lengthGroup = new Group(parent, SWT.NONE);
		lengthGroup.setText("Log Length Controls");
		lengthGroup.setLayout(new GridLayout());
		lengthGroup.setLayoutData(gridData23);
		createRadioGroup();
	}

	/**
	 * This method initializes group
	 */
	private void createFilterGroup(Composite parent) {
		GridData gridData18 = new GridData();
		gridData18.grabExcessHorizontalSpace = true;
		gridData18.horizontalSpan = 2;
		gridData18.horizontalAlignment = GridData.FILL;
		GridData gridData17 = new GridData();
		gridData17.widthHint = 20;
		GridData gridData16 = new GridData();
		gridData16.horizontalSpan = 2;
		GridData gridData15 = new GridData();
		gridData15.horizontalAlignment = GridData.END;
		GridData gridData14 = new GridData();
		gridData14.horizontalSpan = 2;
		GridData gridData13 = new GridData();
		gridData13.horizontalSpan = 2;
		GridLayout gridLayout4 = new GridLayout();
		gridLayout4.numColumns = 3;
		filterGroup = new Group(parent, SWT.NONE);
		filterGroup.setText("Filter Specification");
		filterGroup.setLayoutData(gridData18);
		filterGroup.setLayout(gridLayout4);
		filterCheckBox = new Button(filterGroup, SWT.CHECK);
		filterCheckBox.setText("Enable filter(s)");
		filterCheckBox.setLayoutData(gridData16);
		filterCheckBox.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				Button b = (Button) e.getSource();
				hyperlinkCheckBox.setEnabled(b.getSelection() && NTailPlugin.getDefault().isJDTInstalled());
				ansiCodesCheckBox.setEnabled(b.getSelection());
				filterLinesCheckBox.setEnabled(b.getSelection());
				filterLinesCombo.setEnabled(b.getSelection() && filterLinesCheckBox.getSelection());
				replaceCheckBox.setEnabled(b.getSelection());
				replaceCombo.setEnabled(b.getSelection() && replaceCheckBox.getSelection());
				withLabel.setEnabled(b.getSelection());
				withCombo.setEnabled(b.getSelection() && replaceCheckBox.getSelection());
				highlightCheckBox.setEnabled(b.getSelection());
				highlightCombo.setEnabled(b.getSelection() && highlightCheckBox.getSelection());
			}
		});

		new Label(filterGroup, SWT.NONE);
		label4 = new Label(filterGroup, SWT.NONE);
		label4.setText("");
		label4.setLayoutData(gridData17);
		hyperlinkCheckBox = new Button(filterGroup, SWT.CHECK);
		hyperlinkCheckBox.setText("Enable hyperlink to JDT source");
		hyperlinkCheckBox.setLayoutData(gridData13);
		if (!testMode) hyperlinkCheckBox.setEnabled(NTailPlugin.getDefault().isJDTInstalled());
		new Label(filterGroup, SWT.NONE);
		ansiCodesCheckBox = new Button(filterGroup, SWT.CHECK);
		ansiCodesCheckBox.setText("Enable Ansi highlighting");
		ansiCodesCheckBox.setLayoutData(gridData14);
		new Label(filterGroup, SWT.NONE);
		filterLinesCheckBox = new Button(filterGroup, SWT.CHECK);
		filterLinesCheckBox.setText("Exclude lines");
		filterLinesCheckBox.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				Button b = (Button) e.getSource();
				filterLinesCombo.setEnabled(b.getSelection());
			}
		});
		createFilterLinesCombo();
		new Label(filterGroup, SWT.NONE);
		replaceCheckBox = new Button(filterGroup, SWT.CHECK);
		replaceCheckBox.setText("Replace text");
		replaceCheckBox.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				Button b = (Button) e.getSource();
				replaceCombo.setEnabled(b.getSelection());
				withCombo.setEnabled(b.getSelection());
			}
		});
		createReplaceCombo();
		new Label(filterGroup, SWT.NONE);
		withLabel = new Label(filterGroup, SWT.NONE);
		withLabel.setText("with  ");
		withLabel.setLayoutData(gridData15);
		createWithCombo();
		new Label(filterGroup, SWT.NONE);
		highlightCheckBox = new Button(filterGroup, SWT.CHECK);
		highlightCheckBox.setText("Highlight text");
		highlightCheckBox.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				Button b = (Button) e.getSource();
				highlightCombo.setEnabled(b.getSelection());
			}
		});
		createHighlightCombo();
	}

	/**
	 * This method initializes combo1
	 */
	private void createViewNameCombo() {
		GridData gridData2 = new GridData();
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.widthHint = 300;
		viewNameCombo = new Combo(viewGroup, SWT.NONE);
		viewNameCombo.setLayoutData(gridData2);
	}

	/**
	 * This method initializes combo
	 */
	private void createTailFileCombo() {
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.grabExcessHorizontalSpace = true;
		tailFileCombo = new Combo(viewGroup, SWT.NONE);
		tailFileCombo.setLayoutData(gridData1);
	}

	private void createEncodingCombo() {
		GridData gridData1 = new GridData();
		gridData1.horizontalSpan = 2;
		encodingCombo = new Combo(composite1, SWT.NONE);
		encodingCombo.add(Key.DEFAULT_ENCODING);
		for (String s : Key.ENCODING_NAME) {
			encodingCombo.add(s);
		}
		encodingCombo.setText(Key.DEFAULT_ENCODING);
		encodingCombo.setLayoutData(gridData1);
	}

	/**
	 * This method initializes composite1
	 */
	private void createCountGroup() {
		GridData gridData12 = new GridData();
		gridData12.verticalAlignment = GridData.BEGINNING;
		GridData gridData9 = new GridData();
		gridData9.horizontalAlignment = GridData.END;
		GridData gridData11 = new GridData();
		gridData11.horizontalSpan = 3;
		GridLayout gridLayout3 = new GridLayout();
		gridLayout3.numColumns = 4;
		composite1 = new Composite(controlGroup, SWT.NONE);
		composite1.setLayout(gridLayout3);
		composite1.setLayoutData(gridData12);

		label5 = new Label(composite1, SWT.NONE);
		label5.setText("Refresh interval: ");
		intervalSpinner = new Spinner(composite1, SWT.BORDER);
		intervalSpinner.setMaximum(10000);
		intervalSpinner.setMinimum(250);
		intervalSpinner.setIncrement(250);
		intervalSpinner.setDigits(3);
		label6 = new Label(composite1, SWT.NONE);
		label6.setText("(seconds)");

		new Label(composite1, SWT.NONE);
		releaseCheckBox = new Button(composite1, SWT.CHECK);
		releaseCheckBox.setText("Release log file after interval");
		releaseCheckBox.setLayoutData(gridData11);
		releaseCheckBox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Button b = (Button) e.getSource();
				enableReleaseCount(b.getSelection());
			}
		});
		new Label(composite1, SWT.NONE);
		releaseCountLabel = new Label(composite1, SWT.NONE);
		releaseCountLabel.setText("Interval count:");
		releaseCountLabel.setLayoutData(gridData9);
		releaseCountSpinner = new Spinner(composite1, SWT.BORDER);
		releaseCountSpinner.setMinimum(1);
		releaseCountSpinner.setMaximum(100);

		new Label(composite1, SWT.NONE);
		new Label(composite1, SWT.NONE);
		label9 = new Label(composite1, SWT.NONE);
		label9.setText("Log file encoding:");
		createEncodingCombo();
	}

	/**
	 * This method initializes composite
	 */
	private void createRadioGroup() {
		GridData gridData10 = new GridData();
		gridData10.horizontalSpan = 4;
		GridData gridData8 = new GridData();
		gridData8.horizontalSpan = 2;
		GridData gridData7 = new GridData();
		gridData7.horizontalSpan = 2;
		gridData7.horizontalIndent = 10;
		GridData gridData9 = new GridData();
		gridData9.horizontalSpan = 2;
		GridData gridData6 = new GridData();
		gridData6.horizontalSpan = 3;
		GridData gridData5 = new GridData();
		gridData5.horizontalIndent = 10;
		GridData gridData4 = new GridData();

		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 4;
		composite = new Composite(lengthGroup, SWT.NONE);
		composite.setLayout(gridLayout2);
		composite.setLayoutData(gridData4);

		label2 = new Label(composite, SWT.NONE);
		label2.setText("Tail line count to display:");
		label2.setLayoutData(gridData6);
		new Label(composite, SWT.NONE);
		fullRadioButton = new Button(composite, SWT.RADIO);
		fullRadioButton.setText("Full file");
		fullRadioButton.setLayoutData(gridData5);
		tailRadioButton = new Button(composite, SWT.RADIO);
		tailRadioButton.setText("Tail limited");
		tailRadioButton.setLayoutData(gridData8);
		tailRadioButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Button b = (Button) e.getSource();
				enableTailCount(b.getSelection());
			}
		});
		new Label(composite, SWT.NONE);
		countLabel = new Label(composite, SWT.NONE);
		countLabel.setText("Line count limit:");
		countLabel.setLayoutData(gridData7);
		countSpinner = new Spinner(composite, SWT.BORDER);
		countSpinner.setMinimum(1);
		countSpinner.setMaximum(10000);
		countSpinner.setLayoutData(gridData9);

		GridData gridData11 = new GridData();
		gridData11.horizontalSpan = 4;
		dividerCheckBox = new Button(composite, SWT.CHECK);
		dividerCheckBox.setText("Insert line divider on idle timeout");
		dividerCheckBox.setLayoutData(gridData11);
		dividerCheckBox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Button b = (Button) e.getSource();
				enableDivider(b.getSelection());
			}
		});

		GridData gridData17 = new GridData();
		gridData17.horizontalSpan = 2;
		gridData17.horizontalIndent = 10;
		dividerLabel = new Label(composite, SWT.NONE);
		dividerLabel.setText("Idle timeout: ");
		dividerLabel.setLayoutData(gridData17);
		dividerSpinner = new Spinner(composite, SWT.BORDER);
		dividerSpinner.setMaximum(200);
		dividerSpinner.setMinimum(1);
		dividerLabel1 = new Label(composite, SWT.NONE);
		dividerLabel1.setText("(seconds)");

		truncCheckBox = new Button(composite, SWT.CHECK);
		truncCheckBox.setText("Truncate log file on startup");
		truncCheckBox.setLayoutData(gridData10);
	}

	/**
	 * This method initializes combo6
	 */
	private void createFilterLinesCombo() {
		GridData gridData19 = new GridData();
		gridData19.grabExcessHorizontalSpace = true;
		gridData19.horizontalAlignment = GridData.FILL;
		filterLinesCombo = new Combo(filterGroup, SWT.NONE);
		filterLinesCombo.setLayoutData(gridData19);
	}

	/**
	 * This method initializes combo2
	 */
	private void createHighlightCombo() {
		GridData gridData22 = new GridData();
		gridData22.horizontalAlignment = GridData.FILL;
		gridData22.grabExcessHorizontalSpace = true;
		highlightCombo = new Combo(filterGroup, SWT.NONE);
		highlightCombo.setLayoutData(gridData22);
	}

	/**
	 * This method initializes combo5
	 */
	private void createReplaceCombo() {
		GridData gridData20 = new GridData();
		gridData20.horizontalAlignment = GridData.FILL;
		gridData20.grabExcessHorizontalSpace = true;
		replaceCombo = new Combo(filterGroup, SWT.NONE);
		replaceCombo.setLayoutData(gridData20);
	}

	/**
	 * This method initializes combo4
	 */
	private void createWithCombo() {
		GridData gridData21 = new GridData();
		gridData21.verticalAlignment = GridData.CENTER;
		gridData21.horizontalAlignment = GridData.FILL;
		gridData21.grabExcessHorizontalSpace = true;
		withCombo = new Combo(filterGroup, SWT.NONE);
		withCombo.setLayoutData(gridData21);
	}

	public int open(Viewer w, boolean editMode) {
		this.viewer = w;
		this.editMode = editMode;
		return super.open();
	}

	private void initViewGroup() {
		ViewerSet vset = NTailPlugin.getDefault().getViewerSet();
		viewNameCombo.setItems(vset.getUniqueViewNames().toArray(new String[] {}));
		viewNameCombo.setText(viewer.getViewName());
		tailFileCombo.setItems(vset.getUniqueLogFileNames().toArray(new String[] {}));
		tailFileCombo.setText(viewer.getFileName());
	}

	private void initControlGroup() {
		intervalSpinner.setSelection(viewer.getInterval());
		fullRadioButton.setSelection(viewer.getFullFile());
		tailRadioButton.setSelection(!viewer.getFullFile());
		countSpinner.setSelection(viewer.getNumLines());
		countSpinner.setEnabled(!viewer.getFullFile());

		dividerCheckBox.setSelection(viewer.isDividerAppend());
		dividerLabel.setEnabled(viewer.isDividerAppend());
		dividerSpinner.setSelection(viewer.getDividerTimeout());
		dividerSpinner.setEnabled(viewer.isDividerAppend());
		dividerLabel1.setEnabled(viewer.isDividerAppend());

		truncCheckBox.setSelection(viewer.isTruncOnStart());

		releaseCheckBox.setSelection(viewer.isRelease());
		releaseCountLabel.setEnabled(viewer.isRelease());
		releaseCountSpinner.setSelection(viewer.getReleaseCount());
		releaseCountSpinner.setEnabled(viewer.isRelease());

		if (viewer.getEncoding() != null) {
			encodingCombo.setText(viewer.getEncoding());
		}
	}

	private void initFilterGroup() {
		ViewerSet vset = NTailPlugin.getDefault().getViewerSet();

		// initialize values
		filterCheckBox.setSelection(viewer.isFilters());
		hyperlinkCheckBox.setSelection(viewer.isHyperlink() && NTailPlugin.getDefault().isJDTInstalled());
		ansiCodesCheckBox.setSelection(viewer.isAnsiCodes());
		filterLinesCheckBox.setSelection(viewer.isFilterLines());
		filterLinesCombo.setItems(vset.getUniqueLines().toArray(new String[] {}));
		filterLinesCombo.setText(viewer.getLinesPattern());
		replaceCheckBox.setSelection(viewer.isReplace());
		replaceCombo.setItems(vset.getUniqueReplace().toArray(new String[] {}));
		replaceCombo.setText(viewer.getReplacePattern());
		withCombo.setItems(vset.getUniqueRepWith().toArray(new String[] {}));
		withCombo.setText(viewer.getRepWithText());
		highlightCheckBox.setSelection(viewer.isHighlight());
		highlightCombo.setItems(vset.getUniqueHighlights().toArray(new String[] {}));
		highlightCombo.setText(viewer.getHighlightPattern());

		// initialize state
		filterLinesCombo.setEnabled(viewer.isFilters() && viewer.isFilterLines());
		replaceCombo.setEnabled(viewer.isFilters() && viewer.isReplace());
		withCombo.setEnabled(viewer.isFilters() && viewer.isReplace());
		highlightCombo.setEnabled(viewer.isFilters() && viewer.isHighlight());

		hyperlinkCheckBox.setEnabled(viewer.isFilters() && NTailPlugin.getDefault().isJDTInstalled());
		ansiCodesCheckBox.setEnabled(viewer.isFilters());
		filterLinesCheckBox.setEnabled(viewer.isFilters());
		replaceCheckBox.setEnabled(viewer.isFilters());
		withLabel.setEnabled(viewer.isFilters());
		highlightCheckBox.setEnabled(viewer.isFilters());
	}

	/**
	 * Modify the UI we just created to deal with being in Edit Viewer mode instead of
	 * Create Viewer mode.
	 */
	private void adjustForMode() {
		enableViewGroup(!editMode);
		// countLabel.setEnabled(!editMode);
		// countSpinner.setEnabled(!editMode);
		// fullRadioButton.setEnabled(!editMode);
		// tailRadioButton.setEnabled(!editMode);
	}

	private void enableViewGroup(boolean enabled) {
		viewNameCombo.setEnabled(enabled);
		tailFileCombo.setEnabled(enabled);
		browseButton.setEnabled(enabled);
	}

	protected void enableTailCount(boolean enabled) {
		countLabel.setEnabled(enabled);
		countSpinner.setEnabled(enabled);
	}

	protected void enableReleaseCount(boolean enabled) {
		releaseCountLabel.setEnabled(enabled);
		releaseCountSpinner.setEnabled(enabled);
	}

	protected void enableDivider(boolean enabled) {
		dividerLabel.setEnabled(enabled);
		dividerSpinner.setEnabled(enabled);
		dividerLabel1.setEnabled(enabled);
	}

	/**
	 * Validate user input, set return values.
	 */
	protected void okPressed() {
		if (validate()) {
			viewer.setViewName(viewNameCombo.getText());
			viewer.setFileName(tailFileCombo.getText());

			viewer.setInterval(intervalSpinner.getSelection());
			viewer.setRelease(releaseCheckBox.getSelection());
			viewer.setReleaseCount(releaseCountSpinner.getSelection());

			viewer.setEncoding(encodingCombo.getText());

			viewer.setFullFile(fullRadioButton.getSelection());
			viewer.setNumLines(countSpinner.getSelection());
			viewer.setDividerAppend(dividerCheckBox.getSelection());
			viewer.setDividerTimeout(dividerSpinner.getSelection());
			viewer.setTruncOnStart(truncCheckBox.getSelection());

			viewer.setFilters(filterCheckBox.getSelection());
			viewer.setHyperlink(hyperlinkCheckBox.getSelection() && NTailPlugin.getDefault().isJDTInstalled());
			viewer.setAnsiCodes(ansiCodesCheckBox.getSelection());
			viewer.setFilterLines(filterLinesCheckBox.getSelection());
			viewer.setLinesPattern(filterLinesCombo.getText());
			viewer.setReplace(replaceCheckBox.getSelection());
			viewer.setReplacePattern(replaceCombo.getText());
			viewer.setRepWithText(withCombo.getText());
			viewer.setHighlight(highlightCheckBox.getSelection());
			viewer.setHighlightPattern(highlightCombo.getText());

			super.okPressed();
		} else {
			MessageDialog.openError(getShell(), "NTail", errMsg);
		}
	}

	/**
	 * Validate the user input
	 */
	protected boolean validate() {
		String viewName = viewNameCombo.getText();
		if (viewName == null || viewName.trim().length() == 0) {
			errMsg = "View name not specified.";
			return false;
		}

		File file = new File(tailFileCombo.getText());
		if (!file.exists() || !file.isFile()) {
			errMsg = "File not found: " + tailFileCombo.getText();
			return false;
		} else if (!file.canRead()) {
			errMsg = "File not readable: " + tailFileCombo.getText();
			return false;
		}

		if (filterCheckBox.getSelection()) {
			if (filterLinesCheckBox.getSelection()) {
				try {
					Pattern.compile(filterLinesCombo.getText());
				} catch (PatternSyntaxException e) {
					errMsg = "Filter lines regex pattern invalid: " + filterLinesCombo.getText();
					return false;
				}
			}
			if (replaceCheckBox.getSelection()) {
				try {
					Pattern.compile(replaceCombo.getText());
				} catch (PatternSyntaxException e) {
					errMsg = "Replace regex pattern invalid: " + replaceCombo.getText();
					return false;
				}
				if (withCombo.getText() == null) {
					errMsg = "Replacement text cannot be null.";
					return false;
				}
			}
			if (highlightCheckBox.getSelection()) {
				try {
					Pattern.compile(highlightCombo.getText());
				} catch (PatternSyntaxException e) {
					errMsg = "Highlight regex pattern invalid: " + highlightCombo.getText();
					return false;
				}
			}
		}
		return true;
	}

	public Viewer getViewer() {
		return viewer;
	}
}