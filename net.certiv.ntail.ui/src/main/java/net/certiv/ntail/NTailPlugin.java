package net.certiv.ntail;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import net.certiv.ntail.dialogs.FindDialog.FindState;
import net.certiv.ntail.util.DocUtils;
import net.certiv.ntail.util.log.Level;
import net.certiv.ntail.util.log.Log;
import net.certiv.ntail.viewers.ViewerSet;

/**
 * The main plugin class for the NTail plugin.
 */
public class NTailPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = NTailPlugin.class.getName();

	/** The shared instance of the plugin */
	private static NTailPlugin plugin;
	private ResourceBundle resourceBundle;
	private ViewerSet viewerSet;

	private TreeItem item;
	private DocUtils docUtils = null;
	private FindState findState = null;

	private String openActionViewName = null;

	/**
	 * Construct a new NTail plugin.
	 */
	public NTailPlugin() {
		plugin = this;
	}

	/**
	 * Returns the shared instance.
	 */
	public static NTailPlugin getDefault() {
		return plugin;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		Log.init(plugin, Level.DEBUG);

		try {
			resourceBundle = ResourceBundle.getBundle("net.certiv.ntail.NTailPluginResources");
		} catch (MissingResourceException e) {
			resourceBundle = null;
		}

		// check to see if the JDT is installed & obtain reference
		try {
			docUtils = new DocUtils();
		} catch (NoClassDefFoundError e) {
			Log.debug("JDT not found.");
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public boolean isJDTInstalled() {
		return docUtils != null;
	}

	public DocUtils getDocUtils() {
		return docUtils;
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = NTailPlugin.getDefault().getResourceBundle();
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	/**
	 * Obtains the viewerSet data object.
	 *
	 * @return a reference to the viewerSet
	 */
	public ViewerSet getViewerSet() {
		if (viewerSet == null) {
			viewerSet = new ViewerSet();
		}
		return viewerSet;
	}

	public String getOpenActionViewName() {
		return openActionViewName;
	}

	public void setOpenActionViewName(String openActionViewName) {
		this.openActionViewName = openActionViewName;
	}

	public String putDndObject(Object object) {
		item = (TreeItem) object;
		String key = String.valueOf(item.hashCode());
		return key;
	}

	public Object getDndObject(String key) {
		int code = Integer.parseInt(key);
		if (item.hashCode() == code) return item;
		return null;
	}

	public FindState getFindState() {
		return findState;
	}

	public void setFindState(FindState state) {
		this.findState = state;
	}

	// // ////////////// Debug routines //////////////////////////
	// public void error(String msg) {
	// error(msg, null);
	// }
	//
	// public void error(String msg, Exception e) {
	// Status s = new Status(IStatus.ERROR, PLUGIN_ID, 1, msg, e);
	// getDefault().getLog().log(s);
	// }
	//
	// public void debug(String msg) {
	// debug(msg, null);
	// }
	//
	// public void debug(String msg, Exception e) {
	// Status s = new Status(IStatus.WARNING, PLUGIN_ID, 2, msg, e);
	// getDefault().getLog().log(s);
	// }
}
