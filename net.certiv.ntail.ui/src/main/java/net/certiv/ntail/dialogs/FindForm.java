package net.certiv.ntail.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class FindForm extends Composite {

	private Composite composite = null;
	public Composite compositeButtonBar = null;
	private Label labelFind = null;

	public Group directionGroup = null;
	public Group optionGroup = null;
	public Group scopeGroup = null;

	public Combo findCombo = null;

	public Button forwardRadio = null;
	public Button backwardRadio = null;

	public Button allRadio = null;
	public Button selectedRadio = null;

	public Button caseCheck = null;
	public Button wordCheck = null;
	public Button wrapCheck = null;
	public Button incrCheck = null;

	public Button findButton = null;
	public Button closeButton = null;

	public Label statusText = null;

	public FindForm(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
		gridLayout1.verticalSpacing = 6;
		gridLayout1.marginHeight = 2;
		gridLayout1.makeColumnsEqualWidth = true;
		this.setLayout(gridLayout1);
		createFindComposite();
		createDirectionGroup();
		createScopeGroup();
		createOptionGroup();
		createButtonComposite();
	}

	/**
	 * This method initializes composite
	 */
	private void createFindComposite() {
		GridData gridData2 = new GridData();
		gridData2.widthHint = 40;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginHeight = 4;
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(gridData);
		composite.setLayout(gridLayout);
		labelFind = new Label(composite, SWT.NONE);
		labelFind.setText("&Find:");
		labelFind.setLayoutData(gridData2);
		createFindCombo();
	}

	/**
	 * This method initializes findCombo
	 */
	private void createFindCombo() {
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData1.grabExcessHorizontalSpace = true;
		findCombo = new Combo(composite, SWT.NONE);
		findCombo.setLayoutData(gridData1);
	}

	/**
	 * This method initializes directionGroup
	 */
	private void createDirectionGroup() {
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData4.grabExcessHorizontalSpace = true;
		directionGroup = new Group(this, SWT.NONE);
		directionGroup.setLayout(new GridLayout());
		directionGroup.setLayoutData(gridData4);
		directionGroup.setText("Direction");
		forwardRadio = new Button(directionGroup, SWT.RADIO);
		forwardRadio.setText("F&orward");
		backwardRadio = new Button(directionGroup, SWT.RADIO);
		backwardRadio.setText("&Backward");
	}

	/**
	 * This method initializes optionGroup
	 */
	private void createOptionGroup() {
		GridLayout gridLayout3 = new GridLayout();
		gridLayout3.numColumns = 2;
		GridData gridData3 = new GridData();
		gridData3.horizontalSpan = 2;
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		optionGroup = new Group(this, SWT.NONE);
		optionGroup.setToolTipText("");
		optionGroup.setLayoutData(gridData3);
		optionGroup.setLayout(gridLayout3);
		optionGroup.setText("Options");
		caseCheck = new Button(optionGroup, SWT.CHECK);
		caseCheck.setText("&Case Sensitive");
		wrapCheck = new Button(optionGroup, SWT.CHECK);
		wrapCheck.setText("Wra&p Search");
		wordCheck = new Button(optionGroup, SWT.CHECK);
		wordCheck.setText("&Whole Word");
		incrCheck = new Button(optionGroup, SWT.CHECK);
		incrCheck.setText("&Incremental");
	}

	/**
	 * This method initializes scopeGroup
	 */
	private void createScopeGroup() {
		GridData gridData5 = new GridData();
		gridData5.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData5.grabExcessHorizontalSpace = true;
		scopeGroup = new Group(this, SWT.NONE);
		scopeGroup.setLayout(new GridLayout());
		scopeGroup.setLayoutData(gridData5);
		scopeGroup.setText("Scope");
		allRadio = new Button(scopeGroup, SWT.RADIO);
		allRadio.setText("A&ll");
		allRadio.setEnabled(false);
		selectedRadio = new Button(scopeGroup, SWT.RADIO);
		selectedRadio.setText("Selec&ted Lines");
		selectedRadio.setEnabled(false);
	}

	/**
	 * This method initializes compositeButtonBar
	 */
	private Composite createButtonComposite() {
		GridData gridData9 = new GridData();
		gridData9.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData9.grabExcessHorizontalSpace = true;
		GridData gridData8 = new GridData();
		gridData8.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		GridData gridData7 = new GridData();
		gridData7.widthHint = 75;
		gridData7.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		GridData gridData6 = new GridData();
		gridData6.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData6.grabExcessHorizontalSpace = true;
		gridData6.horizontalSpan = 2;
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 2;
		gridLayout2.marginHeight = 4;
		gridLayout2.marginWidth = 0;
		gridLayout2.verticalSpacing = 10;
		gridLayout2.horizontalSpacing = 5;
		compositeButtonBar = new Composite(this, SWT.NONE);
		compositeButtonBar.setLayout(gridLayout2);
		compositeButtonBar.setLayoutData(gridData6);
		@SuppressWarnings("unused")
		Label filler = new Label(compositeButtonBar, SWT.NONE);
		findButton = new Button(compositeButtonBar, SWT.NONE);
		findButton.setText("Fi&nd");
		findButton.setLayoutData(gridData7);
		statusText = new Label(compositeButtonBar, SWT.NONE);
		statusText.setLayoutData(gridData9);
		closeButton = new Button(compositeButtonBar, SWT.NONE);
		closeButton.setText("Close");
		closeButton.setLayoutData(gridData8);
		return compositeButtonBar;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
