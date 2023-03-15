package net.certiv.ntail.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.window.Window;

import net.certiv.ntail.NTailView;
import net.certiv.ntail.dialogs.ViewerDialog;
import net.certiv.ntail.utils.ImageUtils;
import net.certiv.ntail.viewers.ViewerSetEntry;

/**
 * Edits the currently active Viewer.
 */
public class EditAction extends Action {
	private NTailView view = null;

	public EditAction(NTailView view) {
		this.view = view;
		setText("Edit Viewer");
		setToolTipText("Edit this viewer");
		setImageDescriptor(ImageUtils.createImageDescriptor("icons/edit.png"));
	}

	@Override
	public void run() {
		ViewerSetEntry entry = view.getSelectedEntry();
		if (entry != null) {
			int topIndex = entry.getTextViewer().getTopIndex();
			int caret = entry.getTextViewer().getTextWidget().getCaretOffset();
			ViewerDialog d = new ViewerDialog(view.getFolder().getShell());
			if (d.open(entry.getViewer(), true) == Window.OK) {
				entry.getTextViewer().setDocument(new Document());
				view.editViewer(entry, d.getViewer()); // performs a reload
				entry.getTextViewer().setTopIndex(topIndex);
				entry.getTextViewer().getTextWidget().setCaretOffset(caret);
			}
		}
	}
}
