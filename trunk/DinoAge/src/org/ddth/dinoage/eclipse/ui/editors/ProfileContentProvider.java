package org.ddth.dinoage.eclipse.ui.editors;

import java.util.List;
import java.util.Vector;

import org.ddth.blogging.Blog;
import org.ddth.blogging.Entry;
import org.ddth.dinoage.core.Profile;
import org.ddth.dinoage.core.ProfileChangeEvent;
import org.ddth.dinoage.core.ProfileChangeListener;
import org.ddth.dinoage.core.SessionProfile;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

public class ProfileContentProvider implements ILazyContentProvider, ProfileChangeListener {
	private TableViewer viewer;
	private List<Entry> entries = new Vector<Entry>();
	private Thread loadingThread;
	
	public ProfileContentProvider() {
	}

	public void profileChanged(ProfileChangeEvent event) {
		if (event.getData() == null) {
			return;
		}
		switch (event.getType()) {
		case ProfileChangeEvent.PROFILE_FIRST_LOADED:
			entries.addAll(((Blog) event.getData()).getEntries());
			break;
			
		case ProfileChangeEvent.PROFILE_CHANGED:
			entries.add((Entry) event.getData());
			break;
		}

		// Because #profileChanged might be invoked from another
		// thread, we should fix the item count in the UI thread.
		// Sometimes, when the widget has been disposed but the
		// worker thread is still running a little bit more, 
		// an exception about "Widget is disposed" might be
		// thrown. So we must check for safely updating the view
		// here. It's just a workaround & doesn't resolve issues
		// nicely.
		if (viewer.getControl().isDisposed()) {
			return;
		}
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (viewer.getControl().isDisposed()) {
					return;
				}
				if (entries.size() != viewer.getTable().getItemCount()) {
					viewer.setItemCount(entries.size());
				}
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TableViewer) viewer;
		if (newInput != oldInput) {
			entries.clear();
			this.viewer.setItemCount(0);
			// Brutally stop the loading thread =))
			if (loadingThread != null && loadingThread.isAlive()) {
				loadingThread.interrupt();
				loadingThread = null;
			}
			if (oldInput instanceof ProfileEditorInput) {
				ProfileEditorInput input = (ProfileEditorInput)oldInput;
				Profile profile = (Profile) input.getAdapter(Profile.class);
				profile.removeProfileChangeListener(this);
			}
			if (newInput instanceof ProfileEditorInput) {
				ProfileEditorInput input = (ProfileEditorInput)newInput;
				final Profile profile = (Profile) input.getAdapter(Profile.class);
				profile.addProfileChangeListener(this);
				// Place the loading profile in a thread to ensure
				// it doesn't block the UI thread.
				loadingThread = new Thread(new Runnable() {
					public void run() {
						((SessionProfile)profile).load();
					}
				});
				loadingThread.start();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILazyContentProvider#updateElement(int)
	 */
	public void updateElement(int index) {
		viewer.replace(entries.get(index), index);
	}
}
