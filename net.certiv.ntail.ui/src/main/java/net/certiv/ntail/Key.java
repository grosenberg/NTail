package net.certiv.ntail;

/**
 * @author Gerald B. Rosenberg
 */
public interface Key {

	public static final String NAME = "NTail"; //$NON-NLS-1$
	public static final String ID1 = "net.certiv.ntail.NTailView"; //$NON-NLS-1$

	public static final String VIEW_TITLE_NAME = "viewTitleName"; //$NON-NLS-1$
	public static final String SHOW_INNER_TITLE = "showInnerTitle"; //$NON-NLS-1$
	public static final String ICON_SET = "iconSet"; //$NON-NLS-1$
	public static final String FULL = "1"; //$NON-NLS-1$
	public static final String MINIMAL = "0"; //$NON-NLS-1$

	public static final String VIEWERS_FILENAME = "NTailConfig"; //$NON-NLS-1$
	public static final String DEFAULT_VIEW = "defaultView"; //$NON-NLS-1$
	public static final String HIDDEN_VIEW = "hiddenView"; //$NON-NLS-1$

	public static final String VIEWER = "viewer"; //$NON-NLS-1$

	public static final String NEW_VIEW = "newView"; //$NON-NLS-1$

	public static final String VIEW_NAME = "viewName"; //$NON-NLS-1$
	public static final String FILE = "file"; //$NON-NLS-1$
	public static final String NUM_LINES = "numLines"; //$NON-NLS-1$
	public static final String INTERVAL = "interval"; //$NON-NLS-1$
	public static final String TRUNC_ON_START = "truncOnStart"; //$NON-NLS-1$

	public static final String FILTER = "filter"; //$NON-NLS-1$
	public static final String PATTERN = "pattern"; //$NON-NLS-1$
	public static final String CASE_SENSITIVE = "caseSensitive"; //$NON-NLS-1$
	public static final String CONTAINS = "contains"; //$NON-NLS-1$
	public static final String DESCRIPTION = "description"; //$NON-NLS-1$
	public static final String ACTION = "action"; //$NON-NLS-1$

	public static final String SAVE_VIEWERS = "saveViewers"; //$NON-NLS-1$
	public static final String VIEW_FONT = "viewFont"; //$NON-NLS-1$

	public static final String HIGHLIGHT = "highlight"; //$NON-NLS-1$
	public static final String LINK_COLOR = "linkColor"; //$NON-NLS-1$

	public static final String TREE_KEY = "treeKey"; //$NON-NLS-1$

	public static int DEFAULT_INTERVAL = 1;
	public static int DEFAULT_NUMLINES = 100;

	public static final String RELEASE = "release"; //$NON-NLS-1$
	public static int DEFAULT_RELEASE_COUNT = 5;

	public static final String DEFAULT_ENCODING = "Cp1252"; //$NON-NLS-1$
	public static final String[] ENCODING_VALUE = { "ISO8859_1", "ASCII", "UTF16", "UTF16BE", "UTF16LE", "UTF8" }; //$NON-NLS-1$
	public static final String[] ENCODING_NAME = { "ISO-8859-1", "US-ASCII", "UTF-16", "UTF-16BE", "UTF-16LE",
			"UTF-8" }; //$NON-NLS-1$
}