package net.certiv.ntail.viewers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import net.certiv.ntail.Key;
import net.certiv.ntail.NTailPlugin;
import net.certiv.ntail.NTailView;
import net.certiv.ntail.utils.BufferList;
import net.certiv.ntail.utils.NioFileLock;

/**
 * Watches a text file for any changes, and keeps a list of the most recent lines to have
 * been added to the file. Notifies ViewerListeners when a change to the file being tailed
 * is detected.
 */
public class ViewerTail extends Thread {

	private Viewer viewer;
	// private File file = null;
	private BufferedReader lineBuffer = null;
	private boolean runTail = true;
	private BufferList lineList = null;
	private Pattern linePattern;
	private Pattern replacePattern;
	private boolean truncateLog;
	private boolean reload;

	public ViewerTail(Viewer viewer) throws FileNotFoundException, IOException {
		this.viewer = viewer;
		init();
	}

	private void init() throws FileNotFoundException, IOException {
		if (viewer.getFullFile()) {
			lineList = new BufferList(Integer.MAX_VALUE);
		} else {
			lineList = new BufferList(viewer.getNumLines());
		}
		if (viewer.isFilterLines()) {
			linePattern = Pattern.compile(viewer.getLinesPattern());
		}
		if (viewer.isReplace()) {
			replacePattern = Pattern.compile(viewer.getReplacePattern());
		}
		if (viewer.isTruncOnStart()) {
			truncateLog = true;
		}
	}

	/**
	 * Runs the thread that watches for changes to the file.
	 */
	public void run() {
		boolean updated = false;
		long lastSize = 0;
		int releaseCounter = 0;
		int idleTime = -1;

		File file = viewer.getFile();
		openLogBuffer();

		try {
			while (runTail) {
				if (truncateLog) {
					forceLogTruncation();
					truncateLog = file.length() > 0 ? true : false;
				} else if (reload) {
					init();
					closeLogBuffer();
					lineList.add("Reloaded ...");
					lastSize = 0;
					reload = false;
				}

				if (!file.exists()) {
					closeLogBuffer();
					lineList.add("Deleted ...");
					idleTime = -1;
					updated = true;
					runTail = false;
				} else if (file.length() < lastSize) {
					closeLogBuffer();
					openLogBuffer();
					lineList.add("Truncated ...");
					lastSize = file.length();
					idleTime = -1;
					releaseCounter = 0;
					updated = true;
				} else if (file.length() > lastSize) {
					handleAddition(lastSize);
					lastSize = file.length();
					releaseCounter = 0;
					updated = true;
				} else {
					if (lineBuffer != null && viewer.isRelease()) {
						if (releaseCounter > viewer.getReleaseCount()) {
							closeLogBuffer();
						} else {
							releaseCounter++;
						}
					}
					updated = false;
				}

				if (updated) {
					if (viewer.isDividerAppend()) idleTime = viewer.getDividerTimeout() * 1000;
					notifyListeners();
				} else if (idleTime == 0) {
					lineList.add(NTailView.IDLE);
					notifyListeners();
				}
				sleep(viewer.getInterval());
				if (idleTime >= 0) idleTime -= viewer.getInterval(); // hold on negative
				lineList.clear();
			}
		} catch (InterruptedException e) {
			// NTailPlugin.getDefault().error("Main loop interrupted", e);
		} catch (FileNotFoundException e) {
			NTailPlugin.getDefault().error("Log file went missing in main loop", e);
		} catch (IOException e) {
			NTailPlugin.getDefault().error("Read error in main loop", e);
		}
		closeLogBuffer();
	}

	private void handleAddition(long lastSize) {
		String line = null;

		try {
			if (lineBuffer == null) {
				openLogBuffer();
				lineBuffer.skip(lastSize);
			}

			while ((line = lineBuffer.readLine()) != null) {
				if (line.length() > 0) { // filter using the lines regex
					if (viewer.isFilters() && viewer.isFilterLines()) {
						if (!linePattern.matcher(line).matches()) {
							lineList.add(line);
						}
					} else {
						lineList.add(line);
					}
				} else {
					lineList.add(line);
				}
			}
			if (lineList.size() > 0 && viewer.isFilters() && viewer.isReplace()) {
				// modify using the replace regex
				for (int idx = 0; idx < lineList.size(); idx++) {
					String repline = replacePattern.matcher((String) lineList.get(idx)).replaceAll(
						viewer.getRepWithText());
					lineList.set(idx, repline);
				}
			}
		} catch (IOException e) {
			NTailPlugin.getDefault().error("Error reading from log file", e);
		}
	}

	private void openLogBuffer() {
		if (lineBuffer == null) {
			try {
				if (viewer.getEncoding() != null) {
					lineBuffer = new BufferedReader(new InputStreamReader(
							new FileInputStream(viewer.getFileName()), encValidate(viewer.getEncoding())));
				} else {
					lineBuffer = new BufferedReader(new FileReader(viewer.getFile()));
				}
			} catch (FileNotFoundException e) {
				NTailPlugin.getDefault().error("Error opening log file reader", e);
			} catch (UnsupportedEncodingException e) {
				NTailPlugin.getDefault().error("Error unknown log file encoding preference", e);
			}
		}
	}

	private String encValidate(String encoding) {
		for (int idx = 0; idx < Key.ENCODING_NAME.length; idx++) {
			String s = Key.ENCODING_NAME[idx];
			if (s.equals(encoding)) return Key.ENCODING_VALUE[idx];
		}
		NTailPlugin.getDefault().error("Unmatched log file encoding preference");
		return "CP1252";
	}

	private void closeLogBuffer() {
		if (lineBuffer != null) {
			try {
				lineBuffer.close();
			} catch (IOException e) {
				NTailPlugin.getDefault().error("Error closing log file reader", e);
			}
		}
		lineBuffer = null;
	}

	/**
	 * @return true on success
	 */
	private void forceLogTruncation() {
		File file = viewer.getFile();
		if (file.length() > 0) {
			NioFileLock locker = new NioFileLock(file);
			boolean locked = false;
			try {
				locked = locker.lock();
			} catch (IOException e) {
				NTailPlugin.getDefault().error("Error locking log file for truncation", e);
			}
			if (locked) {
				locker.truncate();
				locker.release();
			}
		}
	}

	/**
	 * Halt the execution of the Viewer.
	 */
	public void halt() {
		runTail = false;
		interrupt();
	}

	public void clear() {
		lineList.clear();
		notifyListeners();
	}

	public void truncate() {
		truncateLog = true;
	}

	public void reload() {
		reload = true;
	}

	// //// Event Controls /////////////////////////////
	private ArrayList<ViewerTailListener> listeners = new ArrayList<ViewerTailListener>();

	public void addListener(ViewerTailListener listener) {
		listeners.add(listener);
	}

	private synchronized void notifyListeners() {
		for (ViewerTailListener listener : listeners) {
			listener.update(lineList);
		}
	}
}