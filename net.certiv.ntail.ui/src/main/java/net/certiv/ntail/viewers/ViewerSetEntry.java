package net.certiv.ntail.viewers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.certiv.ntail.Key;
import net.certiv.ntail.NTailPlugin;
import net.certiv.ntail.preferences.Prefs;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;

/**
 * Management object used to hold a view configured Viewer object for the view instance.
 * Also, provides utility services to deconstruct the Viewer object from the view
 * instance.
 */
public class ViewerSetEntry {

	private TextViewer textViewer = null;
	private ViewerTail viewerTail = null;
	private CTabItem tab = null;
	private Viewer viewer;
	private Color linkColor;
	private Color highlight;
	private Pattern highlightPattern;
	private static final Pattern linkDetect = Pattern.compile("(?:\\()(\\w+\\.\\w+)(?::\\d+\\))");
	private static final Pattern groupDetect = Pattern.compile("(?:\\\\\\()|(?:\\(\\?)|(\\()");

	public ViewerSetEntry(TextViewer textViewer, ViewerTail viewerTail, CTabItem tab, Viewer viewer) {
		setTextViewer(textViewer);
		setViewerTail(viewerTail);
		setTab(tab);
		setViewer(viewer);
		initViewerSetEntry();
	}

	public void initViewerSetEntry() {
		String tmpPattern = getViewer().getHighlightPattern();
		if (tmpPattern != null && tmpPattern.length() > 0) {
			Matcher m = groupDetect.matcher(tmpPattern);
			if (m.find() && m.groupCount() > 0) {
				highlightPattern = Pattern.compile(tmpPattern);
			} else {
				highlightPattern = Pattern.compile("(" + tmpPattern + ")");
			}
		}
		linkColor = Prefs.getColorPreference(Key.LINK_COLOR);
		highlight = Prefs.getColorPreference(Key.HIGHLIGHT);
	}

	public void restart() {
		try {
			initViewerSetEntry();
			getViewerTail().reload();
		} catch (Throwable t) {
			NTailPlugin.getDefault().error("Error restarting the entry");
		}
	}

	public void dispose() {
		try {
			getViewerTail().halt();
			getTab().dispose();
		} catch (Throwable t) {
			NTailPlugin.getDefault().error("Error disposing of the entry");
		}
	}

	public Viewer getViewer() {
		return viewer;
	}

	public void setViewer(Viewer viewer) {
		this.viewer = viewer;
	}

	public TextViewer getTextViewer() {
		return textViewer;
	}

	public void setTextViewer(TextViewer textViewer) {
		this.textViewer = textViewer;
	}

	public ViewerTail getViewerTail() {
		return viewerTail;
	}

	public void setViewerTail(ViewerTail viewerTail) {
		this.viewerTail = viewerTail;
	}

	public CTabItem getTab() {
		return tab;
	}

	public void setTab(CTabItem tab) {
		this.tab = tab;
	}

	public boolean isScroll() {
		return getViewer().isScroll();
	}

	public void setScroll(boolean scroll) {
		getViewer().setScroll(scroll);
	}

	// TODO: install hyperlink with listener
	public void addHyperlink(ExtendedModifyEvent event) {
		if (event.length != 0) {
			StyledText st = (StyledText) event.getSource();
			int begLineIdx = st.getLineAtOffset(event.start);
			int endLineIdx = st.getLineAtOffset(event.start + event.length);
			for (int idx = begLineIdx; idx < endLineIdx; idx++) {
				int begLineOffset = st.getOffsetAtLine(idx);
				int endLineOffset;
				if (idx < endLineIdx) {
					endLineOffset = st.getOffsetAtLine(idx + 1) - st.getLineDelimiter().length();
				} else {
					endLineOffset = event.start + event.length;
				}
				if (begLineOffset >= endLineOffset) continue; // no text
				try {
					Matcher m = linkDetect.matcher(st.getText(begLineOffset, endLineOffset));
					if (m.find()) {
						int beg = begLineOffset + m.start(1);
						int len = m.end(1) - m.start(1);
						StyleRange style = new StyleRange(beg, len, getLinkColor(), null);
						style.underline = true;
						st.setStyleRange(style);
					}
				} catch (IllegalArgumentException ex) {
					NTailPlugin.getDefault().error(
						"Line match failed [line=" + idx + ":" + (endLineIdx - begLineIdx) + ", beg="
								+ begLineOffset + ", end=" + endLineOffset + "]", ex);
				}
			}
		}
	}

	public void addHighlight(ExtendedModifyEvent event) {
		if (highlightPattern != null && event.length != 0) {
			StyledText st = (StyledText) event.getSource();
			int begLineIdx = st.getLineAtOffset(event.start);
			int endLineIdx = st.getLineAtOffset(event.start + event.length);
			for (int idx = begLineIdx; idx <= endLineIdx; idx++) {
				int begLineOffset = st.getOffsetAtLine(idx);
				int endLineOffset;
				if (idx < endLineIdx) {
					endLineOffset = st.getOffsetAtLine(idx + 1) - st.getLineDelimiter().length();
				} else {
					endLineOffset = event.start + event.length;
				}
				try {
					if (endLineOffset < st.getCharCount() && endLineOffset >= begLineOffset) {
						Matcher m = highlightPattern.matcher(st.getText(begLineOffset, endLineOffset));
						if (m.find()) {
							StyleRange[] ranges = new StyleRange[m.groupCount()];
							for (int grp = 1; grp <= m.groupCount(); grp++) {
								int beg = begLineOffset + m.start(grp);
								int len = m.end(grp) - m.start(grp);
								StyleRange style = new StyleRange(beg, len, getHighlight(), null);
								ranges[grp - 1] = style;
							}
							StyleRange[] newSet = arrayAppend(st.getStyleRanges(), ranges);
							st.setStyleRanges(newSet);
						}
					}
				} catch (IllegalArgumentException ex) {
					NTailPlugin.getDefault().error(
						"Line match failed [line=" + idx + ":" + (endLineIdx - begLineIdx) + ", beg="
								+ begLineOffset + ", end=" + endLineOffset + "]", ex);
				}
			}
		}
	}

	private Color getLinkColor() {
		return linkColor;
	}

	private Color getHighlight() {
		return highlight;
	}

	private StyleRange[] arrayAppend(StyleRange[] base, StyleRange[] join) {
		if (base == null && join == null) return null;
		if (base == null) return join;
		if (join == null) return base;

		StyleRange[] array = new StyleRange[base.length + join.length];
		System.arraycopy(base, 0, array, 0, base.length);
		System.arraycopy(join, 0, array, base.length, join.length);
		return array;
	}
}