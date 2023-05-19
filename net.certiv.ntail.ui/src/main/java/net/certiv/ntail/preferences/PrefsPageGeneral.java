package net.certiv.ntail.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FontFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import net.certiv.ntail.NTailPlugin;

/**
 * Base preference page for NTail.
 */
public class PrefsPageGeneral extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PrefsPageGeneral() {
		super(GRID);
		setPreferenceStore(NTailPlugin.getDefault().getPreferenceStore());
		setDescription("");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI
	 * blocks needed to manipulate various types of preferences. Each field editor
	 * knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		Composite parent = getFieldEditorParent();

		Group group = new Group(parent, SWT.NONE);
		group.setText("General");

		addField(new BooleanFieldEditor(Key.SAVE_VIEWERS, "Save configuration", group));
		addField(new BooleanFieldEditor(Key.VIEW_TITLE_NAME,
				"Show log file name in the title of the View tab", group));
		addField(new BooleanFieldEditor(Key.SHOW_INNER_TITLE,
				"Show log file name on the content description line", group));

		addField(new RadioGroupFieldEditor(Key.ICON_SET, "Visible Icons on the View ToolBar", 2,
				new String[][] { { "Full Set", Key.FULL }, { "Minimal Set", Key.MINIMAL } }, parent,
				true));

		// /////////////////////////////////////////////////////////////////////////

		Group group1 = new Group(parent, SWT.NONE);
		group1.setText("Log Text Display");

		addField(new FontFieldEditor(Key.VIEW_FONT, "Log text font:", group1));
		addField(new ColorFieldEditor(Key.HIGHLIGHT, "Highligh color:", group1));
		addField(new ColorFieldEditor(Key.LINK_COLOR, "Hyperlink color:", group1));

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		group.setLayout(layout);
		group.setLayoutData(gridData);
		group.layout();

		// /////////////////////////////////////////////////////////////////////////

		GridLayout layout1 = new GridLayout();
		layout1.numColumns = 3;
		GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL);
		gridData1.horizontalSpan = 3;
		group1.setLayout(layout1);
		group1.setLayoutData(gridData1);
		group1.layout();

		Label version = new Label(getFieldEditorParent(), SWT.NONE);
		String status = NTailPlugin.getDefault().isJDTInstalled() ? "" : "not ";
		version.setText("JDT " + status + "resident");
	}

	public void init(IWorkbench workbench) {}
}