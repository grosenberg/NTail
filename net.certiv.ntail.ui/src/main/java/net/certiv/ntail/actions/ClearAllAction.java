package net.certiv.ntail.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.Document;
import org.eclipse.swt.custom.CTabItem;

import net.certiv.ntail.NTailView;
import net.certiv.ntail.utils.ImageUtils;
import net.certiv.ntail.viewers.ViewerSetEntry;

/**
 * Clears all of the log files displayed in a viewer set.
 */
public class ClearAllAction extends Action {
	private NTailView view = null;

	public ClearAllAction(NTailView view) {
		this.view = view;
		setText("ClearAll");
		setToolTipText("Clear all log displays");
		setImageDescriptor(ImageUtils.createImageDescriptor("icons/clear_all.png"));
	}

	@Override
	public void run() {
		for (CTabItem item : view.getFolder().getItems()) {
			ViewerSetEntry entry = view.findEntry(item);
			if (entry != null) {
				entry.getViewerTail().clear();
				entry.getTextViewer().setDocument(new Document());
			}
		}
	}
}
