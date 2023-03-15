package net.certiv.ntail.actions;

import net.certiv.ntail.NTailPlugin;
import net.certiv.ntail.NTailView;
import net.certiv.ntail.utils.ImageUtils;

import org.eclipse.jface.action.Action;

/**
 * Closes the currently selected Viewer.
 */
public class CloseAction extends Action {
	private NTailView view = null;

	public CloseAction(NTailView view) {
		this.view = view;

		setText("Close Viewer");
		setToolTipText("Close this viewer");
		setImageDescriptor(ImageUtils.createImageDescriptor("icons/close_view.png"));
	}

	public void run() {
		view.closeSelectedViewer();
		NTailPlugin.getDefault().getViewerSet().saveViewers();
	}
}
