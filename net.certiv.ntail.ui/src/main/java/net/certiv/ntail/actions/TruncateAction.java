package net.certiv.ntail.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.Document;

import net.certiv.ntail.NTailView;
import net.certiv.ntail.utils.ImageUtils;
import net.certiv.ntail.viewers.ViewerSetEntry;

/**
 * Clears the text area dislaying the log file.
 */
public class TruncateAction extends Action {
	private NTailView view = null;

	public TruncateAction(NTailView view) {
		this.view = view;
		setText("Truncate");
		setToolTipText("Truncate log");
		setImageDescriptor(ImageUtils.createImageDescriptor("icons/trunc_log.png"));
	}

	@Override
	public void run() {
		ViewerSetEntry entry = view.getSelectedEntry();
		if (entry != null) {
			entry.getViewerTail().truncate();
			entry.getTextViewer().setDocument(new Document());
		}
	}
}
