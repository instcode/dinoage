package org.ddth.dinoage.eclipse.ui.editors;

import java.util.List;
import java.util.Vector;

import org.ddth.blogging.Blog;
import org.ddth.blogging.Entry;
import org.ddth.blogging.yahoo.grabber.YahooProfile;
import org.ddth.dinoage.core.Profile;
import org.ddth.dinoage.core.ProfileChangeEvent;
import org.ddth.dinoage.core.ProfileChangeListener;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

public class ProfileContentProvider implements ILazyContentProvider, ProfileChangeListener {
	private TableViewer viewer;
	private List<Entry> entries = new Vector<Entry>();
	
	public ProfileContentProvider() {
	}

	public void profileChanged(ProfileChangeEvent event) {
		if (event.getData() == null) {
			return;
		}
		switch (event.getType()) {
		case ProfileChangeEvent.PROFILE_LOADED_CHANGE:
			entries.addAll(((Blog) event.getData()).getEntries());
			break;
			
		case ProfileChangeEvent.ENTRY_ADDED_CHANGE:
			entries.add((Entry) event.getData());
			break;
		}

		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			public void run() {
				// Fix the item count in UI thread
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
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		this.viewer = (TableViewer) viewer;
		this.viewer.setItemCount(entries.size());
		if (newInput != oldInput) {
			if (oldInput instanceof ProfileEditorInput) {
				ProfileEditorInput input = (ProfileEditorInput)newInput;
				Profile profile = (Profile) input.getAdapter(Profile.class);
				profile.removeProfileChangeListener(this);
			}
			if (newInput instanceof ProfileEditorInput) {
				ProfileEditorInput input = (ProfileEditorInput)newInput;
				final Profile profile = (Profile) input.getAdapter(Profile.class);
				profile.addProfileChangeListener(this);
				new Thread(new Runnable() {
					public void run() {
						((YahooProfile)profile).load();
					}
				}).start();
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
