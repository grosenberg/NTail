package net.certiv.ntail.preferences;

import net.certiv.ntail.Key;
import net.certiv.ntail.NTailPlugin;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.RGB;

/**
 * Class used to initialize default preference values.
 */
public class PrefsInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = NTailPlugin.getDefault().getPreferenceStore();

		store.setDefault(Key.SAVE_VIEWERS, true);
		store.setDefault(Key.VIEW_TITLE_NAME, false);
		store.setDefault(Key.SHOW_INNER_TITLE, true);
		store.setDefault(Key.TRUNC_ON_START, false);
		store.setDefault(Key.ICON_SET, Key.MINIMAL);
		PreferenceConverter.setDefault(store, Key.HIGHLIGHT, new RGB(230, 235, 255));
		PreferenceConverter.setDefault(store, Key.LINK_COLOR, new RGB(0, 0, 255));
		PreferenceConverter.setDefault(store, Key.VIEW_FONT, JFaceResources.getTextFont().getFontData());
	}
}