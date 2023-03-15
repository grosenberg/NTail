package net.certiv.ntail.actions;

import net.certiv.ntail.NTailView;
import net.certiv.ntail.utils.ImageUtils;
import net.certiv.ntail.viewers.ViewerSetEntry;

import org.eclipse.jface.action.Action;

/**
 * Copies current selection to clipboard.
 */
public class CopyAction extends Action {
	private NTailView view = null;

	public CopyAction(NTailView view) {
		this.view = view;
		setText("Copy");
		setToolTipText("Copy selected text to the clipboard");
		setImageDescriptor(ImageUtils.createImageDescriptor("icons/copy_edit.png"));
	}

	public void run() {
		ViewerSetEntry entry = view.getSelectedEntry();
		if (entry != null) {
			entry.getTextViewer().getTextWidget().copy();
		}
	}
}
