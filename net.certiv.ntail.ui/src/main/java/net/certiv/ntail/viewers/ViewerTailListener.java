package net.certiv.ntail.viewers;

import net.certiv.ntail.utils.BufferList;

/**
 * Listens to a ViewerTail for an update to the file being watched.
 */
public interface ViewerTailListener {

	/**
	 * Notification that an update has occurred in the file being watched.
	 * 
	 * @param list The most recent lines that have been updated in the file.
	 */
	public void update(BufferList list);
}
