/**
 * 
 */
package net.certiv.ntail.utils;

import net.certiv.ntail.NTailPlugin;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * @author Gerald B. Rosenberg
 */
public class TreeItemTransfer extends ByteArrayTransfer {

	private static final String ITEM_TRANSFER = "itemTransfer";
	private static final int ITEM_ID = registerType(ITEM_TRANSFER);
	private static TreeItemTransfer _instance = new TreeItemTransfer();

	private TreeItemTransfer() {
		super();
	}

	public static TreeItemTransfer getInstance() {
		return _instance;
	}

	@Override
	protected void javaToNative(Object object, TransferData transferData) {
		String key = NTailPlugin.getDefault().putDndObject(object);
		byte[] ref = key.getBytes();
		super.javaToNative(ref, transferData);
	}

	@Override
	protected Object nativeToJava(TransferData transferData) {
		byte[] ref = (byte[]) super.nativeToJava(transferData);
		String key = new String (ref);
		return NTailPlugin.getDefault().getDndObject(key);
	}

	@Override
	protected boolean validate(Object object) {
		return true;
	}

	@Override
	protected int[] getTypeIds() {
		return new int[] {ITEM_ID};
	}

	@Override
	protected String[] getTypeNames() {
		return new String[] {ITEM_TRANSFER};
	}
}