package net.certiv.ntail.actions;

import java.util.ArrayList;

import net.certiv.ntail.NTailPlugin;
import net.certiv.ntail.NTailView;
import net.certiv.ntail.preferences.Key;
import net.certiv.ntail.util.ImageUtils;
import net.certiv.ntail.viewers.ViewerSet;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * Creates a new view.
 */
public class OpenAction extends Action {

	@SuppressWarnings("unused")
	private NTailView view = null;
	private String name = null;

	public OpenAction(NTailView view, String name) {
		this.view = view;
		this.name = name;
		if (name.equals(Key.NEW_VIEW)) {
			setText("Open New View");
		} else {
			setText("Open: " + name);
		}
		setToolTipText("Open a named or new View instance");
		setImageDescriptor(ImageUtils.createImageDescriptor("icons/open_view.png"));
	}

	public void run() {
		NTailPlugin.getDefault().setOpenActionViewName(name);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		String id2 = determineSecondaryID();
		try {
			page.showView(Key.ID1, id2, IWorkbenchPage.VIEW_ACTIVATE);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	private String determineSecondaryID() {
		ViewerSet viewerSet = NTailPlugin.getDefault().getViewerSet();
		ArrayList<String> names = viewerSet.getUniqueViewNames();
		for (int idx = 0; idx < names.size(); idx++) {
			if (this.name.equals(names.get(idx))) {
				return String.valueOf(idx);
			}
		}
		return String.valueOf(names.size());
	}

	// // count active views - secondary ID will be next sequential
	// private String getNewSecondaryID() {
	// int id = 0;
	// IViewReference[] refs = view.getViewSite().getPage().getViewReferences();
	// for (IViewReference vref : refs) {
	// String partName = vref.getPartName();
	// if (partName.startsWith(NTailPlugin.PLUGIN_ID + ":")) {
	// String secID = vref.getSecondaryId() == null ? "0" : vref.getSecondaryId();
	// int secId = Integer.parseInt(secID);
	// id = Math.max(secId + 1, id + 1);
	// }
	//
	// }
	// return String.valueOf(id);
	// }

}