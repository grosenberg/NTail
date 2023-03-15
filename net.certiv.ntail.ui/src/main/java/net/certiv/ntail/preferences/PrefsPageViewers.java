package net.certiv.ntail.preferences;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import net.certiv.ntail.Key;
import net.certiv.ntail.NTailPlugin;
import net.certiv.ntail.dialogs.ViewerDialog;
import net.certiv.ntail.utils.TreeItemTransfer;
import net.certiv.ntail.viewers.Viewer;
import net.certiv.ntail.viewers.ViewerSet;

/**
 * This class represents a preference page that is contributed to the Preferences dialog.
 * By subclassing <samp>FieldEditorPreferencePage </samp>, we can use the field support
 * built into JFace that allows us to create a page that is small and knows how to save,
 * restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the preference store
 * that belongs to the main plug-in class. That way, preferences can be accessed directly
 * via the preference store.
 * </p>
 */

public class PrefsPageViewers extends PreferencePage implements IWorkbenchPreferencePage {

	private Tree treePanel;
	private Composite buttonPanel;
	private Button deleteButton;
	private Button editButton;
	private Button newButton;
	private Composite contentComposite;
	private ViewerDialog viewerDialog;
	private ViewerSet viewerSet;
	private Label noteLabel;
	private Button importButton;
	private Button exportButton;

	public PrefsPageViewers() {
		setDescription("Views and Viewers");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI blocks
	 * needed to manipulate various types of preferences. Each field editor knows how to
	 * save and restore itself.
	 */
	protected Control createContents(Composite parent) {
		contentComposite = new Composite(parent, SWT.NULL);

		GridLayout gpLayout = new GridLayout();
		gpLayout.marginWidth = 10;
		gpLayout.marginHeight = 10;
		gpLayout.numColumns = 2;
		gpLayout.makeColumnsEqualWidth = false;
		gpLayout.horizontalSpacing = 10;
		gpLayout.verticalSpacing = 10;
		GridData gpLayoutData = new GridData();
		gpLayoutData.verticalAlignment = GridData.FILL;
		gpLayoutData.horizontalAlignment = GridData.FILL;
		gpLayoutData.grabExcessHorizontalSpace = true;
		gpLayoutData.grabExcessVerticalSpace = true;
		contentComposite.setLayout(gpLayout);
		contentComposite.setLayoutData(gpLayoutData);
		contentComposite.setFont(parent.getFont());

		// /////////////////////////////////////////
		treePanel = new Tree(contentComposite, SWT.SINGLE | SWT.BORDER);
		GridData tree1LData = new GridData();
		tree1LData.verticalAlignment = GridData.FILL;
		tree1LData.horizontalAlignment = GridData.FILL;
		tree1LData.grabExcessVerticalSpace = true;
		tree1LData.grabExcessHorizontalSpace = true;
		treePanel.setLayoutData(tree1LData);

		GridLayout composite2Layout = new GridLayout();
		composite2Layout.verticalSpacing = 10;

		GridData composite2LData = new GridData();
		composite2LData.horizontalAlignment = GridData.END;
		composite2LData.verticalAlignment = GridData.BEGINNING;
		composite2LData.grabExcessVerticalSpace = true;
		composite2LData.widthHint = 80;

		buttonPanel = new Composite(contentComposite, SWT.NONE);
		buttonPanel.setLayoutData(composite2LData);
		buttonPanel.setLayout(composite2Layout);

		GridData button1LData = new GridData();
		button1LData.horizontalAlignment = GridData.FILL;
		newButton = new Button(buttonPanel, SWT.NONE);
		newButton.setText("New");
		newButton.setLayoutData(button1LData);
		newButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent evt) {
				addViewer(evt);
			}
		});

		GridData button2LData = new GridData();
		button2LData.horizontalAlignment = GridData.FILL;
		editButton = new Button(buttonPanel, SWT.NONE);
		editButton.setText("Edit");
		editButton.setLayoutData(button2LData);
		editButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent evt) {
				editViewer(evt);
			}
		});

		GridData button3LData = new GridData();
		button3LData.horizontalAlignment = GridData.FILL;
		deleteButton = new Button(buttonPanel, SWT.NONE);
		deleteButton.setText("Delete");
		deleteButton.setLayoutData(button3LData);
		deleteButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent evt) {
				deleteViewer(evt);
			}
		});

		new Label(buttonPanel, SWT.SEPARATOR | SWT.CENTER | SWT.HORIZONTAL);

		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.FILL;
		importButton = new Button(buttonPanel, SWT.NONE);
		importButton.setText("Import");
		importButton.setLayoutData(gridData3);
		importButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent evt) {
				importViewers(evt);
			}
		});

		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = GridData.FILL;
		exportButton = new Button(buttonPanel, SWT.NONE);
		exportButton.setText("Export");
		exportButton.setLayoutData(gridData4);
		exportButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent evt) {
				exportViewers(evt);
			}
		});

		noteLabel = new Label(contentComposite, SWT.NONE);
		noteLabel.setText("Use DnD to reorder Views and \nmove Viewers between Views");

		// //////////////////////////////////////////////////////////////
		addDnD();
		// //////////////////////////////////////////////////////////////
		viewerSet = NTailPlugin.getDefault().getViewerSet();
		populateTree(treePanel);
		contentComposite.layout();
		return contentComposite;
	}

	protected void exportViewers(SelectionEvent evt) {
		FileDialog dialog = new FileDialog(getShell(), SWT.SINGLE | SWT.SAVE);
		dialog.setFilterExtensions(new String[] {"*.xml"});
		dialog.open();
		if (dialog.getFileName().length() > 0) {
			String filename = dialog.getFilterPath() + File.separator + dialog.getFileName();
			File exportFile = new File(filename);
			if (!exportFile.exists()
					|| MessageDialog.openQuestion(getShell(), "Export Viewer Definitions",
						"Export file exists!  Overwrite?")) {
				NTailPlugin.getDefault().getViewerSet().saveViewers(exportFile);
			}
		}
	}

	protected void importViewers(SelectionEvent evt) {
		FileDialog dialog = new FileDialog(getShell(), SWT.SINGLE);
		dialog.setFilterExtensions(new String[] {"*.xml"});
		dialog.open();
		if (dialog.getFileName().length() > 0) {
			String filename = dialog.getFilterPath() + File.separator + dialog.getFileName();
			File importFile = new File(filename);
			if (importFile.exists() && importFile.canRead()) {
				NTailPlugin.getDefault().getViewerSet().loadViewers(importFile);
				populateTree(treePanel);
				NTailPlugin.getDefault().getViewerSet().firePropertyChangeEvent();
			}
		}
	}

	protected void performApply() {
		NTailPlugin.getDefault().getViewerSet().orderViewers(treeOrderedViewers());
		NTailPlugin.getDefault().getViewerSet().saveViewers();
		NTailPlugin.getDefault().getViewerSet().firePropertyChangeEvent();
		super.performApply();
	}

	/**
	 * TODO: check for modifications before reloading
	 */
	protected void performDefaults() {
		NTailPlugin.getDefault().getViewerSet().loadViewers();
		NTailPlugin.getDefault().getViewerSet().firePropertyChangeEvent();
		populateTree(treePanel);
		super.performDefaults();
	}

	public boolean performOk() {
		NTailPlugin.getDefault().getViewerSet().orderViewers(treeOrderedViewers());
		NTailPlugin.getDefault().getViewerSet().saveViewers();
		NTailPlugin.getDefault().getViewerSet().firePropertyChangeEvent();
		return super.performOk();
	}

	/**
	 * TODO: check for modifications before reloading
	 */
	public boolean performCancel() {
		NTailPlugin.getDefault().getViewerSet().loadViewers();
		return super.performCancel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		setPreferenceStore(NTailPlugin.getDefault().getPreferenceStore());
	}

	/**
	 * @param evt
	 */
	private void addViewer(SelectionEvent evt) {
		Viewer viewer = new Viewer();
		viewer.init();
		if (createViewerDialog(viewer, false) == Window.OK) {
			Viewer w = viewerDialog.getViewer();
			if (w != null) {
				NTailPlugin.getDefault().getViewerSet().addViewer(w);
				addViewerToTree(treePanel, w);
			}
		}
	}

	/**
	 * @param evt
	 */
	private void editViewer(SelectionEvent evt) {
		TreeItem[] items = treePanel.getSelection();
		if (items == null) return;
		TreeItem ti = items[0];
		if (ti.getParentItem() != null) { // only edit viewer nodes
			Viewer w = (Viewer) ti.getData(Key.TREE_KEY);
			if (createViewerDialog(w, true) == Window.OK) {
				w = viewerDialog.getViewer();
				if (w != null) {
					NTailPlugin.getDefault().getViewerSet().addViewer(w);
				}
			}
		}
	}

	private int createViewerDialog(Viewer w, boolean edit) {
		if (viewerDialog == null) {
			viewerDialog = new ViewerDialog(contentComposite.getShell());
		}
		return viewerDialog.open(w, edit);
	}

	/**
	 * @param evt
	 */
	private void deleteViewer(SelectionEvent evt) {
		TreeItem[] items = treePanel.getSelection();
		if (items == null) return;
		TreeItem ti = items[0];
		if (ti.getParentItem() != null) { // only remove viewer nodes
			Viewer w = (Viewer) ti.getData(Key.TREE_KEY);
			NTailPlugin.getDefault().getViewerSet().removeViewer(w);
			if (ti.getParentItem().getItemCount() == 1) {
				ti.getParentItem().dispose();
			} else {
				ti.dispose();
			}
		}
	}

	/**
	 * Clear and reload all of the tree items from the current store.
	 * 
	 * @param tree the tree to (re)populate
	 */
	@SuppressWarnings("rawtypes")
	private void populateTree(Tree tree) {
		tree.removeAll();
		Iterator wItr = viewerSet.getViewerList().iterator();
		while (wItr.hasNext()) {
			Viewer w = (Viewer) wItr.next();
			addViewerToTree(tree, w);
		}
	}

	private void addViewerToTree(Tree tree, Viewer v) {
		// first try to add to existing viewName
		TreeItem[] viewNodes = tree.getItems();
		for (int idx = 0; idx < viewNodes.length; idx++) {
			TreeItem viewNode = viewNodes[idx];
			if (viewNode.getText().equals(v.getViewName())) {
				TreeItem t = new TreeItem(viewNode, SWT.NONE);
				t.setText("Viewer: " + v.getFileName());
				t.setData(Key.TREE_KEY, v);
				return;
			}
		}
		// not found, so create new viewName leaf and add node
		TreeItem t = new TreeItem(tree, SWT.NONE);
		t.setText(v.getViewName());
		TreeItem t1 = new TreeItem(t, SWT.NONE);
		t1.setText("Viewer: " + v.getFileName());
		t1.setData(Key.TREE_KEY, v);
	}

	private ArrayList<Viewer> treeOrderedViewers() {
		ArrayList<Viewer> tv = new ArrayList<Viewer>();
		for (TreeItem treeNode : treePanel.getItems()) {
			for (TreeItem viewItem : treeNode.getItems()) {
				tv.add((Viewer) viewItem.getData(Key.TREE_KEY));
			}
		}
		return tv;
	}

	private void addDnD() {
		Transfer[] types = new Transfer[] {TreeItemTransfer.getInstance()};
		int operations = DND.DROP_MOVE /* | DND.DROP_COPY | DND.DROP_LINK */;

		final DragSource source = new DragSource(treePanel, operations);
		source.setTransfer(types);
		final TreeItem[] dragSourceItem = new TreeItem[1];
		source.addDragListener(new DragSourceListener() {

			public void dragStart(DragSourceEvent event) {
				TreeItem[] selection = treePanel.getSelection();
				if (selection.length == 0) {
					event.doit = false;
				} else {
					event.doit = true;
					dragSourceItem[0] = selection[0];
				}
			};

			public void dragSetData(DragSourceEvent event) {
				event.data = dragSourceItem[0];
			}

			public void dragFinished(DragSourceEvent event) {
				if (event.doit) {
					// NTailPlugin.getDefault().debug("Disposing");
					dragSourceItem[0].dispose();
					dragSourceItem[0] = null;
				}
			}
		});

		DropTarget target = new DropTarget(treePanel, operations);
		target.setTransfer(types);
		target.addDropListener(new DropTargetAdapter() {

			@Override
			public void dropAccept(DropTargetEvent event) {
				TreeItem dropOnItem = (TreeItem) event.item;
				TreeItem parentItem = dropOnItem.getParentItem();
				boolean viewer = dragSourceItem[0].getItemCount() == 0;

				// block dropping a view on a viewer
				if (!viewer && parentItem != null) {
					// NTailPlugin.getDefault().debug("Block drop of view on viewer");
					event.detail = DND.DROP_NONE;
				}
				super.dropAccept(event);
			}

			public void dragOver(DropTargetEvent event) {
				event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
				if (event.item != null) {
					TreeItem dropOnItem = (TreeItem) event.item;
					// TreeItem parentItem = dropOnItem.getParentItem();
					// boolean viewer = dragSourceItem[0].getItemCount() == 0;
					Point pt = Display.getCurrent().map(null, treePanel, event.x, event.y);
					Rectangle bounds = dropOnItem.getBounds();
					if (pt.y < bounds.y + bounds.height / 3) {
						event.feedback |= DND.FEEDBACK_INSERT_BEFORE;
					} else if (pt.y > bounds.y + 2 * bounds.height / 3) {
						event.feedback |= DND.FEEDBACK_INSERT_AFTER;
					} else {
						event.feedback |= DND.FEEDBACK_SELECT;
					}
				}
			}

			public void drop(DropTargetEvent event) {
				if (event.data == null || event.item == null) {
					event.detail = DND.DROP_NONE;
					return;
				}
				TreeItem sourceItem = (TreeItem) event.data; // drop this item
				TreeItem dropOnItem = (TreeItem) event.item; // on this item

				Point pt = Display.getCurrent().map(null, treePanel, event.x, event.y);
				Rectangle bounds = dropOnItem.getBounds();

				TreeItem dropOnParent = dropOnItem.getParentItem();
				boolean viewer = dragSourceItem[0].getItemCount() == 0;
				if (viewer) {
					if (dropOnParent == null) {
						// NTailPlugin.getDefault().debug("Viewer on view");
						// droping a viewer on a view, which is the "dropOnItem"
						int index = dropOnItem.getItemCount(); // so add to end
						dropTreeItem(dropOnItem, sourceItem, index);
					} else {
						// droping a viewer on another viewer
						// NTailPlugin.getDefault().debug("Viewer on viewer");
						TreeItem[] items = dropOnParent.getItems();
						int index = 0;
						for (int i = 0; i < items.length; i++) {
							if (items[i] == dropOnItem) {
								index = i;
								break;
							}
						}
						if (pt.y < bounds.y + bounds.height / 3) {
							dropTreeItem(dropOnParent, sourceItem, index);
						} else if (pt.y > bounds.y + 2 * bounds.height / 3) {
							dropTreeItem(dropOnParent, sourceItem, index + 1);
						} else { // on is same as after
							dropTreeItem(dropOnParent, sourceItem, index + 1);
						}
					}
				} else {
					// moving a view, not a viewer
					if (dropOnParent == null) {
						// dropping a view on a view
						// NTailPlugin.getDefault().debug("View on view");
						TreeItem[] items = treePanel.getItems();
						int index = 0;
						for (int i = 0; i < items.length; i++) {
							if (items[i] == dropOnItem) {
								index = i;
								break;
							}
						}
						if (pt.y < bounds.y + bounds.height / 3) {
							dropTreeItem(treePanel, sourceItem, index);
						} else if (pt.y > bounds.y + 2 * bounds.height / 3) {
							dropTreeItem(treePanel, sourceItem, index + 1);
						} else { // on is same as after
							dropTreeItem(treePanel, sourceItem, index + 1);
						}
					} else {
						// dropping a view on a viewer; block with #dropAccept()
						NTailPlugin.getDefault().error("Attempt to drop view on viewer");
					}
				}
			}
		});
	}

	protected void dropTreeItem(TreeItem parent, TreeItem sourceItem, int index) {
		// NTailPlugin.getDefault().debug("dropItem");
		Viewer v = (Viewer) sourceItem.getData(Key.TREE_KEY);
		v.setViewName(parent.getText());
		TreeItem item = new TreeItem(parent, SWT.NONE, index);
		item.setText(sourceItem.getText());
		item.setData(Key.TREE_KEY, v);
	}

	/**
	 * Creates a new node on the given <i>tree</i> and adds a new leaf item for each of
	 * the leaf items of the given <i>sourceItem</i>.
	 * 
	 * @param tree
	 * @param sourceItem
	 * @param index
	 */
	protected void dropTreeItem(Tree tree, TreeItem sourceItem, int index) {
		// NTailPlugin.getDefault().debug("dropTree");
		TreeItem item = new TreeItem(tree, SWT.NONE, index);
		item.setText(sourceItem.getText());
		TreeItem[] items = sourceItem.getItems();
		for (int idx = 0; idx < items.length; idx++) {
			dropTreeItem(item, items[idx], idx);
		}
	}
}