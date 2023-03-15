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
public class TruncateAllAction extends Action {
	private NTailView view = null;

	public TruncateAllAction(NTailView view) {
		this.view = view;
		setText("TruncateAll");
		setToolTipText("Truncate all logs");
		setImageDescriptor(ImageUtils.createImageDescriptor("icons/trunc_all.png"));
	}

	@Override
	public void run() {
		for (CTabItem item : view.getFolder().getItems()) {
			ViewerSetEntry entry = view.findEntry(item);
			if (entry != null) {
				entry.getViewerTail().truncate();
				entry.getTextViewer().setDocument(new Document());
			}
		}
	}
}
