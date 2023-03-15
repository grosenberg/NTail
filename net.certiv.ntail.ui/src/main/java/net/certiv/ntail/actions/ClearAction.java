package net.certiv.ntail.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.Document;

import net.certiv.ntail.NTailView;
import net.certiv.ntail.utils.ImageUtils;
import net.certiv.ntail.viewers.ViewerSetEntry;

/**
 * Clears the text area dislaying the log file.
 */
public class ClearAction extends Action {
	private NTailView view = null;

	public ClearAction(NTailView view) {
		this.view = view;
		setText("Clear");
		setToolTipText("Clear log display");
		setImageDescriptor(ImageUtils.createImageDescriptor("icons/clear.png"));
	}

	@Override
	public void run() {
		ViewerSetEntry entry = view.getSelectedEntry();
		if (entry != null) {
			entry.getViewerTail().clear();
			entry.getTextViewer().setDocument(new Document());
		}
	}
}
