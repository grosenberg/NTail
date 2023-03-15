package net.certiv.ntail;

import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;

import net.certiv.ntail.actions.ClearAction;
import net.certiv.ntail.actions.ClearAllAction;
import net.certiv.ntail.actions.CloseAction;
import net.certiv.ntail.actions.CopyAction;
import net.certiv.ntail.actions.EditAction;
import net.certiv.ntail.actions.FindAction;
import net.certiv.ntail.actions.NewAction;
import net.certiv.ntail.actions.OpenAction;
import net.certiv.ntail.actions.ScrollAction;
import net.certiv.ntail.actions.TruncateAction;
import net.certiv.ntail.actions.TruncateAllAction;
import net.certiv.ntail.preferences.Prefs;
import net.certiv.ntail.utils.AnsiParser;
import net.certiv.ntail.utils.BufferList;
import net.certiv.ntail.viewers.Viewer;
import net.certiv.ntail.viewers.ViewerSet;
import net.certiv.ntail.viewers.ViewerSetEntry;
import net.certiv.ntail.viewers.ViewerTail;
import net.certiv.ntail.viewers.ViewerTailListener;

/**
 * The customized ViewPart used to construct each NTail instance.
 */
public class NTailView extends ViewPart {

	public static final String IDLE = "<<Idle>>";

	/** List of the Viewer objects installed in this view instance. */
	private ArrayList<ViewerSetEntry> localViewerList = new ArrayList<>();
	/** Tab container for the view instance */
	private CTabFolder viewFolder = null;
	// private static final Pattern linkPattern = Pattern
	// .compile("((?:\\w*\\.)+)\\w*(?:\\$\\d(?:\\.\\w*)?)?(?:\\()((\\w+)\\.\\w+)(?::)(\\d+)(?:\\))");
	private static final Pattern linkPattern = Pattern.compile("((?:\\w*|\\.)+)\\.[A-Z].*\\((\\w+\\.\\w+):(\\d+)");
	private static final String pathSeparator = FileSystems.getDefault().getSeparator();
	private static final Cursor handCursor = new Cursor(null, SWT.CURSOR_HAND);

	private Color hrColor;
	private Color shadow;
	private Color strikeColor;

	private String viewName = null;

	private MenuManager openToolMenu = null;
	private MenuManager openContextMenu = null;

	private Action actionClose = null;
	private Action actionNew = null;
	private Action actionEdit = null;
	private Action actionScroll = null;
	private Action actionFind = null;
	private Action actionCopy = null;
	private Action actionClear = null;
	private Action actionClearAll = null;
	private Action actionTrunc = null;
	private Action actionTruncAll = null;

	/**
	 * Listen for changes to NTail preferences. Currently, only changes to the font are noticed.
	 */
	private IPropertyChangeListener propStoreListener = new IPropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals(Key.VIEW_FONT)) {
				Font font = Prefs.getFontPreference(Key.VIEW_FONT);
				for (ViewerSetEntry entry : localViewerList) {
					entry.getTextViewer().getTextWidget().setFont(font);
				}
			}
		}
	};
	private IPropertyChangeListener viewerSetListener = new IPropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals((Key.VIEW_NAME))) {
				refreshLocalViewerList(event);
				buildOpenSubMenu(openToolMenu);
			}
		}
	};
	private IPartListener viewPartListener = new IPartListener() {

		// this is called before #getViewReferences is properly populated
		@Override
		public void partOpened(IWorkbenchPart part) {}

		@Override
		public void partActivated(IWorkbenchPart part) {
			if (part instanceof NTailView) {
				// NTailPlugin.getDefault().debug("Part opened: " + part.getTitle());
				buildOpenSubMenu(openToolMenu);
			}
		}

		@Override
		public void partClosed(IWorkbenchPart part) {
			if (part instanceof NTailView) {
				// NTailPlugin.getDefault().debug("Part closed: " + part.getTitle());
				buildOpenSubMenu(openToolMenu);
			}
		}

		@Override
		public void partDeactivated(IWorkbenchPart part) {}

		@Override
		public void partBroughtToTop(IWorkbenchPart part) {}
	};

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		NTailPlugin plugin = NTailPlugin.getDefault();
		plugin.getPreferenceStore().addPropertyChangeListener(propStoreListener);
		plugin.getViewerSet().addPropertyChangeListener(viewerSetListener);
		getSite().getPage().addPartListener(viewPartListener);

		hrColor = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
		shadow = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
		strikeColor = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

		String name = null;
		if (memento != null) {
			// this will be non-null if the platform is re-opening the view
			name = memento.getString(Key.VIEW_NAME);
		}
		if (name == null) {
			// check to see if being first opened from the NTail view list
			name = plugin.getOpenActionViewName();
			plugin.setOpenActionViewName(null); // and reset
			// explicitly opening an undefined viewer, so assign a unique name
			if (name != null && name.equals(Key.NEW_VIEW)) name = site.getSecondaryId();
		}
		if (name == null) {
			// must be a first open from the platform view list
			name = plugin.getViewerSet().getViewName(0);
		}
		if (name == null) {
			// no viewers defined, so assign a unique name
			name = site.getSecondaryId();
			name = name == null ? "0" : name;
		}
		viewName = name;
	}

	/**
	 * Callback to create the viewer
	 */
	@Override
	public void createPartControl(Composite parent) {
		setFolder(new CTabFolder(parent, SWT.NONE));

		// Add listeners so the title of the view is always accurate
		getFolder().addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				CTabItem item = (CTabItem) e.item;
				adjustTitles(item.getToolTipText()); // hack to get full file spec
				openContextMenu = new MenuManager("Open View");
				buildOpenSubMenu(openContextMenu);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				CTabItem item = (CTabItem) e.item;
				adjustTitles(item.getToolTipText()); // hack to get full file spec
			}
		});
		makeActions();
		contributeToActionBars();
		setGlobalActionHandlers();
		adjustTitles(null);
		loadViewState();
	}

	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		memento.putString(Key.VIEW_NAME, viewName);
	}

	/**
	 * Create all working actions.
	 */
	private void makeActions() {
		actionClose = new CloseAction(this);
		actionClose.setEnabled(false);

		actionNew = new NewAction(this);
		openToolMenu = new MenuManager("Open View");
		openContextMenu = new MenuManager("Open View");

		actionEdit = new EditAction(this);
		actionEdit.setEnabled(false);

		actionClear = new ClearAction(this);
		actionClear.setEnabled(false);
		actionClearAll = new ClearAllAction(this);
		actionClearAll.setEnabled(false);

		actionTrunc = new TruncateAction(this);
		actionTrunc.setEnabled(false);
		actionTruncAll = new TruncateAllAction(this);
		actionTruncAll.setEnabled(false);

		actionFind = new FindAction(this);
		actionCopy = new CopyAction(this);

		actionScroll = new ScrollAction(this);
		actionScroll.setChecked(false);
		actionScroll.setEnabled(false);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager(), bars.getMenuManager());
	}

	private void fillLocalToolBar(IToolBarManager manager, IMenuManager menuManager) {
		if (Prefs.isStringPreference(Key.ICON_SET, Key.MINIMAL)) {
			manager.add(actionClear);
			manager.add(actionScroll);
			manager.add(actionTrunc);
		} else {
			manager.add(actionNew);
			manager.add(actionEdit);
			manager.add(actionScroll);
			manager.add(actionClose);
			manager.add(actionClear);
			manager.add(actionClearAll);
			manager.add(actionTrunc);
			manager.add(actionTruncAll);
		}

		menuManager.add(actionNew);
		menuManager.add(actionEdit);
		menuManager.add(actionScroll);
		menuManager.add(actionClose);
		menuManager.add(new Separator("1"));
		menuManager.add(openToolMenu);
		menuManager.add(new Separator("2"));
		menuManager.add(actionClear);
		menuManager.add(actionClearAll);
		menuManager.add(new Separator("3"));
		menuManager.add(actionTrunc);
		menuManager.add(actionTruncAll);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(actionFind);
		manager.add(actionCopy);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		manager.add(actionNew);
		manager.add(actionEdit);
		manager.add(actionScroll);
		manager.add(actionClose);
		manager.add(new Separator("11"));
		manager.add(openContextMenu);
		manager.add(new Separator("12"));
		manager.add(actionClear);
		manager.add(actionClearAll);
		manager.add(new Separator("13"));
		manager.add(actionTrunc);
		manager.add(actionTruncAll);
	}

	/**
	 * TODO: need to make sure that the order of viewers is correct. Appears any viewer moved or new
	 * in the preference page is just added at the end
	 *
	 * @param event
	 */
	protected synchronized void refreshLocalViewerList(PropertyChangeEvent event) {
		ViewerSet viewerSet = NTailPlugin.getDefault().getViewerSet();
		ArrayList<Viewer> viewerList = viewerSet.getViewers(viewName);
		for (Iterator<ViewerSetEntry> eItr = localViewerList.iterator(); eItr.hasNext();) {
			ViewerSetEntry entry = eItr.next();
			if (!viewerList.contains(entry.getViewer())) {
				closeViewer(entry); // remove obsolete viewer
				eItr.remove(); // remove the entry from the localViewerList
			}
		}
		for (Viewer w : viewerList) {
			boolean found = false;
			for (ViewerSetEntry entry : localViewerList) {
				if (entry.getViewer().equals(w)) {
					entry.initViewerSetEntry();
					found = true;
					break;
				}
			}
			if (!found) addViewer(w); // add new viewer
		}
	}

	private void buildOpenSubMenu(MenuManager openSubMenu) {
		openSubMenu.removeAll();
		ArrayList<String> viewNames = findUnusedViewers();
		// NTailPlugin.getDefault().debug("Menu: " + viewNames.toString());
		for (String name : viewNames) {
			openSubMenu.add(new OpenAction(this, name));
		}
		openSubMenu.add(new Separator());
		openSubMenu.add(new OpenAction(this, Key.NEW_VIEW));
		openSubMenu.updateAll(true);
	}

	private ArrayList<String> findUnusedViewers() {
		// build list of all known viewer names
		ViewerSet viewerSet = NTailPlugin.getDefault().getViewerSet();
		ArrayList<String> names = viewerSet.getUniqueViewNames();
		ArrayList<String> retNames = new ArrayList<>(names);
		// remove names corresponding to existing views
		IViewReference[] refs = getViewSite().getPage().getViewReferences();
		for (IViewReference vref : refs) {
			String partName = vref.getPartName();
			for (String name : names) {
				if (partName.equals(NTailPlugin.PLUGIN_ID + ":" + name)) {
					retNames.remove(name);
				}
			}
		}
		return retNames;
	}

	private void setGlobalActionHandlers() {
		getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.FIND.getId(), actionFind);
		getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.COPY.getId(), actionCopy);
	}

	public void adjustTitles(String viewName, String logName) {
		this.viewName = viewName;
		adjustTitles(logName);
	}

	private void adjustTitles(String logName) {
		StringBuilder title = new StringBuilder(NTailPlugin.PLUGIN_ID);
		title.append(":").append(viewName);
		if (logName != null && logName.length() > 0) {
			// expand view title
			if (Prefs.getBooleanPreference(Key.VIEW_TITLE_NAME)) {
				title.append("-").append(logName.substring(logName.lastIndexOf(pathSeparator) + 1));
			}
			// Set inner content description line
			if (Prefs.getBooleanPreference(Key.SHOW_INNER_TITLE)) {
				setContentDescription(" " + logName); // (w/leading margin)
			} else {
				setContentDescription("");
			}
		}
		setPartName(title.toString()); // Set the view tab title.
	}

	/**
	 * Load the viewer state from a previous instance.
	 */
	private void loadViewState() {
		if (Prefs.getBooleanPreference(Key.SAVE_VIEWERS)) {
			ViewerSet viewerSet = NTailPlugin.getDefault().getViewerSet();
			for (Viewer v : viewerSet.getViewerList()) {
				if (v.getViewName().equals(viewName)) {
					addViewer(v);
				}
			}
		}
	}

	/**
	 * Close and dispose of the currently selected viewer
	 */
	public void closeSelectedViewer() {
		ViewerSetEntry entry = getSelectedEntry();
		if (entry != null) {
			closeViewer(entry);
			localViewerList.remove(entry); // remove from local list of viewers
			NTailPlugin.getDefault().getViewerSet().removeViewer(entry.getViewer());
		}
	}

	private void closeViewer(ViewerSetEntry entry) {
		if (entry != null) {
			entry.dispose(); // halt the tail thread; dispose tab
			if (getFolder().getItemCount() == 0) {
				actionEdit.setEnabled(false);
				actionClear.setEnabled(false);
				actionClearAll.setEnabled(false);
				actionTrunc.setEnabled(false);
				actionTruncAll.setEnabled(false);
				actionScroll.setEnabled(false);
				actionClose.setEnabled(false);
			}
		}
	}

	/**
	 * Returns the ViewerSetEntry for the currently selected viewer
	 */
	public ViewerSetEntry getSelectedEntry() {
		return findEntry(getFolder().getSelection());
	}

	/**
	 * Find the ViewerSetEntry associated with the given CTabItem.
	 */
	public ViewerSetEntry findEntry(CTabItem item) {
		for (ViewerSetEntry entry : localViewerList) {
			if (entry.getTab().equals(item)) {
				return entry;
			}
		}
		NTailPlugin.getDefault().error("ViewerSetEntry not found");
		return null;
	}

	/**
	 * @param w
	 */
	private void addViewer(Viewer w) {
		if (!validate(w)) return;

		CTabItem viewTab = new CTabItem(getFolder(), 0); // Add tab
		viewTab.setToolTipText(w.getToolTipText());
		viewTab.setText(w.getShortFileName());
		adjustTitles(w.getFileName());
		getFolder().setSelection(viewTab);

		// Create the text viewer and associated document
		final TextViewer viewer = new TextViewer(getFolder(), SWT.H_SCROLL | SWT.V_SCROLL);
		viewTab.setControl(viewer.getControl());
		viewer.setDocument(new Document()); // required for initialization!
		viewer.setEditable(false);

		// Add a context menu to the text viewer
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
		menuMgr.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				buildOpenSubMenu(openContextMenu);
				fillContextMenu(manager);
				manager.updateAll(true);
			}
		});

		// Add the viewer
		ViewerTail viewerTail;
		try {
			viewerTail = new ViewerTail(w);
		} catch (Exception e) {
			NTailPlugin.getDefault().error(
					"Failed to create tail [viewName=" + w.getViewName() + ", fileName=" + w.getFileName() + "]", e);
			return;
		}
		final ViewerSetEntry entry = new ViewerSetEntry(viewer, viewerTail, viewTab, w);
		localViewerList.add(entry);
		addViewerTailListener(viewerTail, entry);

		// Adjust styles
		viewer.getTextWidget().addExtendedModifyListener(new ExtendedModifyListener() {

			@Override
			public void modifyText(ExtendedModifyEvent event) {
				if (entry.getViewer().isFilters()) {
					if (entry.getViewer().isHyperlink()) {
						entry.addHyperlink(event);
					}
					if (entry.getViewer().isHighlight()) {
						entry.addHighlight(event);
					}
				}
			}
		});

		if (entry.getViewer().isHyperlink() && NTailPlugin.getDefault().isJDTInstalled()) {

			// Handle hyperlinks
			viewer.getTextWidget().addMouseListener(new MouseAdapter() {

				@Override
				public void mouseDoubleClick(MouseEvent e) {
					if (entry.getViewer().isFilters() && entry.getViewer().isHyperlink()) {
						processLink((StyledText) e.getSource(), new Point(e.x, e.y));
					}
					super.mouseDoubleClick(e);
				}
			});

			// Adjust cursor/pointer
			viewer.getTextWidget().addMouseMoveListener(new MouseMoveListener() {

				@Override
				public void mouseMove(MouseEvent e) {
					if (entry.getViewer().isFilters() && entry.getViewer().isHyperlink()) {
						adjustCursor((StyledText) e.getSource(), new Point(e.x, e.y));
					}
				}
			});

			viewer.getTextWidget().addPaintListener(new IdleLineListener(viewer.getTextWidget()));
		}

		// Set the font
		Font f = Prefs.getFontPreference(Key.VIEW_FONT);
		viewer.getTextWidget().setFont(f);

		// Start the viewer tail and enable actions.
		viewerTail.start();
		actionEdit.setEnabled(true);
		actionClear.setEnabled(true);
		actionClearAll.setEnabled(true);
		actionTrunc.setEnabled(true);
		actionTruncAll.setEnabled(true);
		actionScroll.setEnabled(true);
		actionClose.setEnabled(true);

		if (entry.isScroll()) { // scroll to reveal the last line
			revealLastLines(viewer);
		}
	}

	/*
	 * Event call to this method is never made if JDT is not installed
	 */
	protected void adjustCursor(StyledText st, Point loc) {
		try {
			int offset = st.getOffsetAtPoint(loc);
			StyleRange range = st.getStyleRangeAtOffset(offset);
			if (range != null && range.underline && range.foreground.equals(Prefs.getColorPreference(Key.LINK_COLOR))) {
				st.setCursor(handCursor);
				return;
			}
		} catch (IllegalArgumentException e) {}
		st.setCursor(null);
	}

	/*
	 * Event call to this method is never made if JDT is not installed
	 */
	protected void processLink(StyledText st, Point loc) {
		String line = null;
		try {
			int offset = st.getOffsetAtPoint(loc);
			// verify that the click was on a link
			StyleRange range = st.getStyleRangeAtOffset(offset);
			if (range == null || !range.underline
					|| !range.foreground.equals(Prefs.getColorPreference(Key.LINK_COLOR))) {
				return;
			}

			int idxLine = st.getLineAtOffset(offset);

			int beg = st.getOffsetAtLine(idxLine);
			int end = st.getText().length() - 1; // default end
			if (idxLine + 1 < st.getLineCount()) { // adjust end
				end = st.getOffsetAtLine(idxLine + 1) - st.getLineDelimiter().length() - 1;
			}
			line = st.getText(beg, end);
		} catch (IllegalArgumentException evt) {
			NTailPlugin.getDefault().error("Clicked on nothing");
			evt.printStackTrace();
			return;
		}
		if (line != null) {
			line = line.replace('$', '.');
			line = line.replace("<init>", "init");
			Matcher m = linkPattern.matcher(line);
			// org.eclipse.jface.viewers.StructuredViewer.refresh(StructuredViewer.java:1387)
			if (m.find()) {
				// build fname as a FQN with extension; fnum as an Integer
				String bpath = m.group(1); // classPath: org.eclipse.jface.viewers
				String fname = m.group(2); // filename.ext: StructuredViewer.java
				String lnumb = m.group(3); // line number: 1387
				fname = bpath + "." + fname;
				int fnum = Integer.parseInt(lnumb);
				NTailPlugin.getDefault().getDocUtils().revealFile(fname, fnum);
			} else {
				NTailPlugin.getDefault().error("Reveal failed [line=" + line + "]");
			}
		}
	}

	private boolean validate(Viewer viewer) {
		if (viewer.getViewName() == null || viewer.getViewName().length() == 0) {
			return false;
		}
		if (viewer.getFileName() == null || viewer.getFileName().length() == 0) {
			return false;
		} else if (!viewer.getFile().exists()) {
			return false;
		} else if (viewer.getInterval() < 250) {
			return false;
		} else if (!viewer.getFullFile() && viewer.getNumLines() <= 0) {
			return false;
		}
		return true;
	}

	/**
	 * Add a ViewerTailListener to the given viewer.
	 */
	private void addViewerTailListener(ViewerTail tail, final ViewerSetEntry entry) {
		ViewerTailUpdater updater = new ViewerTailUpdater();
		updater.setEntry(entry);
		tail.addListener(updater);
	}

	private class ViewerTailUpdater implements ViewerTailListener {

		private ViewerSetEntry entry;

		public void setEntry(ViewerSetEntry entry) {
			this.entry = entry;
		}

		@Override
		public void update(BufferList list) {
			final BufferList flist = (BufferList) list.clone();
			Display display = entry.getTextViewer().getControl().getDisplay();
			display.asyncExec(new Runnable() {

				@Override
				public void run() {
					StyledText styledText = entry.getTextViewer().getTextWidget();
					if (flist.size() == 1 && flist.get(0).equals(IDLE)) {
						styledText.append(styledText.getLineDelimiter());

						StyleRange sr = new StyleRange();
						sr.strikeout = true;
						sr.strikeoutColor = strikeColor;
						sr.start = styledText.getCharCount();
						sr.length = 1;
						styledText.append(" ");
						styledText.append(styledText.getLineDelimiter());
						styledText.setStyleRange(sr);

					} else if (entry.getViewer().isAnsiCodes()) {
						for (Object text : flist) {
							AnsiParser.processAnsiCodes(styledText, (String) text);
						}
					} else {
						String content = flist.getFormattedText();
						try {
							styledText.append(content);
							int lineCount = styledText.getLineCount();
							int lineLimit = flist.getSizeLimit();
							if (lineCount > lineLimit) {
								int offset = styledText.getOffsetAtLine(lineCount - lineLimit);
								styledText.getContent().replaceTextRange(0, offset, "");
							}
						} catch (RuntimeException e) {
							e.printStackTrace();
						}
					}
					if (entry.isScroll()) { // scroll to reveal the last line
						revealLastLines(entry.getTextViewer());
					}
				}
			});
		}
	}

	/**
	 * Change the properties of an active viewer.
	 */
	public void editViewer(ViewerSetEntry entry, Viewer viewer) {
		entry.setViewer(viewer);
		NTailPlugin.getDefault().getViewerSet().saveViewers();
		entry.restart();
	}

	public CTabFolder getFolder() {
		return viewFolder;
	}

	private void setFolder(CTabFolder viewFolder) {
		this.viewFolder = viewFolder;
	}

	@Override
	public void setFocus() {
		getFolder().setFocus();
	}

	@Override
	public void dispose() {
		super.dispose();
		getSite().getPage().removePartListener(viewPartListener);
		NTailPlugin plugin = NTailPlugin.getDefault();
		plugin.getPreferenceStore().removePropertyChangeListener(propStoreListener);
		plugin.getViewerSet().removePropertyChangeListener(viewerSetListener);
		for (ViewerSetEntry entry : localViewerList) {
			entry.dispose();
		}
		handCursor.dispose();
	}

	/**
	 * Given a TextViewer, reveal the last line contained by viewer's document at the bottom of the
	 * viewport. The first visible line is based on a calculation of the number of lines that can
	 * fully fit into the viewport, as determined by dividing the widget's client area height by the
	 * widget's line height. Only valid if the widget does not use variable line heights.
	 */
	protected void revealLastLines(TextViewer textViewer) {
		if (textViewer != null) {
			Rectangle cla = textViewer.getTextWidget().getClientArea();
			if (cla.isEmpty()) return; // skip if no lines can be shown
			IDocument doc = textViewer.getDocument();
			if (doc != null) {
				int docLines = doc.getNumberOfLines();
				if (docLines == 0) return; // skip if no lines to show

				int visibleLines = cla.height / textViewer.getTextWidget().getLineHeight();
				if (docLines <= visibleLines) {
					textViewer.setTopIndex(0);
				} else {
					textViewer.setTopIndex(docLines - visibleLines);
				}
			}
		}
	}

	public class IdleLineListener implements PaintListener {

		private StyledText textWidget;

		public IdleLineListener(StyledText textWidget) {
			this.textWidget = textWidget;
		}

		@Override
		public void paintControl(PaintEvent event) {
			GC gc = event.gc;
			StyleRange[] ranges = textWidget.getStyleRanges(true);
			for (StyleRange sr : ranges) {
				if (sr.strikeout) {
					Rectangle rec = textWidget.getClientArea();
					int offset = sr.start;
					Point ref = textWidget.getLocationAtOffset(offset);
					int height = textWidget.getLineHeight(offset) / 2;
					int width = rec.width - 6;
					gc.setLineWidth(1);
					drawHRule(gc, ref.x + 1, ref.y + height, width, 1, hrColor, shadow);
				}
			}
		}
	}

	private void drawHRule(GC gc, int x, int y, int w, int h, Color topLeft, Color bottomRight) {
		gc.setForeground(bottomRight);
		gc.drawLine(x + w, y, x + w, y + h);
		gc.drawLine(x, y + h, x + w, y + h);

		gc.setForeground(topLeft);
		gc.drawLine(x, y, x + w - 1, y);
		gc.drawLine(x, y, x, y + h - 1);
	}
}
