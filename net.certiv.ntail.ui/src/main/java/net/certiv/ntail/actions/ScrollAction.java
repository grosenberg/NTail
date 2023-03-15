package net.certiv.ntail.actions;

import net.certiv.ntail.NTailView;
import net.certiv.ntail.utils.ImageUtils;
import net.certiv.ntail.viewers.ViewerSetEntry;

import org.eclipse.jface.action.Action;

/**
 * Toggles automatic scrolling.
 */
public class ScrollAction extends Action {
	private NTailView view = null;

	public ScrollAction(NTailView view) {
		this.view = view;
		setText("Scroll Lock");
		setToolTipText("Scroll Lock");
		setImageDescriptor(ImageUtils.createImageDescriptor("icons/toggle_scroll.png"));
	}

	public void run() {
		ViewerSetEntry entry = view.getSelectedEntry();
		if (entry != null) {
			entry.setScroll(!isChecked());
		}
	}
}