package net.certiv.ntail.actions;

import net.certiv.ntail.NTailView;
import net.certiv.ntail.dialogs.FindDialog;
import net.certiv.ntail.utils.ImageUtils;
import net.certiv.ntail.viewers.ViewerSetEntry;

import org.eclipse.jface.action.Action;

/**
 * Displays a standard find dialog
 */
public class FindAction extends Action {
	private NTailView view = null;

	public FindAction(NTailView view) {
		this.view = view;
		setText("Find...");
		setToolTipText("Find in tail file");
		setImageDescriptor(ImageUtils.createImageDescriptor("icons/search.png"));
	}

	public void run() {
		ViewerSetEntry entry = view.getSelectedEntry();
		if (entry != null) {
			FindDialog d = new FindDialog(view.getFolder().getShell(), entry.getTextViewer()
				.getFindReplaceTarget());
			d.open();
		}
	}
}
