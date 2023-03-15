package net.certiv.ntail.preferences;

import net.certiv.ntail.NTailPlugin;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * @author Gerald B. Rosenberg
 */
public class Prefs {

	private Prefs() {}

	public static boolean getBooleanPreference(String key) {
		return NTailPlugin.getDefault().getPreferenceStore().getBoolean(key);
	}

	public static int getIntPreference(String key) {
		return NTailPlugin.getDefault().getPreferenceStore().getInt(key);
	}

	public static String getStringPreference(String key) {
		IPreferenceStore store = NTailPlugin.getDefault().getPreferenceStore();
		return store.getString(key);
	}

	public static boolean isStringPreference(String key, String value) {
		return NTailPlugin.getDefault().getPreferenceStore().getString(key).equals(value);
	}

	public static Font getFontPreference(String key) {
		IPreferenceStore store = NTailPlugin.getDefault().getPreferenceStore();
		FontData[] fda = PreferenceConverter.getFontDataArray(store, key);
		return new Font(Display.getCurrent(), fda[0]);
	}

	public static Color getColorPreference(String key) {
		IPreferenceStore store = NTailPlugin.getDefault().getPreferenceStore();
		RGB rgb = PreferenceConverter.getColor(store, key);
		return new Color(Display.getCurrent(), rgb);
	}
}