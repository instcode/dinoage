package org.ddth.dinoage.core;


public abstract class SessionProfile extends Profile {

	public abstract void saveURL(String url);

	public abstract String getBeginningURL();

	public abstract boolean isNewlyCreated();
	
	public abstract void load();
}
