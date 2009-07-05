package org.ddth.dinoage.eclipse.ui.editors;

import java.util.Collections;
import java.util.Comparator;
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
	
	private Comparator<Entry> descByDateComparator = new Comparator<Entry>() {
		@Override
		public int compare(Entry o1, Entry o2) {
			long diff = o1.getPost().getDate().getTime() - o2.getPost().getDate().getTime();
			// Use "lower or equal 0" to make sure the sorting is *stable*
			return diff <= 0 ? 1 : -1;
		}
	};
	
	public ProfileContentProvider() {
	}

	public void profileChanged(ProfileChangeEvent event) {
		if (event.getData() == null) {
			return;
		}
		switch (event.getType()) {
		case ProfileChangeEvent.PROFILE_FIRST_LOADED:
			entries.clear();
			entries.addAll(((Blog) event.getData()).getEntries());
			Collections.sort(entries, descByDateComparator);
			break;
			
		case ProfileChangeEvent.PROFILE_CHANGED:
			Entry entry = (Entry) event.getData();
			int index = Collections.binarySearch(entries, entry, descByDateComparator);
			entries.add(-1 - index, entry);
			break;
		
		case ProfileChangeEvent.PROFILE_DELTA_CHANGED:
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
				viewer.refresh();
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
			if (oldInput instanceof ProfileEditorInput) {
				ProfileEditorInput input = (ProfileEditorInput)oldInput;
				SessionProfile profile = (SessionProfile) input.getAdapter(Profile.class);
				profile.removeProfileChangeListener(this);
			}
			if (newInput instanceof ProfileEditorInput) {
				ProfileEditorInput input = (ProfileEditorInput)newInput;
				SessionProfile profile = (SessionProfile) input.getAdapter(Profile.class);
				profile.addProfileChangeListener(this);
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
