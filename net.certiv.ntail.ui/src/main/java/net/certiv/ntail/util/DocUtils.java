package net.certiv.ntail.util;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

import net.certiv.ntail.util.log.Log;

public class DocUtils {

	private boolean debug = false;

	public DocUtils() {
		super();
	}

	public void revealFile(String fName, int lineNumber) {
		int endIdx = fName.lastIndexOf(".");
		String s1 = fName.substring(0, endIdx);
		String s2 = fName.substring(endIdx);
		String relName = s1.replaceAll("\\.", "/") + s2;

		try {
			ArrayList<IProject> projs = getOpenProjects();
			if (projs == null || projs.size() == 0) return;
			for (IProject proj : projs) {
				IJavaProject javaProject = JavaCore.create(proj);
				if (javaProject == null) continue;

				ArrayList<IResource> sourceFolders = getSourceFolders(javaProject);
				if (sourceFolders.size() == 0) continue;

				// build ordered list of source folder relative paths
				ArrayList<String> paths = new ArrayList<>();
				paths.add(relName);
				for (IResource folder : sourceFolders) {
					String pathPart = folder.getFullPath().removeFirstSegments(1).toPortableString();
					paths.add(0, pathPart + "/" + relName);
				}

				for (String path : paths) {
					if (debug) {
						String projPath = proj.getProject().getLocationURI().toString();
						Log.debug("Examine [path=" + projPath + "/" + path + "]");
					}
					IResource member = proj.getProject().findMember(path);
					if (member != null && member instanceof IFile) {
						IFile file = (IFile) member;
						if (file.exists()) {
							if (debug) {
								String filePath = file.getLocationURI().toString();
								Log.debug("Found [path=" + filePath + "]");
							}
							openFile(file, lineNumber);
							return;
						}
					}
				}
			}
			Log.debug("File not found [file=" + relName + "]");
		} catch (Exception e) {
			Log.error("General exception", e);
		}
	}

	private ArrayList<IProject> getOpenProjects() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projs = root.getProjects();
		ArrayList<IProject> res = new ArrayList<>();
		for (IProject proj : projs) { // skip closed and non-java projects
			if (!proj.isAccessible()) continue;
			res.add(proj);
		}
		return res;
	}

	private ArrayList<IResource> getSourceFolders(IJavaProject javaProject) {
		IClasspathEntry[] entries = null;
		ArrayList<IResource> sourceFolders = new ArrayList<>();
		try {
			entries = javaProject.getRawClasspath();
		} catch (JavaModelException e) {
			String name = javaProject.getProject().getName();
			Log.error("Project classpath read failed [proj=" + name + "]");
			return sourceFolders;
		}

		for (IClasspathEntry cpe : entries) {
			if (cpe.getEntryKind() == IClasspathEntry.CPE_SOURCE && !javaProject.getPath().equals(cpe.getPath())) {
				IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(cpe.getPath());
				if (resource != null && resource.exists() && (resource.getType() == IResource.FOLDER)) {
					sourceFolders.add(resource);
				}
			}
		}
		return sourceFolders;
	}

	public void openFile(final IFile file, final int lineNumber) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IEditorPart part = IDE.openEditor(page, file, true);
					if (part instanceof ITextEditor) {
						revealLine((ITextEditor) part, lineNumber);
					}
				} catch (PartInitException e) {
					Log.error("Failed to open editor [msg=" + e.getMessage() + "]");
				} catch (BadLocationException e) {
					Log.error("Failed to reveal line [msg=" + e.getMessage() + "]");
				}
			}
		});
	}

	private void revealLine(ITextEditor editor, int lineNumber) throws BadLocationException {
		IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		IRegion region = document.getLineInformation(lineNumber - 1);
		editor.selectAndReveal(region.getOffset(), region.getLength());
	}

	@SuppressWarnings("unused")
	private boolean hasSourceFolders(IJavaProject javaProject) {
		IClasspathEntry[] entries = null;
		try {
			entries = javaProject.getRawClasspath();
		} catch (JavaModelException e) {
			return false;
		}

		for (int i = 0, maxi = entries.length; i < maxi; i++) {
			if ((entries[i].getEntryKind() == IClasspathEntry.CPE_SOURCE)
					&& (!javaProject.getPath().equals(entries[i].getPath())))
				return true;
		}
		return false;
	}
}
