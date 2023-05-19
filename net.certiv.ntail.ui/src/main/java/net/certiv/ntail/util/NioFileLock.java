package net.certiv.ntail.util;

import java.io.*;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

/**
 * Internal class.
 */
public class NioFileLock {
	private File file;
	private RandomAccessFile rwFile;
	private FileLock fileLock;

	public NioFileLock(File file) {
		this.file = file;
	}

	public synchronized boolean lock() throws IOException {
		rwFile = new RandomAccessFile(file, "rws");
		try {
			fileLock = rwFile.getChannel().tryLock();
		} catch (OverlappingFileLockException e) {
			fileLock = null;
		}
		finally {
			if (fileLock != null) return true;
			rwFile.close();
			rwFile = null;
		}
		return false;
	}

	public synchronized boolean truncate() {
		if (rwFile == null) return false;
		try {
			rwFile.setLength(0);
			return (rwFile.length() == 0) ? true : false;
		} catch (IOException e) {}
		return false;
	}

	public synchronized void release() {
		if (fileLock != null) {
			try {
				fileLock.release();
			} catch (IOException e) {}
			fileLock = null;
		}
		if (rwFile != null) {
			try {
				rwFile.close();
			} catch (IOException e) {}
			rwFile = null;
		}
	}
}