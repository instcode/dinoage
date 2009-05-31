package org.ddth.dinoage.grabber;

import org.ddth.dinoage.model.Profile;

public abstract class SessionProfile extends Profile {

	public abstract void saveURL(String url);

	public abstract String getBeginningURL();

	public abstract boolean isNewlyCreated();
}
