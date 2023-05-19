package net.certiv.ntail.util;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;

import net.certiv.ntail.NTailPlugin;

/**
 * Utilities for dealing with images, icons, etc.
 */
public class ImageUtils {

	/**
	 * Create an image descriptor for the given filename (relative to the plugin install directory)
	 */
	public static ImageDescriptor createImageDescriptor(String filename) {
		Bundle bundle = NTailPlugin.getDefault().getBundle();
		URL entry = bundle.getEntry(filename);
		URL fileUrl = null;
		try {
			fileUrl = FileLocator.toFileURL(entry);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ImageDescriptor.createFromURL(fileUrl);
	}

	public static String getPluginFile(String filename) {
		Bundle bundle = NTailPlugin.getDefault().getBundle();
		URL entry = bundle.getEntry(filename);
		URL fileUrl = null;
		try {
			fileUrl = FileLocator.toFileURL(entry);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl.getFile();
	}
}
