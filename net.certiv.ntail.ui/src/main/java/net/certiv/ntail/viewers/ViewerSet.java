package net.certiv.ntail.viewers;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import net.certiv.ntail.Key;
import net.certiv.ntail.NTailPlugin;
import org.eclipse.core.commands.common.EventManager;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.util.SafeRunnable;

/**
 * The ViewerSet is the central data structure for Viewer objects. Each Viewer is
 * associated with a named view.
 * 
 * @author Gerald B. Rosenberg
 */
public class ViewerSet extends EventManager {

	private boolean debug = false;

	private static File viewersFile;
	private static ArrayList<Viewer> viewerList;

	/**
	 * A singleton object held by NTailPlugin that represents the set of Viewer objects.
	 */
	public ViewerSet() {
		super();
		loadViewers();
	}

	public synchronized ArrayList<Viewer> getViewerList() {
		if (viewerList == null) {
			viewerList = new ArrayList<Viewer>();
		}
		return viewerList;
	}

	public synchronized String getViewName(int idx) {
		ArrayList<String> names = getUniqueViewNames();
		if (idx < 0 || idx >= names.size()) {
			return null; // String.valueOf(idx);
		}
		return names.get(idx);
	}

	@SuppressWarnings("unused")
	private synchronized Viewer getViewer(String viewName) {
		for (Viewer w : getViewerList()) {
			if (w.getViewName().equals(viewName)) {
				return w;
			}
		}
		return null;
	}

	public synchronized ArrayList<Viewer> getViewers(String viewName) {
		ArrayList<Viewer> viewers = new ArrayList<Viewer>();
		for (Viewer w : getViewerList()) {
			if (w.getViewName().equals(viewName)) {
				viewers.add(w);
			}
		}
		return viewers;
	}

	public synchronized void addViewer(Viewer w) {
		getViewerList().add(w);
	}

	public synchronized void removeViewer(Viewer w) {
		getViewerList().remove(w);
	}

	public synchronized int size() {
		return getViewerList().size();
	}

	public ArrayList<String> getUniqueViewNames() {
		ArrayList<String> aList = new ArrayList<String>();
		for (Viewer w : getViewerList()) {
			if (!aList.contains(w.getViewName())) {
				aList.add(w.getViewName());
			}
		}
		return aList;
	}

	public ArrayList<String> getUniqueLogFileNames() {
		ArrayList<String> aList = new ArrayList<String>();
		for (Viewer w : getViewerList()) {
			if (!aList.contains(w.getFileName())) {
				aList.add(w.getFileName());
			}
		}
		return aList;
	}

	public ArrayList<String> getUniqueLines() {
		ArrayList<String> aList = new ArrayList<String>();
		for (Viewer w : getViewerList()) {
			if (!aList.contains(w.getLinesPattern())) {
				aList.add(w.getLinesPattern());
			}
		}
		return aList;
	}

	public ArrayList<String> getUniqueReplace() {
		ArrayList<String> aList = new ArrayList<String>();
		for (Viewer w : getViewerList()) {
			if (!aList.contains(w.getReplacePattern())) {
				aList.add(w.getReplacePattern());
			}
		}
		return aList;
	}

	public ArrayList<String> getUniqueRepWith() {
		ArrayList<String> aList = new ArrayList<String>();
		for (Viewer w : getViewerList()) {
			if (!aList.contains(w.getRepWithText())) {
				aList.add(w.getRepWithText());
			}
		}
		return aList;
	}

	public ArrayList<String> getUniqueHighlights() {
		ArrayList<String> aList = new ArrayList<String>();
		for (Viewer w : getViewerList()) {
			if (!aList.contains(w.getHighlightPattern())) {
				aList.add(w.getHighlightPattern());
			}
		}
		return aList;
	}

	/**
	 * Loads the list of viewer objects. The in-core data structure is shared with all
	 * view instances.
	 */
	public synchronized void loadViewers() {
		if (debug) NTailPlugin.getDefault().debug("loadViewers()");
		viewersFile = getViewersFile();
		loadViewers(viewersFile);
	}

	@SuppressWarnings("unchecked")
	public synchronized void loadViewers(File vFile) {
		if (debug) NTailPlugin.getDefault().debug("loadViewers(vFile)");
		XMLDecoder coder = null;
		if (vFile.exists()) {
			try {
				coder = new XMLDecoder(new BufferedInputStream(new FileInputStream(vFile)));
			} catch (FileNotFoundException e) {
				NTailPlugin.getDefault().error("Error loading viewerList", e);
			}
		}
		if (coder != null) {
			try {
				viewerList = (ArrayList<Viewer>) coder.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			}
			coder.close();
			if (viewerList == null) {
				viewerList = new ArrayList<Viewer>();
				NTailPlugin.getDefault().error("Failed to read viewerlist; loading default");
			} else if (viewerList.size() > 0 && viewerList.get(0) == null) {
				viewerList = new ArrayList<Viewer>();
				NTailPlugin.getDefault().error("Corrupted viewerlist; loading default");
			}
			if (debug) NTailPlugin.getDefault().debug("Loaded viewers [size=" + viewerList.size() + "]");
		} else { // initialize the viewer list data structure
			viewerList = new ArrayList<Viewer>();
			if (debug) NTailPlugin.getDefault().debug("Loaded default viewerlist");
		}
		if (debug) dumpViewerList();
	}

	public synchronized void orderViewers(ArrayList<Viewer> treeOrderedViewers) {
		viewerList = treeOrderedViewers;
	}

	public synchronized void saveViewers() {
		viewersFile = getViewersFile();
		saveViewers(viewersFile);
	}

	public synchronized void saveViewers(File vFile) {
		XMLEncoder coder = null;
		try {
			coder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(vFile)));
		} catch (FileNotFoundException e) {
			NTailPlugin.getDefault().error("Failed to create encoder ...", e);
		}
		if (debug) {
			NTailPlugin.getDefault().debug("Saving viewerlist [size=" + viewerList.size() + "]");
			dumpViewerList();
		}
		coder.writeObject(viewerList);
		coder.close();
	}

	private File getViewersFile() {
		if (viewersFile == null) {
			IPath path = NTailPlugin.getDefault().getStateLocation();
			path = path.addTrailingSeparator();
			path = path.append(Key.VIEWERS_FILENAME);
			viewersFile = path.toFile();
		}
		if (debug) NTailPlugin.getDefault().debug("Using [file=" + viewersFile + "]");
		return viewersFile;
	}

	private void dumpViewerList() {
		NTailPlugin.getDefault().debug("Dump ");
		for (Viewer w : getViewerList()) {
			NTailPlugin.getDefault().debug("  " + w.toString());
		}
	}

	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		addListenerObject(listener);
	}

	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		removeListenerObject(listener);
	}

	public void firePropertyChangeEvent() {
		firePropertyChangeEvent(Key.VIEW_NAME, null, null);
	}

	public void firePropertyChangeEvent(String name, Object oldValue, Object newValue) {
		Object[] listeners = getListeners();
		if (listeners.length > 0 && (oldValue == null || !oldValue.equals(newValue))) {
			final PropertyChangeEvent pe = new PropertyChangeEvent(this, name, oldValue, newValue);
			for (int idx = 0; idx < listeners.length; ++idx) {
				final IPropertyChangeListener listener = (IPropertyChangeListener) listeners[idx];

				SafeRunnable.run(new SafeRunnable("ViewerSet change error") { //$NON-NLS-1$

						public void run() {
							listener.propertyChange(pe);
						}

						public void handleException(Throwable e) {
							e.printStackTrace();
							super.handleException(e);
						}
					});
			}
		}
	}

	// // /////////////////////////////////////////////////////////////////////////
	//
	// private static HashMap<String, String> viewerMap;
	// viewerMap = new HashMap<String, String>();
	//
	// public synchronized String lookupViewerSecondaryID(String name) {
	// return viewerMap.get(name);
	// }
	//
	// public synchronized String lookupViewerName(String secID) {
	// for (String name : viewerMap.keySet()) {
	// if (viewerMap.get(name).equals(secID)) return name;
	// }
	// return null;
	// }
	//
	// /**
	// * Creates a name/secID pairing, overriding any existing association.
	// *
	// * @param name the name of a Viewer
	// * @param secID the secondary ID associated with the Viewer
	// */
	// public synchronized void createSecondaryIdAssociation(String name, String secID) {
	// NTailPlugin.getDefault().debug("Create association: " + name + "->" + secID);
	// viewerMap.put(name, secID);
	// }
	//
	// public synchronized void removeSecondaryIdAssociation(String name, String secID) {
	// NTailPlugin.getDefault().debug("Remove association: " + name + "->" + secID);
	// viewerMap.remove(name);
	// }
	//
	// // /////////////////////////////////////////////////////////////////////////
}