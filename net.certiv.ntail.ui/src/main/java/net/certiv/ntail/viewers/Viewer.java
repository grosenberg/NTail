package net.certiv.ntail.viewers;

import java.io.File;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.util.ArrayList;

import net.certiv.ntail.Key;
import net.certiv.ntail.NTailPlugin;

/**
 * Defines an essentially naked data structure that represents a single Viewer instance. This class
 * is serialized to persist a Viewer definition.
 */
public class Viewer implements Serializable {

	private static final long serialVersionUID = -291484486907870342L;

	private static final String pathSeparator = FileSystems.getDefault().getSeparator();
	private File file;

	private String viewName = Key.DEFAULT_VIEW;
	private String fileName = "";

	private int numLines = Key.DEFAULT_NUMLINES;
	private boolean fullFile = false;
	private int interval = 1;
	private boolean dividerAppend = false;
	private int dividerTimeout = 4;
	private boolean truncate = false;

	private boolean release = false;
	private int releaseCount = Key.DEFAULT_RELEASE_COUNT;

	private String encoding = null;

	private int tabOrder = -1;
	private boolean scroll = true;

	private boolean filters = true;
	private boolean hyperlink = true;
	private boolean ansiCodes = false;
	private boolean filterLines = false;
	private boolean replace = false;
	private boolean highlight = false;
	private String linesPattern = "";
	private String replacePattern = "";
	private String repWithText = "";
	private String highlightPattern = "";

	public Viewer() {
		this("Default");
	}

	public Viewer(String viewName) {
		setViewName(viewName);
	}

	public Viewer(String viewName, String fileName, int numLines, boolean scroll) {
		setViewName(viewName);
		setFileName(fileName);
		setNumLines(numLines);
		setScroll(scroll);
	}

	public void init() {
		ViewerSet vset = NTailPlugin.getDefault().getViewerSet();
		if (vset.size() > 0) {
			ArrayList<String> names = vset.getUniqueViewNames();
			if (names.size() > 0) setViewName(names.get(0));
		}
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public String getFileName() {
		if (fileName == null) fileName = "";
		return fileName;
	}

	public String getShortFileName() {
		String fileName = getFileName();
		return fileName.substring(fileName.lastIndexOf(pathSeparator) + 1);
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
		file = null;
	}

	public boolean isScroll() {
		return scroll;
	}

	public void setScroll(boolean scroll) {
		this.scroll = scroll;
	}

	public int getTabOrder() {
		return tabOrder;
	}

	public void setTabOrder(int tabOrder) {
		this.tabOrder = tabOrder;
	}

	public int getInterval() {
		if (interval < 250) {
			interval = interval * 1000;
		}
		return interval;
	}

	public void setInterval(int interval) {
		if (interval < 250) {
			this.interval = interval * 1000;
		} else {
			this.interval = interval;
		}
	}

	public int getNumLines() {
		return numLines;
	}

	public void setNumLines(int numLines) {
		this.numLines = numLines;
	}

	public boolean isRelease() {
		return release;
	}

	public void setRelease(boolean release) {
		this.release = release;
	}

	public int getReleaseCount() {
		return releaseCount;
	}

	public void setReleaseCount(int releaseCount) {
		this.releaseCount = releaseCount;
	}

	public void setEncoding(String encoding) {
		if (encoding == null || encoding.matches(Key.DEFAULT_ENCODING)) {
			this.encoding = null;
		} else {
			this.encoding = encoding;
		}
	}

	public String getEncoding() {
		return encoding;
	}

	public File getFile() {
		if (getFileName().length() > 0) {
			if (file == null) {
				file = new File(getFileName());
			}
			return file;
		}
		return null;
	}

	/**
	 * @return tooltip
	 */
	public String getToolTipText() {
		if (getFile() != null) {
			return getFile().getAbsolutePath();
		}
		return "";
	}

	public void setFullFile(boolean fullFile) {
		this.fullFile = fullFile;
	}

	public boolean getFullFile() {
		return fullFile;
	}

	public boolean isDividerAppend() {
		return dividerAppend;
	}

	public void setDividerAppend(boolean dividerAppend) {
		this.dividerAppend = dividerAppend;
	}

	public int getDividerTimeout() {
		return dividerTimeout;
	}

	public void setDividerTimeout(int dividerTimeout) {
		this.dividerTimeout = dividerTimeout;
	}

	public boolean isTruncOnStart() {
		return truncate;
	}

	public void setTruncOnStart(boolean truncate) {
		this.truncate = truncate;
	}

	public boolean isFilters() {
		return filters;
	}

	public void setFilters(boolean filters) {
		this.filters = filters;
	}

	public boolean isHyperlink() {
		return hyperlink;
	}

	public void setHyperlink(boolean hyperlink) {
		this.hyperlink = hyperlink;
	}

	public boolean isAnsiCodes() {
		return ansiCodes;
	}

	public void setAnsiCodes(boolean ansiCodes) {
		this.ansiCodes = ansiCodes;
	}

	public boolean isFilterLines() {
		return filterLines;
	}

	public void setFilterLines(boolean filterLines) {
		this.filterLines = filterLines;
	}

	public String getLinesPattern() {
		return linesPattern;
	}

	public void setLinesPattern(String linesPattern) {
		this.linesPattern = linesPattern;
	}

	public boolean isReplace() {
		return replace;
	}

	public void setReplace(boolean replace) {
		this.replace = replace;
	}

	public String getReplacePattern() {
		return replacePattern;
	}

	public void setReplacePattern(String replacePattern) {
		this.replacePattern = replacePattern;
	}

	public String getRepWithText() {
		return repWithText;
	}

	public void setRepWithText(String repWithText) {
		this.repWithText = repWithText;
	}

	public boolean isHighlight() {
		return highlight;
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}

	public String getHighlightPattern() {
		return highlightPattern;
	}

	public void setHighlightPattern(String highlightPattern) {
		this.highlightPattern = highlightPattern;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("  [view=" + getViewName() + "]\n");
		sb.append("  [logfile=" + getFileName() + "]\n");
		sb.append("  [interval=" + getInterval() + "]\n");
		sb.append("  [numLines=" + getNumLines() + "]\n");
		sb.append("  [fullfile=" + getFullFile() + "]\n");
		return sb.toString();
	}
}
