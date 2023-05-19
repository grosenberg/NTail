package net.certiv.ntail.actions;

import net.certiv.ntail.NTailPlugin;
import net.certiv.ntail.NTailView;
import net.certiv.ntail.dialogs.ViewerDialog;
import net.certiv.ntail.util.ImageUtils;
import net.certiv.ntail.viewers.Viewer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;

/**
 * Creates a new viewer in the view.
 */
public class NewAction extends Action {
	private NTailView view = null;

	public NewAction(NTailView view) {
		this.view = view;

		setText("New Viewer");
		setToolTipText("Create a new viewer");
		setImageDescriptor(ImageUtils.createImageDescriptor("icons/new.png"));
	}

	public void run() {
		ViewerDialog dialog = new ViewerDialog(view.getFolder().getShell());
		Viewer viewer = new Viewer();
		viewer.init();
		if (dialog.open(viewer, false) == Window.OK) {
			// NTailPlugin.getDefault().debug("Adding new viewer");
			NTailPlugin.getDefault().getViewerSet().addViewer(dialog.getViewer());
			NTailPlugin.getDefault().getViewerSet().saveViewers();
			view.adjustTitles(dialog.getViewer().getViewName(), dialog.getViewer().getFileName());
			NTailPlugin.getDefault().getViewerSet().firePropertyChangeEvent();
		}
	}
}